package com.coming.customer.ui.drawer.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.coming.customer.BuildConfig
import com.coming.customer.R
import com.coming.customer.data.pojo.AppConstants
import com.coming.customer.data.pojo.CustomerServiceDetails
import com.coming.customer.data.pojo.DataWrapper
import com.coming.customer.data.repository.UserRepository
import com.coming.customer.exception.ServerException
import com.coming.customer.ui.auth.activity.AuthActivity
import com.coming.customer.ui.base.BaseFragment
import com.coming.customer.ui.isolated.IsolatedActivity
import com.google.android.material.snackbar.Snackbar
import com.livechatinc.inappchat.ChatWindowActivity
import com.livechatinc.inappchat.ChatWindowConfiguration
import com.throdle.exception.AuthenticationException
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.home_fragment_support.*
import javax.inject.Inject

@AndroidEntryPoint
class SupportFragment : BaseFragment(), View.OnClickListener {
    @Inject
    lateinit var userRepository: UserRepository
    private val parent: IsolatedActivity
        get() = requireActivity() as IsolatedActivity
    var customerServiceDetails: CustomerServiceDetails? = null

    override fun bindData() {
        setupListeners()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCustomerServiceDetails()
    }

    override fun onResume() {
        super.onResume()
        parent.apply {
            showToolbar(true)
            setToolbarTitle(R.string.menu_customer_service)
        }
    }

    private fun setupListeners() {
        textViewLiveChat.setOnClickListener(this)
        textViewCallCenter.setOnClickListener(this)
//        textViewMyTickets.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.textViewLiveChat -> {
                startChatActivity()
            }
            R.id.textViewCallCenter -> {
                val dialIntent = Intent(Intent.ACTION_DIAL)
                dialIntent.data = Uri.parse("tel:" + customerServiceDetails?.customerServiceSupportNumber)
                startActivity(dialIntent)
            }
        }
    }

    override fun createLayout(): Int = R.layout.home_fragment_support


    //var customerServiceDetails: CustomerServiceDetails? = null
    private fun getChatWindowConfiguration(): ChatWindowConfiguration? {
        return if (session.user?.email.isNullOrEmpty() || session.user?.username.isNullOrEmpty()) {
            ChatWindowConfiguration(customerServiceDetails?.customerServiceLicense!!, customerServiceDetails?.customerGroupID, session.user?.phone, session.user?.phone + "@gmail.com", null)
        } else {
            ChatWindowConfiguration(customerServiceDetails?.customerServiceLicense!!, customerServiceDetails?.customerGroupID, session.user?.username, session.user?.email, null)
        }
    }

    private fun startChatActivity() {
        val intent = Intent(requireActivity(), ChatWindowActivity::class.java)
        val config = getChatWindowConfiguration()
        config?.asBundle()?.let { intent.putExtras(it) }
        startActivity(intent)
    }

    private var disposable: Disposable? = null
    private fun getCustomerServiceDetails() {

        userRepository.getCustomerServiceDetails()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<DataWrapper<CustomerServiceDetails>> {
                override fun onSuccess(data: DataWrapper<CustomerServiceDetails>) {
                    when (data.responseBody?.responseCode) {
                        null -> {
                            when (data.throwable?.message) {
                                "Authentication Exception" -> {
                                    Toast.makeText(requireActivity(), resources.getString(R.string.invalid_access), Toast.LENGTH_SHORT).show()
                                    appPreferences.clearAll()
                                    appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                                    navigator.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                                }
                                "Failed to connect to /${BuildConfig.BASE_URL}:8507" -> {
                                    Toast.makeText(requireActivity(), resources.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
                                }
                                else -> {
                                    Toast.makeText(requireActivity(), data.throwable?.message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        1 -> {
                            customerServiceDetails = data.responseBody.data!!
                        }
                    }
                }

                override fun onSubscribe(d: Disposable) {
                    disposable = d
                }

                override fun onError(e: Throwable) {
                    if (e is ServerException) {
                        e.message?.let {
                            showSnackBar(it)
                        }
                    }
                    if (e is AuthenticationException) {
                        val intent = Intent(context, AuthActivity::class.java)
                        startActivity(intent)
                    }
                }
            })
    }

    private fun showSnackBar(message: String) {

        val snackBar = Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT)
        snackBar.setActionTextColor(this.resources.getColor(R.color.colorTextWhite))
        val snackView = snackBar.view
        val textView = snackView.findViewById<TextView>(R.id.snackbar_text)
        textView.maxLines = 4
        textView.setTextColor(this.resources.getColor(R.color.colorTextWhite))
        snackBar.show()
    }

    override fun onDestroyView() {
        disposable?.dispose()
        super.onDestroyView()
    }
}