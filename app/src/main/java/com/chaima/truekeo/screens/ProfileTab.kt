package com.chaima.truekeo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.Chat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.chaima.truekeo.R
import com.chaima.truekeo.managers.AuthContainer
import com.chaima.truekeo.ui.theme.TruekeoTheme

@Composable
fun ProfileTab(
    onMyTruekesClick: () -> Unit,
    onMessagesClick: () -> Unit,
    onMyProductsClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val user = AuthContainer.authManager.userProfile

    TruekeoTheme(dynamicColor = false){
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Logout,
                        contentDescription = stringResource(R.string.logout),
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { onLogoutClick() },
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = "${user?.avatarUrl}?t=${System.currentTimeMillis()}",
                        contentDescription = user?.username,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(160.dp)
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = user?.firstAndLastName.toString(),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "@${user?.username}",
                        fontSize = 22.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onEditProfileClick,
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.edit_profile),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            item { HorizontalDivider() }

            item {
                ProfileOption(
                    icon = Icons.Rounded.SwapHoriz,
                    title = stringResource(R.string.my_truekes),
                    onClick = onMyTruekesClick
                )
            }

            item { HorizontalDivider() }

            item {
                ProfileOption(
                    icon = Icons.Rounded.Inventory2,
                    title = stringResource(R.string.my_products),
                    onClick = onMyProductsClick
                )
            }

            item { HorizontalDivider() }

            item {
                ProfileOption(
                    icon = Icons.Rounded.Chat,
                    title = stringResource(R.string.my_messages),
                    onClick = onMessagesClick
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun ProfileOption(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp)
            .height(110.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.width(20.dp))

        Text(
            text = title,
            fontSize = 30.sp,
            fontWeight = FontWeight.Black
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            imageVector = Icons.Filled.ChevronRight,
            modifier = Modifier.size(48.dp),
            contentDescription = null,
            tint = Color.Gray
        )
    }
}



