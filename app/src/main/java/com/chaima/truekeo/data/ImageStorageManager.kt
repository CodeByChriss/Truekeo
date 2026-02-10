package com.chaima.truekeo.data

import android.content.Context
import android.net.Uri
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.io.File
import java.util.UUID

class ImageStorageManager(private val context: Context) {
    private val storage = SupabaseClient.supabase.storage

    suspend fun uploadProfilePhoto(uid: String, imageUri: Uri): String {
        // Convertimos la URI a File de forma temporal
        val file = File(context.cacheDir, "temp_image_${uid}.jpg")
        context.contentResolver.openInputStream(imageUri)?.use { input ->
            file.outputStream().use { output -> input.copyTo(output) }
        }

        // Comprimimos la imagen
        val compressedFile = Compressor.compress(context, file) {
            default(width = 320, height = 320, quality = 80)
        }

        // Lo subimos a Supabase (Bucket "profile_photos")
        val fileName = "pf_$uid.jpg"
        val bucket = storage.from("profile_photos")

        // Upsert true para que sobrescriba si ya existe
        bucket.upload(fileName, compressedFile.readBytes(), upsert = true)

        return bucket.publicUrl(fileName)
    }

    // Sube y comprime las imágenes de un item a supabase y devuelve sus urls
    suspend fun uploadItemImages(
        itemId: String,
        imageUris: List<Uri>
    ): List<String> = coroutineScope {
        if (imageUris.isEmpty()) return@coroutineScope emptyList()

        imageUris.mapIndexed { index, uri ->
            async {
                val imageIndex = index + 1

                // Convertimos la URI a File de forma temporal
                val tempFile = File(context.cacheDir, "temp_it_${itemId}_$imageIndex.jpg")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    tempFile.outputStream().use { output -> input.copyTo(output) }
                } ?: error("No se pudo leer la imagen ($uri)")

                // Comprimimos la imagen
                val compressedFile = Compressor.compress(context, tempFile) {
                    default(width = 1080, height = 1080, quality = 80)
                }

                // Lo subimos a Supabase con el id del item y su posicion (Bucket "item_photos")
                val path = "it_${itemId}_${imageIndex}.jpg"
                val bucket = storage.from("item_photos")

                // Upsert true para que sobrescriba si ya existe
                bucket.upload(path, compressedFile.readBytes(), upsert = true)

                bucket.publicUrl(path)
            }
        }.awaitAll()
    }
}