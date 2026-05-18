package com.uilover.project261.screens.dashboard


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uilover.project261.R
import com.uilover.project261.viewModel.AuthViewModel

@Composable
fun TopBar(
    onSearchClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    authViewModel: AuthViewModel? = null
) {
    val currentUser = authViewModel?.currentUser?.collectAsState()?.value
    val isLoggedIn = authViewModel?.isLoggedIn() ?: false

    Row(
        modifier = Modifier
            .padding(top = 48.dp, bottom = 16.dp)
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // User Avatar (only visible when logged in)
        if (isLoggedIn && currentUser != null) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(colorResource(R.color.primary_pink).copy(alpha = 0.2f))
                    .clickable { onProfileClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currentUser.name.firstOrNull()?.uppercase() ?: "U",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.primary_pink)
                )
            }
        }

        var text by rememberSaveable { mutableStateOf("") }
        TextField(
            value = text,
            onValueChange = { text = it },
            label = {
                Text(
                    text = "Tìm kiếm sản phẩm",
                    fontSize = 13.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Normal,
                    color = colorResource(R.color.text_hint)
                )
            },
            trailingIcon = {
                Image(
                    painter = painterResource(R.drawable.search),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            },
            shape = RoundedCornerShape(25.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = Color.White,
                focusedBorderColor = colorResource(R.color.primary_pink),
                unfocusedBorderColor = colorResource(R.color.border_color),
                textColor = colorResource(R.color.text_primary),
                unfocusedLabelColor = colorResource(R.color.text_hint),
                cursorColor = colorResource(R.color.primary_pink)
            ),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
                .height(50.dp)
                .clickable { onSearchClick() },
            readOnly = true,
            enabled = false
        )

        // Cart Icon (bigger size to balance with search bar)
        Image(
            painter = painterResource(R.drawable.cart),
            contentDescription = "Giỏ hàng",
            modifier = Modifier
                .size(40.dp)
                .clickable { onCartClick() }
        )
    }
}

@Preview
@Composable
fun TopBarPreview() {
    TopBar()
}
