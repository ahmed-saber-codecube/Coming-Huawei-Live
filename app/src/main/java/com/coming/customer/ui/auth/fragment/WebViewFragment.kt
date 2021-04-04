package com.coming.customer.ui.auth.fragment

import android.view.View
import com.coming.customer.R
import com.coming.customer.core.Session
import com.coming.customer.ui.base.BaseFragment
import com.coming.customer.util.Validator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.web_view_fragment.*
import javax.inject.Inject

@AndroidEntryPoint
class WebViewFragment : BaseFragment(), View.OnClickListener {

    @Inject
    lateinit var validator: Validator

    override fun bindData() {
        imageViewBack.setOnClickListener(this::onViewClick)
        if (arguments?.containsKey(Session.WEB_URL_KEY) == true) {
            webView.loadUrl(requireArguments().getString(Session.WEB_URL))
        }
        toolbar.showToolbar(true)
//        toolbar.setToolbarTitle("sadasdas")
    }

    override fun onClick(view: View) {
        when (view) {
            imageViewBack -> {
                navigator.finish()
            }
        }
    }


    override fun createLayout(): Int = R.layout.web_view_fragment
}