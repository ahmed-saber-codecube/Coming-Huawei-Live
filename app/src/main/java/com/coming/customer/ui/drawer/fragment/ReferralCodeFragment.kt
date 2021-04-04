package com.coming.customer.ui.drawer.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.view.View
import com.coming.customer.R
import com.coming.customer.ui.base.BaseFragment
import com.coming.customer.ui.isolated.IsolatedActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.home_fragment_referral_code.*

@AndroidEntryPoint
class ReferralCodeFragment : BaseFragment(), View.OnClickListener {

    private val parent: IsolatedActivity get() = requireActivity() as IsolatedActivity

    override fun bindData() {
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        parent.apply {
            showToolbar(true)
            setToolbarTitle(R.string.title_ref_code)
        }

        session.user?.refferalCode?.let {
            textViewReferralCode.text = it
        }
    }

    private fun setupListeners() {
        textViewReferralCode.setOnClickListener(this)
        buttonShareCode.setOnClickListener(this)
    }

    private fun copyReferralCode() {
        val clipboard: ClipboardManager? = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("referral", textViewReferralCode.text)
        clipboard?.setPrimaryClip(clip)

        showToastShort(resources.getString(R.string.label_referral_code_copied))
    }

    private fun shareReferralCode() {
        val intent = Intent(Intent.ACTION_SEND)
        val message = "${textViewReferralCode.text} " + resources.getString(R.string.label_in_my_referral_code_for_coming)

        intent.apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.label_share_referral_code))
            putExtra(Intent.EXTRA_TEXT, message)

            startActivity(Intent.createChooser(intent, resources.getString(R.string.label_share_referral_code_using)))
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.textViewReferralCode -> {
                copyReferralCode()
            }

            R.id.buttonShareCode -> {
                shareReferralCode()
            }
        }
    }


    override fun createLayout(): Int = R.layout.home_fragment_referral_code

}