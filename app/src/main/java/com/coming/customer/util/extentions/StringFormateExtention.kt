package com.coming.merchant.util.extentions


import android.text.Editable
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import java.text.DecimalFormat
import java.util.*

fun AppCompatTextView.format(pattern: String, vararg values: Any) {
    this.text = String.format(Locale.ENGLISH, pattern, *values)
}

fun AppCompatEditText.format(string: String) {
    this.text = Editable.Factory.getInstance().newEditable(string)
}

fun TextView.format(pattern: String, vararg values: Any) {
    this.text = String.format(Locale.ENGLISH, pattern, *values)
}

fun String.formatCurrency(): String {
    return try {
        val formatter = DecimalFormat("###,###,##0.00")
        formatter.format(java.lang.Double.parseDouble(this))
    } catch (e: Exception) {
        this
    }

}

fun Double.formatCurrency(): String {
    return try {
        val formatter = DecimalFormat("###,###,##0.00")
        formatter.format(this)
    } catch (e: Exception) {
        this.toString()
    }
}

fun Float.formatCurrency(): String {
    return try {
        val formatter = DecimalFormat("###,###,##0.00")
        formatter.format(this)
    } catch (e: Exception) {
        this.toString()
    }
}