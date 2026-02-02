package com.chaima.truekeo.data

import android.util.Log
import com.chaima.truekeo.models.GeoPoint
import com.chaima.truekeo.models.Item
import com.chaima.truekeo.models.ItemCondition
import com.chaima.truekeo.models.Trueke
import com.chaima.truekeo.models.TruekeStatus
import com.chaima.truekeo.models.User
import com.chaima.truekeo.data.models.TruekeDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import java.time.Instant

object TruekeContainer {
    val truekeManager = TruekeManager()
}

class TruekeManager {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

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

            val dto = TruekeDto(
                id = newRef.id,
                title = title.trim(),
                description = description?.trim().takeUnless { it.isNullOrBlank() },
                hostUserId = uid,
                hostItemId = hostItemId,
                takerUserId = null,
                takerItemId = null,
                location = location,
                status = TruekeStatus.OPEN.name, // "OPEN"
                createdAt = now,
                updatedAt = now
            )

            newRef.set(dto).await()
            Result.success(newRef.id)
        } catch (e: Exception) {
            Log.e("TruekeManager", "Error creando trueke: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getTruekesWhereUserIsInvolved(userId: String): List<Trueke> {
        return try {
            val hostSnap = db.collection("truekes")
                .whereEqualTo("hostUserId", userId)
                .get().await()

            val takerSnap = db.collection("truekes")
                .whereEqualTo("takerUserId", userId)
                .get().await()

            val hostDtos = hostSnap.toObjects(TruekeDto::class.java)
            val takerDtos = takerSnap.toObjects(TruekeDto::class.java)

            val merged = (hostDtos + takerDtos)
                .distinctBy { it.id }

            hydrateTruekes(merged)
        } catch (e: Exception) {
            Log.e("TruekeManager", "Error getTruekesWhereUserIsInvolved: ${e.message}")
            emptyList()
        }
    }

    suspend fun getTruekeById(truekeId: String): Trueke? {
        return try {
            val doc = db.collection("truekes").document(truekeId).get().await()
            val dto = doc.toObject(TruekeDto::class.java) ?: return null
            hydrateTrueke(dto)
        } catch (e: Exception) {
            Log.e("TruekeManager", "Error getTruekeById: ${e.message}")
            null
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

    // TruekeDto -> Trueke UI
    private suspend fun hydrateTruekes(dtos: List<TruekeDto>): List<Trueke> = coroutineScope {
        val usersCache = mutableMapOf<String, User>()
        val itemsCache = mutableMapOf<String, Item>()

        dtos.map { dto ->
            async { hydrateTrueke(dto, usersCache, itemsCache) }
        }.awaitAll().filterNotNull()
    }

    private suspend fun hydrateTrueke(dto: TruekeDto): Trueke? = coroutineScope {
        val usersCache = mutableMapOf<String, User>()
        val itemsCache = mutableMapOf<String, Item>()
        hydrateTrueke(dto, usersCache, itemsCache)
    }

    private suspend fun hydrateTrueke(
        dto: TruekeDto,
        usersCache: MutableMap<String, User>,
        itemsCache: MutableMap<String, Item>
    ): Trueke? = coroutineScope {
        try {
            val hostUserDeferred = async { getUserCached(dto.hostUserId, usersCache) }
            val hostItemDeferred = async { getItemCached(dto.hostItemId, itemsCache) }

            val takerUserDeferred = async {
                dto.takerUserId?.let { getUserCached(it, usersCache) }
            }
            val takerItemDeferred = async {
                dto.takerItemId?.let { getItemCached(it, itemsCache) }
            }

            val hostUser = hostUserDeferred.await()
            val hostItem = hostItemDeferred.await()
            val takerUser = takerUserDeferred.await()
            val takerItem = takerItemDeferred.await()

            if (hostUser == null || hostItem == null) return@coroutineScope null

            Trueke(
                id = dto.id,
                title = dto.title,
                description = dto.description,

                hostUser = hostUser,
                hostItem = hostItem,

                takerUser = takerUser,
                takerItem = takerItem,

                location = dto.location,

                status = runCatching { TruekeStatus.valueOf(dto.status) }
                    .getOrElse { TruekeStatus.OPEN },

                createdAt = Instant.ofEpochMilli(dto.createdAt),
                updatedAt = dto.updatedAt
                    .takeIf { it > 0L && it != dto.createdAt }
                    ?.let { Instant.ofEpochMilli(it) }
            )
        } catch (e: Exception) {
            Log.e("TruekeManager", "Error hidratando ${dto.id}: ${e.message}")
            null
        }
    }

    private suspend fun getUserCached(uid: String, cache: MutableMap<String, User>): User? {
        cache[uid]?.let { return it }
        return try {
            val snap = db.collection("users").document(uid).get().await()
            val user = (snap.toObject(User::class.java) ?: User()).copy(id = snap.id)
            cache[uid] = user
            user
        } catch (_: Exception) {
            null
        }
    }

    private suspend fun getItemCached(itemId: String, cache: MutableMap<String, Item>): Item? {
        cache[itemId]?.let { return it }
        return try {
            val snap = db.collection("items").document(itemId).get().await()

            // Si tu Item tiene defaults en id/name -> perfecto:
            val item = snap.toObject(Item::class.java)?.copy(id = snap.id)
                ?: itemFromDocManual(snap) // fallback por seguridad

            cache[itemId] = item
            item
        } catch (_: Exception) {
            null
        }
    }

    private fun itemFromDocManual(doc: DocumentSnapshot): Item {
        val imageUrls = (doc.get("imageUrls") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
        val conditionStr = doc.getString("condition") ?: "GOOD"
        val condition = runCatching { ItemCondition.valueOf(conditionStr) }
            .getOrElse { ItemCondition.GOOD }

        return Item(
            id = doc.id,
            name = doc.getString("name") ?: "",
            details = doc.getString("details"),
            imageUrls = imageUrls,
            brand = doc.getString("brand"),
            condition = condition
        )
    }
}