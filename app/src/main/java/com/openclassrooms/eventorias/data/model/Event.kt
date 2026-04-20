package com.openclassrooms.eventorias.data.model

data class Event(
    val id: String = "",
    val title: String = "",
    val date: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val time: String = "",
    val authorPhotoUrl: String = "",
    val category: String = "",
    val location: String = ""
)