package com.chaima.truekeo.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chaima.truekeo.R
import com.chaima.truekeo.components.MessageCard
import com.chaima.truekeo.data.MessageMockData
import com.chaima.truekeo.models.Message
import com.chaima.truekeo.ui.theme.TruekeoTheme

@Composable
fun MessagesTab(onMessageClick: (Message) -> Unit){
    val messages = remember { MessageMockData.sampleMessages.sortedBy { it.readed } }

    TruekeoTheme(dynamicColor = false) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.my_messages),
                    fontSize = 32.sp,
                    fontFamily = FontFamily(Font(R.font.saira_medium)),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    textAlign = TextAlign.Start
                )

                Spacer(Modifier.height(12.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.outlineVariant,
                    )

                    messages.forEach { message ->
                        MessageCard(
                            message = message,
                            modifier = Modifier.clickable {
                                onMessageClick(message)
                            }.padding(horizontal = 24.dp)
                        )

                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
            }
        }
    }
}