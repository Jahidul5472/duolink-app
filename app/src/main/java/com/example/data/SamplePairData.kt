package com.example.data

import com.example.model.*

object SamplePairData {
    val initialPhotos = listOf(
        SharedPhoto(
            id = "photo_1",
            senderId = PairLockConstants.USER_B_ID,
            senderName = PairLockConstants.USER_B_NAME,
            url = "https://images.unsplash.com/photo-1518199266791-5375a83190b7?w=800&q=80",
            caption = "Our favorite sunset in the hills 🌅",
            timestamp = System.currentTimeMillis() - 86400000L * 3,
            location = "Amalfi Coast",
            isFavorite = true
        ),
        SharedPhoto(
            id = "photo_2",
            senderId = PairLockConstants.USER_A_ID,
            senderName = PairLockConstants.USER_A_NAME,
            url = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=800&q=80",
            caption = "Morning espresso before your presentation ☕️ You're gonna shine",
            timestamp = System.currentTimeMillis() - 86400000L * 2,
            location = "Corner Bakery",
            isFavorite = false
        ),
        SharedPhoto(
            id = "photo_3",
            senderId = PairLockConstants.USER_B_ID,
            senderName = PairLockConstants.USER_B_NAME,
            url = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800&q=80",
            caption = "Found the shell we looked for all afternoon 🐚",
            timestamp = System.currentTimeMillis() - 86400000L * 1,
            location = "Laguna Beach",
            isFavorite = true
        ),
        SharedPhoto(
            id = "photo_4",
            senderId = PairLockConstants.USER_A_ID,
            senderName = PairLockConstants.USER_A_NAME,
            url = "https://images.unsplash.com/photo-1519741497674-611481863552?w=800&q=80",
            caption = "Saved this corner for our late night stargazing 🌌",
            timestamp = System.currentTimeMillis() - 3600000L * 5,
            location = "Rooftop Garden",
            isFavorite = true
        ),
        SharedPhoto(
            id = "photo_5",
            senderId = PairLockConstants.USER_B_ID,
            senderName = PairLockConstants.USER_B_NAME,
            url = "https://images.unsplash.com/photo-1522673607200-164d1b6ce486?w=800&q=80",
            caption = "Happy 418 days together my love! ❤️",
            timestamp = System.currentTimeMillis() - 3600000L * 2,
            location = "Twofold Memories",
            isFavorite = true
        ),
        SharedPhoto(
            id = "photo_6",
            senderId = PairLockConstants.USER_A_ID,
            senderName = PairLockConstants.USER_A_NAME,
            url = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=800&q=80",
            caption = "You took this right when I was laughing at your joke 😄",
            timestamp = System.currentTimeMillis() - 1800000L,
            location = "Old Town Walk",
            isFavorite = false
        )
    )

    val initialMessages = listOf(
        Message(
            id = "msg_1",
            senderId = PairLockConstants.USER_B_ID,
            receiverId = PairLockConstants.USER_A_ID,
            text = "Good morning love! Did you sleep well? 💕",
            timestamp = System.currentTimeMillis() - 1000L * 60 * 60 * 3,
            status = DeliveryStatus.READ,
            reaction = "🥰"
        ),
        Message(
            id = "msg_2",
            senderId = PairLockConstants.USER_A_ID,
            receiverId = PairLockConstants.USER_B_ID,
            text = "Morning gorgeous. Dreamt about our road trip next month. Can't wait to see you today!",
            timestamp = System.currentTimeMillis() - 1000L * 60 * 60 * 2 - 1000L * 60 * 45,
            status = DeliveryStatus.READ,
            reaction = "❤️"
        ),
        Message(
            id = "msg_3",
            senderId = PairLockConstants.USER_B_ID,
            receiverId = PairLockConstants.USER_A_ID,
            text = "Look what I just picked up for our cozy dinner tonight!",
            mediaType = MediaType.IMAGE,
            mediaUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800&q=80",
            timestamp = System.currentTimeMillis() - 1000L * 60 * 60 * 1 - 1000L * 60 * 30,
            status = DeliveryStatus.READ,
            reaction = "✨"
        ),
        Message(
            id = "msg_4",
            senderId = PairLockConstants.USER_B_ID,
            receiverId = PairLockConstants.USER_A_ID,
            text = "Voice message",
            mediaType = MediaType.VOICE_NOTE,
            voiceDurationSeconds = 14,
            timestamp = System.currentTimeMillis() - 1000L * 60 * 40,
            status = DeliveryStatus.READ,
            reaction = "🫂"
        ),
        Message(
            id = "msg_5",
            senderId = PairLockConstants.USER_A_ID,
            receiverId = PairLockConstants.USER_B_ID,
            text = "Your voice always melts my stress away. Wrapping up my last review now, heading home soon! 🚗💨",
            timestamp = System.currentTimeMillis() - 1000L * 60 * 15,
            status = DeliveryStatus.READ,
            reaction = "🔥"
        ),
        Message(
            id = "msg_6",
            senderId = PairLockConstants.USER_B_ID,
            receiverId = PairLockConstants.USER_A_ID,
            text = "I'll keep dinner warm. Call me when you're in the car? I want to hear your voice.",
            timestamp = System.currentTimeMillis() - 1000L * 60 * 3,
            status = DeliveryStatus.DELIVERED,
            reaction = null
        )
    )

    val initialNotifications = listOf(
        AppNotification(
            id = "notif_1",
            title = "Aria sent a photo",
            body = "Look what I just picked up for our cozy dinner tonight! 🐚",
            type = NotificationType.PHOTO_SHARED,
            timestamp = System.currentTimeMillis() - 1000L * 60 * 90,
            isRead = true
        ),
        AppNotification(
            id = "notif_2",
            title = "Aria",
            body = "I'll keep dinner warm. Call me when you're in the car? ❤️",
            type = NotificationType.MESSAGE,
            timestamp = System.currentTimeMillis() - 1000L * 60 * 3,
            isRead = false
        )
    )
}
