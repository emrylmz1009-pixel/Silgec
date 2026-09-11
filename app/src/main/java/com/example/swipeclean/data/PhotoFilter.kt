package com.example.swipeclean.data

enum class PhotoFilter(val title: String, val badge: String) {
    ALL("Tüm Medyalar", "Hepsi"),
    SCREENSHOTS("Ekran Görüntüleri", "Ekran"),
    LARGE("Büyük Dosyalar (>3 MB)", ">3 MB"),
    VIDEOS("Videolar", "Video")
}
