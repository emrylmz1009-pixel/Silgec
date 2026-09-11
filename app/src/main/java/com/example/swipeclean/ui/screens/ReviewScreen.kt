package com.example.swipeclean.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.RemoveShoppingCart
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.swipeclean.data.PhotoItem
import com.example.swipeclean.theme.CyberPurple
import com.example.swipeclean.theme.DarkBackground
import com.example.swipeclean.theme.DarkCardElevated
import com.example.swipeclean.theme.DarkCardSurface
import com.example.swipeclean.theme.ElectricBlue
import com.example.swipeclean.theme.GlassBorder
import com.example.swipeclean.theme.GlassBorderHighlight
import com.example.swipeclean.theme.GlassSurface
import com.example.swipeclean.theme.GlowAmber
import com.example.swipeclean.theme.NeonCyan
import com.example.swipeclean.theme.NeonDelete
import com.example.swipeclean.theme.NeonDeleteGlow
import com.example.swipeclean.theme.NeonKeep
import com.example.swipeclean.theme.TextPrimary
import com.example.swipeclean.theme.TextSecondary
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    pendingPhotos: List<PhotoItem>,
    isDeleting: Boolean,
    onConfirmDelete: (List<PhotoItem>) -> Unit,
    onBackToSwipe: () -> Unit,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedIds = remember(pendingPhotos) {
        mutableStateListOf<Long>().apply {
            addAll(pendingPhotos.map { it.id })
        }
    }

    var showConfirmDialog by remember { mutableStateOf(false) }

    val selectedPhotos = pendingPhotos.filter { selectedIds.contains(it.id) }
    val totalSizeBytes = selectedPhotos.sumOf { it.size }
    val formattedTotalSize = PhotoItem.formatSize(totalSizeBytes)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Silinecekler Sepeti",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = TextPrimary
                        )
                        if (pendingPhotos.isNotEmpty()) {
                            Text(
                                text = "${selectedPhotos.size} / ${pendingPhotos.size} seçili",
                                fontSize = 11.sp,
                                color = NeonDelete
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menü", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground
                )
            )
        },
        bottomBar = {
            if (pendingPhotos.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkCardSurface)
                        .border(1.dp, GlassBorder, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Button(
                        onClick = { showConfirmDialog = true },
                        enabled = selectedPhotos.isNotEmpty() && !isDeleting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(16.dp, RoundedCornerShape(18.dp), spotColor = NeonDelete),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonDelete,
                            disabledContainerColor = NeonDelete.copy(alpha = 0.35f)
                        )
                    ) {
                        if (isDeleting) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Güvenle Siliniyor...", color = Color.White, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.DeleteForever, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Seçilenleri Sil (${selectedPhotos.size} Fotoğraf • $formattedTotalSize)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        },
        containerColor = DarkBackground,
        modifier = modifier
    ) { innerPadding ->
        if (pendingPhotos.isEmpty()) {
            // Empty State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(DarkCardElevated),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.RemoveShoppingCart,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Text(
                        text = "Sepetiniz Boş",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )

                    Text(
                        text = "Sola kaydırarak silmek istediğiniz fotoğraflar bu sepet ekranında güvenle toplanır.",
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp,
                        color = TextSecondary,
                        lineHeight = 19.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onBackToSwipe,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                    ) {
                        Text("Fotoğrafları Ayıklamaya Başla", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Top Storage Recovery Speedometer Banner
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .border(1.dp, GlassBorderHighlight, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCardSurface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(NeonDelete.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Speed,
                                        contentDescription = null,
                                        tint = NeonDelete,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Text(
                                    text = "Kazanılacak Depolama",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextSecondary
                                )
                            }

                            Text(
                                text = formattedTotalSize,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = NeonDelete
                            )
                        }

                        // Selection Action Chips Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Select All / Deselect
                            ActionPill(
                                label = if (selectedIds.size == pendingPhotos.size) "Seçimi Kaldır" else "Tümünü Seç",
                                icon = Icons.Default.SelectAll,
                                isPrimary = true,
                                onClick = {
                                    if (selectedIds.size == pendingPhotos.size) {
                                        selectedIds.clear()
                                    } else {
                                        selectedIds.clear()
                                        selectedIds.addAll(pendingPhotos.map { it.id })
                                    }
                                }
                            )

                            // Select Only Large (>3 MB)
                            ActionPill(
                                label = "Sadece Büyükleri (>3MB)",
                                icon = Icons.Default.FilterList,
                                isPrimary = false,
                                onClick = {
                                    selectedIds.clear()
                                    selectedIds.addAll(pendingPhotos.filter { it.isLarge }.map { it.id })
                                }
                            )
                        }
                    }
                }

                // Grid of Photos
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(pendingPhotos, key = { it.id }) { photo ->
                        val isSelected = selectedIds.contains(photo.id)
                        ReviewPhotoGridItem(
                            photo = photo,
                            isSelected = isSelected,
                            onToggle = {
                                if (isSelected) {
                                    selectedIds.remove(photo.id)
                                } else {
                                    selectedIds.add(photo.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Confirmation Alert Dialog
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            containerColor = DarkCardSurface,
            icon = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(NeonDelete.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.DeleteForever,
                        contentDescription = null,
                        tint = NeonDelete,
                        modifier = Modifier.size(32.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "Fotoğraflar Kalıcı Silinsin mi?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = "Seçilen ${selectedPhotos.size} adet fotoğraf cihazınızdan kalıcı olarak silinecektir. Bu işlemle cihazınızda $formattedTotalSize alan boşalacak. Onaylıyor musunuz?",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        onConfirmDelete(selectedPhotos)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonDelete),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Evet, Sil", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Vazgeç", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun ActionPill(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isPrimary: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isPrimary) NeonCyan.copy(alpha = 0.15f) else DarkCardElevated)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (isPrimary) NeonCyan else TextSecondary,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isPrimary) NeonCyan else TextPrimary
            )
        }
    }
}

@Composable
private fun ReviewPhotoGridItem(
    photo: PhotoItem,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(14.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) NeonDelete else GlassBorder,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onToggle() }
    ) {
        AsyncImage(
            model = photo.uri,
            contentDescription = photo.displayName,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Dim overlay when unselected
        if (!isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
            )
        }

        // Top Checkbox Indicator
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) NeonDelete else Color.Black.copy(alpha = 0.5f))
                    .border(
                        1.dp,
                        if (isSelected) NeonDelete else Color.White.copy(alpha = 0.5f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Bottom Size Tag
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                    )
                )
                .padding(horizontal = 6.dp, vertical = 4.dp)
        ) {
            Text(
                text = photo.formattedSize,
                color = if (photo.isLarge) GlowAmber else Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
