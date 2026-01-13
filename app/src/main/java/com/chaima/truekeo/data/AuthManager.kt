package com.chaima.truekeo.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthManager {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    suspend fun signUp(email: String, username: String, pass: String): Result<Unit> {
        return try {
            // Creamos el usuario en Auth
            val result = auth.createUserWithEmailAndPassword(email, pass).await()
            val uid = result.user?.uid ?: throw Exception("Error al obtener UID")

            // Guardamos los datos adicionales en Firestore (el nombre de usuario)
            val user = User(uid = uid, username = username.lowercase(), email = email)
            db.collection("users").document(uid).set(user).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(identifier: String, pass: String): Result<Unit> {
        return try {
            var emailToUse = identifier

            // Si no tiene '@', asumimos que es un username y buscamos el email
            if (!identifier.contains("@")) {
                val snapshot = db.collection("users")
                    .whereEqualTo("username", identifier.lowercase())
                    .get()
                    .await()

                if (snapshot.isEmpty) throw Exception("El usuario no existe")
                emailToUse = snapshot.documents[0].getString("email") ?: ""
            }

            // Login con Firebase Auth
            auth.signInWithEmailAndPassword(emailToUse, pass).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}