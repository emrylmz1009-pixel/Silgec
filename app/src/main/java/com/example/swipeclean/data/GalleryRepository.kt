package com.example.swipeclean.data

import android.content.ContentUris
import android.content.Context
import android.content.IntentSender
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GalleryRepository(private val context: Context) {

    suspend fun fetchPhotos(): List<PhotoItem> = withContext(Dispatchers.IO) {
        val mediaList = mutableListOf<PhotoItem>()

        // 1. Fetch Images Safely
        try {
            val imageProjection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT
            )

            context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                imageProjection,
                null,
                null,
                "${MediaStore.Images.Media.DATE_ADDED} DESC"
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val nameColumn = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
                val sizeColumn = cursor.getColumnIndex(MediaStore.Images.Media.SIZE)
                val dateColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED)
                val bucketColumn = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
                val widthColumn = cursor.getColumnIndex(MediaStore.Images.Media.WIDTH)
                val heightColumn = cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT)

                while (cursor.moveToNext()) {
                    if (idColumn == -1) continue
                    val id = cursor.getLong(idColumn)
                    val name = if (nameColumn != -1) cursor.getString(nameColumn) ?: "Fotoğraf_$id" else "Fotoğraf_$id"
                    val size = if (sizeColumn != -1) cursor.getLong(sizeColumn) else 0L
                    val dateAdded = if (dateColumn != -1) cursor.getLong(dateColumn) else System.currentTimeMillis() / 1000
                    val bucket = if (bucketColumn != -1) cursor.getString(bucketColumn) ?: "" else ""
                    val width = if (widthColumn != -1) cursor.getInt(widthColumn) else 0
                    val height = if (heightColumn != -1) cursor.getInt(heightColumn) else 0

                    val uri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    mediaList.add(
                        PhotoItem(
                            id = id,
                            uri = uri,
                            displayName = name,
                            size = size,
                            dateAdded = dateAdded,
                            bucketName = bucket,
                            width = width,
                            height = height,
                            isVideo = false,
                            durationMs = 0L
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 2. Fetch Videos Safely
        try {
            val videoProjection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.WIDTH,
                MediaStore.Video.Media.HEIGHT,
                MediaStore.Video.Media.DURATION
            )

            context.contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                videoProjection,
                null,
                null,
                "${MediaStore.Video.Media.DATE_ADDED} DESC"
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndex(MediaStore.Video.Media._ID)
                val nameColumn = cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)
                val sizeColumn = cursor.getColumnIndex(MediaStore.Video.Media.SIZE)
                val dateColumn = cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED)
                val bucketColumn = cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
                val widthColumn = cursor.getColumnIndex(MediaStore.Video.Media.WIDTH)
                val heightColumn = cursor.getColumnIndex(MediaStore.Video.Media.HEIGHT)
                val durationColumn = cursor.getColumnIndex(MediaStore.Video.Media.DURATION)

                while (cursor.moveToNext()) {
                    if (idColumn == -1) continue
                    val id = cursor.getLong(idColumn)
                    val name = if (nameColumn != -1) cursor.getString(nameColumn) ?: "Video_$id" else "Video_$id"
                    val size = if (sizeColumn != -1) cursor.getLong(sizeColumn) else 0L
                    val dateAdded = if (dateColumn != -1) cursor.getLong(dateColumn) else System.currentTimeMillis() / 1000
                    val bucket = if (bucketColumn != -1) cursor.getString(bucketColumn) ?: "" else ""
                    val width = if (widthColumn != -1) cursor.getInt(widthColumn) else 0
                    val height = if (heightColumn != -1) cursor.getInt(heightColumn) else 0
                    val duration = if (durationColumn != -1) cursor.getLong(durationColumn) else 0L

                    val uri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    mediaList.add(
                        PhotoItem(
                            id = id,
                            uri = uri,
                            displayName = name,
                            size = size,
                            dateAdded = dateAdded,
                            bucketName = bucket,
                            width = width,
                            height = height,
                            isVideo = true,
                            durationMs = duration
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // If no device media was found (e.g. empty emulator, restricted photo picker, empty gallery),
        // provide high quality sample photos so the user can immediately test and enjoy the app!
        if (mediaList.isEmpty()) {
            getSamplePhotos()
        } else {
            mediaList.sortedByDescending { it.dateAdded }
        }
    }

    /**
     * High quality sample photos so that the deck is never empty if the phone has 0 photos.
     */
    fun getSamplePhotos(): List<PhotoItem> {
        val now = System.currentTimeMillis() / 1000
        return listOf(
            PhotoItem(
                id = 900001L,
                uri = Uri.parse("https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?w=800&q=80"),
                displayName = "Sanat_Sergisi_Ornek.jpg",
                size = 4_850_000L,
                dateAdded = now - 3600,
                bucketName = "Örnek Galeri",
                width = 2400,
                height = 1600,
                isVideo = false
            ),
            PhotoItem(
                id = 900002L,
                uri = Uri.parse("https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800&q=80"),
                displayName = "Doga_Manzarasi.jpg",
                size = 6_200_000L,
                dateAdded = now - 7200,
                bucketName = "Örnek Galeri",
                width = 3840,
                height = 2160,
                isVideo = false
            ),
            PhotoItem(
                id = 900003L,
                uri = Uri.parse("https://images.unsplash.com/photo-1518770660439-4636190af475?w=800&q=80"),
                displayName = "Screenshot_2026_Sistem.png",
                size = 1_450_000L,
                dateAdded = now - 10800,
                bucketName = "Screenshots",
                width = 1080,
                height = 2400,
                isVideo = false
            ),
            PhotoItem(
                id = 900004L,
                uri = Uri.parse("https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?w=800&q=80"),
                displayName = "Sisli_Daglari_Yuruyus.jpg",
                size = 3_800_000L,
                dateAdded = now - 14400,
                bucketName = "Örnek Galeri",
                width = 2048,
                height = 1365,
                isVideo = false
            ),
            PhotoItem(
                id = 900005L,
                uri = Uri.parse("https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=800&q=80"),
                displayName = "Gunesli_Orman_Patikasi.jpg",
                size = 5_400_000L,
                dateAdded = now - 18000,
                bucketName = "Örnek Galeri",
                width = 3000,
                height = 2000,
                isVideo = false
            ),
            PhotoItem(
                id = 900006L,
                uri = Uri.parse("https://images.unsplash.com/photo-1517841905240-472988babdf9?w=800&q=80"),
                displayName = "Portre_Stil_Foto.jpg",
                size = 2_950_000L,
                dateAdded = now - 21600,
                bucketName = "Örnek Galeri",
                width = 1920,
                height = 1280,
                isVideo = false
            )
        )
    }

    /**
     * Prepares an IntentSender to request deletion permission from user on Android 10+ (API 29/30+)
     */
    fun createDeleteIntentSender(uris: List<Uri>): IntentSender? {
        val realUris = uris.filter { it.scheme == "content" && it.authority?.startsWith("media") == true }
        if (realUris.isEmpty()) return null

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                MediaStore.createDeleteRequest(context.contentResolver, realUris).intentSender
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else {
            null
        }
    }

    /**
     * Direct delete fallback for Android 9 or below
     */
    suspend fun directDeletePhotos(uris: List<Uri>): Int = withContext(Dispatchers.IO) {
        var deletedCount = 0
        val realUris = uris.filter { it.scheme == "content" && it.authority?.startsWith("media") == true }
        for (uri in realUris) {
            try {
                val rows = context.contentResolver.delete(uri, null, null)
                if (rows > 0) deletedCount++
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        deletedCount
    }
}
