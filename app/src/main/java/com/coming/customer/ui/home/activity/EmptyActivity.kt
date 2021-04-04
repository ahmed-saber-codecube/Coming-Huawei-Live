package com.coming.customer.ui.home.activity

import android.os.Bundle
import com.coming.customer.R
import com.coming.customer.ui.base.BaseActivity
import com.coming.customer.ui.base.BaseFragment
import com.coming.customer.ui.manager.ActivityStarter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmptyActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empty)
        handleFirstPage()
    }

    override fun findFragmentPlaceHolder(): Int = R.id.placeHolder

    override fun findContentView(): Int = R.layout.activity_empty


    private fun handleFirstPage() {

        val page = intent.getSerializableExtra(ActivityStarter.ACTIVITY_FIRST_PAGE)
        if (page != null) {
            load(page as Class<BaseFragment>)
                .setBundle(intent.extras ?: Bundle())
                .add(false)
        }
    }
}