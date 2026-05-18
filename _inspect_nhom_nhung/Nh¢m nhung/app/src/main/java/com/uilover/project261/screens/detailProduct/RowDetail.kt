package com.uilover.project261.screens.detailProduct

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.uilover.project261.domain.ProductModel


@Composable

fun RowDetail(item: ProductModel, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .height(50.dp)
            .fillMaxWidth()
            .background(
                color = Color.White,
                RoundedCornerShape(12.dp)
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            item.categoryTitle,
            modifier = Modifier.padding(horizontal = 8.dp),
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = colorResource(R.color.text_primary)
        )

        Spacer(modifier = Modifier.width(12.dp))
        Image(painter = painterResource(R.drawable.star), contentDescription = null)
        Text(
            "${item.rated}",
            modifier = Modifier.padding(start = 4.dp),
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = colorResource(R.color.orange)
        )

        Spacer(modifier = Modifier.width(12.dp))
        Text(
            if (item.capacity.isNotEmpty()) item.capacity else item.weight,
            modifier = Modifier.padding(horizontal = 8.dp),
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = colorResource(R.color.text_secondary)
        )

    }
}

@Preview
@Composable
fun RowDetailPreview() {
    val item = ProductModel(
        id = "dior_lipstick_999",
        title = "Son Dior Rouge 999",
        categoryId = "dior",
        categoryTitle = "Dior",
        price = 1150000.0,
        rated = 4.9,
        weight = "3.5g"
    )
    RowDetail(item)
}


