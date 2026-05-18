package com.uilover.project261.screens.dashboard

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.uilover.project261.R

@Composable
@Preview
fun MyBottomBar(
    onHomeClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onFavoriteClick: () -> Unit = {},
    onOrderClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    currentRoute: String = "Home"
) {
    val bottomMenuItemsList = prepareBottomMenu()
    var selectedItem by remember { mutableStateOf(currentRoute) }

    BottomAppBar(
        backgroundColor = Color.White,
        elevation = 8.dp,
        contentColor = colorResource(R.color.primary_pink),
        modifier = Modifier
            .height(72.dp) // Increased from default to be more visible
    ) {
        bottomMenuItemsList.forEach { bottomMenuItem ->
            BottomNavigationItem(
                selected = (selectedItem == bottomMenuItem.lable),
                onClick = {
                    selectedItem = bottomMenuItem.lable
                    when (bottomMenuItem.lable) {
                        "Home" -> onHomeClick()
                        "Cart" -> onCartClick()
                        "Favorite" -> onFavoriteClick()
                        "Order" -> onOrderClick()
                        "Profile" -> onProfileClick()
                    }
                },
                selectedContentColor = colorResource(R.color.primary_pink),
                unselectedContentColor = colorResource(R.color.text_secondary),
                icon = {
                    Icon(
                        painter = bottomMenuItem.icon,
                        contentDescription = bottomMenuItem.lable,
                        modifier = Modifier
                            .padding(vertical = 12.dp) // Increased padding
                            .size(26.dp), // Slightly larger icons
                        tint = if (selectedItem == bottomMenuItem.lable)
                            colorResource(R.color.primary_pink)
                        else
                            colorResource(R.color.text_secondary)
                    )
                }
            )
        }
    }

}

data class BottomMenuItem(
    val lable: String, val icon: Painter
)

@Composable
fun prepareBottomMenu(): List<BottomMenuItem> {
    return listOf(
        BottomMenuItem(lable = "Home", icon = painterResource(R.drawable.btn_1)),
        BottomMenuItem(lable = "Cart", icon = painterResource(R.drawable.btn_2)),
        BottomMenuItem(lable = "Favorite", icon = painterResource(R.drawable.btn_3)),
        BottomMenuItem(lable = "Order", icon = painterResource(R.drawable.btn_4)),
        BottomMenuItem(lable = "Profile", icon = painterResource(R.drawable.btn_5)),
    )
}