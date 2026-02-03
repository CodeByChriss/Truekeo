package com.chaima.truekeo

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.chaima.truekeo.data.AuthContainer
import com.chaima.truekeo.data.ChatContainer
import com.chaima.truekeo.navigation.AppNavigation
import com.chaima.truekeo.ui.theme.TruekeoTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        createNotificationChannel()

        setContent {
            val user = AuthContainer.authManager.userProfile
            val chatManager = ChatContainer.chatManager
            val context = LocalContext.current

            // para solicitar al usuario poder mostrarle notificaciones (a partir de android 13 hay que pedirlo)
            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (isGranted) {
                    Log.d("Permisos", "Notificaciones permitidas")
                } else {
                    Log.d("Permisos", "Notificaciones denegadas")
                }
            }

            // Para las notificaciones de mensajes nuevos
            LaunchedEffect(user?.id) {
                if (user?.id != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val isPermissionGranted = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED

                        if (!isPermissionGranted) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }

                    chatManager.getConversationsFlow(user.id).collect { conversations ->
                        conversations.forEach { conv ->
                            val myUnread = conv.unread_count[user.id] ?: 0

                            if (myUnread > 0) { //  && currentOpenConversationId != conv.id
                                chatManager.showNotification(
                                    context = context,
                                    title = conv.otherUserName,
                                    message = conv.last_message,
                                    conversationId = conv.id
                                )
                            }
                        }
                    }
                }
            }

            TruekeoTheme {
                AppNavigation()
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Mensajes Nuevos"
            val descriptionText = "Notificaciones de chats de Truekeo"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("CHAT_CHANNEL", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}