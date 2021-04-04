package com.coming.customer.ui.drawer.fragment

import android.os.Handler
import com.coming.customer.R
import com.coming.customer.data.pojo.AppConstants
import com.coming.customer.ui.base.BaseFragment
import com.coming.customer.ui.isolated.IsolatedActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.home_fragment_success.*

@AndroidEntryPoint
class SuccessFragment : BaseFragment() {

    private val parent: IsolatedActivity get() = requireActivity() as IsolatedActivity

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private val splashDelay = 2000L

    override fun bindData() {
        setSuccessMessage()
    }

    private fun setSuccessMessage() {
        val message = arguments?.get(AppConstants.KEY_SUCCESS_MSG) as String

        textViewSuccess.text = message
    }

    override fun onResume() {
        super.onResume()
        parent.apply {
            showToolbar(false)
        }

        runnable = Runnable {
            navigator.goBack()
        }

        handler = Handler()
        handler.postDelayed(runnable, splashDelay)
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(runnable)
    }

    override fun createLayout(): Int = R.layout.home_fragment_success

}