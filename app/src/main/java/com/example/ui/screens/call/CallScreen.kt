package com.example.ui.screens.call

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.CallSession
import com.example.model.CallStatus
import com.example.model.CallType
import com.example.model.PairUser
import com.example.ui.PairAppViewModel
import com.example.ui.theme.*
import kotlin.math.roundToInt

@Composable
fun CallScreen(
    viewModel: PairAppViewModel,
    modifier: Modifier = Modifier
) {
    val callSession by viewModel.callSession.collectAsState()
    val partnerUser by viewModel.partnerUser.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    if (callSession == null) {
        // Fallback placeholder if opened with no active session
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(SlateDeep),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No active call", color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.startOutgoingCall(CallType.VIDEO) },
                    colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary)
                ) {
                    Text("Start Video Call With ${partnerUser.displayName}")
                }
            }
        }
        return
    }

    val session = callSession!!

    // Draggable PiP coordinates
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    var showMetrics by remember { mutableStateOf(false) }

    val formattedDuration = remember(session.durationSeconds) {
        val minutes = session.durationSeconds / 60
        val seconds = session.durationSeconds % 60
        "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("call_screen")
    ) {
        // Remote Full-screen Video Stream Simulation
        if (session.status == CallStatus.CONNECTED && session.type == CallType.VIDEO) {
            AsyncImage(
                model = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=1200&q=85",
                contentDescription = "Partner video stream",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Subtle dark vignette gradient for overlay legibility
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.55f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.75f)
                            )
                        )
                    )
            )
        } else {
            // Audio call or Ringing screen background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            listOf(SlateDeep, SlateSurfaceVariant, Color(0xFF231620))
                        )
                    )
            )
        }

        // Floating Local Draggable Camera Preview (PiP)
        if (session.status == CallStatus.CONNECTED && session.isVideoEnabled) {
            Box(
                modifier = Modifier
                    .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                    .align(Alignment.TopEnd)
                    .padding(top = 80.dp, end = 16.dp)
                    .size(width = 110.dp, height = 160.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.5.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                        }
                    }
                    .testTag("pip_local_camera_preview")
            ) {
                // Simulated self camera feed
                AsyncImage(
                    model = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&q=80",
                    contentDescription = "My camera preview",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Front/Back indicator badge
                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(6.dp)
                ) {
                    Text(
                        text = if (session.isFrontCamera) "Front" else "Rear",
                        fontSize = 9.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        // Top Status Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = partnerUser.displayName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            when (session.status) {
                CallStatus.OUTGOING_RINGING -> {
                    RingingIndicator(label = "Calling...")
                }
                CallStatus.INCOMING_RINGING -> {
                    RingingIndicator(label = "Incoming...")
                }
                CallStatus.CONNECTED -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.45f))
                            .clickable { showMetrics = !showMetrics }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(OnlineGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = formattedDuration,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "• WebRTC HD",
                            style = MaterialTheme.typography.bodySmall,
                            color = BlushAccent
                        )
                    }
                }
                CallStatus.ENDED -> {
                    Text(
                        text = "Call Ended",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.LightGray
                    )
                }
                CallStatus.IDLE -> {}
            }

            // WebRTC Signaling Metrics Inspector Badge
            if (showMetrics && session.status == CallStatus.CONNECTED) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = Color.Black.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, TerracottaPrimary.copy(alpha = 0.5f)),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "WebRTC P2P Direct Telemetry",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TerracottaPrimary
                        )
                        Text(
                            text = "Latency: ${session.metrics.latencyMs} ms | Packet Loss: ${session.metrics.packetLossPct}%",
                            fontSize = 10.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Candidate: ${session.metrics.candidateType} | Stream: ${session.metrics.resolution}",
                            fontSize = 10.sp,
                            color = Color.LightGray
                        )
                        Text(
                            text = "Audio: ${session.metrics.audioCodec}",
                            fontSize = 10.sp,
                            color = Color.LightGray
                        )
                    }
                }
            }
        }

        // Center Avatar for Audio Calls or Ringing
        if (session.status != CallStatus.CONNECTED || session.type == CallType.AUDIO) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(TerracottaPrimary, TerracottaTertiary)
                            )
                        )
                        .border(3.dp, Color.White.copy(alpha = 0.85f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = partnerUser.avatarInitials,
                        color = Color.White,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (session.type == CallType.VIDEO) "1-on-1 Intimate Video" else "Encrypted Audio Session",
                    color = BlushAccent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Bottom Call Controls Dock
        Surface(
            color = Color.Black.copy(alpha = 0.65f),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mic Mute Toggle
                CallDockButton(
                    icon = if (session.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                    label = if (session.isMuted) "Unmute" else "Mute",
                    isActive = session.isMuted,
                    activeColor = CallRed,
                    onClick = { viewModel.toggleMute() }
                )

                // Video On/Off Toggle
                CallDockButton(
                    icon = if (session.isVideoEnabled) Icons.Default.Videocam else Icons.Default.VideocamOff,
                    label = if (session.isVideoEnabled) "Cam Off" else "Cam On",
                    isActive = !session.isVideoEnabled,
                    activeColor = CallRed,
                    onClick = { viewModel.toggleVideo() }
                )

                // Flip Camera (Front/Rear)
                CallDockButton(
                    icon = Icons.Default.FlipCameraIos,
                    label = "Flip",
                    isActive = false,
                    onClick = { viewModel.flipCamera() }
                )

                // End Call Button
                IconButton(
                    onClick = { viewModel.endCall() },
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(CallRed)
                        .testTag("end_call_action_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.CallEnd,
                        contentDescription = "End Call",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CallDockButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    activeColor: Color = TerracottaPrimary,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (isActive) activeColor else Color.White.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun RingingIndicator(label: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_ringing")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.scale(pulse)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = BlushAccent
        )
    }
}
