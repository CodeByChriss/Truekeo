package com.chaima.truekeo.data

import android.content.Context
import android.net.Uri
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import io.github.jan.supabase.storage.storage
import java.io.File

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
}