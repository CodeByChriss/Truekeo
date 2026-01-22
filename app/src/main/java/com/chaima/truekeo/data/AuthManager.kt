package com.chaima.truekeo.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthManager {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    suspend fun signUp(
        email: String,
        username: String,
        pass: String
    ): Result<Unit> {
        return try {
            val normalizedUsername = username.lowercase()
            val usernameRef = db.collection("usernames").document(normalizedUsername)

            val snapshot = usernameRef.get().await()
            if (snapshot.exists()) {
                return Result.failure(Exception("El nombre de usuario ya está en uso"))
            }

            val result = auth.createUserWithEmailAndPassword(email, pass).await()
            val user = result.user?: throw Exception("UID no encontrado")
            val uid = result.user?.uid ?: throw Exception("UID no encontrado")

            val newUser = User(
                uid = uid,
                username = normalizedUsername,
                email = email
            )

            db.runTransaction { transaction ->
                transaction.set(db.collection("users").document(uid), newUser)
                transaction.set(usernameRef, mapOf("uid" to uid))
            }.await()

            user.sendEmailVerification().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(emailoUsuario: String, pass: String): Result<Unit> {
        return try {
            var email = emailoUsuario

            if(!email.contains('@')){
                val username = email.lowercase()

                val snapshot = db.collection("users")
                    .whereEqualTo("username", username)
                    .limit(1)
                    .get()
                    .await()

                if (snapshot.isEmpty) {
                    return Result.failure(Exception("Usuario no encontrado"))
                }

                email = snapshot.documents[0].getString("email")
                    ?: return Result.failure(Exception("Email no encontrado para este usuario"))
            }

            auth.signInWithEmailAndPassword(email, pass).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(idToken: String): Result<Unit> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val firebaseUser = result.user ?: throw Exception("Usuario de Google nulo")

            checkAndCreateUserInFirestore(
                uid = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                displayName = firebaseUser.displayName ?: "Usuario de Google"
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun checkAndCreateUserInFirestore(
        uid: String,
        email: String,
        displayName: String
    ) {
        val userRef = db.collection("users").document(uid)
        val snapshot = userRef.get().await()

        if (!snapshot.exists()) {
            generateUniqueUsername(uid)
        }
    }

    private suspend fun generateUniqueUsername(uid: String): String {
        while (true) {
            val username = generateRandomUsername()
            val usernameRef = db.collection("usernames").document(username)
            val userRef = db.collection("users").document(uid)

            try {
                db.runTransaction { transaction ->
                    val snapshot = transaction.get(usernameRef)

                    if (snapshot.exists()) {
                        throw Exception("Username ocupado")
                    }

                    transaction.set(usernameRef, mapOf("uid" to uid))
                    transaction.set(
                        userRef,
                        User(
                            uid = uid,
                            username = username,
                            email = auth.currentUser?.email ?: ""
                        )
                    )
                }.await()

                return username
            } catch (_: Exception) {
                // lo reintentamos
            }
        }
    }

    private fun generateRandomUsername(): String {
        val random = (1..999999).random()
        return "user$random"
    }

    suspend fun updateUsername(uid: String, newUsername: String): Result<Unit> {
        val normalized = newUsername.lowercase()

        return try {
            val usernameRef = db.collection("usernames").document(normalized)
            val userRef = db.collection("users").document(uid)

            db.runTransaction { transaction ->
                val snapshot = transaction.get(usernameRef)

                if (snapshot.exists()) {
                    throw Exception("Nombre de usuario ya en uso")
                }

                val userSnapshot = transaction.get(userRef)
                val oldUsername = userSnapshot.getString("username")

                oldUsername?.let {
                    transaction.delete(db.collection("usernames").document(it))
                }

                transaction.set(usernameRef, mapOf("uid" to uid))
                transaction.update(userRef, "username", normalized)
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentUser() = auth.currentUser

    fun logout() = auth.signOut()
}