package com.coming.customer.util.extentions

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes

fun Context.getColorFromResource(@ColorRes colorInt: Int): Int {

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        this.resources.getColor(colorInt, theme)
    else
        this.resources.getColor(colorInt)
}

fun Context.getDrawable(@DrawableRes drawable: Int): Drawable {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        this.resources.getDrawable(drawable, theme)
    else
        this.resources.getDrawable(drawable)
}
