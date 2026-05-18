package com.uilover.project261.screens.detailProduct

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
import com.google.accompanist.flowlayout.FlowRow
import com.uilover.project261.R

/**
 * Product Options Selector - Shopee/Lazada Style
 * Allows users to select capacity, weight, or color options
 */

@Composable
fun ProductOptionsSelector(
    availableCapacities: List<String>,
    availableWeights: List<String>,
    availableColors: List<String>,
    selectedCapacity: String,
    selectedWeight: String,
    selectedColor: String,
    onCapacitySelected: (String) -> Unit,
    onWeightSelected: (String) -> Unit,
    onColorSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Capacity options (for liquids - cleansers, sunscreens)
        if (availableCapacities.isNotEmpty()) {
            OptionSection(
                title = "Dung tích",
                options = availableCapacities,
                selectedOption = selectedCapacity,
                onOptionSelected = onCapacitySelected
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Weight options (for solid products - lipsticks)
        if (availableWeights.isNotEmpty()) {
            OptionSection(
                title = "Khối lượng",
                options = availableWeights,
                selectedOption = selectedWeight,
                onOptionSelected = onWeightSelected
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Color options (for makeup products)
        if (availableColors.isNotEmpty()) {
            OptionSection(
                title = "Màu sắc",
                options = availableColors,
                selectedOption = selectedColor,
                onOptionSelected = onColorSelected
            )
        }
    }
}

@Composable
fun OptionSection(
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Column {
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorResource(R.color.text_primary),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        FlowRow(
            mainAxisSpacing = 8.dp,
            crossAxisSpacing = 8.dp
        ) {
            options.forEach { option ->
                OptionChip(
                    text = option,
                    isSelected = option == selectedOption,
                    onClick = { onOptionSelected(option) }
                )
            }
        }
    }
}

@Composable
fun OptionChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
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
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected)
                colorResource(R.color.primary_pink)
            else
                colorResource(R.color.text_primary)
        )
    }
}

