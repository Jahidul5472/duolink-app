package com.example.ui.screens.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.SharedPhoto
import com.example.ui.PairAppViewModel
import com.example.ui.theme.BlushAccent
import com.example.ui.theme.TerracottaPrimary

enum class VaultFilter {
    ALL,
    FAVORITES,
    FROM_PARTNER,
    FROM_ME
}

@Composable
fun MediaVaultScreen(
    viewModel: PairAppViewModel,
    modifier: Modifier = Modifier
) {
    val photos by viewModel.photos.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val partnerUser by viewModel.partnerUser.collectAsState()

    var activeFilter by remember { mutableStateOf(VaultFilter.ALL) }

    val filteredPhotos = remember(photos, activeFilter, currentUser.id) {
        when (activeFilter) {
            VaultFilter.ALL -> photos
            VaultFilter.FAVORITES -> photos.filter { it.isFavorite }
            VaultFilter.FROM_PARTNER -> photos.filter { it.senderId != currentUser.id }
            VaultFilter.FROM_ME -> photos.filter { it.senderId == currentUser.id }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("media_vault_screen")
    ) {
        // Vault Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = TerracottaPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Shared Media Vault",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "${photos.size} private memories exchanged exclusively between you two",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    FilledTonalButton(
                        onClick = {
                            viewModel.sendPhoto(
                                url = "https://images.unsplash.com/photo-1518199266791-5375a83190b7?w=800&q=80",
                                caption = "Captured another sunset together 🌅"
                            )
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = "Add memory",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Filter Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = activeFilter == VaultFilter.ALL,
                        onClick = { activeFilter = VaultFilter.ALL },
                        label = { Text("All (${photos.size})") }
                    )
                    FilterChip(
                        selected = activeFilter == VaultFilter.FAVORITES,
                        onClick = { activeFilter = VaultFilter.FAVORITES },
                        label = { Text("Favorites (${photos.count { it.isFavorite }})") }
                    )
                    FilterChip(
                        selected = activeFilter == VaultFilter.FROM_PARTNER,
                        onClick = { activeFilter = VaultFilter.FROM_PARTNER },
                        label = { Text(partnerUser.displayName) }
                    )
                    FilterChip(
                        selected = activeFilter == VaultFilter.FROM_ME,
                        onClick = { activeFilter = VaultFilter.FROM_ME },
                        label = { Text("You") }
                    )
                }
            }
        }

        // Photo Grid
        if (filteredPhotos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No memories in this view yet.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredPhotos, key = { it.id }) { photo ->
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { viewModel.openPhotoViewer(photo) }
                            .testTag("vault_photo_${photo.id}")
                    ) {
                        AsyncImage(
                            model = photo.url,
                            contentDescription = photo.caption,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        if (photo.isFavorite) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(6.dp)
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Favorite",
                                    tint = BlushAccent,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
