package com.uilover.project261.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")

    data object Search : Screen("search")

    data object Cart : Screen("cart")

    data object Items : Screen("itemsList/{id}/{title}") {
        fun path(id: String, title: String) =
            "itemsList/$id/${
                java.net.URLEncoder
                    .encode(title, Charsets.UTF_8.name())
            }"
    }

    data object Detail : Screen("detail")

    data object Login : Screen("login")

    data object Register : Screen("register")

    data object Profile : Screen("profile")

    data object Checkout : Screen("checkout")

    data object OrderHistory : Screen("orderHistory")

    data object OrderDetail : Screen("orderDetail")

    data object Favorites : Screen("favorites")
}