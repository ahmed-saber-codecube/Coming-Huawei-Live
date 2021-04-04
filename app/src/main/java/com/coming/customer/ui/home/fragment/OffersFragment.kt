package com.coming.customer.ui.home.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coming.customer.R
import com.coming.customer.apiparams.ApiRequestParams
import com.coming.customer.data.pojo.AppConstants
import com.coming.customer.data.pojo.GetOfferList
import com.coming.customer.exception.ServerException
import com.coming.customer.ui.auth.activity.AuthActivity
import com.coming.customer.ui.base.BaseFragment
import com.coming.customer.ui.home.OfferAdapter
import com.coming.customer.ui.viewmodel.AuthViewModel
import com.throdle.exception.AuthenticationException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.home_fragment_offers.*
import java.net.ConnectException
import java.net.ProtocolException

@AndroidEntryPoint
class OffersFragment : BaseFragment(), View.OnClickListener {


    val getOfferList = ArrayList<GetOfferList>()
    var adapter: OfferAdapter? = null

    override fun bindData() {
        setupListeners()
        setupRecyclerViewOffers()
        callGetOfferListApi()
    }

    private fun setupListeners() {

    }

    private fun setupRecyclerViewOffers() {
        adapter = OfferAdapter(getOfferList)
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        recyclerViewOffers.adapter = adapter
        recyclerViewOffers.layoutManager = layoutManager
    }

    override fun onClick(view: View) {
        when (view.id) {

        }
    }

    override fun createLayout(): Int = R.layout.home_fragment_offers

    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setLiveData()
        super.onCreate(savedInstanceState)
    }

    private fun setLiveData() {
        //getOfferList live data source
        authViewModel.getOfferListLiveData.observe(this,
            { responseBody ->
                hideLoader()
//                showToastLong(responseBody.message)
                if (responseBody.responseCode == 1 && responseBody.data != null) {
                    getOfferList.clear()
                    responseBody.data?.let {
                        getOfferList.addAll(it)
                    }
                    adapter?.notifyDataSetChanged()

                    if (getOfferList.size == 0) {
                        recyclerViewOffers.visibility = View.GONE
                        textViewNoDataFound.visibility = View.VISIBLE
                    } else {
                        recyclerViewOffers.visibility = View.VISIBLE
                        textViewNoDataFound.visibility = View.GONE
                    }
                }
            },
            { throwable ->
                hideLoader()
                if (throwable is ServerException) {

                    /* * as per backend infom
                     * 0->operation failed, // show message
                     * 2->no data found // show message
                     * 3->inactive account // exit and signin/signup screen
                     * 11->not approve // exit and signin/signup screen
                     *
                     **/
                    if (throwable.code == 0) {
                        throwable.message?.let { showToastLong(it) }
                    } else if (throwable.code == 2) {
                        if (getOfferList.size == 0) {
                            recyclerViewOffers.visibility = View.GONE
                            textViewNoDataFound.visibility = View.VISIBLE
                            throwable.message?.let { textViewNoDataFound.text = it }
                        } else {
                            recyclerViewOffers.visibility = View.VISIBLE
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


    //getOfferList api
    private fun callGetOfferListApi() {
        /**
         *
         * params : getOfferList()
         *
         * optional :
         *
         * */
        showLoader()
        val apiRequestParams = ApiRequestParams()
        authViewModel.getOfferList(apiRequestParams)
    }

}