package com.uilover.project261.screens.detailProduct

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uilover.project261.R

@Composable
fun DescriptionSection(description: String) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = "Chi tiết",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.text_primary),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Text(
            text = description,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = colorResource(R.color.text_secondary),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        Text(
            text = "🎁 Mua 2 sản phẩm miễn phí giao hàng",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorResource(R.color.primary_pink),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}