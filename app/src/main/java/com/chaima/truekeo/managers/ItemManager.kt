package com.chaima.truekeo.managers

import android.content.Context
import android.net.Uri
import android.util.Log
import com.chaima.truekeo.models.Item
import com.chaima.truekeo.models.ItemCondition
import com.chaima.truekeo.models.ItemStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object ItemContainer {
    val itemManager = ItemManager()
}

class ItemManager {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Obtiene todos los items del usuario actual que estén disponibles para trueke
    suspend fun getMyAvailableItems(): List<Item> {
        return try {
            val uid = auth.currentUser?.uid ?: return emptyList()

            val snapshot = db.collection("items")
                .whereEqualTo("ownerId", uid)
                .whereEqualTo("status", ItemStatus.AVAILABLE.name) // Sigue siendo .name para la query
                .get()
                .await()

            snapshot.toObjects(Item::class.java)
        } catch (e: Exception) {
            Log.e("ItemManager", "Error getMyAvailableItems: ${e.message}")
            emptyList()
        }
    }

    // Obtiene todos los items del usuario actual sin importar el estado
    suspend fun getMyItems(): List<Item> {
        return try {
            val uid = auth.currentUser?.uid ?: return emptyList()

            val snapshot = db.collection("items")
                .whereEqualTo("ownerId", uid)
                .get()
                .await()

            snapshot.toObjects(Item::class.java)
        } catch (e: Exception) {
            Log.e("ItemManager", "Error getMyItems: ${e.message}")
            emptyList()
        }
    }

    // Crea un nuevo producto subiendo sus imágenes a Supabase y guarda el item en Firestore
    suspend fun createItem(
        name: String,
        details: String?,
        imageUris: List<Uri>,
        brand: String?,
        condition: ItemCondition,
        context: Context,
    ): Result<String> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("No autenticado"))

            // Generar itemId para usarlo en las rutas de Supabase y en Firestore
            val itemId = db.collection("items").document().id

            // Subir imágenes comprimidas a supabase y nos devuelve urls
            val imageStorage = ImageStorageManager(context)
            val imageUrls = imageStorage.uploadItemImages(
                itemId = itemId,
                imageUris = imageUris
            )

            val ref = db.collection("items").document(itemId)
            val item = Item(
                id = itemId,
                name = name.trim(),
                details = details?.trim(),
                imageUrls = imageUrls,
                brand = brand?.trim(),
                condition = condition,
                ownerId = uid,
                status = ItemStatus.AVAILABLE
            )

            ref.set(item).await()
            Result.success(itemId)

        } catch (e: Exception) {
            Log.e("ItemManager", "Error createItem: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun updateItemStatus(itemId: String, newStatus: ItemStatus): Result<Unit> {
        return try {
            db.collection("items").document(itemId)
                .update("status", newStatus.name) // Usar .name para actualizar
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ItemManager", "Error updateItemStatus: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getItemById(itemId: String): Item? {
        return try {
            val doc = db.collection("items").document(itemId).get().await()
            doc.toObject(Item::class.java)
        } catch (e: Exception) {
            Log.e("ItemManager", "Error getItemById: ${e.message}")
            null
        }
    }


    suspend fun deleteItem(itemId: String): Result<Unit> {
        return try {
            val item = getItemById(itemId)
            if (item?.status != ItemStatus.AVAILABLE) { // Ya no necesitas .name aquí
                return Result.failure(Exception("No se puede eliminar un item reservado o intercambiado"))
            }

            db.collection("items").document(itemId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ItemManager", "Error deleteItem: ${e.message}")
            Result.failure(e)
        }
    }
}