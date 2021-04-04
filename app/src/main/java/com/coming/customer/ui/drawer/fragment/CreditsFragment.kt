package com.coming.customer.ui.drawer.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coming.customer.R
import com.coming.customer.apiparams.ApiRequestParams
import com.coming.customer.data.pojo.AppConstants
import com.coming.customer.data.pojo.TransactionItem
import com.coming.customer.exception.ServerException
import com.coming.customer.ui.auth.activity.AuthActivity
import com.coming.customer.ui.base.BaseFragment
import com.coming.customer.ui.drawer.TransactionAdapter
import com.coming.customer.ui.isolated.IsolatedActivity
import com.coming.customer.ui.viewmodel.AuthViewModel
import com.coming.customer.util.twoDecimal
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.home_fragment_credits.*

@AndroidEntryPoint
class CreditsFragment : BaseFragment(), View.OnClickListener {

    var walletTransactionArrayList = ArrayList<TransactionItem>()
    var adapter: TransactionAdapter? = null

    private val parent: IsolatedActivity get() = requireActivity() as IsolatedActivity

    override fun bindData() {
        setupListeners()
        setupRecyclerViewTransactions()
        callGetWalletTransactionApi()
    }

    override fun onResume() {
        super.onResume()
        parent.apply {
            showToolbar(false)
        }
    }

    private fun setupListeners() {
        buttonRecharge.setOnClickListener(this)
        textViewLabelOptions.setOnClickListener(this)
    }

    private fun setupRecyclerViewTransactions() {
        adapter = TransactionAdapter(walletTransactionArrayList)
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        recyclerViewTransactions.adapter = adapter
        recyclerViewTransactions.layoutManager = layoutManager
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.buttonRecharge -> {
                navigator.load(RechargeFragment::class.java).replace(true)
            }
            R.id.textViewLabelOptions -> {
                navigator.goBack()
            }
        }
    }

    override fun createLayout(): Int = R.layout.home_fragment_credits

    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setLiveData()
        super.onCreate(savedInstanceState)
    }

    private fun setLiveData() {
        //getWalletTransaction live data source
        authViewModel.getWalletTransactionLiveData.observe(this,
            { responseBody ->
                hideLoader()
//                showToastLong(responseBody.message)
                if (responseBody.responseCode == 1 && responseBody.data != null) {

                    responseBody.data?.wallet?.let {
                        textViewCurrentBal.text = resources.getString(R.string.label_sar) + " " + it.twoDecimal()
                    }

                    responseBody.data?.transaction?.let {
                        walletTransactionArrayList.addAll(it)
                    }
                    adapter?.notifyDataSetChanged()

                    if (walletTransactionArrayList.size == 0) {
                        textViewNoDataFound.visibility = View.VISIBLE
                        recyclerViewTransactions.visibility = View.GONE
                    } else {
                        textViewNoDataFound.visibility = View.GONE
                        recyclerViewTransactions.visibility = View.VISIBLE
                    }
                }
            },
            { throwable ->
                hideLoader()
                if (throwable is ServerException) {
                    /*
                    * as per backend infom
                    * 0->operation failed, // show message
                    * 2->no data found // show message
                    * 3->inactive account // exit and signin/signup screen
                    * 11->not approve // exit and signin/signup screen
                    *
                    * */
                    if (throwable.code == 0) {
                        throwable.message?.let { showToastLong(it) }
                    } else if (throwable.code == 2) {
                        if (walletTransactionArrayList.size == 0) {
                            textViewNoDataFound.visibility = View.VISIBLE
                            recyclerViewTransactions.visibility = View.GONE
                        } else {
                            textViewNoDataFound.visibility = View.GONE
                            recyclerViewTransactions.visibility = View.VISIBLE
                        }
                    } else if (throwable.code == 3 || throwable.code == 11) {
                        Toast.makeText(requireActivity(), resources.getString(R.string.inactive_account), Toast.LENGTH_SHORT).show()
                        appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                        navigator.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                    }
                }
                false
            })

    }

    //getWalletTransaction api
    private fun callGetWalletTransactionApi() {
        /**
         *
         * params : getWalletTransaction(user_id)
         *
         * optional :
         *
         * */
        showLoader()
        val apiRequestParams = ApiRequestParams()
        apiRequestParams.user_id = session.userId
        authViewModel.getWalletTransaction(apiRequestParams)
    }

}