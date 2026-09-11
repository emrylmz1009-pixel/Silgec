package com.example.swipeclean.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.swipeclean.theme.CyberPurple
import com.example.swipeclean.theme.DarkBackground
import com.example.swipeclean.theme.DarkCardSurface
import com.example.swipeclean.theme.ElectricBlue
import com.example.swipeclean.theme.GlassBorder
import com.example.swipeclean.theme.GlassBorderHighlight
import com.example.swipeclean.theme.NeonCyan
import com.example.swipeclean.theme.NeonKeep
import com.example.swipeclean.theme.TextPrimary
import com.example.swipeclean.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Gizlilik Politikası",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
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
        containerColor = DarkBackground,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Cyber Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(NeonCyan, ElectricBlue, CyberPurple)
                        )
                    )
                    .padding(22.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Gizliliğiniz Önceliğimizdir",
                            color = Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "%100 Çevrimdışı ve Güvenli Çalışma",
                            color = Color.Black.copy(alpha = 0.8f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Cyber Info Cards
            ModernPrivacyCard(
                icon = Icons.Default.CloudOff,
                iconColor = NeonKeep,
                title = "1. Çevrimdışı Çalışma Prensibi",
                content = "SilGeç uygulaması tamamen cihazınızın içinde (on-device) çalışır. Uygulama internet izni dahi istemez ve hiçbir uzak sunucuya veri göndermez."
            )

            ModernPrivacyCard(
                icon = Icons.Default.PhotoLibrary,
                iconColor = NeonCyan,
                title = "2. Galeri İzni ve Kullanımı",
                content = "İstenen Galeri / Medya erişim izni yalnızca fotoğraflarınızı cihazınızda listeleyip sağa/sola kaydırma arayüzünde görüntülemeniz ve seçilenleri silmeniz için yerel olarak kullanılır."
            )

            ModernPrivacyCard(
                icon = Icons.Default.Block,
                iconColor = CyberPurple,
                title = "3. Veri Toplanmaması",
                content = "Uygulamamız ad, konum, rehber veya herhangi bir kişisel veri toplamaz. Analitik araçları, kullanıcı takibi veya reklam kodları barındırmaz."
            )

            ModernPrivacyCard(
                icon = Icons.Default.Delete,
                iconColor = NeonKeep,
                title = "4. Silme İşlemleri ve Güvenlik",
                content = "Hiçbir fotoğraf sizin açık onayınız olmadan silinmez. Silinmek üzere sola kaydırdığınız fotoğraflar sepetinizde toplanır. Son aşamada Android'in resmi güvenlik ekranında onaylamanız gerekir."
            )

            ModernPrivacyCard(
                icon = Icons.Default.Lock,
                iconColor = ElectricBlue,
                title = "5. Şeffaflık ve Güven",
                content = "Bu gizlilik politikası SilGeç uygulamasının şeffaf ve güvenli kullanımını garanti eder. Fotoğraflarınız tamamen size aittir."
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Son Güncelleme: Eylül 2026 • SilGeç v2.0",
                fontSize = 11.sp,
                color = TextSecondary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun ModernPrivacyCard(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    content: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, GlassBorder, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCardSurface)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextPrimary
                )
            }

            Text(
                text = content,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = TextSecondary
            )
        }
    }
}
