package com.chaima.truekeo.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.GetCredentialProviderConfigurationException
import androidx.credentials.exceptions.NoCredentialException
import com.chaima.truekeo.R
import com.chaima.truekeo.data.AuthManager
import com.chaima.truekeo.ui.theme.TruekeoTheme
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onSignUp: () -> Unit, onLogin: () -> Unit) {
    val authManager = remember { AuthManager() }
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // autenticarse con google
    val credentialManager = CredentialManager.create(context)
    val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(
            context.getString(R.string.default_web_client_id)
        )
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    TruekeoTheme(dynamicColor = false) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "logo truekeo",
                    modifier = Modifier.size(96.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Iniciar sesión",
                    fontSize = 36.sp,
                    fontFamily = FontFamily(Font(R.font.saira_regular)),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .width(300.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (showPassword)
                            ImageVector.vectorResource(id = R.drawable.ic_hide_password)
                        else ImageVector.vectorResource(id = R.drawable.ic_show_password)

                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = image,
                                contentDescription = "Toggle password visibility",
                                modifier = Modifier.width(24.dp)
                            )
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .width(300.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        scope.launch {
                            val result = authManager.login(email, password)
                            if (result.isSuccess) {
                                onLogin()
                            } else {
                                val errorMsg = result.exceptionOrNull()?.message ?: "Error desconocido."
                                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .width(300.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "ACCEDER",
                        fontFamily = FontFamily(Font(R.font.saira_regular)),
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row {
                    Text(
                        text = "Si no tienes cuenta, crea una ",
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.saira_regular))
                    )
                    Text(
                        text = "aquí.",
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.saira_medium)),
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.clickable {
                            onSignUp()
                        }
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "O",
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.saira_regular)),
                    color = Color.Black,
                    modifier = Modifier.alpha(0.5f)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        scope.launch {
                            try {
                                val result = credentialManager.getCredential(
                                    context = context,
                                    request = request
                                )

                                val credential = result.credential

                                if (credential is GoogleIdTokenCredential) {
                                    val idToken = credential.idToken

                                    val authResult = authManager.signInWithGoogle(idToken)

                                    authResult.onSuccess {
                                        onLogin()
                                    }.onFailure {
                                        val errorMsg = it.message ?: "Error desconocido"
                                        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Credencial no soportada",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            } catch (_: GetCredentialCancellationException) {
                                // El usuario cerró el diálogo o presionó atrás
                                Toast.makeText(context, "Login cancelado por el usuario", Toast.LENGTH_SHORT).show()

                            } catch (_: NoCredentialException) {
                                // No hay cuentas Google en el dispositivo
                                Toast.makeText(
                                    context,
                                    "No hay cuentas de Google. Agrega una cuenta en Ajustes.",
                                    Toast.LENGTH_LONG
                                ).show()

                            } catch (_: GetCredentialProviderConfigurationException) {
                                // Error de configuración (SHA-1, clientId, Play Services, etc.)
                                Toast.makeText(
                                    context,
                                    "Error de configuración de Google Sign-In",
                                    Toast.LENGTH_LONG
                                ).show()

                            } catch (_: GetCredentialException) {
                                // Cualquier otro error de Credential Manager
                                Toast.makeText(
                                    context,
                                    "Error al obtener credenciales",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (_: Exception) {
                                // Cualquier otro crash
                                Toast.makeText(
                                    context,
                                    "Error inesperado",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .width(300.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color.LightGray),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color(0xFFF2F2F2),
                        contentColor = Color.Black
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_google_logo),
                            contentDescription = "Google Logo",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Continuar con Google",
                            fontFamily = FontFamily(Font(R.font.saira_regular)),
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}