package com.coming.customer.di.module

import androidx.fragment.app.Fragment
import com.coming.customer.ui.base.BaseFragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.scopes.FragmentScoped

/**
 * Created by hlink21 on 31/5/16.
 */
@Module
@InstallIn(FragmentComponent::class)
class FragmentModule {

    @Provides
    @FragmentScoped
    fun provideBaseFragment(fragment: Fragment): BaseFragment {
        return fragment as BaseFragment
    }
}
