package com.chaima.truekeo.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import coil3.compose.AsyncImage
import com.chaima.truekeo.R
import com.chaima.truekeo.data.AuthContainer
import kotlinx.coroutines.launch

@Composable
fun EditProfileScreen(onSaveChangesClick: () -> Unit) {
    val authManager = AuthContainer.authManager
    val user = authManager.userProfile
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var name by remember { mutableStateOf(user?.firstAndLastName.toString()) }
    var username by remember { mutableStateOf(user?.username.toString()) }
    var itemImageUri by remember { mutableStateOf<Uri?>(null) }
    val pickItemImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) itemImageUri = uri
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = stringResource(R.string.edit_profile),
                fontSize = 32.sp,
                fontFamily = FontFamily(Font(R.font.saira_medium)),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(24.dp))

            AsyncImage(
                model = user?.avatarUrl,
                contentDescription = user?.username,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .clickable {
                        pickItemImageLauncher.launch("image/*")
                    }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.change_picture),
                fontSize = 16.sp,
                color = Color(0xFF5EC1A9),
                modifier = Modifier.clickable {
                    pickItemImageLauncher.launch("image/*")
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.name)) },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text(stringResource(R.string.user)) },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    // primeros comprobamos si hay cambios para evitar llamadas a la base de datos innecesarias
                    var changed = false
                    if(username != user?.username.toString()) changed = true
                    else if(name != user?.firstAndLastName.toString()) changed = true
                    else if(itemImageUri.toString() != user?.avatarUrl) changed = true
                    if (changed) {
                        scope.launch {
                            val result = authManager.updateUserProfile(
                                uid = user?.id.toString(),
                                newUsername = username,
                                newFullName = name,
                                newAvatarUrl = itemImageUri.toString()
                            )

                            result.onSuccess { available ->
                                if (available) {
                                    Toast.makeText(
                                        context,
                                        getString(context, R.string.profile_updated),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        getString(context, R.string.username_already_exits),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }.onFailure {
                                Toast.makeText(
                                    context,
                                    getString(context, R.string.error_unknown),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    onSaveChangesClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(
                    text = stringResource(R.string.save),
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = FontFamily(Font(R.font.saira_medium))
                )
            }
        }
    }
}

