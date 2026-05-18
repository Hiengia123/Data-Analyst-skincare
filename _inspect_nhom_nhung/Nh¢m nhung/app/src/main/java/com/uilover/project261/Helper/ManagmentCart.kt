package com.uilover.project261.Helper

import android.content.Context
import android.widget.Toast
import com.uilover.project261.domain.ProductModel

import kotlin.collections.indexOfFirst


class ManagmentCart(val context: Context) {

    private val tinyDB = TinyDB(context)

    fun insertItem(item: ProductModel) {
        var listProduct = getListCart()

        // Ensure the item has at least quantity 1
        if (item.numberInCart <= 0) {
            item.numberInCart = 1
        }

        // Check if exact same item with same variants exists
        val existingIndex = listProduct.indexOfFirst {
            it.title == item.title &&
            it.selectedCapacity == item.selectedCapacity &&
            it.selectedWeight == item.selectedWeight &&
            it.selectedColor == item.selectedColor
        }

        if (existingIndex != -1) {
            // Same product with same options - increase quantity
            listProduct[existingIndex].numberInCart += item.numberInCart
        } else {
            // New product or different options - add as new item
            listProduct.add(item)
        }

        tinyDB.putListObject("CartList", listProduct)
        Toast.makeText(context, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show()
    }

    fun getListCart(): ArrayList<ProductModel> {
        return tinyDB.getListObject("CartList") ?: arrayListOf()
    }

    fun minusItem(listProduct: ArrayList<ProductModel>, position: Int, listener: ChangeNumberItemsListener) {
        if (position < 0 || position >= listProduct.size) return

        val currentCount = listProduct[position].numberInCart
        if (currentCount <= 1) {
            // Remove item from cart
            listProduct.removeAt(position)
            tinyDB.putListObject("CartList", listProduct)
            Toast.makeText(context, "Đã xóa sản phẩm khỏi giỏ hàng", Toast.LENGTH_SHORT).show()
        } else {
            // Decrease quantity
            listProduct[position].numberInCart = currentCount - 1
            tinyDB.putListObject("CartList", listProduct)
        }
        listener.onChanged()
    }

    fun plusItem(listProduct: ArrayList<ProductModel>, position: Int, listener: ChangeNumberItemsListener) {
        listProduct[position].numberInCart++
        tinyDB.putListObject("CartList", listProduct)
        listener.onChanged()
    }

    fun getTotalFee(): Double {
        val listProduct = getListCart()
        var fee = 0.0
        for (item in listProduct) {
            fee += item.price * item.numberInCart
        }
        return fee
    }

    fun clearCart() {
        tinyDB.putListObject("CartList", arrayListOf<ProductModel>())
        Toast.makeText(context, "Giỏ hàng đã được xóa", Toast.LENGTH_SHORT).show()
    }
}