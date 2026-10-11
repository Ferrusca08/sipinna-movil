package com.sipinna.atizapan.reportes.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.sipinna.atizapan.reportes.domain.model.Ubicacion
import com.sipinna.atizapan.reportes.domain.repository.TimeoutError
import com.sipinna.atizapan.reportes.domain.repository.UbicacionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class UbicacionRepositoryImpl @Inject constructor(
    @ApplicationContext private val contexto: Context,
    private val fusedLocation: FusedLocationProviderClient,
    private val io: CoroutineDispatcher,
) : UbicacionRepository {

    /**
     * Ambito propio del repositorio. El Geocoder sincrono de API < 33 bloquea el
     * hilo y no atiende la cancelacion de la corrutina, asi que su trabajo se
     * lanza aqui: cuando vence el plazo la pantalla sigue su curso y el hilo
     * huerfano termina por su cuenta, sin dejar colgado a quien llamo.
     */
    private val ambitoGeocoder = CoroutineScope(io + SupervisorJob())

    /**
     * El permiso se pide en la pantalla antes de llamar aqui; si aun asi falta,
     * getCurrentLocation lanza SecurityException y se devuelve como Result fallido.
     */
    override suspend fun obtenerUbicacionActual(tiempoLimiteMs: Long): Result<Ubicacion> =
        withContext(io) {
            try {
                // Si el GPS no fija a tiempo se usa la ultima posicion conocida
                // antes de rendirse: dentro de un edificio la fijacion de alta
                // precision puede no llegar nunca.
                val posicion = posicionConPlazo(tiempoLimiteMs)
                    ?: ultimaPosicionConocida()
                    ?: throw TimeoutError("El GPS tardó demasiado. Puedes ajustar el punto en el mapa.")

                Result.success(describirUbicacion(posicion.latitude, posicion.longitude))
            } catch (e: CancellationException) {
                // La cancelacion es de quien llama (salio de la pantalla), no un fallo.
                throw e
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    @SuppressLint("MissingPermission")
    private suspend fun posicionConPlazo(tiempoLimiteMs: Long): Location? {
        val cancelacion = CancellationTokenSource()
        return try {
            withTimeoutOrNull(tiempoLimiteMs) {
                fusedLocation
                    .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancelacion.token)
                    .await()
            }
        } finally {
            // Corta la peticion al proveedor si el plazo vencio; si ya respondio
            // es una llamada sin efecto.
            cancelacion.cancel()
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun ultimaPosicionConocida(): Location? =
        withTimeoutOrNull(PLAZO_ULTIMA_POSICION_MS) {
            runCatching { fusedLocation.lastLocation.await() }.getOrNull()
        }

    /**
     * Geocodificacion inversa. Sale a la red y el servicio puede tardar sin
     * limite propio, asi que se le impone un plazo: si no contesta se devuelven
     * solo las coordenadas, que bastan para un reporte valido.
     */
    override suspend fun describirUbicacion(lat: Double, lng: Double): Ubicacion =
        withContext(io) {
            val base = Ubicacion(lat = lat, lng = lng)

            if (!Geocoder.isPresent()) return@withContext base

            val direccion = withTimeoutOrNull(PLAZO_GEOCODER_MS) { primeraDireccion(lat, lng) }
                ?: return@withContext base

            base.copy(
                direccion = listOfNotNull(direccion.thoroughfare, direccion.subThoroughfare)
                    .joinToString(" ")
                    .ifBlank { direccion.getAddressLine(0).orEmpty() },
                colonia = direccion.subLocality ?: direccion.locality.orEmpty(),
                cp = direccion.postalCode.orEmpty(),
            )
        }

    private suspend fun primeraDireccion(lat: Double, lng: Double): Address? {
        val geocoder = Geocoder(contexto, Locale("es", "MX"))

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            direccionAsincrona(geocoder, lat, lng)
        } else {
            direccionBloqueante(geocoder, lat, lng)
        }
    }

    /** API 33+ trae la variante con callback, que si responde a la cancelacion. */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private suspend fun direccionAsincrona(
        geocoder: Geocoder,
        lat: Double,
        lng: Double,
    ): Address? = suspendCancellableCoroutine { continuacion ->
        geocoder.getFromLocation(
            lat,
            lng,
            1,
            object : Geocoder.GeocodeListener {
                override fun onGeocode(direcciones: MutableList<Address>) {
                    if (continuacion.isActive) continuacion.resume(direcciones.firstOrNull())
                }

                override fun onError(errorMessage: String?) {
                    if (continuacion.isActive) continuacion.resume(null)
                }
            },
        )
    }

    /**
     * Debajo de API 33 solo existe la llamada bloqueante. Se aisla en el ambito
     * del repositorio para que el plazo de [describirUbicacion] pueda vencer
     * aunque el hilo siga esperando la respuesta del servicio.
     */
    @Suppress("DEPRECATION")
    private suspend fun direccionBloqueante(
        geocoder: Geocoder,
        lat: Double,
        lng: Double,
    ): Address? {
        val trabajo: Deferred<Address?> = ambitoGeocoder.async {
            runCatching { geocoder.getFromLocation(lat, lng, 1)?.firstOrNull() }.getOrNull()
        }
        return trabajo.await()
    }

    override fun ubicacionHabilitada(): Boolean {
        val manager = contexto.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return false
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private companion object {
        /** La ultima posicion conocida es un dato en cache: si tarda, no existe. */
        const val PLAZO_ULTIMA_POSICION_MS = 3_000L

        /** La direccion textual es opcional; no vale la pena esperarla mas que esto. */
        const val PLAZO_GEOCODER_MS = 6_000L
    }
}
