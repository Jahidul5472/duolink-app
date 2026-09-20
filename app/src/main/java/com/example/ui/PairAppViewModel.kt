package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SamplePairData
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppScreen {
    CHAT,
    CALL,
    MEDIA_VAULT,
    NOTIFICATIONS,
    ARCHITECTURE_SPEC
}

class PairAppViewModel : ViewModel() {

    // Current active logged in identity (default Jahidul)
    private val _currentUser = MutableStateFlow(PairLockConstants.USER_A)
    val currentUser: StateFlow<PairUser> = _currentUser.asStateFlow()

    // The single paired soulmate
    private val _partnerUser = MutableStateFlow(PairLockConstants.USER_B)
    val partnerUser: StateFlow<PairUser> = _partnerUser.asStateFlow()

    // Navigation
    private val _currentScreen = MutableStateFlow(AppScreen.CHAT)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Chat messages
    private val _messages = MutableStateFlow(SamplePairData.initialMessages)
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    // Media Vault
    private val _photos = MutableStateFlow(SamplePairData.initialPhotos)
    val photos: StateFlow<List<SharedPhoto>> = _photos.asStateFlow()

    // Fullscreen viewing image
    private val _selectedPhoto = MutableStateFlow<SharedPhoto?>(null)
    val selectedPhoto: StateFlow<SharedPhoto?> = _selectedPhoto.asStateFlow()

    // Notifications
    private val _notifications = MutableStateFlow(SamplePairData.initialNotifications)
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    // Call session
    private val _callSession = MutableStateFlow<CallSession?>(null)
    val callSession: StateFlow<CallSession?> = _callSession.asStateFlow()

    // Voice recording simulation
    private val _isRecordingVoice = MutableStateFlow(false)
    val isRecordingVoice: StateFlow<Boolean> = _isRecordingVoice.asStateFlow()

    private val _voiceRecordingSeconds = MutableStateFlow(0)
    val voiceRecordingSeconds: StateFlow<Int> = _voiceRecordingSeconds.asStateFlow()

    // Security dialogue & Pair test
    private val _showSecurityDialog = MutableStateFlow(false)
    val showSecurityDialog: StateFlow<Boolean> = _showSecurityDialog.asStateFlow()

    private val _pairAuthFeedback = MutableStateFlow<PairAuthResult?>(null)
    val pairAuthFeedback: StateFlow<PairAuthResult?> = _pairAuthFeedback.asStateFlow()

    private var callTimerJob: Job? = null
    private var voiceTimerJob: Job? = null

    fun setScreen(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun openPhotoViewer(photo: SharedPhoto) {
        _selectedPhoto.value = photo
    }

    fun closePhotoViewer() {
        _selectedPhoto.value = null
    }

    fun openSecurityDialog() {
        _showSecurityDialog.value = true
    }

    fun closeSecurityDialog() {
        _showSecurityDialog.value = false
    }

    fun switchActivePairAccount() {
        if (_currentUser.value.id == PairLockConstants.USER_A_ID) {
            _currentUser.value = PairLockConstants.USER_B
            _partnerUser.value = PairLockConstants.USER_A
        } else {
            _currentUser.value = PairLockConstants.USER_A
            _partnerUser.value = PairLockConstants.USER_B
        }
    }

    fun testPairAuth(identifier: String) {
        val result = PairLockManager.authenticate(identifier)
        _pairAuthFeedback.value = result
    }

    fun clearPairAuthFeedback() {
        _pairAuthFeedback.value = null
    }

    // --- Messaging ---

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        val newMessage = Message(
            id = "msg_${System.currentTimeMillis()}",
            senderId = _currentUser.value.id,
            receiverId = _partnerUser.value.id,
            text = text.trim(),
            timestamp = System.currentTimeMillis(),
            status = DeliveryStatus.SENT
        )

        _messages.update { it + newMessage }

        // Simulate delivery tick and partner read after brief delay
        viewModelScope.launch {
            delay(1200)
            _messages.update { list ->
                list.map { if (it.id == newMessage.id) it.copy(status = DeliveryStatus.DELIVERED) else it }
            }
            delay(1800)
            _messages.update { list ->
                list.map { if (it.id == newMessage.id) it.copy(status = DeliveryStatus.READ) else it }
            }
        }
    }

    fun sendPhoto(url: String, caption: String = "Private memory ❤️") {
        val newMsgId = "msg_${System.currentTimeMillis()}"
        val newPhoto = SharedPhoto(
            id = "photo_${System.currentTimeMillis()}",
            senderId = _currentUser.value.id,
            senderName = _currentUser.value.displayName,
            url = url,
            caption = caption,
            timestamp = System.currentTimeMillis(),
            location = "Shared Vault"
        )

        val newMsg = Message(
            id = newMsgId,
            senderId = _currentUser.value.id,
            receiverId = _partnerUser.value.id,
            text = caption,
            mediaType = MediaType.IMAGE,
            mediaUrl = url,
            timestamp = System.currentTimeMillis(),
            status = DeliveryStatus.SENT
        )

        _messages.update { it + newMsg }
        _photos.update { listOf(newPhoto) + it }

        viewModelScope.launch {
            delay(1000)
            _messages.update { list ->
                list.map { if (it.id == newMsgId) it.copy(status = DeliveryStatus.READ) else it }
            }
        }
    }

    fun startVoiceRecording() {
        _isRecordingVoice.value = true
        _voiceRecordingSeconds.value = 0
        voiceTimerJob?.cancel()
        voiceTimerJob = viewModelScope.launch {
            while (_isRecordingVoice.value) {
                delay(1000)
                _voiceRecordingSeconds.update { it + 1 }
            }
        }
    }

    fun stopAndSendVoiceRecording() {
        val seconds = _voiceRecordingSeconds.value.coerceAtLeast(1)
        _isRecordingVoice.value = false
        voiceTimerJob?.cancel()

        val newMsg = Message(
            id = "msg_${System.currentTimeMillis()}",
            senderId = _currentUser.value.id,
            receiverId = _partnerUser.value.id,
            text = "Voice note ($seconds s)",
            mediaType = MediaType.VOICE_NOTE,
            voiceDurationSeconds = seconds,
            timestamp = System.currentTimeMillis(),
            status = DeliveryStatus.SENT
        )

        _messages.update { it + newMsg }

        viewModelScope.launch {
            delay(1500)
            _messages.update { list ->
                list.map { if (it.id == newMsg.id) it.copy(status = DeliveryStatus.READ) else it }
            }
        }
    }

    fun cancelVoiceRecording() {
        _isRecordingVoice.value = false
        _voiceRecordingSeconds.value = 0
        voiceTimerJob?.cancel()
    }

    fun addReaction(messageId: String, emoji: String) {
        _messages.update { list ->
            list.map { msg ->
                if (msg.id == messageId) {
                    val newReaction = if (msg.reaction == emoji) null else emoji
                    msg.copy(reaction = newReaction)
                } else {
                    msg
                }
            }
        }
    }

    // --- Calling & WebRTC ---

    fun startOutgoingCall(type: CallType = CallType.VIDEO) {
        _callSession.value = CallSession(
            callerId = _currentUser.value.id,
            callerName = _currentUser.value.displayName,
            receiverId = _partnerUser.value.id,
            type = type,
            status = CallStatus.OUTGOING_RINGING
        )
        _currentScreen.value = AppScreen.CALL

        // Auto answer after 3 seconds for seamless demo
        viewModelScope.launch {
            delay(3500)
            if (_callSession.value?.status == CallStatus.OUTGOING_RINGING) {
                _callSession.update { it?.copy(status = CallStatus.CONNECTED) }
                startCallTimer()
            }
        }
    }

    fun triggerIncomingCall(type: CallType = CallType.VIDEO) {
        _callSession.value = CallSession(
            callerId = _partnerUser.value.id,
            callerName = _partnerUser.value.displayName,
            receiverId = _currentUser.value.id,
            type = type,
            status = CallStatus.INCOMING_RINGING
        )
        // Add push notification for incoming call
        val notif = AppNotification(
            id = "call_notif_${System.currentTimeMillis()}",
            title = "Incoming ${if (type == CallType.VIDEO) "Video" else "Audio"} Call",
            body = "${_partnerUser.value.displayName} is calling you...",
            type = NotificationType.MISSED_CALL,
            timestamp = System.currentTimeMillis(),
            isRead = false
        )
        _notifications.update { listOf(notif) + it }
    }

    fun acceptCall() {
        _callSession.update { it?.copy(status = CallStatus.CONNECTED) }
        _currentScreen.value = AppScreen.CALL
        startCallTimer()
    }

    fun declineCall() {
        _callSession.update { it?.copy(status = CallStatus.ENDED) }
        viewModelScope.launch {
            delay(800)
            _callSession.value = null
        }
    }

    fun toggleMute() {
        _callSession.update { it?.copy(isMuted = !it.isMuted) }
    }

    fun toggleVideo() {
        _callSession.update { it?.copy(isVideoEnabled = !it.isVideoEnabled) }
    }

    fun flipCamera() {
        _callSession.update { it?.copy(isFrontCamera = !it.isFrontCamera) }
    }

    fun endCall() {
        callTimerJob?.cancel()
        _callSession.update { it?.copy(status = CallStatus.ENDED) }
        viewModelScope.launch {
            delay(800)
            _callSession.value = null
            if (_currentScreen.value == AppScreen.CALL) {
                _currentScreen.value = AppScreen.CHAT
            }
        }
    }

    private fun startCallTimer() {
        callTimerJob?.cancel()
        callTimerJob = viewModelScope.launch {
            while (_callSession.value?.status == CallStatus.CONNECTED) {
                delay(1000)
                _callSession.update { it?.copy(durationSeconds = it.durationSeconds + 1) }
            }
        }
    }

    fun markNotificationAsRead(id: String) {
        _notifications.update { list ->
            list.map { if (it.id == id) it.copy(isRead = true) else it }
        }
    }

    fun clearAllNotifications() {
        _notifications.value = emptyList()
    }

    override fun onCleared() {
        super.onCleared()
        callTimerJob?.cancel()
        voiceTimerJob?.cancel()
    }
}
