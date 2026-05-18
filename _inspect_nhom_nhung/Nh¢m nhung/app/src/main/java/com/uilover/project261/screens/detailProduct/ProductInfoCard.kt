package com.uilover.project261.screens.detailProduct

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uilover.project261.Helper.CurrencyFormatter
import com.uilover.project261.R
import com.uilover.project261.domain.ProductModel

@Composable
fun ProductInfoCard(
    item: ProductModel,
    numberInCart: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Price Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = CurrencyFormatter.formatVND(item.price),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.primary_pink)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = CurrencyFormatter.formatVND(item.price * 1.3),
                    fontSize = 16.sp,
                    color = colorResource(R.color.text_secondary),
                    textDecoration = TextDecoration.LineThrough,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Product Title
            Text(
                text = item.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorResource(R.color.text_primary),
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Rating and Sold Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rating
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.star),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${item.rated}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = colorResource(R.color.orange)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Category
                Text(
                    text = item.categoryTitle,
                    fontSize = 14.sp,
                    color = colorResource(R.color.text_secondary)
                )

                Spacer(modifier = Modifier.weight(1f))

                // Size/Weight
                Text(
                    text = if (item.capacity.isNotEmpty()) item.capacity else item.weight,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorResource(R.color.text_primary)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = colorResource(R.color.divider_color))
            Spacer(modifier = Modifier.height(16.dp))

            // Quantity Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Số lượng",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorResource(R.color.text_primary)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            color = colorResource(R.color.light_pink),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(4.dp)
                ) {
                    // Minus Button
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = if (numberInCart > 1) colorResource(R.color.primary_pink) else colorResource(R.color.border_color),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable { if (numberInCart > 1) onDecrement() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "−",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (numberInCart > 1) Color.White else colorResource(R.color.text_hint)
                        )
                    }

                    // Number
                    Text(
                        text = "$numberInCart",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colorResource(R.color.text_primary),
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )

                    // Plus Button
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = colorResource(R.color.primary_pink),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable { onIncrement() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Total Price Section
            if (numberInCart > 1) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = colorResource(R.color.divider_color))
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Tổng",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = colorResource(R.color.text_primary)
                    )

                    Text(
                        text = CurrencyFormatter.formatVND(item.price * numberInCart),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.primary_pink)
                    )
                }
            }
        }
    }
}

