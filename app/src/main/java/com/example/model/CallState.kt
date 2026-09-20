package com.example.model

enum class CallType {
    VIDEO,
    AUDIO
}

enum class CallStatus {
    IDLE,
    OUTGOING_RINGING,
    INCOMING_RINGING,
    CONNECTED,
    ENDED
}

data class WebRtcSignalingMetrics(
    val audioCodec: String = "Opus (48 kHz, 32 kbps)",
    val videoCodec: String = "VP9 / H.264 Scalable",
    val latencyMs: Int = 38,
    val packetLossPct: Double = 0.1,
    val candidateType: String = "srflx (STUN P2P direct)",
    val resolution: String = "1080p @ 30fps"
)

data class CallSession(
    val callId: String = "call_${System.currentTimeMillis()}",
    val callerId: String = PairLockConstants.USER_B_ID,
    val callerName: String = PairLockConstants.USER_B_NAME,
    val receiverId: String = PairLockConstants.USER_A_ID,
    val type: CallType = CallType.VIDEO,
    val status: CallStatus = CallStatus.IDLE,
    val durationSeconds: Int = 0,
    val isMuted: Boolean = false,
    val isVideoEnabled: Boolean = true,
    val isFrontCamera: Boolean = true,
    val isSpeakerOn: Boolean = true,
    val metrics: WebRtcSignalingMetrics = WebRtcSignalingMetrics()
)
