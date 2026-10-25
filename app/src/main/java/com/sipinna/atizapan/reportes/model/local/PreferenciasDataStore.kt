package com.sipinna.atizapan.reportes.model.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sipinna.atizapan.reportes.model.TokenSeguimiento
import com.sipinna.atizapan.reportes.model.Usuario
import com.sipinna.atizapan.reportes.model.repository.PreferenciasRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: androidx.datastore.core.DataStore<Preferences> by preferencesDataStore(
    name = "sipinna_preferencias",
)

/**
 * Persistencia local. Guarda:
 *  - los tokens de los reportes enviados desde este teléfono (anónimos);
 *  - la sesión del usuario ciudadano activo;
 *  - el registro de cuentas y los reportes asociados a cada cuenta de usuario;
 *  - el borrador del formulario, para no perder la captura si la app muere.
 */
@Singleton
class PreferenciasDataStore @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext private val contexto: Context,
) : PreferenciasRepository {
    private val clavesTokens = stringPreferencesKey("tokens_seguimiento")
    private val claveBorrador = stringPreferencesKey("borrador_reporte")

    // Claves de sesión activa
    private val claveUsuarioId = stringPreferencesKey("usuario_id")
    private val claveUsuarioEmail = stringPreferencesKey("usuario_email")
    private val claveUsuarioNombre = stringPreferencesKey("usuario_nombre")
    private val claveUsuarioToken = stringPreferencesKey("usuario_token")

    // Claves de registro y aislamiento
    private val claveCuentas = stringPreferencesKey("cuentas_registradas")
    private val claveReportesUsuario = stringPreferencesKey("reportes_por_usuario")

    // Formato por línea: reporteId|fechaISO|categoria
    override val tokens: Flow<List<TokenSeguimiento>> = contexto.dataStore.data.map { prefs ->
        prefs[clavesTokens]
            .orEmpty()
            .lineSequence()
            .filter { it.isNotBlank() }
            .mapNotNull { linea ->
                val partes = linea.split("|")
                if (partes.size >= 3) {
                    TokenSeguimiento(partes[0], partes[1], partes[2])
                } else {
                    null
                }
            }
            .toList()
    }

    override suspend fun guardarToken(reporteId: String, fecha: String, categoria: String) {
        contexto.dataStore.edit { prefs ->
            val existentes = prefs[clavesTokens].orEmpty()
                .lineSequence()
                .filter { it.isNotBlank() && !it.startsWith("$reporteId|") }
                .toList()

            // Se conservan los 20 más recientes; el resto se descarta.
            val nuevos = (listOf("$reporteId|$fecha|$categoria") + existentes).take(20)
            prefs[clavesTokens] = nuevos.joinToString("\n")
        }
    }

    override suspend fun eliminarToken(reporteId: String) {
        contexto.dataStore.edit { prefs ->
            prefs[clavesTokens] = prefs[clavesTokens].orEmpty()
                .lineSequence()
                .filter { it.isNotBlank() && !it.startsWith("$reporteId|") }
                .joinToString("\n")
        }
    }

    override val borrador: Flow<String?> = contexto.dataStore.data.map { it[claveBorrador] }

    override suspend fun guardarBorrador(json: String) {
        contexto.dataStore.edit { it[claveBorrador] = json }
    }

    override suspend fun limpiarBorrador() {
        contexto.dataStore.edit { it.remove(claveBorrador) }
    }

    // --- Sesión de usuario activo -------------------------------------------
    override val usuarioActual: Flow<Usuario?> = contexto.dataStore.data.map { prefs ->
        val id = prefs[claveUsuarioId]
        val email = prefs[claveUsuarioEmail]
        val nombre = prefs[claveUsuarioNombre]
        val token = prefs[claveUsuarioToken]
        if (!id.isNullOrBlank() && !email.isNullOrBlank() && !token.isNullOrBlank()) {
            Usuario(id = id, email = email, nombre = nombre.orEmpty(), token = token)
        } else {
            null
        }
    }

    override suspend fun guardarUsuario(usuario: Usuario) {
        contexto.dataStore.edit { prefs ->
            prefs[claveUsuarioId] = usuario.id
            prefs[claveUsuarioEmail] = usuario.email
            prefs[claveUsuarioNombre] = usuario.nombre
            prefs[claveUsuarioToken] = usuario.token
        }
    }

    override suspend fun cerrarSesion() {
        contexto.dataStore.edit { prefs ->
            prefs.remove(claveUsuarioId)
            prefs.remove(claveUsuarioEmail)
            prefs.remove(claveUsuarioNombre)
            prefs.remove(claveUsuarioToken)
        }
    }

    override suspend fun obtenerTokenAuth(): String? {
        return contexto.dataStore.data.map { it[claveUsuarioToken] }.firstOrNull()
    }

    // --- Reportes por cuenta de usuario -------------------------------------
    override fun tokensDeUsuario(usuarioId: String): Flow<List<TokenSeguimiento>> =
        contexto.dataStore.data.map { prefs ->
            prefs[claveReportesUsuario]
                .orEmpty()
                .lineSequence()
                .filter { it.isNotBlank() && it.startsWith("$usuarioId|") }
                .mapNotNull { linea ->
                    val partes = linea.split("|")
                    if (partes.size >= 4) {
                        TokenSeguimiento(
                            reporteId = partes[1],
                            fecha = partes[2],
                            categoria = partes[3],
                        )
                    } else {
                        null
                    }
                }
                .toList()
        }

    override suspend fun guardarTokenUsuario(
        usuarioId: String,
        reporteId: String,
        fecha: String,
        categoria: String,
    ) {
        contexto.dataStore.edit { prefs ->
            val existentes = prefs[claveReportesUsuario].orEmpty()
                .lineSequence()
                .filter { it.isNotBlank() && !it.startsWith("$usuarioId|$reporteId|") }
                .toList()
            val nuevo = "$usuarioId|$reporteId|$fecha|$categoria"
            prefs[claveReportesUsuario] = (listOf(nuevo) + existentes).joinToString("\n")
        }
    }

    // --- Registro y autenticación local -------------------------------------
    override suspend fun registrarCuenta(nombre: String, email: String, hash: String): Result<Usuario> {
        val normalizado = email.trim().lowercase()
        var resultado: Result<Usuario> = Result.failure(IllegalStateException("No se pudo registrar"))
        contexto.dataStore.edit { prefs ->
            val lineas = prefs[claveCuentas].orEmpty().lineSequence().filter { it.isNotBlank() }.toList()
            val yaExiste = lineas.any { it.split("|").getOrNull(1)?.lowercase() == normalizado }
            if (yaExiste) {
                resultado = Result.failure(IllegalArgumentException("Ya existe una cuenta registrada con este correo."))
                return@edit
            }
            val id = "USR" + System.currentTimeMillis().toString(36).uppercase() + (100..999).random()
            val token = "jwt_" + id + "_" + System.currentTimeMillis()
            val nuevaLinea = "$id|$normalizado|${nombre.trim()}|$hash"
            prefs[claveCuentas] = (listOf(nuevaLinea) + lineas).joinToString("\n")
            prefs[claveUsuarioId] = id
            prefs[claveUsuarioEmail] = normalizado
            prefs[claveUsuarioNombre] = nombre.trim()
            prefs[claveUsuarioToken] = token
            resultado = Result.success(Usuario(id, normalizado, nombre.trim(), token))
        }
        return resultado
    }

    override suspend fun autenticarCuenta(email: String, hash: String): Result<Usuario> {
        val normalizado = email.trim().lowercase()
        var usuarioEncontrado: Usuario? = null
        val prefs = contexto.dataStore.data.first()
        val lineas = prefs[claveCuentas].orEmpty().lineSequence().filter { it.isNotBlank() }.toList()
        for (linea in lineas) {
            val partes = linea.split("|")
            if (partes.size >= 4) {
                val cId = partes[0]
                val cEmail = partes[1].lowercase()
                val cNombre = partes[2]
                val cHash = partes[3]
                if (cEmail == normalizado && cHash == hash) {
                    val token = "jwt_" + cId + "_" + System.currentTimeMillis()
                    usuarioEncontrado = Usuario(cId, cEmail, cNombre, token)
                    break
                }
            }
        }
        return if (usuarioEncontrado != null) {
            guardarUsuario(usuarioEncontrado)
            Result.success(usuarioEncontrado)
        } else {
            Result.failure(IllegalArgumentException("Correo o contraseña incorrectos."))
        }
    }
}
