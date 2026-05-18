package com.uilover.project261.screens.detailProduct

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uilover.project261.R


@Composable
@Preview
fun FooterSection(
    onAddToCartClick: () -> Unit = {},
    onBuyNowClick: () -> Unit = {},
    totalPrice: Double = 24.99,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        // Add to Cart Button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
                .clickable { onAddToCartClick() }
                .background(
                    color = Color.White,
                    RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 16.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.cart),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Thêm vào giỏ",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorResource(R.color.primary_pink)
            )
        }

        // Buy Now Button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
                .clickable { onBuyNowClick() }
                .background(
                    color = colorResource(R.color.primary_pink),
                    RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 16.dp)
        ) {
            Text(
                "Thanh toán ngay",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}