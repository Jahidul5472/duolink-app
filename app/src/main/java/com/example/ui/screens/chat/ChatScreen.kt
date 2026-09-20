package com.example.ui.screens.chat

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.*
import com.example.ui.PairAppViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChatScreen(
    viewModel: PairAppViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.messages.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val partnerUser by viewModel.partnerUser.collectAsState()
    val isRecordingVoice by viewModel.isRecordingVoice.collectAsState()
    val voiceSeconds by viewModel.voiceRecordingSeconds.collectAsState()

    var inputText by remember { mutableStateOf("") }
    var showQuickMediaPicker by remember { mutableStateOf(false) }
    var selectedMessageForReaction by remember { mutableStateOf<String?>(null) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Scroll to latest message on new message
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("chat_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Intimate header banner with anniversary/connection stats
            IntimateStatusBanner(
                currentUser = currentUser,
                partnerUser = partnerUser
            )

            // Conversation Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 12.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    val isMe = msg.senderId == currentUser.id

                    ChatBubble(
                        message = msg,
                        isFromMe = isMe,
                        onImageClick = { url ->
                            viewModel.openPhotoViewer(
                                SharedPhoto(
                                    id = msg.id,
                                    senderId = msg.senderId,
                                    senderName = if (isMe) currentUser.displayName else partnerUser.displayName,
                                    url = url,
                                    caption = msg.text,
                                    timestamp = msg.timestamp
                                )
                            )
                        },
                        onToggleReactionPicker = {
                            selectedMessageForReaction = if (selectedMessageForReaction == msg.id) null else msg.id
                        }
                    )

                    // Reaction picker overlay
                    AnimatedVisibility(visible = selectedMessageForReaction == msg.id) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                        ) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                tonalElevation = 6.dp,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf("❤️", "🥰", "🫂", "✨", "🔥").forEach { emoji ->
                                        Text(
                                            text = emoji,
                                            fontSize = 20.sp,
                                            modifier = Modifier
                                                .clickable {
                                                    viewModel.addReaction(msg.id, emoji)
                                                    selectedMessageForReaction = null
                                                }
                                                .padding(2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Quick romantic media preset selector sheet
            AnimatedVisibility(visible = showQuickMediaPicker) {
                QuickMediaPickerTray(
                    onSelectImage = { url, caption ->
                        viewModel.sendPhoto(url, caption)
                        showQuickMediaPicker = false
                    },
                    onClose = { showQuickMediaPicker = false }
                )
            }

            // Bottom Input Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                shadowElevation = 8.dp
            ) {
                if (isRecordingVoice) {
                    // Voice Recording Active State
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(CallRed)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Recording audio: 0:${voiceSeconds.toString().padStart(2, '0')}",
                                color = TerracottaPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(onClick = { viewModel.cancelVoiceRecording() }) {
                                Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            IconButton(
                                onClick = { viewModel.stopAndSendVoiceRecording() },
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(TerracottaPrimary)
                                    .testTag("send_voice_note_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send Voice Note",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                } else {
                    // Normal Input Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Quick Media / Camera buttons
                        IconButton(
                            onClick = { showQuickMediaPicker = !showQuickMediaPicker },
                            modifier = Modifier
                                .size(40.dp)
                                .testTag("camera_picker_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Camera",
                                tint = TerracottaPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        IconButton(
                            onClick = { showQuickMediaPicker = !showQuickMediaPicker },
                            modifier = Modifier
                                .size(40.dp)
                                .testTag("gallery_picker_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Gallery",
                                tint = TerracottaSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Text Field
                        TextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = {
                                Text(
                                    "Whisper to ${partnerUser.displayName}...",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp)
                                .testTag("chat_input_field"),
                            shape = RoundedCornerShape(24.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            maxLines = 4
                        )

                        // Send or Mic button
                        if (inputText.isNotBlank()) {
                            IconButton(
                                onClick = {
                                    viewModel.sendMessage(inputText)
                                    inputText = ""
                                },
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(TerracottaPrimary)
                                    .testTag("send_message_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        } else {
                            IconButton(
                                onClick = { viewModel.startVoiceRecording() },
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .testTag("voice_record_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Record Voice Note",
                                    tint = TerracottaPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IntimateStatusBanner(
    currentUser: PairUser,
    partnerUser: PairUser
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "🔒 Pair Vault Enclave",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TerracottaPrimary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "• 418 Days Together",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "End-to-End P2P",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ChatBubble(
    message: Message,
    isFromMe: Boolean,
    onImageClick: (String) -> Unit,
    onToggleReactionPicker: () -> Unit
) {
    val timeFormat = remember(message.timestamp) {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        sdf.format(Date(message.timestamp))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("chat_bubble_${message.id}"),
        horizontalAlignment = if (isFromMe) Alignment.End else Alignment.Start
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .wrapContentWidth(if (isFromMe) Alignment.End else Alignment.Start),
            horizontalArrangement = if (isFromMe) Arrangement.End else Arrangement.Start
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 18.dp,
                    topEnd = 18.dp,
                    bottomStart = if (isFromMe) 18.dp else 4.dp,
                    bottomEnd = if (isFromMe) 4.dp else 18.dp
                ),
                color = if (isFromMe) {
                    TerracottaPrimary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                },
                tonalElevation = 2.dp,
                modifier = Modifier
                    .clickable { onToggleReactionPicker() }
                    .border(
                        width = 0.5.dp,
                        color = if (isFromMe) TerracottaSecondary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp,
                            bottomStart = if (isFromMe) 18.dp else 4.dp,
                            bottomEnd = if (isFromMe) 4.dp else 18.dp
                        )
                    )
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    // Photo Media Attachment
                    if (message.mediaType == MediaType.IMAGE && message.mediaUrl != null) {
                        Box(
                            modifier = Modifier
                                .widthIn(max = 240.dp)
                                .height(160.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onImageClick(message.mediaUrl) }
                                .padding(bottom = 6.dp)
                        ) {
                            AsyncImage(
                                model = message.mediaUrl,
                                contentDescription = "Shared photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    // Voice Note representation
                    if (message.mediaType == MediaType.VOICE_NOTE) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .widthIn(min = 180.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(if (isFromMe) Color.White.copy(alpha = 0.25f) else TerracottaPrimary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play voice note",
                                    tint = if (isFromMe) Color.White else TerracottaPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            // Waveform bars simulation
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val heights = listOf(6, 14, 20, 10, 24, 18, 8, 22, 16, 12, 19, 14)
                                heights.forEach { h ->
                                    Box(
                                        modifier = Modifier
                                            .width(3.dp)
                                            .height(h.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(if (isFromMe) Color.White.copy(alpha = 0.8f) else TerracottaPrimary)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "0:${message.voiceDurationSeconds.toString().padStart(2, '0')}",
                                fontSize = 11.sp,
                                color = if (isFromMe) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Message Text
                    if (message.text.isNotBlank() && message.mediaType != MediaType.VOICE_NOTE) {
                        Text(
                            text = message.text,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isFromMe) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.5.sp
                        )
                    }

                    // Footer: Timestamp & Delivery Ticks
                    Row(
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = timeFormat,
                            fontSize = 10.sp,
                            color = if (isFromMe) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )

                        if (isFromMe) {
                            Spacer(modifier = Modifier.width(4.dp))
                            DeliveryTickIndicator(status = message.status)
                        }
                    }
                }
            }
        }

        // Reaction Badge
        if (message.reaction != null) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .offset(y = (-8).dp, x = if (isFromMe) (-10).dp else 10.dp)
                    .clickable { onToggleReactionPicker() }
            ) {
                Text(
                    text = message.reaction,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun DeliveryTickIndicator(status: DeliveryStatus) {
    when (status) {
        DeliveryStatus.SENDING -> {
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = "Sending",
                tint = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(11.dp)
            )
        }
        DeliveryStatus.SENT -> {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Sent",
                tint = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.size(12.dp)
            )
        }
        DeliveryStatus.DELIVERED -> {
            Row(horizontalArrangement = Arrangement.spacedBy((-6).dp)) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(12.dp)
                )
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Delivered",
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(12.dp)
                )
            }
        }
        DeliveryStatus.READ -> {
            // Distinct intimate blush double check
            Row(horizontalArrangement = Arrangement.spacedBy((-6).dp)) {
                Icon(
                    imageVector = Icons.Default.DoneAll,
                    contentDescription = "Read",
                    tint = BlushAccent,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun QuickMediaPickerTray(
    onSelectImage: (String, String) -> Unit,
    onClose: () -> Unit
) {
    val presets = listOf(
        Pair("https://images.unsplash.com/photo-1518199266791-5375a83190b7?w=800&q=80", "Sunset for us 🌅"),
        Pair("https://images.unsplash.com/photo-1517841905240-472988babdf9?w=800&q=80", "Your morning coffee ☕️"),
        Pair("https://images.unsplash.com/photo-1519741497674-611481863552?w=800&q=80", "Thinking of our stargazing ✨"),
        Pair("https://images.unsplash.com/photo-1522673607200-164d1b6ce486?w=800&q=80", "Us forever ❤️")
    )

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Share Private Photo Memory",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presets.forEach { (url, caption) ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(76.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onSelectImage(url, caption) }
                    ) {
                        AsyncImage(
                            model = url,
                            contentDescription = caption,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}
