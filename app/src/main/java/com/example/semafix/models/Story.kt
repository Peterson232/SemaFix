package com.example.semafix.models

data class Story(
    val id : String = "",
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val userId: String = "",
    val location: String = "",
    val status: String = "Pending",
    val date: String = "",
    val resolved: Boolean = false,
    val likesCount: Int = 0,  // Add a likes count
    val likedByUser: Boolean = false,
    val likedBy: List<String>? = (emptyList()),
    val likes: Int = 0
)

