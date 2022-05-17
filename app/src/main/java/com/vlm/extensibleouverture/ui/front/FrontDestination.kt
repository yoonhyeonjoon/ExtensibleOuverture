package com.vlm.extensibleouverture.ui.front

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import com.vlm.extensibleouverture.R


enum class FrontDestination(
    val title: String,
    val route: String
) {
    FRONT("front", "front/main"),
}

enum class FrontSections(
    @StringRes val title: Int,
    val sectionName: String,
    val icon: ImageVector,
    val route: String
) {
    GATHERING(R.string.home_gathering, "gathering", Icons.Outlined.Search, "front/gathering"),
    WRITING(R.string.home_writing, "writing", Icons.Outlined.Search, "front/writing"),
    CHATTING(R.string.home_chatting, "chatting", Icons.Outlined.Home, "front/chatting"),
    HISTORY(R.string.home_history, "history", Icons.Outlined.ShoppingCart, "front/history"),
    PROFILE(R.string.home_profile,"profile",  Icons.Outlined.AccountCircle, "front/profile")
}

