package com.chaima.truekeo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import com.chaima.truekeo.models.Message
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.chaima.truekeo.R

@Composable
fun MessageCard(
    message: Message,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = message.profile_photo,
                contentDescription = message.profile_name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
            )
            Column(
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message.profile_name,
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = FontFamily(Font(if(message.readed) R.font.saira_medium else R.font.saira_semibold)),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Solo se muestra si el último mensaje no ha sido leido ya
                    if(!message.readed){
                        Spacer(modifier = Modifier.width(12.dp))

                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = message.new_messages_count.toString(),
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = 12.sp,
                                fontFamily = FontFamily(Font(R.font.saira_medium)),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                Text(
                    text = message.last_message,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily(Font(if(message.readed) R.font.saira_medium else R.font.saira_semibold)),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
