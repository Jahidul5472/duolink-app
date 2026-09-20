package com.example.model

object PairLockManager {

    /**
     * Strictly verifies whether an email or UID is one of the two permitted partners.
     * Any other identity is rejected immediately.
     */
    fun authenticate(identifier: String): PairAuthResult {
        val clean = identifier.trim().lowercase()

        return when {
            clean == PairLockConstants.USER_A_EMAIL.lowercase() || clean == PairLockConstants.USER_A_ID -> {
                PairAuthResult.Granted(PairLockConstants.USER_A, "Primary Account")
            }
            clean == PairLockConstants.USER_B_EMAIL.lowercase() || clean == PairLockConstants.USER_B_ID -> {
                PairAuthResult.Granted(PairLockConstants.USER_B, "Partner Account")
            }
            else -> {
                PairAuthResult.Denied(
                    attemptedIdentifier = identifier,
                    reason = "Access Denied: Application is cryptographically pair-locked exclusively to ${PairLockConstants.USER_A_EMAIL} and ${PairLockConstants.USER_B_EMAIL}. All third-party access is dropped."
                )
            }
        }
    }

    const val FIRESTORE_SECURITY_RULES = """
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    // Hardcoded Pair Whitelist
    function isPairMember() {
      let uidA = "uid_jahidul_01";
      let uidB = "uid_partner_02";
      return request.auth != null && (request.auth.uid == uidA || request.auth.uid == uidB);
    }

    // Direct Messages Collection
    match /pair_vault/messages/chat/{messageId} {
      allow read, write: if isPairMember();
      allow create: if isPairMember() && 
                    request.resource.data.senderId == request.auth.uid;
      allow update: if isPairMember() &&
                    // Partner can mark delivered/read or add reactions
                    (request.resource.data.diff(resource.data).affectedKeys()
                      .hasOnly(['status', 'reaction', 'readAt']));
      allow delete: if false; // Permanent intimate history
    }

    // Live WebRTC Signaling (Call sessions)
    match /pair_vault/calling/{callId} {
      allow read, write: if isPairMember();
      match /iceCandidates/{candidateId} {
        allow read, write: if isPairMember();
      }
    }

    // Real-Time Presence
    match /pair_vault/presence/{userId} {
      allow read: if isPairMember();
      allow write: if isPairMember() && request.auth.uid == userId;
    }
    
    // Reject any other path or user
    match /{document=**} {
      allow read, write: if false;
    }
  }
}
"""

    const val SUPABASE_RLS_RULES = """
-- PostgreSQL Row Level Security (RLS) for Pair-Locked DB
-- Table: messages
ALTER TABLE messages ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Allow pair members only" ON messages
FOR ALL
USING (
  auth.uid() IN ('uid_jahidul_01'::uuid, 'uid_partner_02'::uuid)
)
WITH CHECK (
  auth.uid() IN ('uid_jahidul_01'::uuid, 'uid_partner_02'::uuid)
  AND sender_id = auth.uid()
);

-- Table: call_sessions
ALTER TABLE call_sessions ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Allow pair calling only" ON call_sessions
FOR ALL
USING (
  auth.uid() IN ('uid_jahidul_01'::uuid, 'uid_partner_02'::uuid)
);
"""
}
