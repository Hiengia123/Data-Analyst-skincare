package com.uilover.project261.screens.ItemsList

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.uilover.project261.Helper.CurrencyFormatter
import com.uilover.project261.R
import com.uilover.project261.domain.ProductModel

@Composable
fun ItemsList(
    items: List<ProductModel>,
    onItemClick: (ProductModel) -> Unit,
    onAddToCart: (ProductModel) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        itemsIndexed(items) { _, item ->
            Items(
                item = item,
                onItemClick = { onItemClick(item) },
                onAddToCart = { onAddToCart(item) }
            )
        }
    }
}

@Preview
@Composable
fun ItemsListPreview() {
    val items = listOf(
        ProductModel(title = "Face Cream", price = 1250000.0, rated = 4.5, capacity = "50ml"),
        ProductModel(title = "Lipstick", price = 800000.0, rated = 4.2, weight = "3.5g"),
        ProductModel(title = "Mascara", price = 720000.0, rated = 4.8, capacity = "30ml")
    )
    ItemsList(items = items, onItemClick = {})
}

@Composable
fun Items(
    item: ProductModel,
    onItemClick: () -> Unit,
    onAddToCart: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .clickable { onItemClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(12.dp)
        ) {
            ProductImage(item = item)
            ProductDetail(
                item = item,
                onAddToCart = onAddToCart
            )
        }
    }
}

@Composable
fun RowScope.ProductDetail(
    item: ProductModel,
    onAddToCart: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .padding(start = 12.dp)
            .fillMaxWidth()
            .weight(1f)
    ) {
        Text(
            text = item.title,
            color = colorResource(R.color.text_primary),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 20.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        SizeRow(if (item.capacity.isNotEmpty()) item.capacity else item.weight)
        RatingBarRow(item.rated)
        PriceRow(
            price = item.price,
            onAddToCart = onAddToCart
        )
    }
}

@Composable
fun PriceRow(
    price: Double,
    onAddToCart: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 8.dp)
    ) {
        Text(
            text = CurrencyFormatter.formatVND(price),
            color = colorResource(R.color.primary_pink),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "+ Thêm",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .clickable { onAddToCart() }
                .background(
                    color = colorResource(R.color.primary_pink),
                    shape = RoundedCornerShape(50.dp)
                )
                .padding(horizontal = 16.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun RatingBarRow(star: Double) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 6.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.star),
            contentDescription = null,
            modifier = Modifier
                .size(16.dp)
                .padding(end = 4.dp)
        )
        Text(
            text = "$star",
            color = colorResource(R.color.orange),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun SizeRow(size: String) {
    Row(
        modifier = Modifier.padding(top = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Size: $size",
            color = colorResource(R.color.text_secondary),
            style = MaterialTheme.typography.bodySmall,
            fontSize = 13.sp
        )
    }
}

@Composable
fun ProductImage(item: ProductModel) {
    AsyncImage(
        model = item.image,
        contentDescription = null,
        modifier = Modifier
            .size(110.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                colorResource(R.color.light_pink),
                shape = RoundedCornerShape(8.dp)
            ),
        contentScale = ContentScale.Crop
    )
}