package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
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
import com.example.model.CallSession
import com.example.model.CallStatus
import com.example.model.CallType
import com.example.ui.theme.*

@Composable
fun IncomingCallBanner(
    session: CallSession?,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isRinging = session != null && session.status == CallStatus.INCOMING_RINGING

    val infiniteTransition = rememberInfiniteTransition(label = "ring_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    AnimatedVisibility(
        visible = isRinging,
        enter = slideInVertically(initialOffsetY = { -it }),
        exit = slideOutVertically(targetOffsetY = { -it }),
        modifier = modifier
    ) {
        if (session != null) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = SlateDeep,
                tonalElevation = 8.dp,
                shadowElevation = 12.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .border(
                        width = 1.5.dp,
                        brush = Brush.horizontalGradient(listOf(TerracottaPrimary, BlushAccent)),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .testTag("incoming_call_banner")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Animated caller avatar
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .scale(pulseScale)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(TerracottaPrimary, TerracottaTertiary)
                                    )
                                )
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = session.callerName.take(2).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = session.callerName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (session.type == CallType.VIDEO) Icons.Default.Videocam else Icons.Default.Call,
                                    contentDescription = null,
                                    tint = BlushAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Incoming ${if (session.type == CallType.VIDEO) "Video" else "Audio"}...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = BlushAccent
                                )
                            }
                        }
                    }

                    // Action buttons: Decline & Accept
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Decline
                        IconButton(
                            onClick = onDecline,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(CallRed)
                                .testTag("decline_call_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CallEnd,
                                contentDescription = "Decline Call",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Accept
                        IconButton(
                            onClick = onAccept,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(OnlineGreen)
                                .testTag("accept_call_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Accept Call",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
