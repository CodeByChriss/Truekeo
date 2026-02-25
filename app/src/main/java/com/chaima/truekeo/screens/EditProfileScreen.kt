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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import coil3.compose.AsyncImage
import com.chaima.truekeo.R
import com.chaima.truekeo.managers.AuthContainer
import com.chaima.truekeo.managers.ImageStorageManager
import com.chaima.truekeo.models.User
import com.chaima.truekeo.ui.theme.TruekeoTheme
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun EditProfileScreen(
    onCloseClick: () -> Unit,
    onSaveChangesClick: () -> Unit
) {
    val authManager = AuthContainer.authManager
    val user = authManager.userProfile
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var isLoading by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    // Valores originales (para comparar)
    val originalName = remember { user?.firstAndLastName ?: "" }
    val originalUsername = remember { user?.username ?: "" }

    // Valores editables
    var name by remember { mutableStateOf(user?.firstAndLastName ?: "") }
    var username by remember { mutableStateOf(user?.username ?: "") }
    var itemImageUri by remember { mutableStateOf<Uri?>(null) }

    val hasChanges by remember(name, username, itemImageUri) {
        derivedStateOf {
            name != originalName ||
                    username != originalUsername ||
                    itemImageUri != null
        }
    }

    val pickItemImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) itemImageUri = uri
    }

    TruekeoTheme(dynamicColor = false) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.edit_profile),
                    fontSize = 32.sp,
                    fontFamily = FontFamily(Font(R.font.saira_medium)),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start
                )

                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close",
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { if (hasChanges) showDialog = true else onCloseClick() }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            AsyncImage(
                model = itemImageUri ?: user?.avatarUrl,
                contentDescription = user?.username,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .clickable { pickItemImageLauncher.launch("image/*") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.change_picture),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { pickItemImageLauncher.launch("image/*") }
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

            Spacer(modifier = Modifier.height(7.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text(stringResource(R.string.user)) },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (!isLoading) {
                        isLoading = true
                        saveProfile(
                            user = user,
                            name = name,
                            username = username,
                            itemImageUri = itemImageUri,
                            context = context,
                            scope = scope,
                            authManager = authManager,
                            onComplete = {
                                isLoading = false
                                onSaveChangesClick()
                            }
                        )
                    }
                },
                enabled = hasChanges && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                } else {
                    Text(
                        text = stringResource(R.string.save).uppercase(Locale.getDefault()),
                        style = MaterialTheme.typography.bodyLarge,
                        fontFamily = FontFamily(Font(R.font.saira_medium))
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = stringResource(R.string.unsaved_changes)) },
                text = { Text(text = stringResource(R.string.save_changes_question)) },
                confirmButton = {
                    TextButton(onClick = {
                        showDialog = false
                        if (!isLoading) {
                            isLoading = true
                            saveProfile(
                                user = user,
                                name = name,
                                username = username,
                                itemImageUri = itemImageUri,
                                context = context,
                                scope = scope,
                                authManager = authManager,
                                onComplete = {
                                    isLoading = false
                                    onCloseClick()
                                }
                            )
                        }
                    }) {
                        Text(stringResource(R.string.save))
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDialog = false
                        onCloseClick()
                    }) {
                        Text(stringResource(R.string.discard))
                    }
                }
            )
        }
    }
}

private fun saveProfile(
    user: User?,
    name: String,
    username: String,
    itemImageUri: Uri?,
    context: android.content.Context,
    scope: kotlinx.coroutines.CoroutineScope,
    authManager: com.chaima.truekeo.managers.AuthManager,
    onComplete: () -> Unit
) {
    scope.launch {
        var finalImageUrl = user?.avatarUrl ?: ""

        if (itemImageUri != null) {
            val storageManager = ImageStorageManager(context)
            finalImageUrl = storageManager.uploadProfilePhoto(
                user?.id ?: "",
                itemImageUri
            )
        }

        val result = authManager.updateUserProfile(
            uid = user?.id ?: "",
            newUsername = username,
            newFullName = name,
            newAvatarUrl = finalImageUrl
        )

        result.onSuccess { available ->
            if (available) {
                Toast.makeText(
                    context,
                    context.getString(R.string.profile_updated),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.username_already_exits),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }.onFailure {
            Toast.makeText(
                context,
                context.getString(R.string.error_unknown),
                Toast.LENGTH_SHORT
            ).show()
        }
        onComplete()
    }
}
