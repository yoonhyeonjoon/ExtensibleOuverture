package com.vlm.extensibleouverture.ui.user

enum class LoginFront(
    val title: String, //@StringRes val title: Int,
    val route: String
) {
    FRONT("login", "login/main"),
    SIGN_UP("login", "login/signup"),
}