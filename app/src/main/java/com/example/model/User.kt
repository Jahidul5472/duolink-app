package com.example.model

data class PairUser(
    val id: String,
    val email: String,
    val displayName: String,
    val avatarInitials: String,
    val avatarColorHex: Long,
    val statusMessage: String,
    val isOnline: Boolean,
    val lastSeenText: String
)

object PairLockConstants {
    const val USER_A_ID = "uid_jahidul_01"
    const val USER_A_EMAIL = "jahidulbadla@gmail.com"
    const val USER_A_NAME = "Jahidul"

    const val USER_B_ID = "uid_partner_02"
    const val USER_B_EMAIL = "aria.partner@twofold.app"
    const val USER_B_NAME = "Aria"

    val HARDCODED_PAIR_UIDS = setOf(USER_A_ID, USER_B_ID)
    val HARDCODED_PAIR_EMAILS = setOf(
        USER_A_EMAIL.lowercase(),
        USER_B_EMAIL.lowercase()
    )

    val USER_A = PairUser(
        id = USER_A_ID,
        email = USER_A_EMAIL,
        displayName = USER_A_NAME,
        avatarInitials = "JB",
        avatarColorHex = 0xFFD96B5B,
        statusMessage = "Holding you in my thoughts ❤️",
        isOnline = true,
        lastSeenText = "Active now"
    )

    val USER_B = PairUser(
        id = USER_B_ID,
        email = USER_B_EMAIL,
        displayName = USER_B_NAME,
        avatarInitials = "AR",
        avatarColorHex = 0xFFE88B7D,
        statusMessage = "Counting the minutes until tonight ✨",
        isOnline = true,
        lastSeenText = "Active now"
    )
}

sealed class PairAuthResult {
    data class Granted(val user: PairUser, val role: String) : PairAuthResult()
    data class Denied(val attemptedIdentifier: String, val reason: String) : PairAuthResult()
}
