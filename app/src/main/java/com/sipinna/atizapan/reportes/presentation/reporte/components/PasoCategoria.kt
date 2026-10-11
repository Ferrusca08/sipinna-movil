package com.sipinna.atizapan.reportes.presentation.reporte.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Yard
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sipinna.atizapan.reportes.domain.model.CategoriaTrabajoInfantil
import com.sipinna.atizapan.reportes.presentation.theme.AzulInstitucional
import com.sipinna.atizapan.reportes.presentation.theme.BordeGris
import com.sipinna.atizapan.reportes.presentation.theme.RojoAlerta
import com.sipinna.atizapan.reportes.presentation.theme.TextoSecundario

private fun iconoDe(categoria: CategoriaTrabajoInfantil): ImageVector = when (categoria) {
    CategoriaTrabajoInfantil.VENTA_AMBULANTE -> Icons.Filled.ShoppingBag
    CategoriaTrabajoInfantil.LIMPIA_PARABRISAS -> Icons.Filled.WaterDrop
    CategoriaTrabajoInfantil.CARGA_MERCANCIA -> Icons.Filled.Build
    CategoriaTrabajoInfantil.TRABAJO_DOMESTICO -> Icons.Filled.Home
    CategoriaTrabajoInfantil.MENDICIDAD -> Icons.Filled.Person
    CategoriaTrabajoInfantil.RECOLECCION_BASURA -> Icons.Filled.Delete
    CategoriaTrabajoInfantil.TRABAJO_AGRICOLA -> Icons.Filled.Yard
    CategoriaTrabajoInfantil.COMERCIO_ESTABLECIDO -> Icons.Filled.ShoppingCart
    CategoriaTrabajoInfantil.ESPECTACULO_CALLEJERO -> Icons.Filled.Star
    CategoriaTrabajoInfantil.EXPLOTACION_SEXUAL -> Icons.Filled.Warning
    CategoriaTrabajoInfantil.ACTIVIDAD_ILICITA -> Icons.Filled.Lock
    CategoriaTrabajoInfantil.OTRO -> Icons.Filled.MoreHoriz
}

@Composable
fun PasoCategoria(
    seleccionada: CategoriaTrabajoInfantil?,
    alSeleccionar: (CategoriaTrabajoInfantil) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
            Column {
                Text(
                    text = "¿Qué situación observaste?",
                    style = MaterialTheme.typography.titleLarge,
                    color = AzulInstitucional,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Elige la opción que más se parezca a lo que viste.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario,
                )
                Spacer(Modifier.height(8.dp))
            }
        }

        items(CategoriaTrabajoInfantil.entries) { categoria ->
            ChipCategoria(
                categoria = categoria,
                seleccionada = categoria == seleccionada,
                alSeleccionar = { alSeleccionar(categoria) },
            )
        }
    }
}

@Composable
private fun ChipCategoria(
    categoria: CategoriaTrabajoInfantil,
    seleccionada: Boolean,
    alSeleccionar: () -> Unit,
) {
    // Las categorías urgentes se marcan en rojo desde la selección: la persona
    // debe saber que ese caso se atiende de inmediato.
    val colorAcento = if (categoria.esUrgente) RojoAlerta else AzulInstitucional

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(112.dp)
            .border(
                width = if (seleccionada) 0.dp else 1.dp,
                color = if (categoria.esUrgente) RojoAlerta.copy(alpha = 0.4f) else BordeGris,
                shape = RoundedCornerShape(12.dp),
            )
            .clickable(onClick = alSeleccionar),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (seleccionada) colorAcento else Color.White,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = iconoDe(categoria),
                contentDescription = null,
                tint = if (seleccionada) Color.White else colorAcento,
                modifier = Modifier.size(28.dp),
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = categoria.etiqueta,
                style = MaterialTheme.typography.labelMedium,
                color = if (seleccionada) Color.White else AzulInstitucional,
                textAlign = TextAlign.Center,
            )
        }
    }
}
