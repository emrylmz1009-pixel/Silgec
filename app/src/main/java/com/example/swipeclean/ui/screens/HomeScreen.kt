package com.example.swipeclean.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Screenshot
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.swipeclean.R
import com.example.swipeclean.data.PhotoFilter
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
import com.example.swipeclean.theme.NeonDeleteContainer
import com.example.swipeclean.theme.NeonKeep
import com.example.swipeclean.theme.NeonKeepContainer
import com.example.swipeclean.theme.TextPrimary
import com.example.swipeclean.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    hasPermission: Boolean,
    isLoading: Boolean,
    totalPhotos: Int,
    totalGallerySize: Long,
    screenshotsCount: Int,
    largePhotosCount: Int,
    videosCount: Int,
    videosTotalSize: Long,
    activeFilter: PhotoFilter,
    onSelectFilter: (PhotoFilter) -> Unit,
    swipedCount: Int,
    pendingDeleteCount: Int,
    pendingDeleteSize: Long,
    onRequestPermission: () -> Unit,
    onStartSwiping: () -> Unit,
    onGoToReview: () -> Unit,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.app_logo),
                            contentDescription = "SilGeç",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "SilGeç",
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary,
                                letterSpacing = (-0.5).sp
                            )
                            Text(
                                text = "Galeri & Video Temizleyici",
                                fontSize = 11.sp,
                                color = NeonCyan
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Menü",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    if (pendingDeleteCount > 0) {
                        IconButton(onClick = onGoToReview) {
                            BadgedBox(badge = {
                                Badge(
                                    containerColor = NeonDelete,
                                    contentColor = Color.White
                                ) {
                                    Text(pendingDeleteCount.toString())
                                }
                            }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Sepet",
                                    tint = NeonDelete
                                )
                            }
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Permission Banner (if permission not granted)
            if (!hasPermission) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, GlassBorderHighlight, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCardSurface)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(NeonCyan, ElectricBlue))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.PhotoLibrary,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Text(
                            text = "Galeri ve Medya İzni Gerekli",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = TextPrimary
                        )

                        Text(
                            text = "Gereksiz fotoğraf ve videoları ayıklayıp devasa alan açabilmek için galeri iznine ihtiyacımız var. Medyalarınız %100 çevrimdışı ve cihazınızda kalır.",
                            textAlign = TextAlign.Center,
                            fontSize = 13.sp,
                            color = TextSecondary,
                            lineHeight = 18.sp
                        )

                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onRequestPermission()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonCyan,
                                contentColor = Color.Black
                            )
                        ) {
                            Text(
                                "Galeri İzni Ver ve Başla",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            } else if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(color = NeonCyan, strokeWidth = 3.dp)
                        Text(
                            "Fotoğraflar ve videolar taranıyor...",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                // Storage Analysis Dashboard Card
                StorageAnalysisCard(
                    totalPhotos = totalPhotos,
                    totalSizeStr = PhotoItem.formatSize(totalGallerySize),
                    pendingCount = pendingDeleteCount,
                    pendingSizeStr = PhotoItem.formatSize(pendingDeleteSize),
                    pendingSizeBytes = pendingDeleteSize,
                    totalSizeBytes = totalGallerySize
                )

                // Quick Mode Selection Chips
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.Speed,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Temizleme Modu Seçin",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                    }

                    // 4 Filter Cards Grid / Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterCard(
                            title = "Tümü",
                            count = totalPhotos,
                            icon = Icons.Default.PhotoLibrary,
                            isSelected = activeFilter == PhotoFilter.ALL,
                            accentColor = NeonCyan,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onSelectFilter(PhotoFilter.ALL)
                            }
                        )

                        FilterCard(
                            title = "Videolar",
                            count = videosCount,
                            icon = Icons.Default.Videocam,
                            isSelected = activeFilter == PhotoFilter.VIDEOS,
                            accentColor = NeonDelete,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onSelectFilter(PhotoFilter.VIDEOS)
                            }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterCard(
                            title = "Ekran Resimleri",
                            count = screenshotsCount,
                            icon = Icons.Default.Screenshot,
                            isSelected = activeFilter == PhotoFilter.SCREENSHOTS,
                            accentColor = CyberPurple,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onSelectFilter(PhotoFilter.SCREENSHOTS)
                            }
                        )

                        FilterCard(
                            title = "Büyükler (>3MB)",
                            count = largePhotosCount,
                            icon = Icons.Default.FolderZip,
                            isSelected = activeFilter == PhotoFilter.LARGE,
                            accentColor = GlowAmber,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onSelectFilter(PhotoFilter.LARGE)
                            }
                        )
                    }
                }

                // Gamification & Cleaning Streak Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, GlassBorder, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCardSurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(NeonKeepContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = NeonKeep,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "$swipedCount Öğe İncelendi",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (pendingDeleteCount > 0)
                                        "${PhotoItem.formatSize(pendingDeleteSize)} alan temizlenmeye hazır"
                                    else
                                        "Galeriniz tertemiz ve güncel",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        if (pendingDeleteCount > 0) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(NeonDeleteContainer)
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        onGoToReview()
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "Sepet ($pendingDeleteCount)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonDelete
                                )
                            }
                        }
                    }
                }

                // Glowing Action CTA Button with Haptics
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onStartSwiping()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .shadow(16.dp, RoundedCornerShape(18.dp), spotColor = NeonCyan),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(NeonCyan, ElectricBlue, CyberPurple)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = if (swipedCount > 0) "Ayıklamaya Devam Et" else "Temizlemeye Başla",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            // How It Works Cyber Guide
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GlassBorder, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface.copy(alpha = 0.8f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.CleaningServices,
                            contentDescription = null,
                            tint = ElectricBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Nasıl Kullanılır?",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                    }

                    ModernGuideRow(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        iconColor = NeonDelete,
                        title = "Sola Kaydır (Sil)",
                        desc = "Gereksiz fotoğraf veya videoyu silme sepetine fırlatır."
                    )

                    ModernGuideRow(
                        icon = Icons.AutoMirrored.Filled.ArrowForward,
                        iconColor = NeonKeep,
                        title = "Sağa Kaydır (Kalsın)",
                        desc = "Beğendiğiniz medyayı güvenle galeride saklar."
                    )

                    ModernGuideRow(
                        icon = Icons.Default.Videocam,
                        iconColor = NeonCyan,
                        title = "Video Oynatma ve İnceleme",
                        desc = "Videoların üzerine basarak doğrudan oynatıp izleyebilirsiniz."
                    )

                    ModernGuideRow(
                        icon = Icons.AutoMirrored.Filled.Undo,
                        iconColor = GlowAmber,
                        title = "Geri Al (Undo)",
                        desc = "Hatalı bir kaydırma yaparsanız tek tıkla geri çağırın."
                    )

                    ModernGuideRow(
                        icon = Icons.Default.Shield,
                        iconColor = ElectricBlue,
                        title = "Toplu Sistem Onayı",
                        desc = "Sepetteki tüm seçimler resmi Android onayıyla güvenle silinir."
                    )
                }
            }
        }
    }
}

@Composable
private fun StorageAnalysisCard(
    totalPhotos: Int,
    totalSizeStr: String,
    pendingCount: Int,
    pendingSizeStr: String,
    pendingSizeBytes: Long,
    totalSizeBytes: Long
) {
    val cleanRatio = if (totalSizeBytes > 0) {
        (pendingSizeBytes.toFloat() / totalSizeBytes.toFloat()).coerceIn(0f, 1f)
    } else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, GlassBorderHighlight, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCardSurface)
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Galeri ve Video Hafızası",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NeonCyan
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = totalSizeStr,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(GlassSurface)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "$totalPhotos Öğe",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }

            // Custom Segmented Meter
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(DarkCardElevated)
                ) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        if (cleanRatio > 0.01f) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(cleanRatio)
                                    .fillMaxSize()
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(NeonDelete, GlowAmber)
                                        )
                                    )
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (pendingCount > 0)
                            "⚡ $pendingSizeStr alan açılabilir"
                        else
                            "Ayıklanacak medya bekleniyor",
                        fontSize = 11.sp,
                        color = if (pendingCount > 0) NeonDelete else TextSecondary,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = "Toplam $totalSizeStr",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterCard(
    title: String,
    count: Int,
    icon: ImageVector,
    isSelected: Boolean,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) accentColor else GlassBorder,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) accentColor.copy(alpha = 0.15f) else DarkCardSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) accentColor else TextSecondary,
                modifier = Modifier.size(20.dp)
            )

            Text(
                text = count.toString(),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 17.sp,
                color = if (isSelected) accentColor else TextPrimary
            )

            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) TextPrimary else TextSecondary,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ModernGuideRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    desc: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(18.dp)
            )
        }
        Column {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = TextPrimary
            )
            Text(
                text = desc,
                fontSize = 11.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )
        }
    }
}
