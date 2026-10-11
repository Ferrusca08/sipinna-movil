package com.sipinna.atizapan.reportes.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: androidx.datastore.core.DataStore<Preferences> by preferencesDataStore(
    name = "sipinna_preferencias",
)

/** Un token de seguimiento guardado en el dispositivo. */
data class TokenGuardado(
    val reporteId: String,
    val fecha: String,
    val categoria: String,
)

/**
 * Persistencia local. Guarda dos cosas:
 *  - los tokens de los reportes enviados desde este teléfono, para que la
 *    persona pueda consultarlos sin memorizar 26 caracteres;
 *  - el borrador del formulario, para no perder la captura si la app muere.
 */
@Singleton
class PreferenciasDataStore @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext private val contexto: Context,
) {
    private val clavesTokens = stringPreferencesKey("tokens_seguimiento")
    private val claveBorrador = stringPreferencesKey("borrador_reporte")

    // Formato por línea: reporteId|fechaISO|categoria
    val tokens: Flow<List<TokenGuardado>> = contexto.dataStore.data.map { prefs ->
        prefs[clavesTokens]
            .orEmpty()
            .lineSequence()
            .filter { it.isNotBlank() }
            .mapNotNull { linea ->
                val partes = linea.split("|")
                if (partes.size >= 3) {
                    TokenGuardado(partes[0], partes[1], partes[2])
                } else {
                    null
                }
            }
            .toList()
    }

    suspend fun guardarToken(reporteId: String, fecha: String, categoria: String) {
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

    suspend fun eliminarToken(reporteId: String) {
        contexto.dataStore.edit { prefs ->
            prefs[clavesTokens] = prefs[clavesTokens].orEmpty()
                .lineSequence()
                .filter { it.isNotBlank() && !it.startsWith("$reporteId|") }
                .joinToString("\n")
        }
    }

    val borrador: Flow<String?> = contexto.dataStore.data.map { it[claveBorrador] }

    suspend fun guardarBorrador(json: String) {
        contexto.dataStore.edit { it[claveBorrador] = json }
    }

    suspend fun limpiarBorrador() {
        contexto.dataStore.edit { it.remove(claveBorrador) }
    }
}
