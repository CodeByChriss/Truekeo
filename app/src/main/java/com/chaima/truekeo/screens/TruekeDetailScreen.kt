package com.chaima.truekeo.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.chaima.truekeo.data.MockData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TruekeDetailsScreen(
    truekeId: String,
    onBack: () -> Unit
) {
    val trueke = remember(truekeId) {
        MockData.sampleTruekesWithTaker.firstOrNull { it.id == truekeId }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Hola") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (trueke == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Trueke no encontrado")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Text(trueke.title, style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(8.dp))
                trueke?.description?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
                Spacer(Modifier.height(16.dp))
                Text("Estado: ${trueke.status.name}")
                // Aquí metes imágenes, usuarios, items, ubicación, etc.
            }
        }
    }
}

@Composable
private fun UserItemCard(
    userTitle: String,
    userName: String,
    userSubtitle: String?,
    itemName: String,
    itemDetails: String?,
    itemBrand: String?,
    // itemImageUrl: String?
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(14.dp)) {

            Text(
                text = userTitle,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = userName,
                style = MaterialTheme.typography.titleMedium
            )
            userSubtitle?.takeIf { it.isNotBlank() }?.let {
                Spacer(Modifier.height(2.dp))
                Text(text = it, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(Modifier.height(12.dp))

            // Aquí podrías poner una fila con imagen + textos
            // Row(verticalAlignment = Alignment.CenterVertically) { ... }

            Text(
                text = itemName,
                style = MaterialTheme.typography.titleMedium
            )

            itemBrand?.takeIf { it.isNotBlank() }?.let {
                Spacer(Modifier.height(4.dp))
                Text("Marca: $it", style = MaterialTheme.typography.bodyMedium)
            }

            itemDetails?.takeIf { it.isNotBlank() }?.let {
                Spacer(Modifier.height(6.dp))
                Text(it, style = MaterialTheme.typography.bodyMedium)
            }

            // Si quieres imagen con Coil:
            // Spacer(Modifier.height(10.dp))
            // AsyncImage(
            //     model = itemImageUrl,
            //     contentDescription = "Imagen del item",
            //     modifier = Modifier
            //         .fillMaxWidth()
            //         .height(180.dp)
            // )
        }
    }
}