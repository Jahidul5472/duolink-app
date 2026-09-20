package com.example.ui.screens.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppNotification
import com.example.model.CallType
import com.example.model.NotificationType
import com.example.ui.AppScreen
import com.example.ui.PairAppViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotificationsScreen(
    viewModel: PairAppViewModel,
    modifier: Modifier = Modifier
) {
    val notifications by viewModel.notifications.collectAsState()
    val partnerUser by viewModel.partnerUser.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("notifications_screen")
    ) {
        // Notifications Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Intimate Alerts & Push",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Push alerts exclusively for your pair",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (notifications.isNotEmpty()) {
                        TextButton(onClick = { viewModel.clearAllNotifications() }) {
                            Text("Clear", color = TerracottaPrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Interactive Test Trigger buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledTonalButton(
                        onClick = { viewModel.triggerIncomingCall(CallType.VIDEO) },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Simulate Call", fontSize = 11.sp)
                    }

                    FilledTonalButton(
                        onClick = {
                            viewModel.sendMessage("Checking in on you ❤️ (Triggered demo)")
                            viewModel.setScreen(AppScreen.CHAT)
                        },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChatBubble,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Simulate Msg", fontSize = 11.sp)
                    }
                }
            }
        }

        // Notification List
        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.NotificationsNone,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No unread alerts",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Use the buttons above to test live push incoming calls & alerts",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(notifications, key = { it.id }) { notif ->
                    NotificationItemCard(
                        notification = notif,
                        onClick = {
                            viewModel.markNotificationAsRead(notif.id)
                            if (notif.type == NotificationType.MISSED_CALL) {
                                viewModel.startOutgoingCall(CallType.VIDEO)
                            } else {
                                viewModel.setScreen(AppScreen.CHAT)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationItemCard(
    notification: AppNotification,
    onClick: () -> Unit
) {
    val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
    val timeText = sdf.format(Date(notification.timestamp))

    val (icon, iconColor) = when (notification.type) {
        NotificationType.MESSAGE -> Icons.Default.Chat to TerracottaPrimary
        NotificationType.MISSED_CALL -> Icons.Default.PhoneMissed to CallRed
        NotificationType.PHOTO_SHARED -> Icons.Default.Photo to RoseGold
        NotificationType.SECURITY_ALERT -> Icons.Default.Security to CallRed
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (notification.isRead) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface,
        tonalElevation = if (notification.isRead) 1.dp else 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("notif_card_${notification.id}")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (notification.isRead) FontWeight.Medium else FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = timeText,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = notification.body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (!notification.isRead) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(TerracottaPrimary)
                )
            }
        }
    }
}
