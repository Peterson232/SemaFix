package com.example.semafix.navigation

sealed class Routes(val route: String) {
    object Login : Routes("login")
    object Dashboard : Routes("dashboard")
    object Register : Routes("register")
    object Splash : Routes("splash")
}
const val ROUTE_LOGIN = "login"
const val ROUTE_REGISTER = "register"
const val ROUTE_DASHBOARD = "dashboard"
const val ROUTE_SPLASH = "splash"

