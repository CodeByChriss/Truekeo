package com.chaima.truekeo.data

import android.util.Log
import com.chaima.truekeo.models.ChatMessage
import com.chaima.truekeo.models.Conversation
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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
                    val userDoc = db.collection("users").document(otherId).get().await()
                    conv.copy(
                        otherUserName = userDoc.getString("username") ?: "Usuario",
                        otherUserPhoto = userDoc.getString("avatarUrl") ?: "https://xcawesphifjagaixywdh.supabase.co/storage/v1/object/public/profile_photos/pf_default.png",
                        messages = getMessages(conv.id, currentUserId)
                    )
                } else conv.copy(messages = getMessages(conv.id, currentUserId))
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun sendMessage(conversationId: String, senderId: String, text: String): Boolean {
        val message = ChatMessage(
            senderId = senderId,
            text = text,
            timestamp = System.currentTimeMillis(),
            isFromMe = true
        )

        return try {
            db.collection("conversations")
                .document(conversationId)
                .collection("messages")
                .add(message)
                .await()

            db.collection("conversations").document(conversationId)
                .update("readed", false)
                .await()
            true
        } catch (_: Exception) {
            false
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

    suspend fun startOrGetConversation(myUid: String, otherUid: String): Conversation? {
        return try {
            if(myUid == "error"){
                null
            }
            // recogemos todas las conversaciones del usuario
            val existing = db.collection("conversations")
                .whereArrayContains("participants", myUid)
                .get()
                .await()

            // filtramos manualmente porque Firestore no permite "whereArrayContains" doble
            val alreadyExists = existing.documents.firstOrNull { doc ->
                val participants = doc.get("participants") as? List<*>
                participants?.contains(otherUid) == true
            }

            if (alreadyExists != null) { // Si ya existe, devolvemos la conversación
                val conv = alreadyExists.toObject(Conversation::class.java)
                val userDoc = db.collection("users").document(otherUid).get().await()
                val finalConversation = conv?.copy(
                    otherUserName = userDoc.getString("username") ?: "Not found",
                    otherUserPhoto = userDoc.getString("avatarUrl") ?: "https://xcawesphifjagaixywdh.supabase.co/storage/v1/object/public/profile_photos/pf_default.png",
                    messages = getMessages(conv.id, myUid)
                )
                return finalConversation
            }

            val newConvRef = db.collection("conversations").document()

            val newConversation = Conversation(
                id = newConvRef.id,
                participants = listOf(myUid, otherUid),
                readed = true,
                new_messages_count = 0
            )

            // Guardamos la conversacion en firestore
            newConvRef.set(newConversation).await()
            return newConversation
        } catch (e: Exception) {
            Log.e("ChatManager", "Error al crear conversación: ${e.message}")
            null
        }
    }
}