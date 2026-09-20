package com.example.ui.screens.security

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.PairAuthResult
import com.example.model.PairLockConstants
import com.example.model.PairLockManager
import com.example.ui.PairAppViewModel
import com.example.ui.theme.CallRed
import com.example.ui.theme.OnlineGreen
import com.example.ui.theme.TerracottaPrimary

@Composable
fun PairLockSecurityDialog(
    viewModel: PairAppViewModel,
    onDismiss: () -> Unit
) {
    var testEmailInput by remember { mutableStateOf("unauthorized_intruder@evilcorp.com") }
    var activeTab by remember { mutableStateOf(0) } // 0: Whitelist & Intruder Test, 1: Firestore Rules, 2: Supabase RLS
    val authFeedback by viewModel.pairAuthFeedback.collectAsState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f)
                .testTag("pair_lock_security_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(TerracottaPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = TerracottaPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Pair-Lock Security Enclave",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Strict 2-Person Cryptographic Perimeter",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Sub-tabs
                TabRow(
                    selectedTabIndex = activeTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Tab(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        text = { Text("Pair Whitelist & Test", fontSize = 12.sp) }
                    )
                    Tab(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        text = { Text("Firestore Rules", fontSize = 12.sp) }
                    )
                    Tab(
                        selected = activeTab == 2,
                        onClick = { activeTab = 2 },
                        text = { Text("Supabase RLS", fontSize = 12.sp) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tab Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    when (activeTab) {
                        0 -> {
                            // Whitelisted Couple identities
                            Text(
                                text = "Hardcoded Permitted Identities",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = TerracottaPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            // Partner 1 Card
                            WhitelistUserCard(
                                title = "User 1 (You)",
                                email = PairLockConstants.USER_A_EMAIL,
                                uid = PairLockConstants.USER_A_ID,
                                role = "Primary Account"
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Partner 2 Card
                            WhitelistUserCard(
                                title = "User 2 (Soulmate Partner)",
                                email = PairLockConstants.USER_B_EMAIL,
                                uid = PairLockConstants.USER_B_ID,
                                role = "Pair Partner"
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Switch simulated account
                            OutlinedButton(
                                onClick = { viewModel.switchActivePairAccount() },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Switch Active Account (Test Perspective as Partner)")
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Live Intruder Rejection Simulator
                            Text(
                                text = "Test Unauthorized Access Rejection",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = TerracottaPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Enter an unauthorized email to verify that registration/login is blocked:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = testEmailInput,
                                onValueChange = {
                                    testEmailInput = it
                                    viewModel.clearPairAuthFeedback()
                                },
                                label = { Text("Identifier to Test") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.testPairAuth(testEmailInput) },
                                    colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Test Access")
                                }

                                FilledTonalButton(
                                    onClick = {
                                        testEmailInput = PairLockConstants.USER_A_EMAIL
                                        viewModel.testPairAuth(testEmailInput)
                                    }
                                ) {
                                    Text("Fill Valid")
                                }
                            }

                            // Result Banner
                            if (authFeedback != null) {
                                Spacer(modifier = Modifier.height(12.dp))
                                when (val res = authFeedback!!) {
                                    is PairAuthResult.Granted -> {
                                        Surface(
                                            color = OnlineGreen.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(12.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, OnlineGreen),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    tint = OnlineGreen
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Text(
                                                        text = "Access Granted: ${res.user.displayName}",
                                                        fontWeight = FontWeight.Bold,
                                                        color = OnlineGreen,
                                                        fontSize = 13.sp
                                                    )
                                                    Text(
                                                        text = "Verified pair member (${res.role}). Decryption keys unlocked.",
                                                        fontSize = 12.sp,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    is PairAuthResult.Denied -> {
                                        Surface(
                                            color = CallRed.copy(alpha = 0.12f),
                                            shape = RoundedCornerShape(12.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, CallRed),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Warning,
                                                    contentDescription = null,
                                                    tint = CallRed
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Text(
                                                        text = "403 Access Denied: Unauthorized Party",
                                                        fontWeight = FontWeight.Bold,
                                                        color = CallRed,
                                                        fontSize = 13.sp
                                                    )
                                                    Text(
                                                        text = res.reason,
                                                        fontSize = 11.5.sp,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        1 -> {
                            Text(
                                text = "Firestore Security Rules (Strict 2-UID Lock)",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = TerracottaPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = PairLockManager.FIRESTORE_SECURITY_RULES.trimIndent(),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.5.sp,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                        2 -> {
                            Text(
                                text = "Supabase PostgreSQL Row Level Security (RLS)",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = TerracottaPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = PairLockManager.SUPABASE_RLS_RULES.trimIndent(),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.5.sp,
                                    modifier = Modifier.padding(12.dp)
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
fun WhitelistUserCard(
    title: String,
    email: String,
    uid: String,
    role: String
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = role,
                    fontSize = 11.sp,
                    color = TerracottaPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Email: $email",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "UID: $uid",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}
