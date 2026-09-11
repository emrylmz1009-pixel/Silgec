package com.example.swipeclean.data

import android.net.Uri
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

data class PhotoItem(
    val id: Long,
    val uri: Uri,
    val displayName: String,
    val size: Long,
    val dateAdded: Long,
    val bucketName: String = "",
    val width: Int = 0,
    val height: Int = 0,
    val isVideo: Boolean = false,
    val durationMs: Long = 0L
) {
    val formattedSize: String
        get() = formatSize(size)

    val isLarge: Boolean
        get() = size >= 3L * 1024L * 1024L // >= 3 MB

    val isScreenshot: Boolean
        get() {
            if (isVideo) return false
            val lowerName = displayName.lowercase(Locale.ROOT)
            val lowerBucket = bucketName.lowercase(Locale.ROOT)
            return lowerName.contains("screenshot") || lowerName.contains("ekran") ||
                    lowerBucket.contains("screenshot") || lowerBucket.contains("ekran")
        }

    val resolutionString: String
        get() {
            if (width > 0 && height > 0) {
                val mp = (width.toLong() * height.toLong()) / 1_000_000.0
                return if (mp >= 1.0) String.format(Locale.US, "%.1f MP", mp) else "${width}x${height}"
            }
            return ""
        }

    val formattedDuration: String
        get() {
            if (durationMs <= 0) return "00:00"
            val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs)
            val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) % 60
            val hours = TimeUnit.MILLISECONDS.toHours(durationMs)
            return if (hours > 0) {
                String.format(Locale.US, "%d:%02d:%02d", hours, minutes % 60, seconds)
            } else {
                String.format(Locale.US, "%02d:%02d", minutes, seconds)
            }
        }

    val formattedDate: String
        get() {
            return try {
                val sdf = SimpleDateFormat("dd MMM yyyy", Locale.forLanguageTag("tr-TR"))
                sdf.format(Date(dateAdded * 1000L))
            } catch (e: Exception) {
                ""
            }
        }

    companion object {
        fun formatSize(bytes: Long): String {
            if (bytes <= 0) return "0 B"
            val kb = bytes / 1024.0
            val mb = kb / 1024.0
            val gb = mb / 1024.0
            return when {
                gb >= 1.0 -> String.format(Locale.US, "%.2f GB", gb)
                mb >= 1.0 -> String.format(Locale.US, "%.1f MB", mb)
                else -> String.format(Locale.US, "%.0f KB", kb)
            }
        }
    }
}
