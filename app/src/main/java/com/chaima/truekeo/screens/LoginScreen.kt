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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.core.content.ContextCompat.getString
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.GetCredentialProviderConfigurationException
import androidx.credentials.exceptions.NoCredentialException
import com.chaima.truekeo.R
import com.chaima.truekeo.data.AuthContainer
import com.chaima.truekeo.ui.theme.TruekeoTheme
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch
import java.util.Locale
import androidx.compose.ui.res.stringResource

@Composable
fun LoginScreen(onGoToSignup: () -> Unit, onLogin: () -> Unit) {
    val authManager = remember { AuthContainer.authManager }
    val scope = rememberCoroutineScope()
    var emailOusuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Para evitar realizar más de una vez la misma llamada
    var isLoading by remember { mutableStateOf(false) }

    // autenticarse con google
    val credentialManager = CredentialManager.create(context)
    val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(
            stringResource(R.string.default_web_client_id)
        )
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    TruekeoTheme(dynamicColor = false) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = getString(context, R.string.truekeo_logo),
                    modifier = Modifier.size(96.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = getString(context, R.string.login),
                    fontSize = 36.sp,
                    fontFamily = FontFamily(Font(R.font.saira_regular)),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = emailOusuario,
                    onValueChange = { emailOusuario = it },
                    label = { Text(getString(context, R.string.username_or_email)) },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .width(300.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(getString(context, R.string.password)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (showPassword)
                            ImageVector.vectorResource(id = R.drawable.ic_hide_password)
                        else ImageVector.vectorResource(id = R.drawable.ic_show_password)

                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = image,
                                contentDescription = getString(
                                    context,
                                    R.string.toggle_password_visibility
                                ),
                                modifier = Modifier.width(24.dp)
                            )
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .width(300.dp)
                )

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = {
                        if (!isLoading) {
                            isLoading = true
                            scope.launch {
                                val result = authManager.login(emailOusuario, password)
                                if (result.isSuccess) {
                                    onLogin()
                                } else {
                                    val errorMsg = result.exceptionOrNull()?.message ?: getString(
                                        context,
                                        R.string.error_unknown
                                    )
                                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                                }
                                isLoading = false
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
                    if (isLoading) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    } else {
                        Text(
                            text = getString(
                                context,
                                R.string.login_button
                            ).uppercase(Locale.getDefault()),
                            style = MaterialTheme.typography.bodyLarge,
                            fontFamily = FontFamily(Font(R.font.saira_medium))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row {
                    Text(
                        text = getString(context, R.string.dont_have_account),
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.saira_regular)),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = getString(context, R.string.here),
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.saira_medium)),
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.clickable {
                            onGoToSignup()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = getString(context, R.string.or),
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.saira_regular)),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.alpha(0.7f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (!isLoading) {
                            isLoading = true
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
                                            val errorMsg =
                                                it.message ?: getString(
                                                    context,
                                                    R.string.error_unknown
                                                )
                                            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                    } else {
                                        Toast.makeText(
                                            context,
                                            getString(context, R.string.error_getting_credentials),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                } catch (_: GetCredentialCancellationException) {
                                    // El usuario cerró el diálogo o presionó atrás
                                    Toast.makeText(
                                        context,
                                        getString(context, R.string.login_cancelled_by_user),
                                        Toast.LENGTH_SHORT
                                    ).show()

                                } catch (_: NoCredentialException) {
                                    // No hay cuentas Google en el dispositivo
                                    Toast.makeText(
                                        context,
                                        getString(context, R.string.no_google_account),
                                        Toast.LENGTH_LONG
                                    ).show()

                                } catch (_: GetCredentialProviderConfigurationException) {
                                    // Error de configuración (SHA-1, clientId, Play Services, etc.)
                                    Toast.makeText(
                                        context,
                                        getString(context, R.string.error_google_sign_in),
                                        Toast.LENGTH_LONG
                                    ).show()

                                } catch (_: GetCredentialException) {
                                    // Cualquier otro error de Credential Manager
                                    Toast.makeText(
                                        context,
                                        getString(context, R.string.error_getting_credentials),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } catch (_: Exception) {
                                    // Cualquier otro crash
                                    Toast.makeText(
                                        context,
                                        getString(context, R.string.error_unknown),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .width(300.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color.LightGray),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.onSurface,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    if (isLoading) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    } else {
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
                                text = getString(context, R.string.continue_with_google),
                                fontFamily = FontFamily(Font(R.font.saira_regular)),
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}