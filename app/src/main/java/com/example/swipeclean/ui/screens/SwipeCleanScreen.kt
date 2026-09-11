package com.example.swipeclean.ui.screens

import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PhotoSizeSelectActual
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
import com.example.swipeclean.theme.NeonKeepGlow
import com.example.swipeclean.theme.TextPrimary
import com.example.swipeclean.theme.TextSecondary
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeCleanScreen(
    currentPhoto: PhotoItem?,
    nextPhoto: PhotoItem?,
    thirdPhoto: PhotoItem?,
    currentIndex: Int,
    totalCount: Int,
    pendingDeleteCount: Int,
    filterName: String,
    canUndo: Boolean,
    isLoading: Boolean = false,
    onSwipeDelete: (PhotoItem) -> Unit,
    onSwipeKeep: (PhotoItem) -> Unit,
    onUndo: () -> Unit,
    onGoToReview: () -> Unit,
    onOpenDrawer: () -> Unit,
    onRefreshGallery: () -> Unit = {},
    onLoadSamplePhotos: () -> Unit = {},
    onResetDeck: () -> Unit = {},
    onBackToHome: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    val animOffsetX = remember { Animatable(0f) }
    val animOffsetY = remember { Animatable(0f) }

    var inspectingPhoto by remember { mutableStateOf<PhotoItem?>(null) }

    val swipeThreshold = 260f
    val progress = if (totalCount > 0) (currentIndex.toFloat() / totalCount.toFloat()).coerceIn(0f, 1f) else 0f

    // Auto-recovery: If deck is reported completed but user has not swiped anything, automatically reset deck
    LaunchedEffect(totalCount, currentIndex, canUndo) {
        if (totalCount > 0 && currentIndex >= totalCount && !canUndo) {
            onResetDeck()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Medyaları Ayıkla",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = TextPrimary
                            )
                            if (filterName.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(NeonCyan.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = filterName,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = NeonCyan
                                    )
                                }
                            }
                        }
                        if (totalCount > 0) {
                            Text(
                                text = "${currentIndex + 1} / $totalCount Öğe",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menü", tint = TextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = onGoToReview) {
                        if (pendingDeleteCount > 0) {
                            BadgedBox(badge = {
                                Badge(containerColor = NeonDelete) {
                                    Text(pendingDeleteCount.toString(), color = Color.White)
                                }
                            }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Sepet",
                                    tint = NeonDelete
                                )
                            }
                        } else {
                            Icon(Icons.Default.Delete, contentDescription = "Sepet", tint = TextPrimary)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground
                )
            )
        },
        containerColor = DarkBackground,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 18.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Glowing Linear Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(DarkCardElevated)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                listOf(NeonCyan, ElectricBlue)
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (isLoading) {
                // 1. Loading State
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, GlassBorderHighlight, RoundedCornerShape(26.dp)),
                        shape = RoundedCornerShape(26.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkCardSurface)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(
                                color = NeonCyan,
                                modifier = Modifier.size(52.dp),
                                strokeWidth = 4.dp
                            )
                            Text(
                                text = "Medyalar Taranıyor...",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Galerinizdeki fotoğraf ve videolar taranıp ayıklama kartları hazırlanıyor. Lütfen birkaç saniye bekleyin...",
                                fontSize = 13.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                lineHeight = 19.sp
                            )
                        }
                    }
                }
            } else if (totalCount == 0) {
                // 2. Empty State (No photos in gallery or filter)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, GlassBorderHighlight, RoundedCornerShape(26.dp)),
                        shape = RoundedCornerShape(26.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkCardSurface)
                    ) {
                        Column(
                            modifier = Modifier.padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(CircleShape)
                                    .background(ElectricBlue.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.PhotoLibrary,
                                    contentDescription = null,
                                    tint = NeonCyan,
                                    modifier = Modifier.size(38.dp)
                                )
                            }

                            Text(
                                text = "Taranabilir Medya Yok",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "Cihazınızda gösterilecek medya bulunamadı veya henüz izin verilmedi. Örnek kartlarla hemen test edebilir ya da galeriyi yeniden tarayabilirsiniz.",
                                fontSize = 13.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                lineHeight = 19.sp
                            )

                            Button(
                                onClick = onLoadSamplePhotos,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Örnek Fotoğraflarla Başla", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }

                            OutlinedButton(
                                onClick = onRefreshGallery,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(14.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderHighlight)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, tint = TextPrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Galeriyi Yeniden Tara", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                            }

                            TextButton(
                                onClick = onBackToHome,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Ana Sayfaya Dön", color = TextSecondary, fontSize = 13.sp)
                            }
                        }
                    }
                }
            } else if (currentPhoto == null || currentIndex >= totalCount) {
                // 3. Completed Celebration View (User actually swiped all items!)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, GlassBorderHighlight, RoundedCornerShape(26.dp)),
                        shape = RoundedCornerShape(26.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkCardSurface)
                    ) {
                        Column(
                            modifier = Modifier.padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(NeonKeep.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = NeonKeep,
                                    modifier = Modifier.size(42.dp)
                                )
                            }

                            Text(
                                text = "Tebrikler! Deste Bitti 🎉",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = if (pendingDeleteCount > 0)
                                    "$pendingDeleteCount adet medya silinmek üzere sepetinizde bekliyor. Hemen onaylayıp hafızanızı temizleyin!"
                                else
                                    "Tüm medyaları tek tek incelediniz, galeriniz tertemiz.",
                                fontSize = 13.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                lineHeight = 19.sp
                            )

                            if (pendingDeleteCount > 0) {
                                Button(
                                    onClick = onGoToReview,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonDelete)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Silinecekleri Onayla ($pendingDeleteCount)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color.White
                                    )
                                }
                            }

                            Button(
                                onClick = onResetDeck,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (pendingDeleteCount > 0) DarkCardElevated else NeonCyan,
                                    contentColor = if (pendingDeleteCount > 0) TextPrimary else Color.Black
                                )
                            ) {
                                Icon(
                                    Icons.Default.Replay,
                                    contentDescription = null,
                                    tint = if (pendingDeleteCount > 0) TextPrimary else Color.Black
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Desteyi Baştan Başlat", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }

                            OutlinedButton(
                                onClick = onRefreshGallery,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(14.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderHighlight)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, tint = TextPrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Galeriyi Yeniden Tara", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                            }

                            TextButton(
                                onClick = onBackToHome,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Ana Sayfaya Dön", color = TextSecondary, fontSize = 13.sp)
                            }
                        }
                    }
                }
            } else {
                // 3D Interactive Card Stack
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    // Third Card (Deep Background)
                    if (thirdPhoto != null) {
                        ModernPhotoCard(
                            photo = thirdPhoto,
                            onInspect = {},
                            modifier = Modifier
                                .fillMaxSize()
                                .scale(0.88f)
                                .offset(y = 24.dp)
                        )
                    }

                    // Second Card (Middle Background)
                    if (nextPhoto != null) {
                        val backgroundScale = (0.93f + (abs(animOffsetX.value) / 2500f)).coerceAtMost(1f)
                        ModernPhotoCard(
                            photo = nextPhoto,
                            onInspect = {},
                            modifier = Modifier
                                .fillMaxSize()
                                .scale(backgroundScale)
                                .offset(y = 12.dp)
                        )
                    }

                    // Current Top Card (Swipable)
                    val rotation = (animOffsetX.value / 22f).coerceIn(-22f, 22f)
                    val deleteAlpha = ((-animOffsetX.value - 30f) / 150f).coerceIn(0f, 1f)
                    val keepAlpha = ((animOffsetX.value - 30f) / 150f).coerceIn(0f, 1f)

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .offset {
                                IntOffset(
                                    animOffsetX.value.roundToInt(),
                                    animOffsetY.value.roundToInt()
                                )
                            }
                            .rotate(rotation)
                            .pointerInput(currentPhoto.id) {
                                detectDragGestures(
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        coroutineScope.launch {
                                            animOffsetX.snapTo(animOffsetX.value + dragAmount.x)
                                            animOffsetY.snapTo(animOffsetY.value + dragAmount.y * 0.35f)
                                        }
                                    },
                                    onDragEnd = {
                                        coroutineScope.launch {
                                            when {
                                                animOffsetX.value < -swipeThreshold -> {
                                                    // Haptic trigger for delete
                                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                    animOffsetX.animateTo(-1400f, tween(220))
                                                    onSwipeDelete(currentPhoto)
                                                    animOffsetX.snapTo(0f)
                                                    animOffsetY.snapTo(0f)
                                                }
                                                animOffsetX.value > swipeThreshold -> {
                                                    // Haptic trigger for keep
                                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                    animOffsetX.animateTo(1400f, tween(220))
                                                    onSwipeKeep(currentPhoto)
                                                    animOffsetX.snapTo(0f)
                                                    animOffsetY.snapTo(0f)
                                                }
                                                else -> {
                                                    // Spring Back
                                                    launch {
                                                        animOffsetX.animateTo(
                                                            0f,
                                                            spring(
                                                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                                                stiffness = Spring.StiffnessMediumLow
                                                            )
                                                        )
                                                    }
                                                    launch {
                                                        animOffsetY.animateTo(
                                                            0f,
                                                            spring(
                                                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                                                stiffness = Spring.StiffnessMediumLow
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                )
                            }
                    ) {
                        ModernPhotoCard(
                            photo = currentPhoto,
                            onInspect = { inspectingPhoto = currentPhoto },
                            modifier = Modifier.fillMaxSize()
                        )

                        // Glowing Neon SİL Stamp
                        if (deleteAlpha > 0.05f) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(24.dp)
                                    .rotate(14f)
                                    .shadow(20.dp, RoundedCornerShape(16.dp), spotColor = NeonDelete)
                                    .border(
                                        width = 3.dp,
                                        color = NeonDelete.copy(alpha = deleteAlpha),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .background(
                                        NeonDelete.copy(alpha = deleteAlpha * 0.3f),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .padding(horizontal = 20.dp, vertical = 10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = NeonDelete.copy(alpha = deleteAlpha),
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Text(
                                        text = "SİL",
                                        color = NeonDelete.copy(alpha = deleteAlpha),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 28.sp,
                                        letterSpacing = 1.sp
                                    )
                                }
                            }
                        }

                        // Glowing Neon KALSIN Stamp
                        if (keepAlpha > 0.05f) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(24.dp)
                                    .rotate(-14f)
                                    .shadow(20.dp, RoundedCornerShape(16.dp), spotColor = NeonKeep)
                                    .border(
                                        width = 3.dp,
                                        color = NeonKeep.copy(alpha = keepAlpha),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .background(
                                        NeonKeep.copy(alpha = keepAlpha * 0.3f),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .padding(horizontal = 20.dp, vertical = 10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = NeonKeep.copy(alpha = keepAlpha),
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Text(
                                        text = "KALSIN",
                                        color = NeonKeep.copy(alpha = keepAlpha),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 28.sp,
                                        letterSpacing = 1.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Neomorphic Modern Action Bar with Haptics
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // SİL Butonu (Left - Neon Coral)
                    FilledIconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            coroutineScope.launch {
                                animOffsetX.animateTo(-1400f, tween(220))
                                onSwipeDelete(currentPhoto)
                                animOffsetX.snapTo(0f)
                                animOffsetY.snapTo(0f)
                            }
                        },
                        modifier = Modifier
                            .size(68.dp)
                            .shadow(12.dp, CircleShape, spotColor = NeonDelete),
                        shape = CircleShape,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = NeonDelete.copy(alpha = 0.2f),
                            contentColor = NeonDelete
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Sola Kaydır (Sil)",
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    // Geri Al Butonu (Undo)
                    FilledIconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onUndo()
                        },
                        enabled = canUndo,
                        modifier = Modifier.size(50.dp),
                        shape = CircleShape,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = DarkCardElevated,
                            contentColor = GlowAmber,
                            disabledContainerColor = DarkCardSurface,
                            disabledContentColor = TextSecondary.copy(alpha = 0.3f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Undo,
                            contentDescription = "Geri Al",
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Tam Ekran / Video İzleme Butonu (Inspect / Play)
                    FilledIconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            inspectingPhoto = currentPhoto
                        },
                        modifier = Modifier.size(50.dp),
                        shape = CircleShape,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = DarkCardElevated,
                            contentColor = if (currentPhoto.isVideo) NeonCyan else CyberPurple
                        )
                    ) {
                        Icon(
                            imageVector = if (currentPhoto.isVideo) Icons.Default.PlayArrow else Icons.Default.ZoomIn,
                            contentDescription = "İncele / Oynat",
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    // Sepete Git Butonu
                    FilledIconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onGoToReview()
                        },
                        modifier = Modifier.size(50.dp),
                        shape = CircleShape,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = DarkCardElevated,
                            contentColor = CyberPurple
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Sepet",
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // KALSIN Butonu (Right - Neon Emerald)
                    FilledIconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            coroutineScope.launch {
                                animOffsetX.animateTo(1400f, tween(220))
                                onSwipeKeep(currentPhoto)
                                animOffsetX.snapTo(0f)
                                animOffsetY.snapTo(0f)
                            }
                        },
                        modifier = Modifier
                            .size(68.dp)
                            .shadow(12.dp, CircleShape, spotColor = NeonKeep),
                        shape = CircleShape,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = NeonKeep.copy(alpha = 0.2f),
                            contentColor = NeonKeep
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Sağa Kaydır (Kalsın)",
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }
            }
        }
    }

    // Full Screen Photo or Video Inspect / Player Dialog
    inspectingPhoto?.let { photo ->
        val context = LocalContext.current
        Dialog(
            onDismissRequest = { inspectingPhoto = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.96f))
            ) {
                if (photo.isVideo) {
                    // Video Player Preview
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 80.dp, bottom = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AndroidView(
                            factory = { ctx ->
                                VideoView(ctx).apply {
                                    setVideoURI(photo.uri)
                                    val mediaController = MediaController(ctx)
                                    mediaController.setAnchorView(this)
                                    setMediaController(mediaController)
                                    setOnPreparedListener { mp ->
                                        mp.isLooping = true
                                        start()
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                    // Photo Preview
                    AsyncImage(
                        model = photo.uri,
                        contentDescription = photo.displayName,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Top Header Info and Close Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 42.dp, start = 20.dp, end = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = photo.displayName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (photo.isVideo)
                                "🎥 Video • ${photo.formattedDuration} • ${photo.formattedSize}"
                            else
                                "📸 Fotoğraf • ${photo.formattedSize} • ${photo.resolutionString}",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    IconButton(
                        onClick = { inspectingPhoto = null },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun ModernPhotoCard(
    photo: PhotoItem,
    onInspect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .shadow(16.dp, RoundedCornerShape(26.dp), spotColor = Color.Black)
            .border(1.dp, GlassBorder, RoundedCornerShape(26.dp)),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCardSurface)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = photo.uri,
                contentDescription = photo.displayName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onInspect() }
            )

            // Video Center Play Badge
            if (photo.isVideo) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.65f))
                        .border(1.5.dp, NeonCyan, CircleShape)
                        .clickable { onInspect() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Oynat",
                        tint = NeonCyan,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }

            // Top Quick Action Badge (Inspect or Play)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(14.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { onInspect() }
                    .padding(8.dp)
            ) {
                Icon(
                    if (photo.isVideo) Icons.Default.PlayArrow else Icons.Default.Fullscreen,
                    contentDescription = "İncele",
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Bottom Glassmorphic Information Panel
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.6f),
                                Color.Black.copy(alpha = 0.94f)
                            )
                        )
                    )
                    .padding(horizontal = 18.dp, vertical = 14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = photo.displayName,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Information Chips Row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Video duration or Image resolution
                        if (photo.isVideo) {
                            InfoChip(
                                text = "▶ ${photo.formattedDuration}",
                                textColor = NeonCyan,
                                bgColor = NeonCyan.copy(alpha = 0.2f)
                            )
                        } else if (photo.resolutionString.isNotEmpty()) {
                            InfoChip(
                                text = photo.resolutionString,
                                textColor = Color.White.copy(alpha = 0.9f),
                                bgColor = GlassSurface
                            )
                        }

                        // Size Chip
                        InfoChip(
                            text = photo.formattedSize,
                            textColor = if (photo.isLarge) GlowAmber else NeonCyan,
                            bgColor = if (photo.isLarge) GlowAmber.copy(alpha = 0.18f) else NeonCyan.copy(alpha = 0.15f)
                        )

                        // Bucket Name Chip
                        if (photo.bucketName.isNotEmpty()) {
                            InfoChip(
                                text = photo.bucketName,
                                textColor = CyberPurple,
                                bgColor = CyberPurple.copy(alpha = 0.18f)
                            )
                        }

                        // Date
                        if (photo.formattedDate.isNotEmpty()) {
                            Text(
                                text = photo.formattedDate,
                                fontSize = 11.sp,
                                color = TextSecondary,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoChip(
    text: String,
    textColor: Color,
    bgColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
    }
}
