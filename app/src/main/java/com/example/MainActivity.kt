package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.CallStatus
import com.example.ui.AppScreen
import com.example.ui.PairAppViewModel
import com.example.ui.components.IncomingCallBanner
import com.example.ui.components.InlinePhotoViewerDialog
import com.example.ui.components.IntimateTopBar
import com.example.ui.screens.call.CallScreen
import com.example.ui.screens.chat.ChatScreen
import com.example.ui.screens.gallery.MediaVaultScreen
import com.example.ui.screens.notifications.NotificationsScreen
import com.example.ui.screens.security.PairLockSecurityDialog
import com.example.ui.theme.TwofoldTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TwofoldTheme {
                TwofoldApp()
            }
        }
    }
}

@Composable
fun TwofoldApp(
    viewModel: PairAppViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val partnerUser by viewModel.partnerUser.collectAsState()
    val callSession by viewModel.callSession.collectAsState()
    val selectedPhoto by viewModel.selectedPhoto.collectAsState()
    val showSecurityDialog by viewModel.showSecurityDialog.collectAsState()
    val notifications by viewModel.notifications.collectAsState()

    val unreadCount = notifications.count { !it.isRead }
    val isFullscreenCall = currentScreen == AppScreen.CALL && callSession?.status == CallStatus.CONNECTED

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Scaffold(
            topBar = {
                if (!isFullscreenCall) {
                    IntimateTopBar(
                        partner = partnerUser,
                        activeScreen = currentScreen,
                        unreadNotificationsCount = unreadCount,
                        onNavigate = { screen -> viewModel.setScreen(screen) },
                        onStartCall = { type -> viewModel.startOutgoingCall(type) },
                        onOpenSecurity = { viewModel.openSecurityDialog() },
                        onSwitchUser = { viewModel.switchActivePairAccount() }
                    )
                }
            },
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Crossfade(targetState = currentScreen, label = "screen_transition") { screen ->
                    when (screen) {
                        AppScreen.CHAT -> {
                            ChatScreen(viewModel = viewModel)
                        }
                        AppScreen.CALL -> {
                            CallScreen(viewModel = viewModel)
                        }
                        AppScreen.MEDIA_VAULT -> {
                            MediaVaultScreen(viewModel = viewModel)
                        }
                        AppScreen.NOTIFICATIONS -> {
                            NotificationsScreen(viewModel = viewModel)
                        }
                        AppScreen.ARCHITECTURE_SPEC -> {
                            ChatScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }

        // Global Floating Incoming Call Overlay Banner
        if (!isFullscreenCall && callSession?.status == CallStatus.INCOMING_RINGING) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .align(Alignment.TopCenter)
            ) {
                IncomingCallBanner(
                    session = callSession,
                    onAccept = { viewModel.acceptCall() },
                    onDecline = { viewModel.declineCall() }
                )
            }
        }

        // Fullscreen Image Inspector Modal
        if (selectedPhoto != null) {
            InlinePhotoViewerDialog(
                photo = selectedPhoto!!,
                onDismiss = { viewModel.closePhotoViewer() }
            )
        }

        // Pair-Lock Security & Rules Dialog
        if (showSecurityDialog) {
            PairLockSecurityDialog(
                viewModel = viewModel,
                onDismiss = { viewModel.closeSecurityDialog() }
            )
        }
    }
}
