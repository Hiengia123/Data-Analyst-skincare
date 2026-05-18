package com.uilover.project261.screens.ItemsList

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uilover.project261.R

/**
 * Price Filter for Product List - Shopee/Lazada Style
 */

data class PriceRange(
    val label: String,
    val minPrice: Double,
    val maxPrice: Double
)
@Composable
fun PriceFilterBar(
    selectedRange: PriceRange?,
    onRangeSelected: (PriceRange?) -> Unit
) {
    val priceRanges = listOf(
        PriceRange("Tất cả", 0.0, Double.MAX_VALUE),
        PriceRange("Dưới 1 triệu", 0.0, 1000000.0),
        PriceRange("1tr - 2tr", 1000000.0, 2000000.0),
        PriceRange("Trên 2 triệu", 2000000.0, Double.MAX_VALUE)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Khoảng giá",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorResource(R.color.text_primary),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            priceRanges.forEach { range ->
                PriceFilterChip(
                    range = range,
                    isSelected = selectedRange == range,
                    onClick = {
                        // Toggle selection - click again to deselect
                        if (selectedRange == range && range.label != "Tất cả") {
                            onRangeSelected(priceRanges[0]) // Reset to "Tất cả"
                        } else {
                            onRangeSelected(range)
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun PriceFilterChip(
    range: PriceRange,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(36.dp)
            .clickable { onClick() }
            .background(
                color = if (isSelected)
                    colorResource(R.color.primary_pink).copy(alpha = 0.1f)
                else
                    Color.White,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 1.5.dp,
                color = if (isSelected)
                    colorResource(R.color.primary_pink)
                else
                    colorResource(R.color.border_color),
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = range.label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected)
                colorResource(R.color.primary_pink)
            else
                colorResource(R.color.text_primary)
        )
    }
}

