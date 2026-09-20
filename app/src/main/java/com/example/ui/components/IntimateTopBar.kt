package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CallType
import com.example.model.PairUser
import com.example.ui.AppScreen
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntimateTopBar(
    partner: PairUser,
    activeScreen: AppScreen,
    unreadNotificationsCount: Int,
    onNavigate: (AppScreen) -> Unit,
    onStartCall: (CallType) -> Unit,
    onOpenSecurity: () -> Unit,
    onSwitchUser: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("intimate_top_bar"),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Partner Profile info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onSwitchUser() }
                        .padding(4.dp)
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(TerracottaPrimary, TerracottaTertiary)
                                    )
                                )
                                .border(1.5.dp, Color.White.copy(alpha = 0.8f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = partner.avatarInitials,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        // Online dot indicator
                        Box(
                            modifier = Modifier
                                .size(13.dp)
                                .scale(pulseScale)
                                .clip(CircleShape)
                                .background(OnlineGreen)
                                .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = partner.displayName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            // Intimate pair lock badge
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                modifier = Modifier
                                    .clickable { onOpenSecurity() }
                                    .testTag("pair_lock_badge")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Pair-Locked",
                                        tint = TerracottaPrimary,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "Pair 1:1",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TerracottaPrimary
                                    )
                                }
                            }
                        }

                        Text(
                            text = partner.lastSeenText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Action icons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onStartCall(CallType.AUDIO) },
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("audio_call_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Audio Call",
                            tint = TerracottaPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    IconButton(
                        onClick = { onStartCall(CallType.VIDEO) },
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("video_call_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = "Video Call",
                            tint = TerracottaPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    IconButton(
                        onClick = { onOpenSecurity() },
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("security_inspect_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Pair Security Rules",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Navigation Pill Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NavigationPill(
                    title = "Direct Chat",
                    isSelected = activeScreen == AppScreen.CHAT,
                    onClick = { onNavigate(AppScreen.CHAT) },
                    modifier = Modifier.weight(1f)
                )

                NavigationPill(
                    title = "Vault",
                    icon = Icons.Default.PhotoLibrary,
                    isSelected = activeScreen == AppScreen.MEDIA_VAULT,
                    onClick = { onNavigate(AppScreen.MEDIA_VAULT) },
                    modifier = Modifier.weight(1f)
                )

                NavigationPill(
                    title = "Alerts",
                    icon = Icons.Default.Notifications,
                    badgeCount = unreadNotificationsCount,
                    isSelected = activeScreen == AppScreen.NOTIFICATIONS,
                    onClick = { onNavigate(AppScreen.NOTIFICATIONS) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun NavigationPill(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    badgeCount: Int = 0
) {
    val background = if (isSelected) {
        TerracottaPrimary
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }

    val contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = background,
        modifier = modifier
            .height(34.dp)
            .clickable { onClick() }
            .testTag("nav_pill_${title.lowercase().replace(" ", "_")}")
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = contentColor
            )

            if (badgeCount > 0) {
                Spacer(modifier = Modifier.width(5.dp))
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color.White else TerracottaPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = badgeCount.toString(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) TerracottaPrimary else Color.White
                    )
                }
            }
        }
    }
}
