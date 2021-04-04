package com.coming.customer.di

import android.content.Context
import com.coming.customer.core.AppCommon
import com.coming.customer.core.localization.LocaleAwareApplication
import com.coming.customer.core.localization.LocaleHelper.getPreferences
import com.coming.customer.core.localization.LocaleHelper.persist
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import java.util.*

@HiltAndroidApp
class App : LocaleAwareApplication() {
    companion object {
        var appContext: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(applicationContext)
        persist(this, Locale(getPreferences(this).getString(AppCommon.LANGUAGE, Locale.getDefault().language) ?: Locale.getDefault().language))
    }
}
