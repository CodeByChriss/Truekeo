package com.chaima.truekeo.screens

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.chaima.truekeo.models.Message

@Composable
fun MessageTab(message: Message?, onBack: () -> Unit){
    if(message == null){ // En caso de fallar el envio del mensaje
        onBack()
        return
    }
    val context = LocalContext.current
    Toast.makeText(context,message.profile_name, Toast.LENGTH_SHORT).show()
}