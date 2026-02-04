package com.chaima.truekeo.data

import android.util.Log
import com.chaima.truekeo.models.GeoPoint
import com.chaima.truekeo.models.Item
import com.chaima.truekeo.models.ItemCondition
import com.chaima.truekeo.models.ItemStatus
import com.chaima.truekeo.models.Trueke
import com.chaima.truekeo.models.TruekeStatus
import com.chaima.truekeo.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

object TruekeContainer {
    val truekeManager = TruekeManager()
}

class TruekeManager {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun getMyTruekes(): List<Trueke> {
        return try {
            val uid = auth.currentUser?.uid ?: return emptyList()

            val hostSnap = db.collection("truekes")
                .whereEqualTo("hostUserId", uid)
                .get().await()

            val takerSnap = db.collection("truekes")
                .whereEqualTo("takerUserId", uid)
                .get().await()

            val hostTruekes = hostSnap.toObjects(Trueke::class.java)
            val takerTruekes = takerSnap.toObjects(Trueke::class.java)

            val merged = (hostTruekes + takerTruekes).distinctBy { it.id }

            hydrateTruekes(merged)
        } catch (e: Exception) {
            Log.e("TruekeManager", "Error getTruekesWhereUserIsInvolved: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getTruekeById(truekeId: String): Trueke? {
        return try {
            val doc = db.collection("truekes").document(truekeId).get().await()
            val trueke = doc.toObject(Trueke::class.java) ?: return null
            hydrateTrueke(trueke)
        } catch (e: Exception) {
            Log.e("TruekeManager", "Error getTruekeById: ${e.message}")
            null
        }
    }

    suspend fun createTrueke(
        title: String,
        description: String?,
        location: GeoPoint,
        hostItemId: String
    ): Result<String> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("No autenticado"))

            val newRef = db.collection("truekes").document()
            val now = System.currentTimeMillis()

            val trueke = Trueke(
                id = newRef.id,
                title = title.trim(),
                description = description?.trim().takeUnless { it.isNullOrBlank() },
                hostUserId = uid,
                hostItemId = hostItemId,
                location = location,
                status = TruekeStatus.OPEN,
                createdAt = now,
                updatedAt = now
            )

            newRef.set(trueke).await()
            Result.success(newRef.id)
        } catch (e: Exception) {
            Log.e("TruekeManager", "Error creando trueke: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun updateTruekeStatus(truekeId: String, newStatus: TruekeStatus): Result<Unit> {
        return try {
            val now = System.currentTimeMillis()
            db.collection("truekes").document(truekeId)
                .update(
                    mapOf(
                        "status" to newStatus.name,
                        "updatedAt" to now
                    )
                ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("TruekeManager", "Error update status: ${e.message}")
            Result.failure(e)
        }
    }

    private suspend fun hydrateTruekes(truekes: List<Trueke>): List<Trueke> = coroutineScope {
        val usersCache = mutableMapOf<String, User>()
        val itemsCache = mutableMapOf<String, Item>()

        truekes.map { trueke ->
            async { hydrateTrueke(trueke, usersCache, itemsCache) }
        }.awaitAll().filterNotNull()
    }

    private suspend fun hydrateTrueke(trueke: Trueke): Trueke? = coroutineScope {
        val usersCache = mutableMapOf<String, User>()
        val itemsCache = mutableMapOf<String, Item>()
        hydrateTrueke(trueke, usersCache, itemsCache)
    }

    private suspend fun hydrateTrueke(
        trueke: Trueke,
        usersCache: MutableMap<String, User>,
        itemsCache: MutableMap<String, Item>
    ): Trueke? = coroutineScope {
        try {
            Log.d("TruekeManager", ">>> Hidratando trueke: ${trueke.id}")

            val hostUserDeferred = async { getUserCached(trueke.hostUserId, usersCache) }
            val hostItemDeferred = async { getItemCached(trueke.hostItemId, itemsCache) }

            val takerUserDeferred = async {
                trueke.takerUserId?.let { getUserCached(it, usersCache) }
            }
            val takerItemDeferred = async {
                trueke.takerItemId?.let { getItemCached(it, itemsCache) }
            }

            val hostUser = hostUserDeferred.await()
            val hostItem = hostItemDeferred.await()
            val takerUser = takerUserDeferred.await()
            val takerItem = takerItemDeferred.await()

            Log.d("TruekeManager", "    hostUser: ${hostUser?.id ?: "NULL"}")
            Log.d("TruekeManager", "    hostItem: ${hostItem?.id ?: "NULL"}")

            if (hostUser == null || hostItem == null) {
                Log.e("TruekeManager", "❌ Descartando trueke ${trueke.id}")
                return@coroutineScope null
            }

            Log.d("TruekeManager", "✓ Trueke ${trueke.id} hidratado correctamente")

            trueke.copy(
                hostUser = hostUser,
                hostItem = hostItem,
                takerUser = takerUser,
                takerItem = takerItem
            )
        } catch (e: Exception) {
            Log.e("TruekeManager", "❌ Error hidratando ${trueke.id}: ${e.message}", e)
            null
        }
    }

    private suspend fun getUserCached(uid: String, cache: MutableMap<String, User>): User? {
        cache[uid]?.let { return it }
        return try {
            val snap = db.collection("users").document(uid).get().await()
            if (!snap.exists()) return null
            val user = snap.toObject(User::class.java)?.copy(id = snap.id) ?: return null
            cache[uid] = user
            user
        } catch (e: Exception) {
            Log.e("TruekeManager", "getUserCached ERROR: ${e.message}")
            null
        }
    }

    private suspend fun getItemCached(itemId: String, cache: MutableMap<String, Item>): Item? {
        cache[itemId]?.let { return it }
        return try {
            val snap = db.collection("items").document(itemId).get().await()
            if (!snap.exists()) return null

            val item = snap.toObject(Item::class.java)?.copy(id = snap.id)
                ?: itemFromDocManual(snap)

            cache[itemId] = item
            item
        } catch (e: Exception) {
            Log.e("TruekeManager", "getItemCached ERROR: ${e.message}")
            null
        }
    }

    private fun itemFromDocManual(doc: DocumentSnapshot): Item {
        val imageUrls = (doc.get("imageUrls") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
        val conditionStr = doc.getString("condition") ?: "GOOD"
        val condition = runCatching { ItemCondition.valueOf(conditionStr) }
            .getOrElse { ItemCondition.GOOD }

        val statusStr = doc.getString("status") ?: "AVAILABLE"
        val status = runCatching { ItemStatus.valueOf(statusStr) }
            .getOrElse { ItemStatus.AVAILABLE }

        return Item(
            id = doc.id,
            name = doc.getString("name") ?: "",
            details = doc.getString("details"),
            imageUrls = imageUrls,
            brand = doc.getString("brand"),
            condition = condition,
            ownerId = doc.getString("ownerId") ?: "",
            status = status
        )
    }
}