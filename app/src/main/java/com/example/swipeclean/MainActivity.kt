package com.example.swipeclean

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.swipeclean.data.GalleryRepository
import com.example.swipeclean.data.PhotoFilter
import com.example.swipeclean.data.PhotoItem
import com.example.swipeclean.theme.SwipeCleanTheme
import com.example.swipeclean.ui.components.AppDrawerContent
import com.example.swipeclean.ui.navigation.AppScreen
import com.example.swipeclean.ui.screens.HomeScreen
import com.example.swipeclean.ui.screens.PrivacyPolicyScreen
import com.example.swipeclean.ui.screens.ReviewScreen
import com.example.swipeclean.ui.screens.SwipeCleanScreen
import kotlinx.coroutines.launch

enum class SwipeActionType {
    KEEP,
    DELETE
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SwipeCleanTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SwipeCleanApp()
                }
            }
        }
    }
}

@Composable
fun SwipeCleanApp() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val galleryRepo = remember { GalleryRepository(context) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Navigation and Drawer
    var currentScreen by remember { mutableStateOf(AppScreen.HOME) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // Gallery State
    val allPhotos = remember { mutableStateListOf<PhotoItem>() }
    var activeFilter by remember { mutableStateOf(PhotoFilter.ALL) }
    var currentIndex by remember { mutableIntStateOf(0) }
    val pendingDeleteList = remember { mutableStateListOf<PhotoItem>() }
    val keptList = remember { mutableStateListOf<PhotoItem>() }
    val actionHistory = remember { mutableStateListOf<Pair<PhotoItem, SwipeActionType>>() }

    var isLoading by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }
    var hasPermission by remember { mutableStateOf(false) }

    // Photos currently being deleted
    var deletingPhotos = remember { mutableListOf<PhotoItem>() }

    // Filtered media items based on active category
    val currentDeckPhotos: List<PhotoItem> = when (activeFilter) {
        PhotoFilter.ALL -> allPhotos.toList()
        PhotoFilter.SCREENSHOTS -> allPhotos.filter { it.isScreenshot }
        PhotoFilter.LARGE -> allPhotos.filter { it.isLarge }
        PhotoFilter.VIDEOS -> allPhotos.filter { it.isVideo }
    }

    // Function to reload media
    fun loadGalleryPhotos(autoNavigateToSwipe: Boolean = false) {
        isLoading = true
        if (autoNavigateToSwipe) {
            currentScreen = AppScreen.SWIPE
        }
        coroutineScope.launch {
            val list = galleryRepo.fetchPhotos()
            allPhotos.clear()
            allPhotos.addAll(list)
            currentIndex = 0
            pendingDeleteList.clear()
            keptList.clear()
            actionHistory.clear()
            isLoading = false
        }
    }

    // Function to load demo/sample photos explicitly
    fun loadSamplePhotos() {
        isLoading = true
        coroutineScope.launch {
            val list = galleryRepo.getSamplePhotos()
            allPhotos.clear()
            allPhotos.addAll(list)
            currentIndex = 0
            pendingDeleteList.clear()
            keptList.clear()
            actionHistory.clear()
            isLoading = false
        }
    }

    // Permission Checking Helper (lenient: any granted media permission is accepted)
    fun checkPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) == PackageManager.PERMISSION_GRANTED
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    // Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        val granted = permissionsMap.values.any { it }
        hasPermission = granted
        if (granted) {
            loadGalleryPhotos(autoNavigateToSwipe = true)
        } else {
            Toast.makeText(
                context,
                "Fotoğraf ve videoları görüntülemek için galeri izni gereklidir.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun requestAppPermissions() {
        val permissionsToRequest = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                )
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO
                )
            }
            else -> {
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
        permissionLauncher.launch(permissionsToRequest)
    }

    // Check initial permission
    LaunchedEffect(Unit) {
        val isGranted = checkPermissions()
        hasPermission = isGranted
        if (isGranted) {
            loadGalleryPhotos()
        }
    }

    // System Deletion Intent Sender Launcher
    val deleteIntentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        isDeleting = false
        if (result.resultCode == Activity.RESULT_OK) {
            val count = deletingPhotos.size
            val freedBytes = deletingPhotos.sumOf { it.size }
            val freedSizeStr = PhotoItem.formatSize(freedBytes)

            // Remove from state
            allPhotos.removeAll(deletingPhotos)
            pendingDeleteList.removeAll(deletingPhotos)
            deletingPhotos.clear()

            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    "🎉 $count öğe başarıyla temizlendi ($freedSizeStr devasa alan açıldı)!"
                )
            }

            if (pendingDeleteList.isEmpty()) {
                currentScreen = AppScreen.HOME
            }
        } else {
            Toast.makeText(context, "Silme işlemi iptal edildi.", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to trigger deletion
    fun executeDelete(photosToDelete: List<PhotoItem>) {
        if (photosToDelete.isEmpty()) return
        isDeleting = true
        deletingPhotos = photosToDelete.toMutableList()

        val realPhotos = photosToDelete.filter { it.id < 900000L }

        coroutineScope.launch {
            if (realPhotos.isNotEmpty()) {
                val intentSender = galleryRepo.createDeleteIntentSender(realPhotos.map { it.uri })
                if (intentSender != null) {
                    val intentSenderRequest = IntentSenderRequest.Builder(intentSender).build()
                    deleteIntentLauncher.launch(intentSenderRequest)
                    return@launch
                } else {
                    galleryRepo.directDeletePhotos(realPhotos.map { it.uri })
                }
            }

            // Cleanup deleted items from state (real or sample)
            isDeleting = false
            val count = photosToDelete.size
            val freedBytes = photosToDelete.sumOf { it.size }
            allPhotos.removeAll(photosToDelete)
            pendingDeleteList.removeAll(photosToDelete)
            deletingPhotos.clear()
            currentIndex = 0
            snackbarHostState.showSnackbar(
                "🎉 $count öğe başarıyla temizlendi (${PhotoItem.formatSize(freedBytes)} devasa alan açıldı)!"
            )
            if (pendingDeleteList.isEmpty()) {
                currentScreen = AppScreen.HOME
            }
        }
    }

    // Back handling
    BackHandler {
        if (drawerState.isOpen) {
            coroutineScope.launch { drawerState.close() }
        } else if (currentScreen != AppScreen.HOME) {
            currentScreen = AppScreen.HOME
        } else {
            (context as? Activity)?.finish()
        }
    }

    val totalGalleryBytes = allPhotos.sumOf { it.size }
    val pendingTotalBytes = pendingDeleteList.sumOf { it.size }
    val screenshotsCount = allPhotos.count { it.isScreenshot }
    val largePhotosCount = allPhotos.count { it.isLarge }
    val videosCount = allPhotos.count { it.isVideo }
    val videosTotalBytes = allPhotos.filter { it.isVideo }.sumOf { it.size }

    val currentPhoto = currentDeckPhotos.getOrNull(currentIndex)
    val nextPhoto = currentDeckPhotos.getOrNull(currentIndex + 1)
    val thirdPhoto = currentDeckPhotos.getOrNull(currentIndex + 2)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                currentScreen = currentScreen,
                totalPhotos = allPhotos.size,
                totalSizeStr = PhotoItem.formatSize(totalGalleryBytes),
                pendingDeleteCount = pendingDeleteList.size,
                onNavigate = { screen ->
                    currentScreen = screen
                    coroutineScope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { scaffoldPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding)
            ) {
                when (currentScreen) {
                    AppScreen.HOME -> {
                        HomeScreen(
                            hasPermission = hasPermission,
                            isLoading = isLoading,
                            totalPhotos = allPhotos.size,
                            totalGallerySize = totalGalleryBytes,
                            screenshotsCount = screenshotsCount,
                            largePhotosCount = largePhotosCount,
                            videosCount = videosCount,
                            videosTotalSize = videosTotalBytes,
                            activeFilter = activeFilter,
                            onSelectFilter = { filter ->
                                activeFilter = filter
                                currentIndex = 0
                            },
                            swipedCount = actionHistory.size,
                            pendingDeleteCount = pendingDeleteList.size,
                            pendingDeleteSize = pendingTotalBytes,
                            onRequestPermission = { requestAppPermissions() },
                            onStartSwiping = {
                                if (!hasPermission) {
                                    requestAppPermissions()
                                } else {
                                    if (allPhotos.isEmpty() && !isLoading) {
                                        loadGalleryPhotos(autoNavigateToSwipe = true)
                                    } else {
                                        if (currentIndex >= currentDeckPhotos.size || actionHistory.isEmpty()) {
                                            currentIndex = 0
                                        }
                                        currentScreen = AppScreen.SWIPE
                                    }
                                }
                            },
                            onGoToReview = { currentScreen = AppScreen.REVIEW },
                            onOpenDrawer = { coroutineScope.launch { drawerState.open() } }
                        )
                    }

                    AppScreen.SWIPE -> {
                        SwipeCleanScreen(
                            currentPhoto = currentPhoto,
                            nextPhoto = nextPhoto,
                            thirdPhoto = thirdPhoto,
                            currentIndex = currentIndex,
                            totalCount = currentDeckPhotos.size,
                            pendingDeleteCount = pendingDeleteList.size,
                            filterName = if (activeFilter != PhotoFilter.ALL) activeFilter.title else "",
                            canUndo = actionHistory.isNotEmpty(),
                            isLoading = isLoading,
                            onSwipeDelete = { photo ->
                                pendingDeleteList.add(photo)
                                actionHistory.add(photo to SwipeActionType.DELETE)
                                currentIndex++
                            },
                            onSwipeKeep = { photo ->
                                keptList.add(photo)
                                actionHistory.add(photo to SwipeActionType.KEEP)
                                currentIndex++
                            },
                            onUndo = {
                                val lastAction = actionHistory.removeLastOrNull()
                                if (lastAction != null) {
                                    val (photo, action) = lastAction
                                    if (action == SwipeActionType.DELETE) {
                                        pendingDeleteList.remove(photo)
                                    } else {
                                        keptList.remove(photo)
                                    }
                                    currentIndex = (currentIndex - 1).coerceAtLeast(0)
                                }
                            },
                            onGoToReview = { currentScreen = AppScreen.REVIEW },
                            onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                            onRefreshGallery = { loadGalleryPhotos(autoNavigateToSwipe = false) },
                            onLoadSamplePhotos = { loadSamplePhotos() },
                            onResetDeck = {
                                currentIndex = 0
                                actionHistory.clear()
                            },
                            onBackToHome = { currentScreen = AppScreen.HOME }
                        )
                    }

                    AppScreen.REVIEW -> {
                        ReviewScreen(
                            pendingPhotos = pendingDeleteList.toList(),
                            isDeleting = isDeleting,
                            onConfirmDelete = { photosToDelete ->
                                executeDelete(photosToDelete)
                            },
                            onBackToSwipe = { currentScreen = AppScreen.SWIPE },
                            onOpenDrawer = { coroutineScope.launch { drawerState.open() } }
                        )
                    }

                    AppScreen.PRIVACY -> {
                        PrivacyPolicyScreen(
                            onOpenDrawer = { coroutineScope.launch { drawerState.open() } }
                        )
                    }
                }
            }
        }
    }
}
