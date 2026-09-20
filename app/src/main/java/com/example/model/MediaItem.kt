package com.example.model

data class SharedPhoto(
    val id: String,
    val senderId: String,
    val senderName: String,
    val url: String,
    val caption: String,
    val timestamp: Long,
    val location: String = "Private Vault",
    val isFavorite: Boolean = false
)

data class AppNotification(
    val id: String,
    val title: String,
    val body: String,
    val type: NotificationType,
    val timestamp: Long,
    val isRead: Boolean = false,
    val actionPayload: String? = null
)

enum class NotificationType {
    MESSAGE,
    MISSED_CALL,
    PHOTO_SHARED,
    SECURITY_ALERT
}
