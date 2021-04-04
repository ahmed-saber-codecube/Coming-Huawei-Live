package com.coming.customer.util


import android.annotation.SuppressLint
import android.widget.ImageView
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.coming.customer.R
import com.coming.customer.di.module.GlideApp
import com.evente.customer.utils.Utils
import java.io.File
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*


fun AppCompatImageView.loadFile(url: String) {

    GlideApp.with(this).load(File(url)).into(this)

}

fun AppCompatImageView.loadFile(url: String, @DrawableRes placeHolder: Int = R.drawable.sign_up_avatar, width: Int, height: Int) {
    if (placeHolder == 0)

        GlideApp.with(this)
            .load(File(url))
            .override(Utils.dpToPx(width), Utils.dpToPx(height))
            .centerCrop()
            .into(this)
    else

        GlideApp.with(this)
            .load(File(url))
            .placeholder(placeHolder)
            .override(Utils.dpToPx(width), Utils.dpToPx(height))
            .centerCrop()
            .into(this)
}

fun AppCompatImageView.loadFileRoundedCorner(url: String, radius: Int) {
    var requestOptions = RequestOptions()

    requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(radius))

    GlideApp.with(this)
        .load(File(url))
        .centerCrop()
        .apply(requestOptions)
        .into(this)

}
/*fun AppCompatImageView.loadUrl(url: String, @DrawableRes placeHolder: Int, width: Int, height: Int) {

    GlideApp.with(this)
            .load(url)
            .override(width, height)
            .centerCrop()
            .placeholder(drawable)
            .into(this)
}*/

fun AppCompatImageView.loadUrl(url: String, @DrawableRes placeHolder: Int = R.drawable.sign_up_avatar, width: Int, height: Int) {

    if (placeHolder == 0)

        GlideApp.with(this)
            .load(url)
            .override(Utils.dpToPx(width), Utils.dpToPx(height))
            .centerCrop()
            .into(this)
    else

        GlideApp.with(this)
            .load(url)
            .override(Utils.dpToPx(width), Utils.dpToPx(height))
            .centerCrop()
            .placeholder(placeHolder)
            .into(this)
}

fun AppCompatImageView.loadUrl(url: String, @DrawableRes placeHolder: Int = R.drawable.sign_up_avatar) {

    if (placeHolder == 0) {

        GlideApp.with(this)
            .load(url)
            .transition(withCrossFade())
            .centerCrop()
            .into(this)

    } else {

        GlideApp.with(this)
            .load(url)
            .placeholder(placeHolder)
            .centerCrop()
            .into(this)

    }
}


fun AppCompatImageView.loadUrlStore(url: String, @DrawableRes placeHolder: Int = R.mipmap.ic_launcher) {

    if (placeHolder == 0) {

        GlideApp.with(this)
            .load(url)
            .centerCrop()
            .into(this)

    } else {

        GlideApp.with(this)
            .load(url)
            .placeholder(placeHolder)
            .centerCrop()
            .into(this)

    }
}

fun AppCompatImageView.loadUrlWithActualSize(url: String, @DrawableRes placeHolder: Int) {

    if (placeHolder == 0) {

        GlideApp.with(this)
            .load(url)
            .transition(withCrossFade())
            .centerCrop()
            .into(this)

    } else {

        GlideApp.with(this)
            .load(url)
            .transition(withCrossFade())
            .placeholder(placeHolder)
            .centerCrop()
            .override(Target.SIZE_ORIGINAL)
            .into(this)

    }
}

fun AppCompatImageView.loadUrlWithActualSizeNoCenterCrop(url: String, @DrawableRes placeHolder: Int = R.drawable.sign_up_avatar) {

    if (placeHolder == 0) {

        GlideApp.with(this)
            .load(url)
            .into(this)

    } else {

        GlideApp.with(this)
            .load(url)
            .placeholder(placeHolder)
            .override(Target.SIZE_ORIGINAL)
            .into(this)

    }
}

fun AppCompatImageView.loadDrawable(@DrawableRes drawableRes: Int) {
    GlideApp.with(this).load(drawableRes).into(this)
}

fun AppCompatImageView.loadDrawable(@DrawableRes drawableRes: Int, width: Int, height: Int) {

    GlideApp.with(this)
        .load(drawableRes)
        .override(Utils.dpToPx(width), Utils.dpToPx(height))
        .centerCrop()
        .into(this)
}

fun AppCompatImageView.loadCenterCrop(url: String, @DrawableRes placeHolder: Int = R.drawable.sign_up_avatar) {

    if (placeHolder == 0)

        GlideApp.with(this)
            .load(url)
            .centerCrop()
            .into(this)
    else

        GlideApp.with(this)
            .load(url)
            .centerCrop()
            .placeholder(placeHolder)
            .into(this)
//                .override(Utils.dpToPx(width), Utils.dpToPx(height))

}

fun AppCompatImageView.loadFileCircle(url: String, @DrawableRes placeHolder: Int = R.drawable.sign_up_avatar, @DimenRes width: Int = 0, @DimenRes height: Int = 0) {

    GlideApp.with(this)
        .load(File(url))
        .apply(RequestOptions().circleCrop())
        .into(this)
//            .override(Utils.dpToPx(width), Utils.dpToPx(height))
//            .centerCrop()
}

fun AppCompatImageView.loadDrawableCircle(@SuppressLint("SupportAnnotationUsage") @DrawableRes drawableRes: String?, @DrawableRes placeHolder: Int = R.drawable.sign_up_avatar, @DimenRes width: Int = 0, @DimenRes height: Int = 0) {
    GlideApp.with(this)
        .load(drawableRes)
        .placeholder(placeHolder)
        .transition(withCrossFade())
        .apply(RequestOptions.circleCropTransform())
        .into(this)
//            .override(Utils.dpToPx(width), Utils.dpToPx(height))

}


fun AppCompatImageView.loadDrawableRoundedCorner(@DrawableRes drawableRes: Int) {

    var requestOptions = RequestOptions()

    requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(20))

    GlideApp.with(this)
        .load(drawableRes)
        .apply(requestOptions)
        .into(this)

}

fun AppCompatImageView.loadDrawableRoundedCorner(@DrawableRes drawableRes: Int, radius: Int) {

    var requestOptions = RequestOptions()

    requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(radius))

    GlideApp.with(this)
        .load(drawableRes)
        .apply(requestOptions)
        .into(this)

}

fun ImageView.loadUrlRoundedCorner(url: String, @DrawableRes placeHolder: Int = R.drawable.sign_up_avatar, radius: Int) {

    var requestOptions = RequestOptions()

    requestOptions = requestOptions.transforms(RoundedCorners(radius))

    GlideApp.with(this)
        .load(url)
        .transition(withCrossFade())
        .placeholder(placeHolder)
        .apply(requestOptions)
        .into(this)

}

fun String.twoDecimal(): String {
    val nf = NumberFormat.getNumberInstance(Locale.US)
    val df = nf as DecimalFormat
    return if (this.isNotEmpty()) {
        df.applyPattern("0.00")
        df.format(this.toFloat()).toString()
    } else {
        ""
    }
}

fun String.oneDecimal(): String {
    val nf = NumberFormat.getNumberInstance(Locale.US)
    val df = nf as DecimalFormat
    df.applyPattern("0.0")
    return df.format(this.toDouble()).toString()
}

/*

fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}
*/

