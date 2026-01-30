package com.chaima.truekeo.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cached
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.chaima.truekeo.R

@Composable
fun FabOverlayActions(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onCreateTrueke: () -> Unit,
    onCreateProduct: () -> Unit
) {
    val scrimAlpha by animateFloatAsState(
        targetValue = if (expanded) 0.25f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "scrim-alpha"
    )

    if (scrimAlpha > 0f) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onDismiss() }
        )
    }

    // 2) Progreso de expansión (0 -> 1) para mover/escala/alpha
    val t by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec = spring(
            dampingRatio = 0.75f, // “pop” suave
            stiffness = Spring.StiffnessLow
        ),
        label = "fab-menu-progress"
    )

    //
    if (t < 0.01f) return

    // Punto ancla: centro inferior (donde cae el +)
    Box(modifier = Modifier.fillMaxSize()) {

        // Ajusta este bottom para que el origen sea el +
        val originBottom = 34.dp // sube/baja el "punto origen" respecto al bottom
        val spreadUp = 96.dp // cuánto suben
        val spreadSide = 36.dp // cuánto se separan a los lados

        // Botón izquierdo (Trueke): sale arriba-izq desde el +
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(
                    x = (-spreadSide * t),
                    y = (-spreadUp * t) - originBottom
                )
                .alpha(t)
        ) {
            SmallFabAction(icon = Icons.Outlined.Cached, label = stringResource(R.string.create_trueke), scale = t, onClick = onCreateTrueke)
        }

        // Botón derecho (Producto): sale arriba-der desde el +
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(
                    x = (spreadSide * t),
                    y = (-spreadUp * t) - originBottom
                )
                .alpha(t)
        ) {
            SmallFabAction(icon = Icons.Outlined.Category, label = stringResource(R.string.create_product), scale = t, onClick = onCreateProduct)
        }
    }
}

@Composable
private fun SmallFabAction(
    icon: ImageVector,
    label: String,
    scale: Float,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .graphicsLayer {
                scaleX = 0.6f + (0.4f * scale)
                scaleY = 0.6f + (0.4f * scale)
            }
    ) {
        SmallFloatingActionButton(
            onClick = onClick,
            containerColor = MaterialTheme.colorScheme.tertiary,
            contentColor = Color.Transparent,
            shape = RoundedCornerShape(14.dp),
            elevation = FloatingActionButtonDefaults.elevation(0.dp),
            modifier = Modifier
                .size(52.dp)
                .border(1.5.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(14.dp))
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(22.dp),
                tint = Color.White
            )
        }
    }
}