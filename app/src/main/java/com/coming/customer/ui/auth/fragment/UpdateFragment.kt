package com.coming.customer.ui.auth.fragment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.view.View
import com.coming.customer.R
import com.coming.customer.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_update.*

@AndroidEntryPoint
class UpdateFragment : BaseFragment(), View.OnClickListener {


    override fun bindData() {

        setupListeners()
    }


    private fun setupListeners() {
        updateBtn.setOnClickListener(this)
    }

    override fun createLayout(): Int = R.layout.fragment_update

    override fun onClick(view: View) {
        when (view.id) {
            R.id.updateBtn -> {
                startOpenInStore()
            }
        }
    }

    private fun startOpenInStore() {
        val playStoreScheme = "market://details?id="
        val huaweiScheme = "appmarket://details?id="
        val appPackageName: String = context?.packageName!!.toString()
        if (!openInStore(playStoreScheme + appPackageName)) {
            if (!openInStore(huaweiScheme + appPackageName)) {
                openInStore("https://play.google.com/store/apps/details?id=" + appPackageName)
            }
        }
    }

    private fun openInStore(uri: String): Boolean {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        return try {
            startActivity(Intent.createChooser(intent, getString(R.string.open_with)))
            true
        } catch (anfe: ActivityNotFoundException) {
            false
        }
    }

}