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
import com.coming.customer.data.pojo.AppConstants
import com.coming.customer.data.pojo.CategoryDataItem
import com.coming.customer.data.pojo.ItemDetailItem
import com.coming.customer.data.pojo.Store
import com.coming.customer.data.repository.UserRepository
import com.coming.customer.exception.ServerException
import com.coming.customer.ui.auth.activity.AuthActivity
import com.coming.customer.ui.base.BaseFragment
import com.coming.customer.ui.home.DishAdapter
import com.coming.customer.ui.home.StoreDetailsUpdateCounterInf
import com.coming.customer.ui.payment.StoreDetailsActivity
import com.coming.customer.ui.viewmodel.AuthViewModel
import com.paginate.Paginate
import com.throdle.exception.AuthenticationException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.home_fragment_dishes.*
import kotlinx.android.synthetic.main.home_fragment_providers.*
import java.net.ConnectException
import java.net.ProtocolException
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class DishesFragment : BaseFragment(), View.OnClickListener {

    lateinit var categoryDataItem: CategoryDataItem
    lateinit var store: Store
    lateinit var storeDetailsUpdateCounterInf: StoreDetailsUpdateCounterInf
    lateinit var storeDetailsActivity: StoreDetailsActivity
    fun newInstance(mCategoryDataItem: CategoryDataItem, mStore: Store, mStoreDetailsUpdateCounterInf: StoreDetailsUpdateCounterInf, mStoreDetailsActivity: StoreDetailsActivity) = DishesFragment().apply {
        categoryDataItem = mCategoryDataItem
        store = mStore
        storeDetailsUpdateCounterInf = mStoreDetailsUpdateCounterInf
        storeDetailsActivity = mStoreDetailsActivity
    }

    var itemDetail = ArrayList<ItemDetailItem>()
    var name = ""
    var adapter: DishAdapter? = null

    private var page = 1

    //    private var limit = 5
//    private var maxPage = 1
    var hasNext = true
    var isLoading = false

    override fun bindData() {
        setLiveData()
        setupRecyclerViewDishes()
    }

    override fun onResume() {
        super.onResume()
//        itemDetail.clear()
        //     adapter?.notifyDataSetChanged()
        val callBack = object : Paginate.Callbacks {
            override fun onLoadMore() {
                if (hasNext) {
                    this@DishesFragment.isLoading = true
                    Log.e("testing", "onLoadMore: callGetMerchantListApi()")
                    callGetMerchantDetailApi()
                }
            }

            override fun isLoading(): Boolean {
                return this@DishesFragment.isLoading
            }

            override fun hasLoadedAllItems(): Boolean {
                return !hasNext
            }
        }
        Paginate.with(recyclerViewDishes, callBack)
            .setLoadingTriggerThreshold(2)
            .addLoadingListItem(false)
            .build()
    }

    var inSearch = false

    fun onSearch(searchText: String = "") {
        inSearch = true
        itemDetail.clear()
        adapter?.submitList(null)
        name = searchText
        callGetMerchantDetailApi()
    }

    private fun setupRecyclerViewDishes() {
        adapter = DishAdapter()
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        recyclerViewDishes.adapter = adapter
        recyclerViewDishes.layoutManager = layoutManager
        adapter?.dishSelected = { itemDetailItem ->
            val addDishDialogFragment = AddDishDialogFragment().newInstance(storeDetailsUpdateCounterInf, locationManager)
            addDishDialogFragment.show(childFragmentManager, AddDishDialogFragment::class.simpleName)
            addDishDialogFragment.repository = userRepository
            addDishDialogFragment.userId = session.userId
            addDishDialogFragment.itemDetailItem = itemDetailItem
            addDishDialogFragment.store = store
            addDishDialogFragment.storeDetailActivity = storeDetailsActivity
        }

    }


    override fun onClick(view: View) {
        when (view.id) {

        }
    }

    override fun createLayout(): Int = R.layout.home_fragment_dishes


//    @Inject
//    lateinit var locationManager: LocationManager

    @Inject
    lateinit var userRepository: UserRepository

    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
    }

    private fun setLiveData() {
        //getMerchantDetail live data source
        authViewModel.getMerchantDetailLiveData.observe(this,
            { responseBody ->
                hideLoader()
//                showToastLong(responseBody.message)
                if (responseBody.responseCode == 1 && responseBody.data != null) {
                    responseBody.data?.itemDetail?.let {
                        if (inSearch)
                            itemDetail.clear()
                        itemDetail.addAll(it)
                    }
                    adapter?.submitList(itemDetail)
                    hasNext = true
                    page++
                    isLoading = false

                    if (itemDetail.size == 0) {
                        recyclerViewDishes.visibility = View.GONE
                        textViewDataNotFound.visibility = View.VISIBLE
                    } else {
                        recyclerViewDishes.visibility = View.VISIBLE
                        textViewDataNotFound.visibility = View.GONE
                        adapter?.notifyDataSetChanged()
                    }

                }

            },
            { throwable ->
                hideLoader()
                when (throwable) {
                    is ServerException -> {
                        /*
                                * as per backend infom
                                * 0->operation failed, // show message
                                * 2->no data found // show message
                                * 3->inactive account // exit and signin/signup screen
                                * 11->not approve // exit and signin/signup screen
                                *
                                * */
                        when (throwable.code) {
                            0 -> {
                                throwable.message?.let { showToastLong(it) }
                            }
                            2 -> {
                                hasNext = false
                                page = 1
                                throwable.message?.let { showToastLong(it) }
                            }
                            3, 11 -> {
                                Toast.makeText(requireActivity(), resources.getString(R.string.inactive_account), Toast.LENGTH_SHORT).show()
                                appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                                navigator.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                            }
                        }
                    }
                    is AuthenticationException -> {
                        Toast.makeText(requireActivity(), resources.getString(R.string.invalid_access), Toast.LENGTH_SHORT).show()
                        appPreferences.clearAll()
                        appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                        navigator.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                    }
                    is ProtocolException, is ConnectException -> {
                        Toast.makeText(requireActivity(), resources.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(requireActivity(), throwable.message, Toast.LENGTH_SHORT).show()
                    }
                }
                false
            })

    }

    //getMerchantDetail api
    private fun callGetMerchantDetailApi() {
        /**
         *
         * params : getMerchantDetail(merchant_id ,lat,lng)
         *
         * optional : category_id,user_id,page
         *
         * */
//
//        locationManager.triggerLocation(object : LocationManager.LocationListener {
//            override fun onLocationAvailable(latLng: LatLng) {
//                if (latLng != null) {
//                    showLoader()
        val apiRequestParams = ApiRequestParams()
        if (session.userId != null && session.userId.length != 0) {
            apiRequestParams.user_id = session.userId
        }

        apiRequestParams.lng = appPreferences.getString("longi")
        apiRequestParams.lat = appPreferences.getString("lati")
        apiRequestParams.name = name
        apiRequestParams.merchant_id = store.id
        apiRequestParams.branch_id = store.branchId
        apiRequestParams.category_id = categoryDataItem.id
        when {
            inSearch -> apiRequestParams.page = "0"
            else -> apiRequestParams.page = page.toString()
        }

        authViewModel.getMerchantDetail(apiRequestParams)
        //  locationManager.stop()
//                }
//            }

//            override fun onFail(status: LocationManager.LocationListener.Status) {
//                showAlertDialog(resources.getString(R.string.label_we_need_location_permission_for_get_nearest_store), object : BaseActivity.DialogOkListener {
//                    override fun onClick() {
//                        callGetMerchantDetailApi()
//                    }
//                }, getString(R.string.label_ok))
//            }
//        })

    }
}