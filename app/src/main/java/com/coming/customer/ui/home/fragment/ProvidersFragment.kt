package com.coming.customer.ui.home.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coming.customer.R
import com.coming.customer.apiparams.ApiRequestParams
import com.coming.customer.core.Session
import com.coming.customer.data.pojo.AppConstants
import com.coming.customer.data.pojo.CategoryList
import com.coming.customer.data.pojo.Store
import com.coming.customer.exception.ServerException
import com.coming.customer.ui.auth.activity.AuthActivity
import com.coming.customer.ui.base.BaseFragment
import com.coming.customer.ui.home.StoreAdapter
import com.coming.customer.ui.payment.StoreDetailsActivity
import com.coming.customer.ui.viewmodel.AuthViewModel
import com.paginate.Paginate
import com.throdle.exception.AuthenticationException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.home_fragment_providers.*
import java.net.ConnectException
import java.net.ProtocolException

@AndroidEntryPoint
class ProvidersFragment : BaseFragment() {

    private var categoryList: CategoryList? = null

    fun newInstance(mCategoryList: CategoryList) = ProvidersFragment().apply {
        categoryList = mCategoryList
    }

    var name = ""
    var merchantCategoryId = ""
    var adapter: StoreAdapter? = null
    var storeArrayList = ArrayList<Store>()

    var hasSelectCurrantLocation = false
    var LATITUDE = ""
    var LONGITUDE = ""

    var isResume = true
    var inSearch = false

    override fun bindData() {
        setupListeners()
        setupRecyclerViewStores()
        //  storeArrayList.clear()
        //getMerchantList api call
        // callGetMerchantListApi()
        setLiveData()
    }


    override fun onResume() {
        super.onResume()
        //  setLiveData()
//        storeArrayList.clear()
//        adapter?.notifyDataSetChanged()
        //getMerchantList api call
//        if (isResume) {
////            storeArrayList.clear()
////            F?.notifyDataSetChanged()
//              callGetMerchantListApi()
//        }
        val callBack = object : Paginate.Callbacks {
            override fun onLoadMore() {
                if (hasNext) {
                    this@ProvidersFragment.isLoading = true
                    Log.e("testing", "onLoadMore: callGetMerchantListApi()")
                    callGetMerchantListApi()
                }
            }

            override fun isLoading(): Boolean {
                return this@ProvidersFragment.isLoading
            }

            override fun hasLoadedAllItems(): Boolean {
                return !hasNext
            }
        }
        Paginate.with(recyclerViewProviders, callBack)
            .setLoadingTriggerThreshold(2)
            .addLoadingListItem(false)
            .build()
    }


    private fun setupListeners() {

    }

    fun onSetLatLong(latitude: String, longitude: String) {
        isResume = false

        hasSelectCurrantLocation = true
//        LATITUDE = latitude
//        LONGITUDE = longitude
        appPreferences.putString("lati", latitude)
        appPreferences.putString("longi", longitude)
//        latitudeLocation = latitude.toDouble()
//        longtudeLocation = longitude.toDouble()


//        storeArrayList.clear()
//        adapter?.notifyDataSetChanged()
        //       callSetLocationGetMerchantListApi()
        Log.e("testing", "onSetLatLong: callGetMerchantListApi()")
        callGetMerchantListApi()
    }

    fun onSearch(searchText: String = "") {
        inSearch = true
        storeArrayList.clear()
        adapter?.submitList(null)
        name = searchText
        Log.e("testing", "onSearch: callGetMerchantListApi()")
        callGetMerchantListApi()
    }

    fun onMerchantCategoryId(merchantCategoryIdTemp: String = "") {
        page = 1
        storeArrayList.clear()
        adapter?.submitList(null)
        Log.e("testing", "onMerchantCategoryId: callGetMerchantListApi()")
        merchantCategoryId = merchantCategoryIdTemp
        callGetMerchantListApi()
    }

    private var page = 1
    var hasNext = true
    var isLoading = false
    private fun setupRecyclerViewStores() {
//        shimmer_recycler_view.showShimmerAdapter()
        recyclerViewProviders.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        adapter = StoreAdapter()
        recyclerViewProviders.adapter = adapter
        adapter?.storeSelected = { store ->
            navigator.loadActivity(StoreDetailsActivity::class.java).addBundle(Bundle().apply {
                /*putSerializable(ActivityStarter.ACTIVITY_FIRST_PAGE, StoreDetailFragment::class.java)*/
                putSerializable(Session.DATA, store)
            }).start()
        }
    }

//    override fun onStoreSelected(store: Store) {
//        /*navigator.loadActivity(IsolatedPaymentGetawayActivity::class.java)
//            .addBundle(Bundle().apply {
//                putSerializable(ActivityStarter.ACTIVITY_FIRST_PAGE, StoreDetailFragment::class.java)
//                putSerializable(Session.DATA, store)
//            })
//            .start()*/
//
//
//        navigator.loadActivity(StoreDetailsActivity::class.java).addBundle(Bundle().apply {
//            /*putSerializable(ActivityStarter.ACTIVITY_FIRST_PAGE, StoreDetailFragment::class.java)*/
//            putSerializable(Session.DATA, store)
//        }).start()
//
//        /*   .addBundle(Bundle().apply {
//               putSerializable(ActivityStarter.ACTIVITY_FIRST_PAGE, StoreDetailFragment::class.java)
//               putSerializable(Session.DATA, store)
//           })
//           .start()*/
//    }
//
//    override fun onClick(view: View) {
//        when (view.id) {
//
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                   inSearch = false
//                   activity?.onBackPressed()
//            }    })
    }


    override fun createLayout(): Int = R.layout.home_fragment_providers

    private val authViewModel by viewModels<AuthViewModel>()
    private fun setLiveData() {
        //getMerchantList live data source
        authViewModel.getMerchantListLiveData.observe(this,
            { responseBody ->
                isResume = true
                hideLoader()
//                showToastLong(responseBody.message)
                if (responseBody.responseCode == 1 && responseBody.data != null) {
                    responseBody.data?.let {
                        // shimmer_recycler_view.hideShimmerAdapter()
                        // storeArrayList.clear()
                        if (inSearch)
                            storeArrayList.clear()
                        storeArrayList.addAll(it)
                        Log.i("clicked", "observe data : " + it.toString())
                        //maxPage = unitsResponse.response.total
                        //hasNext = (maxPage > page)
                    }
                    adapter?.submitList(storeArrayList)
                    hasNext = true
                    page++
                    isLoading = false

                    if (storeArrayList.size == 0) {
                        // shimmer_recycler_view.hideShimmerAdapter()
                        recyclerViewProviders.visibility = View.GONE
                        textViewDtaNotFound.visibility = View.VISIBLE
                    } else {
                        // shimmer_recycler_view.hideShimmerAdapter()
                        recyclerViewProviders.visibility = View.VISIBLE
                        textViewDtaNotFound.visibility = View.GONE
                        adapter?.notifyDataSetChanged()
                    }
                }

            },
            { throwable ->
                isResume = true

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
                        hasNext = false
                        page = 1
                        if (storeArrayList.size == 0) {
                            Log.i("clicked", "storeArrayList.size code(2) : " + storeArrayList.size)
//                            shimmer_recycler_view.hideShimmerAdapter()
                            recyclerViewProviders.visibility = View.GONE
                            textViewDtaNotFound.visibility = View.VISIBLE
                            throwable.message?.let { textViewDtaNotFound.text = resources.getString(R.string.stores_soon) }
                        } else {
                            Log.i("clicked", "storeArrayList.size else : " + storeArrayList.size)

                            recyclerViewProviders.visibility = View.VISIBLE
                            textViewDtaNotFound.visibility = View.GONE
                        }
                    } else if (throwable.code == 3 || throwable.code == 11) {
                        Log.i("clicked", "storeArrayList.size code(3||11) : " + throwable.code)
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
                }
//                else {
//                    Toast.makeText(requireActivity(), throwable.message, Toast.LENGTH_SHORT).show()
//                }
                false
            })

    }

    //getMerchantList api
    private fun callGetMerchantListApi() {
        /**
         *
         * params : getMerchantList(lat ,lng)
         *
         * optional : user_id(if login user), name,category_id
         *
         * */
        //showLoader()
//        shimmer_recycler_view.showShimmerAdapter()

        val apiRequestParams = ApiRequestParams()
        session.userId?.let {
            apiRequestParams.user_id = it
        }
        name?.let {
            apiRequestParams.name = name
        }
        categoryList?.id?.let {
            Log.i("clicked", "hit api:category_id : *" + it)

            apiRequestParams.category_id = it
        } ?: run {
            //   Log.i("clicked", "hit api:category_id : x"+this.id.toString())
            apiRequestParams.category_id = ""
        }
        apiRequestParams.merchant_category_id = appPreferences.getString("merchantCategoryId")
        Log.i("clicked", "hit api:merchantCategoryId " + appPreferences.getString("merchantCategoryId"))
//                    merchantCategoryId?.let {
//                        apiRequestParams.merchant_category_id = merchantCategoryId
//                    }
        apiRequestParams.lng = appPreferences.getString("longi")
        apiRequestParams.lat = appPreferences.getString("lati")
        when {
            inSearch -> apiRequestParams.page = "0"
            //hasSelectCurrantLocation -> apiRequestParams.page = "1"
            else -> apiRequestParams.page = page.toString()
        }
        authViewModel.getMerchantList(apiRequestParams)
    }


//    //getMerchantList api
//    private fun callSetLocationGetMerchantListApi() {
//        /**
//         *
//         * params : getMerchantList(lat ,lng)
//         *
//         * optional : user_id(if login user), name,category_id
//         *
//         * */
//        //showLoader()
//        locationManager.stop()
//        val apiRequestParams = ApiRequestParams()
//        session.userId?.let {
//            apiRequestParams.user_id = it
//        }
//        name?.let {
//            apiRequestParams.name = name
//        }
//        categoryList.id?.let {
//            apiRequestParams.category_id = it
//        } ?: run {
//            apiRequestParams.category_id = ""
//        }
//      //  apiRequestParams.merchant_category_id =  appPreferences.getString("merchantCategoryId")
//        Log.i("clicked", "hit api:callSetLocationGetMerchantListApi " + appPreferences.getString("merchantCategoryId"))
//
//        merchantCategoryId?.let {
//            apiRequestParams.merchant_category_id = merchantCategoryId
//        }
//
//        apiRequestParams.lng =LONGITUDE
//        apiRequestParams.lat =LATITUDE
//        apiRequestParams.page = newPage.toString()
//        authViewModel.getMerchantList(apiRequestParams)
//
//    }
}