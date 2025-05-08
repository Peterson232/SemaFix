package com.example.semafix.models

data class User(
    val uid: String = "", // Unique user ID
    val name: String = "",
    val username: String = "",
    val phoneNumber: String = "",
    val county: String = "",
    val constituency: String = "", // You can change to 'district' if needed
    val profileImageUrl: String = "",
    val about: String = ""
)
