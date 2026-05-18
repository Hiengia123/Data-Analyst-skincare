package com.uilover.project261.Helper

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object CurrencyFormatter {
    /**
     * Format a price value to Vietnamese Dong format with thousand separators
     * Example: 1000000 → "1.000.000 đ"
     */
    fun formatVND(price: Double): String {
        val symbols = DecimalFormatSymbols.getInstance(Locale("vi", "VN"))
        symbols.groupingSeparator = '.'
        symbols.decimalSeparator = ','

        val formatter = DecimalFormat("#,###", symbols)
        return "${formatter.format(price.toInt())} đ"
    }
}

