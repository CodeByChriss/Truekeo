package com.chaima.truekeo.data

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.chaima.truekeo.MainActivity
import com.chaima.truekeo.R
import com.chaima.truekeo.models.ChatMessage
import com.chaima.truekeo.models.Conversation
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

// Creamos una única instancia del ChatManager para toda la aplicación y asi evitamos errores
object ChatContainer {
    val chatManager = ChatManager()
}

class ChatManager {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getConversations(currentUserId: String): List<Conversation> {
        return try {
            val snapshot = db.collection("conversations")
                .whereArrayContains("participants", currentUserId)
                .get()
                .await()

            val conversations = snapshot.toObjects(Conversation::class.java)

            // Para cada conversación, buscamos los datos del otro usuario
            conversations.map { conv ->
                val otherId = conv.participants.firstOrNull { it != currentUserId }
                if (otherId != null) {
                    val userDoc = getUserData(otherId)
                    conv.copy(
                        otherUserName = userDoc?.getValue("username") ?: "Usuario",
                        otherUserPhoto = userDoc?.getValue("avatarUrl") ?: "https://xcawesphifjagaixywdh.supabase.co/storage/v1/object/public/profile_photos/pf_default.png"
                    )
                } else conv
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun getConversationsFlow(currentUserId: String): Flow<List<Conversation>> = callbackFlow {
        val subscription = db.collection("conversations")
            .whereArrayContains("participants", currentUserId)
            .addSnapshotListener { snapshot, _ ->
                val conversations = snapshot?.toObjects(Conversation::class.java) ?: emptyList()
                trySend(conversations)
            }
        awaitClose { subscription.remove() }
    }

    suspend fun getUserData(userId: String): Map<String, String>? {
        return try {
            val doc = db.collection("users").document(userId).get().await()
            if (doc.exists()) {
                mapOf(
                    "username" to (doc.getString("username") ?: "Usuario"),
                    "avatarUrl" to (doc.getString("avatarUrl") ?: "https://xcawesphifjagaixywdh.supabase.co/storage/v1/object/public/profile_photos/pf_default.png")
                )
            } else null
        } catch (_: Exception) {
            null
        }
    }

    suspend fun sendMessage(conversationId: String, senderId: String, text: String, otherId: String): Boolean {
        val message = ChatMessage(
            senderId = senderId,
            text = text
        )

        return try {
            db.collection("conversations")
                .document(conversationId)
                .collection("messages")
                .add(message)
                .await()

            db.collection("conversations").document(conversationId)
                .update("unread_count.$otherId", com.google.firebase.firestore.FieldValue.increment(1), "last_message", text)
                .await()
            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun markAsRead(conversationId: String, myId: String) {
        try {
            db.collection("conversations").document(conversationId).update(
                "unread_count.$myId", 0
            ).await()
        } catch (e: Exception) {
            Log.e("ChatManager", "Error al marcar como leído: ${e.message}")
        }
    }

    suspend fun getMessages(conversationId: String, currentUserId: String): List<ChatMessage> {
        return try {
            if(currentUserId == "error"){
                emptyList<ChatMessage>()
            }
            val snapshot = db.collection("conversations")
                .document(conversationId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                val msg = doc.toObject(ChatMessage::class.java)
                // Comprobamos si el mensaje es del usuario comparandolo con su ID
                msg?.copy(isFromMe = msg.senderId == currentUserId)
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun getConversationById(conversationId: String, currentUserId: String) : Conversation?{
        return try {
            if(currentUserId == "error"){
                null
            }
            val snapshot = db.collection("conversations")
                .document(conversationId)
                .get()
                .await()

            val conversation = snapshot.toObject(Conversation::class.java) ?: return null

            val otherId = conversation.participants.firstOrNull { it != currentUserId }

            if (otherId != null) {
                val userDoc = getUserData(otherId)
                conversation.copy(
                    otherUserName = userDoc?.getValue("username") ?: "Usuario",
                    otherUserPhoto = userDoc?.getValue("avatarUrl") ?: "https://xcawesphifjagaixywdh.supabase.co/storage/v1/object/public/profile_photos/pf_default.png",
                    messages = getMessages(conversationId, currentUserId)
                )
            } else {
                conversation.copy(messages = getMessages(conversationId, currentUserId))
            }
        } catch (e: Exception) {
            Log.e("ChatManager", "Error al obtener conversación por ID: ${e.message}")
            null
        }
    }

    fun getMessagesFlow(conversationId: String, currentUserId: String): Flow<List<ChatMessage>> = callbackFlow {
        val query = db.collection("conversations")
            .document(conversationId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)

        val subscription = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val messages = snapshot?.documents?.mapNotNull { doc ->
                val msg = doc.toObject(ChatMessage::class.java)
                msg?.copy(isFromMe = msg.senderId == currentUserId)
            } ?: emptyList()

            trySend(messages) // Enviamos la nueva lista a la UI
        }

        // cerramos el listener cuando se destruya el Composable para evitar errores
        awaitClose { subscription.remove() }
    }

    suspend fun startOrGetConversation(myUid: String, otherUid: String): String? {
        return try {
            if(myUid == "error"){
                null
            }
            val existing = db.collection("conversations")
                .whereArrayContains("participants", myUid)
                .get()
                .await()

            val alreadyExists = existing.documents.firstOrNull { doc ->
                val participants = doc.get("participants") as? List<*>
                participants?.contains(otherUid) == true
            }

            if (alreadyExists != null) {
                return alreadyExists.id // Si ya existe, devolvemos el ID
            }

            val newConvRef = db.collection("conversations").document()
            val newConversation = mapOf(
                "id" to newConvRef.id,
                "participants" to listOf(myUid, otherUid),
                "readed" to true,
                "new_messages_count" to 0
            )

            newConvRef.set(newConversation).await()
            newConvRef.id // Devolvemos el nuevo ID generado
        } catch (e: Exception) {
            Log.e("ChatManager", "Error al crear conversación: ${e.message}")
            null
        }
    }

    fun showNotification(context: Context, title: String, message: String, conversationId: String) {
        // Esto hace que al tocar la notificación se abra la app en el chat específico
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("conversationId", conversationId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, "CHAT_CHANNEL")
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notify(conversationId.hashCode(), builder.build())
            }
        }
    }
}