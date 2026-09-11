package com.example.swipeclean.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.swipeclean.R
import com.example.swipeclean.theme.CyberPurple
import com.example.swipeclean.theme.DarkBackground
import com.example.swipeclean.theme.DarkCardElevated
import com.example.swipeclean.theme.DarkCardSurface
import com.example.swipeclean.theme.ElectricBlue
import com.example.swipeclean.theme.GlassBorder
import com.example.swipeclean.theme.GlassBorderHighlight
import com.example.swipeclean.theme.NeonCyan
import com.example.swipeclean.theme.NeonDelete
import com.example.swipeclean.theme.TextPrimary
import com.example.swipeclean.theme.TextSecondary
import com.example.swipeclean.ui.navigation.AppScreen

@Composable
fun AppDrawerContent(
    currentScreen: AppScreen,
    totalPhotos: Int,
    totalSizeStr: String,
    pendingDeleteCount: Int,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier.width(320.dp),
        drawerContainerColor = DarkBackground
    ) {
        // Drawer Header with Modern Gradient Mesh
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(DarkCardSurface, DarkBackground)
                    )
                )
                .border(1.dp, GlassBorder, RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                .padding(24.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.app_logo),
                        contentDescription = "SilGeç Logo",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .border(1.5.dp, GlassBorderHighlight, RoundedCornerShape(18.dp))
                    )

                    Column {
                        Text(
                            text = "SilGeç",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Galeri Temizleme Asistanı",
                            fontSize = 11.sp,
                            color = NeonCyan
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mini stats badge
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkCardElevated)
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Toplam Galeri:",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "$totalPhotos Fotoğraf ($totalSizeStr)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigation Items
        NavigationDrawerItem(
            label = { Text("Ana Sayfa (Dashboard)", fontWeight = FontWeight.SemiBold, color = TextPrimary) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Ana Sayfa", tint = if (currentScreen == AppScreen.HOME) NeonCyan else TextSecondary) },
            selected = currentScreen == AppScreen.HOME,
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = NeonCyan.copy(alpha = 0.15f),
                unselectedContainerColor = Color.Transparent
            ),
            onClick = { onNavigate(AppScreen.HOME) },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        NavigationDrawerItem(
            label = { Text("Fotoğrafları Ayıkla (Swipe)", fontWeight = FontWeight.SemiBold, color = TextPrimary) },
            icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Ayıkla", tint = if (currentScreen == AppScreen.SWIPE) NeonCyan else TextSecondary) },
            selected = currentScreen == AppScreen.SWIPE,
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = NeonCyan.copy(alpha = 0.15f),
                unselectedContainerColor = Color.Transparent
            ),
            onClick = { onNavigate(AppScreen.SWIPE) },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        NavigationDrawerItem(
            label = { Text("Silinecekler Sepeti", fontWeight = FontWeight.SemiBold, color = TextPrimary) },
            icon = {
                if (pendingDeleteCount > 0) {
                    BadgedBox(badge = {
                        Badge(containerColor = NeonDelete) {
                            Text(pendingDeleteCount.toString(), color = Color.White)
                        }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Sepet", tint = NeonDelete)
                    }
                } else {
                    Icon(Icons.Default.Delete, contentDescription = "Sepet", tint = TextSecondary)
                }
            },
            badge = {
                if (pendingDeleteCount > 0) {
                    Text(
                        "$pendingDeleteCount öğe",
                        fontSize = 12.sp,
                        color = NeonDelete,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            selected = currentScreen == AppScreen.REVIEW,
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = NeonDelete.copy(alpha = 0.15f),
                unselectedContainerColor = Color.Transparent
            ),
            onClick = { onNavigate(AppScreen.REVIEW) },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            color = GlassBorder
        )

        NavigationDrawerItem(
            label = { Text("Gizlilik Politikası", fontWeight = FontWeight.SemiBold, color = TextPrimary) },
            icon = { Icon(Icons.Default.PrivacyTip, contentDescription = "Gizlilik Politikası", tint = if (currentScreen == AppScreen.PRIVACY) NeonCyan else TextSecondary) },
            selected = currentScreen == AppScreen.PRIVACY,
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = NeonCyan.copy(alpha = 0.15f),
                unselectedContainerColor = Color.Transparent
            ),
            onClick = { onNavigate(AppScreen.PRIVACY) },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Cyberpunk Security Footer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
                .background(DarkCardSurface)
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(NeonCyan.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Güvenli",
                        tint = NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column {
                    Text(
                        text = "%100 Çevrimdışı Güvenlik",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Fotoğraflarınız cihazınızda kalır",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}
