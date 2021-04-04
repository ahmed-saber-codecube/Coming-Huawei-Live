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
import com.coming.customer.data.pojo.InvoiceList
import com.coming.customer.exception.ServerException
import com.coming.customer.ui.auth.activity.AuthActivity
import com.coming.customer.ui.base.BaseFragment
import com.coming.customer.ui.drawer.InvoiceAdapter
import com.coming.customer.ui.isolated.IsolatedActivity
import com.coming.customer.ui.viewmodel.AuthViewModel
import com.throdle.exception.AuthenticationException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.home_fragment_invoices.*
import java.net.ConnectException
import java.net.ProtocolException

@AndroidEntryPoint
class InvoicesFragment : BaseFragment() {

    var invoiceList = ArrayList<InvoiceList>()
    var adapter: InvoiceAdapter? = null

    private val parent: IsolatedActivity get() = requireActivity() as IsolatedActivity

    override fun bindData() {
        setupRecyclerViewInvoices()
        callGetInvoiceListApi()
    }

    override fun onResume() {
        super.onResume()
        parent.apply {
            showToolbar(true)
            setToolbarTitle(R.string.menu_invoices)
        }
    }

    private fun setupRecyclerViewInvoices() {
        adapter = InvoiceAdapter(invoiceList)
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        recyclerViewInvoices.adapter = adapter
        recyclerViewInvoices.layoutManager = layoutManager
    }


    override fun createLayout(): Int = R.layout.home_fragment_invoices

    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setLiveData()
        super.onCreate(savedInstanceState)
    }

    private fun setLiveData() {
        //getInvoiceList live data source
        authViewModel.getInvoiceListLiveData.observe(this,
            { responseBody ->
                hideLoader()
//                showToastLong(responseBody.message)
                if (responseBody.responseCode == 1 && responseBody.data != null) {
                    responseBody.data?.let {
                        invoiceList.addAll(it)
                    }
                    adapter?.notifyDataSetChanged()

                    if (invoiceList.size == 0) {
                        recyclerViewInvoices.visibility = View.GONE
                        textViewNoDataFound.visibility = View.VISIBLE
                    } else {
                        recyclerViewInvoices.visibility = View.VISIBLE
                        textViewNoDataFound.visibility = View.GONE
                    }
                }
            },
            { throwable ->
                hideLoader()
                if (throwable is ServerException) {
                    /*
                    * as per backend inform
                    * 0->operation failed, // show message
                    * 2->no data found // show message
                    * 3->inactive account // exit and signin/signup screen
                    * 11->not approve // exit and signin/signup screen
                    *
                    * */
                    if (throwable.code == 0) {
                        throwable.message?.let { showToastLong(it) }
                    } else if (throwable.code == 2) {
                        if (invoiceList.size == 0) {
                            recyclerViewInvoices.visibility = View.GONE
                            textViewNoDataFound.visibility = View.VISIBLE
                            throwable.message?.let { textViewNoDataFound.text = it }
                        } else {
                            recyclerViewInvoices.visibility = View.VISIBLE
                            textViewNoDataFound.visibility = View.GONE
                        }
                    } else if (throwable.code == 3 || throwable.code == 11) {
                        Toast.makeText(requireActivity(), resources.getString(R.string.inactive_account), Toast.LENGTH_SHORT).show()
                        appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                        navigator.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                    }
                } else if (throwable is AuthenticationException) {
                    Toast.makeText(requireActivity(), resources.getString(R.string.invalid_access), Toast.LENGTH_SHORT).show()
                    appPreferences.clearAll()
                    appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                    navigator.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                } else if (throwable is ProtocolException || throwable is ConnectException) {
                    Toast.makeText(requireActivity(), resources.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireActivity(), throwable.message, Toast.LENGTH_SHORT).show()
                }
                false
            })
    }

    //getInvoiceList api
    private fun callGetInvoiceListApi() {
        /**
         *
         * params : getInvoiceList(user_id)
         *
         * optional :
         *
         * */
        showLoader()
        val apiRequestParams = ApiRequestParams()
        apiRequestParams.user_id = session.userId
        authViewModel.getInvoiceList(apiRequestParams)
    }

}