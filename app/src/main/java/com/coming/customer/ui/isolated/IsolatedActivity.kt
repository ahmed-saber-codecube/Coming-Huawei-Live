package com.coming.customer.ui.isolated

import android.os.Bundle
import android.view.View
import com.coming.customer.R
import com.coming.customer.ui.base.BaseActivity
import com.coming.customer.ui.base.BaseFragment
import com.coming.customer.ui.manager.ActivityStarter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.isolated_activity.*

@AndroidEntryPoint
class IsolatedActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupToolbar()
        handleFirstPage()
    }

    override fun findFragmentPlaceHolder(): Int = R.id.placeHolder

    override fun findContentView(): Int = R.layout.isolated_activity


    private fun handleFirstPage() {
//        val page = intent.getSerializableExtra(ActivityStarter.ACTIVITY_FIRST_PAGE) as Class<BaseFragment>
//        val order = intent.getSerializableExtra(AppConstants.KEY_ORDER) as Order?
        /*load(page).setBundle(Bundle().apply {
            if (order != null) {
                putSerializable(AppConstants.KEY_ORDER, order)
            }
        }).add(false)*/

        val page = intent.getSerializableExtra(ActivityStarter.ACTIVITY_FIRST_PAGE)
//            val page = getIntent().getSerializableExtra(Commons.ACTIVITY_FIRST_PAGE) as Class<BaseFragment>
        if (page != null) {
            load(page as Class<BaseFragment>)
                .setBundle(intent.extras ?: Bundle())
                .add(false)
        }
    }

    private fun setupToolbar() {
        setToolbar(toolBar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        imageViewBack.setOnClickListener { goBack() }
    }

    override fun showToolbar(b: Boolean) {
        if (b) {
            toolBar.visibility = View.VISIBLE
        } else {
            toolBar.visibility = View.GONE
        }
    }

    override fun setToolbarTitle(title: Int) {
        textViewTitle.text = getString(title)
    }
}
