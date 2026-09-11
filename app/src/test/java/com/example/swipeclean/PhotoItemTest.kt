package com.example.swipeclean

import android.net.Uri
import com.example.swipeclean.data.PhotoItem
import org.junit.Assert.assertEquals
import org.junit.Test

class PhotoItemTest {

    @Test
    fun testFormatSizeZero() {
        assertEquals("0 B", PhotoItem.formatSize(0))
    }

    @Test
    fun testFormatSizeKilobytes() {
        assertEquals("500 KB", PhotoItem.formatSize(500 * 1024))
    }

    @Test
    fun testFormatSizeMegabytes() {
        assertEquals("4.5 MB", PhotoItem.formatSize((4.5 * 1024 * 1024).toLong()))
    }

    @Test
    fun testFormatSizeGigabytes() {
        assertEquals("1.25 GB", PhotoItem.formatSize((1.25 * 1024 * 1024 * 1024).toLong()))
    }
}
