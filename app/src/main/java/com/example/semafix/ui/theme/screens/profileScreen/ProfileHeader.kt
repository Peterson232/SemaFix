package com.example.semafix.ui.theme.screens.profileScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter

@Composable
fun ProfileHeader(
    name: String,
    username: String,
    phone: String,
    location: String,
    about: String,
    profileImageUrl: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(profileImageUrl),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(80.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text("@$username", style = MaterialTheme.typography.bodySmall)
            Text(phone, style = MaterialTheme.typography.bodySmall)
            Text(location, style = MaterialTheme.typography.bodySmall)
            Text(about, style = MaterialTheme.typography.bodySmall)
        }
    }
}
