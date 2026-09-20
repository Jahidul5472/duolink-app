package com.example.model

enum class DeliveryStatus {
    SENDING,
    SENT,
    DELIVERED,
    READ
}

enum class MediaType {
    NONE,
    IMAGE,
    VOICE_NOTE
}

data class Message(
    val id: String,
    val senderId: String,
    val receiverId: String,
    val text: String,
    val mediaType: MediaType = MediaType.NONE,
    val mediaUrl: String? = null,
    val voiceDurationSeconds: Int = 0,
    val timestamp: Long,
    val status: DeliveryStatus = DeliveryStatus.READ,
    val reaction: String? = null
) {
    val isFromMe: Boolean
        get() = senderId == PairLockConstants.USER_A_ID
}
