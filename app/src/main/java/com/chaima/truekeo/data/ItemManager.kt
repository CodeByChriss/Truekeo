package com.chaima.truekeo.data

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

    /**
     * Obtiene todos los items del usuario actual que estén disponibles para trueke
     */
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

    /**
     * Obtiene todos los items del usuario actual (sin importar el status)
     */
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

    /**
     * Crea un nuevo item
     */
    suspend fun createItem(
        name: String,
        details: String?,
        imageUrls: List<String>,
        brand: String?,
        condition: ItemCondition
    ): Result<String> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("No autenticado"))

            val newRef = db.collection("items").document()

            val item = Item(
                id = newRef.id,
                name = name.trim(),
                details = details?.trim(),
                imageUrls = imageUrls,
                brand = brand?.trim(),
                condition = condition,
                ownerId = uid,
                status = ItemStatus.AVAILABLE // Ya es enum directamente
            )

            newRef.set(item).await()
            Result.success(newRef.id)
        } catch (e: Exception) {
            Log.e("ItemManager", "Error createItem: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Actualiza el status de un item
     */
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

    /**
     * Obtiene un item por su ID
     */
    suspend fun getItemById(itemId: String): Item? {
        return try {
            val doc = db.collection("items").document(itemId).get().await()
            doc.toObject(Item::class.java)
        } catch (e: Exception) {
            Log.e("ItemManager", "Error getItemById: ${e.message}")
            null
        }
    }

    /**
     * Elimina un item (solo si es AVAILABLE)
     */
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