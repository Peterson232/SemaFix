package com.example.semafix.navigation

sealed class Routes(val route: String) {
    object Login : Routes("login")
    object Dashboard : Routes("dashboard")
    object Register : Routes("register")
    object Splash : Routes("splash")
    object Settings : Routes("settings")
}
const val ROUTE_LOGIN = "login"
const val ROUTE_REGISTER = "register"
const val ROUTE_DASHBOARD = "dashboard"
const val ROUTE_SPLASH = "splash"
const val SETTINGS_SCREEN = "manage_account"


