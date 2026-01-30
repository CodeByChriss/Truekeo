package com.chaima.truekeo.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.chaima.truekeo.models.User
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf

// Creamos una única instancia del AuthManager para toda la aplicación y asi evitamos errores
object AuthContainer {
    val authManager = AuthManager()
}

class AuthManager {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Para guardar los datos del usuario en memoria
    var userProfile by mutableStateOf<User?>(null)
        private set

    // Verificamos si hay ya un usuario al iniciar la aplicación
    suspend fun checkUserSession(): Boolean {
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            // Si hay sesión, cargamos sus datos de Firestore
            fetchUserData(firebaseUser.uid)
            return true
        }
        return false
    }

    // Cargamos los datos de Firestore que es la base de datos donde están los datos del usuario
    private suspend fun fetchUserData(uid: String) {
        try {
            val snapshot = db.collection("users").document(uid).get().await()
            userProfile = snapshot.toObject(User::class.java)
        } catch (_: Exception) {
            userProfile = null
        }
    }

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
                id = uid,
                username = normalizedUsername,
                firstAndLastName = "No name",
                avatarUrl = null,
                email = email
            )

            db.runTransaction { transaction ->
                transaction.set(db.collection("users").document(uid), newUser)
                transaction.set(usernameRef, mapOf("uid" to uid))
            }.await()

            user.sendEmailVerification().await()

            fetchUserData(uid)

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

            val authResult = auth.signInWithEmailAndPassword(email, pass).await()
            authResult.user?.let { fetchUserData(it.uid) }

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
                uid = firebaseUser.uid
            )

            fetchUserData(firebaseUser.uid)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun checkAndCreateUserInFirestore(
        uid: String,
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

                    val newUser = User(
                        id = uid,
                        username = username,
                        firstAndLastName = "No name",
                        avatarUrl = null,
                        email = auth.currentUser?.email ?: ""
                    )

                    transaction.set(usernameRef, mapOf("uid" to uid))
                    transaction.set(userRef,newUser)
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

    suspend fun updateUserProfile(
        uid: String,
        newUsername: String,
        newFullName: String,
        newAvatarUrl: String?
    ): Result<Boolean> {
        val normalizedNew = newUsername.lowercase().trim()

        return try {
            val userRef = db.collection("users").document(uid)

            val isAvailable = db.runTransaction { transaction ->
                val userSnapshot = transaction.get(userRef)
                val oldUsername = userSnapshot.getString("username") ?: ""

                if (normalizedNew != oldUsername) {
                    val newUsernameRef = db.collection("usernames").document(normalizedNew)
                    val oldUsernameRef = db.collection("usernames").document(oldUsername)

                    val newUsernameSnapshot = transaction.get(newUsernameRef)
                    if (newUsernameSnapshot.exists()) {
                        return@runTransaction false
                    }

                    if (oldUsername.isNotEmpty()) {
                        transaction.delete(oldUsernameRef)
                    }
                    transaction.set(newUsernameRef, mapOf("uid" to uid))
                }

                val updates = mutableMapOf<String, Any?>(
                    "username" to normalizedNew,
                    "firstAndLastName" to newFullName,
                    "avatarUrl" to newAvatarUrl
                )
                transaction.update(userRef, updates)

                true
            }.await()

            if (isAvailable) {
                fetchUserData(uid)
                Result.success(true)
            } else {
                Result.success(false)
            }
        } catch (e: Exception) {
            Log.e("AuthManager","Error en el updateUserProfile: ${e.message}")
            Result.failure(e)
        }
    }

    fun logout(){
        auth.signOut()
        userProfile = null
    }
}