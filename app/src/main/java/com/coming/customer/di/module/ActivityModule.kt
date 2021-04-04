package com.coming.customer.di.module


import android.app.Activity
import com.coming.customer.ui.base.BaseActivity
import com.coming.customer.ui.manager.FragmentHandler
import com.coming.customer.ui.manager.FragmentManager
import com.coming.customer.ui.manager.Navigator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Named

/**
 * Created by hlink21 on 9/5/16.
 */
@Module
@InstallIn(ActivityComponent::class)
class ActivityModule {


    @Provides
    @ActivityScoped
    fun provideBaseActivity(activity: Activity): BaseActivity {
        return activity as BaseActivity
    }

    @Provides
    @ActivityScoped
    internal fun navigator(activity: BaseActivity): Navigator {
        return activity
    }

    @Provides
    @ActivityScoped
    internal fun fragmentManager(baseActivity: BaseActivity): androidx.fragment.app.FragmentManager {
        return baseActivity.supportFragmentManager
    }

    @Provides
    @Named("placeholder")
    internal fun placeHolder(baseActivity: BaseActivity): Int {
        return baseActivity.findFragmentPlaceHolder()
    }

    @Provides
    @ActivityScoped
    internal fun fragmentHandler(fragmentManager: FragmentManager): FragmentHandler {
        return fragmentManager
    }
}
