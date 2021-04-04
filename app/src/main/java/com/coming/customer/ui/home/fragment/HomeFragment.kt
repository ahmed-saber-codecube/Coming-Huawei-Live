package com.coming.customer.ui.home.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coming.customer.R
import com.coming.customer.apiparams.ApiRequestParams
import com.coming.customer.core.AppCommon
import com.coming.customer.core.AppCommon.RequestCode.ADD_SERVICE
import com.coming.customer.core.AppPreferences
import com.coming.customer.core.Session
import com.coming.customer.data.pojo.*
import com.coming.customer.exception.ServerException
import com.coming.customer.ui.HomeCategoryAdapter
import com.coming.customer.ui.auth.activity.AuthActivity
import com.coming.customer.ui.base.BaseFragment
import com.coming.customer.ui.home.DeliveryTypeAdapter
import com.coming.customer.ui.home.HomePagerAdapter
import com.coming.customer.ui.home.OnDeliveryTypeSelectedListener
import com.coming.customer.ui.home.ProvidersPagerAdapter
import com.coming.customer.ui.home.activity.HomeActivity
import com.coming.customer.ui.payment.StoreDetailsActivity
import com.coming.customer.ui.viewmodel.AuthViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.throdle.exception.AuthenticationException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.home_fragment.*
import java.net.ConnectException
import java.net.ProtocolException
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment(), OnDeliveryTypeSelectedListener, HomeCategoryAdapter.OnItemClick, HomePagerAdapter.OnSliderClicked {
    private lateinit var latitude: String
    private lateinit var longitude: String

    @Inject
    lateinit var preferences: AppPreferences
    var homeMerchantSlider = ArrayList<HomeMerchantSlider>()
    var homeSliderPagerAdapter: HomePagerAdapter? = null

    var merchantCategory = ArrayList<MerchantCategory>()
    var adapter: DeliveryTypeAdapter? = null


    //var categoryAdapter: HomeCategoryAdapter? = null

    // val categoryArrayList = ArrayList<CategoryList>()

    private val fragmentList = arrayListOf<ProvidersFragment>()

    var pagerAdapter: ProvidersPagerAdapter? = null

    // private var chipsMap: HashMap<Int, Chip> = HashMap()

    /*private var onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            chipGroupCategory.check(chipsMap[position + 1]?.id!!)
        }
    }*/
    override fun bindData() {
//       if (NetworkConnection.isOnline(requireContext())) {
        hideLoader()
        // setupListeners()
        // observeCategory()
        //setupRecyclearview()
        //getCategory api call
        //done
        callGetCategoryApi("13")
        //top viewPager Offer Slider
        setupViewPager()
        setupRecyclerViewOrderTypes()
        //store list viewPager
        //setupViewPagerProviders()
        //prepareChipsMap()
        //setupChipGroupCategory()
        callGetTopSliderApi()
        callGetMerchantCategoryListApi()
        setLiveData()
//       }else{
//           showAlertDialog(resources.getString(R.string.connection_exception))
//       }
    }

//    private fun setupRecyclearview() {
////        private val confirmationCartAdapter by lazy { BookingRequiestDetailsAdapter(myBookingArrayList, this,session) }
//
//        categoryAdapter = HomeCategoryAdapter(categoryArrayList, this)
//        recyclerViewCategory.adapter = categoryAdapter
//        recyclerViewCategory.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
//    }

    override fun onItemClicked(position: Int) {
        viewPagerProviders.currentItem = position
//            if (fragmentList.size != 0) {
//                fragmentList[viewPagerProviders.currentItem].onMerchantCategoryId(position.toString())
//            }
    }

    override fun onResume() {
        super.onResume()
        if (session.userId != null && session.userId.length != 0) {
            //getProfile api call
            callGetProfileApi()
        } else {

        }
    }

    private fun setupViewPager() {
        homeSliderPagerAdapter = HomePagerAdapter(homeMerchantSlider, this)
        viewPagerHome.adapter = homeSliderPagerAdapter
        // tabLayoutHome.setViewPager2(viewPagerHome)
//        viewPagerHome.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//                 override fun onPageSelected(position: Int) {
//                     for (i in 2 until homeMerchantSlider.size) {
//
//                     }
//                         when (position) {
//                             0 -> {
//                                 // you are on the first page
//                                 tabLayoutHome.setDotSelection(0)
//
//                             }
//                             1 -> {
//                                 // you are on the last page
//                                 tabLayoutHome.setDotSelection(1)
//                             }
//                             else -> {
//                                 // you are on one of the middle pages
//
//                                 tabLayoutHome.setDotSelection(2)
//
//                             }
//
//                     }
//                         super.onPageSelected(position)
//
//                         }
//        })
        TabLayoutMediator(tabLayoutHome, viewPagerHome) { _, _ ->

        }.attach()
    }


    override fun onMerchantOpen(homeMerchantSlider: HomeMerchantSlider) {
        navigator.loadActivity(StoreDetailsActivity::class.java).addBundle(Bundle().apply {
            /*putSerializable(ActivityStarter.ACTIVITY_FIRST_PAGE, StoreDetailFragment::class.java)*/
            putSerializable(Session.MERCHANT_ID, homeMerchantSlider)

        }).start()
    }

    private fun setupViewPagerProviders() {

        pagerAdapter = ProvidersPagerAdapter(childFragmentManager, fragmentList)
        viewPagerProviders.adapter = pagerAdapter
//commented for current time
//      viewPagerProviders.registerOnPageChangeCallback(onPageChangeCallback)
//        viewPagerProviders?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
//            override fun onPageScrollStateChanged(state: Int) {
//
//            }
//
//            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
//
//            }
//
//            override fun onPageSelected(position: Int) {
//
////                chipGroupCategory.check(chipsMap[position + 1]?.id!!)
//
//                for (i in 0 until categoryArrayList.size) {
//                    categoryArrayList[i].isSelectedCategory = i == position
//                }
//                categoryAdapter?.notifyDataSetChanged()
//                recyclerViewCategory?.smoothScrollToPosition(position)
//            }
//
//        })
    }

//    private fun prepareChipsMap() {
//        chipGroupCategory.removeView(chipAll)
//        chipsMap.apply {
//            put(1, chipAll)
//            put(2, chipBuilder("Fast Food", R.drawable.ic_category))
//            put(3, chipBuilder("Dessert", R.drawable.ic_donut))
//            put(4, chipBuilder("Coffee", R.drawable.ic_coffee))
//        }
//    }

//    private fun setupChipGroupCategory() {
//        chipGroupCategory.removeView(chipAll)
//        for ((key, chip) in chipsMap) {
//            //add this line to fix remove child's parent first
//            chipGroupCategory.removeView(chip)
//            chipGroupCategory.addView(chip)
//        }
//        chipGroupCategory.check(chipAll.id)
//
//        chipGroupCategory.setOnCheckedChangeListener(ChipGroup.OnCheckedChangeListener { chipGroup, i ->
//            /*try {
//                val chip: Chip = chipGroup.findViewById(i)
//                if (chip != null) {
//                    for (i in 0 until categoryArrayList.size) {
//                        if (categoryArrayList[i].categoryName?.equals(chip.chipText)!!) {
//                            viewPagerProviders.currentItem = i
//                        }
//                    }
//                }
//            } catch (e: Exception) {
//            }*/
//        })
//
//    }

//    private fun chipBuilder(chipTitle: String, chipIcon: Int = 0): Chip {
//        val chip = Chip(requireContext())
//        chip.setChipDrawable(ChipDrawable.createFromAttributes(requireContext(), null, 0, R.style.BaseChip))
//        chip.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.color_chip_text))
//        chip.text = chipTitle
//
//        /* Glide.with(this)
//             .asBitmap()
//             .load("https://hlink-bucket-office1.s3-eu-west-1.amazonaws.com/coming/profile_image/5f4c9b998c97a1598824800.jpg")
//             .into(object : CustomTarget<Bitmap>(){
//                 override fun onLoadCleared(placeholder: Drawable?) {
//                     // this is called when imageView is cleared on lifecycle call or for
//                     // some other reason.
//                     // if you are referencing the bitmap somewhere else too other than this imageView
//                     // clear it here as you can no longer have the bitmap
//                 }
//
//                 override fun onResourceReady(resource: Bitmap, transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?) {
//                     val drawable: Drawable = BitmapDrawable(resources, resource)
//                     chip.chipIcon = drawable
//                 }
//             })*/
//
//        //chip.chipIcon = ContextCompat.getDrawable(requireContext(), chipIcon)
//        chip.chipIconTint = ContextCompat.getColorStateList(requireContext(), R.color.color_chip_text)
//        chip.isChipIconVisible = true
//        return chip
//    }

    private fun setupRecyclerViewOrderTypes() {
        val types = arrayListOf(
            DeliveryType("Personal\nShopper", R.drawable.ic_bag, R.color.colorTypePersonalShopper),
            DeliveryType("Grocery", R.drawable.ic_food, R.color.colorTypeGrocery),
            DeliveryType("Health\n& Beauty", R.drawable.ic_health, R.color.colorTypeHealth),
            DeliveryType("Grill it\nYourself", R.drawable.ic_grill, R.color.colorTypeGrill)
        )

        adapter = DeliveryTypeAdapter(merchantCategory, this)

        recyclerViewTypes.adapter = adapter
        recyclerViewTypes.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
    }

    fun updateDetails() {
        session.user?.let {
            try {
                (activity as HomeActivity).let {
                    it.updateDetails()
                }
            } catch (e: Exception) {

            }


        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_SERVICE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.hasExtra(AppCommon.Location.LATITUDE)) {
                latitude = data.getStringExtra(AppCommon.Location.LATITUDE)
                longitude = data.getStringExtra(AppCommon.Location.LONGITUDE)
                val address = data.getStringExtra(AppCommon.Location.ADDRESS)
                preferences.putString("lati", latitude)
                preferences.putString("longi", longitude)
                preferences.putString("addr", address)

                Log.e("Ahmed", "onActivityResult: " + appPreferences.getString("longi") + appPreferences.getString("lati"))

                Log.e("clicked", "DATA : $latitude")
                Log.e("clicked", "DATA : $longitude")
                if (fragmentList.size != 0) {
                    observeCategory()
                    Log.e("clicked", "انا هنا")
                    fragmentList[viewPagerProviders.currentItem].onSetLatLong(latitude, longitude)
                }

            }
        }
    }

    override fun onDeliverTypeSelected(adapterPosition: Int, merchantCategory: MerchantCategory) {
//        if (adapterPosition == 0) {
//            navigator.loadActivity(IsolatedActivity::class.java).addBundle(Bundle().apply { putSerializable(ActivityStarter.ACTIVITY_FIRST_PAGE, SpecialOrderFragment::class.java) }).start()
//        } else {

        callGetCategoryApi(merchantCategory.id.toString())
        Log.i("clicked", "adapter position : $adapterPosition")
        if (fragmentList.size != 0) {
            Log.i("clicked", "here")
            merchantCategory.id?.let {
                fragmentList[viewPagerProviders.currentItem].onMerchantCategoryId(it)
            }
        }

        //  }
    }


    override fun createLayout(): Int = R.layout.home_fragment

    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        //handle error by removing try catch block
        try {
            super.onCreate(savedInstanceState)
        } catch (e: Exception) {

        }
    }

    private fun setLiveData() {
        //GetCategory live data source
        observeCategory()
        //getProfile live data source
        authViewModel.getProfileLiveData.observe(this,
            { responseBody ->
                hideLoader()
//                showToastLong(responseBody.message)
                if (responseBody.responseCode == 1 && responseBody.data != null) {
                    responseBody.data?.let {
                        session.user = it
                        session.userSession = it.token.toString()
                        session.userId = it.id.toString()

                        session.user?.isVerified?.let {
                            if (it == "0") {
                                appPreferences.putBoolean(AppConstants.USER_LOGIN_FIRST_TIME, true)
                            } else {
                                appPreferences.putBoolean(AppConstants.USER_LOGIN_FIRST_TIME, false)
                            }
                        }
                        updateDetails()

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
                    if (throwable.code == 0 || throwable.code == 2) {
                        throwable.message?.let { showToastLong(it) }
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

        //getTopSlider live data source
        authViewModel.getTopSliderLiveData.observe(this,
            { responseBody ->
                hideLoader()
                // showToastLong(responseBody.message)
                if (responseBody.responseCode == 1 && responseBody.data != null) {
                    homeMerchantSlider.clear()
                    responseBody.data?.let {
                        homeMerchantSlider.addAll(it)
                    }
                    homeSliderPagerAdapter?.notifyDataSetChanged()
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
                    if (throwable.code == 0 || throwable.code == 2) {
                        throwable.message?.let { showToastLong(it) }
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


        //getMerchantCategoryList live data source
        authViewModel.getMerchantCategoryListLiveData.observe(this,
            { responseBody ->
                hideLoader()
//                showToastLong(responseBody.message)
                if (responseBody.responseCode == 1 && responseBody.data != null) {
                    merchantCategory.clear()
                    responseBody.data?.let {
                        //  merchantCategory.add(MerchantCategory(resources.getString(R.string.label_personal_shopper), "", "", "", "", "", ""))
                        for (i in 0 until it.size) {
//                            //do not translate this string
//                            if (it[i].merchantCategoryName.equals("Restaurant Merchant")) {
//
//                            } else {
                            merchantCategory.add(it[i])
//                            }
                        }
                    }
                    adapter?.notifyDataSetChanged()
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
                    if (throwable.code == 0 || throwable.code == 2) {
                        throwable.message?.let { showToastLong(it) }
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

    private fun observeCategory() {
        //GetCategory live data source
        authViewModel.getCategoryLiveData.observe(this,
            { responseBody ->
                hideLoader()
                //showToastLong(responseBody.message)
                if (responseBody.responseCode == 1 && responseBody.data != null) {
                    fragmentList.clear()
                    fragmentList.add(ProvidersFragment().newInstance(CategoryList("", "", resources.getString(R.string.label_all), "", "", "", "", true, "")))
                    //  responseBody.data?.let {
//                        categoryArrayList.clear()
//                        categoryArrayList.add(CategoryList("", "", resources.getString(R.string.label_all), "", "", "", " ", true, ""))
//                        categoryArrayList.addAll(it)
//                        Log.e("clicked", "categoryArrayList: "+categoryArrayList.toString() )

//                        chipGroupCategory.removeView(chipAll)
//                        chipGroupCategory.addView(chipAll)
//                        chipsMap.put(1, chipAll)
//                        for (i in 0 until it.size) {
//                            Log.e("clicked", "ProvidersFragment:it[i]: " + it[i])
//                            fragmentList.add(ProvidersFragment(it[i]))
//                            // chipsMap.put(i + 2, chipBuilder(it[i].categoryName.toString()))
//                        }

                    pagerAdapter?.notifyDataSetChanged()
                    //categoryAdapter?.notifyDataSetChanged()

                    setupViewPagerProviders()
                    //setupChipGroupCategory()
                    // }

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
                    when (throwable.code) {
                        0 -> {
                            throwable.message?.let { showToastLong(it) }
                            //{"category_id":"","lat":"30.7904709","lng":"31.0023716","merchant_category_id":"3","name":"","page":"2","user_id":"1510"}
                        }
                        // Category not found
                        2 -> {
                            fragmentList.clear()
                            fragmentList.add(ProvidersFragment().newInstance(CategoryList("", "", resources.getString(R.string.label_all), "", "", "", "", true, "")))
                            pagerAdapter?.notifyDataSetChanged()
                            setupViewPagerProviders()
                        }
                        3, 11 -> {
                            Toast.makeText(requireActivity(), resources.getString(R.string.inactive_account), Toast.LENGTH_SHORT).show()
                            appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                            navigator.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                        }
                    }
                } else if (throwable is AuthenticationException) {
                    Toast.makeText(requireActivity(), resources.getString(R.string.invalid_access), Toast.LENGTH_SHORT).show()
                    appPreferences.clearAll()
                    appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                    navigator.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                }
//                else if(throwable is ProtocolException ||throwable is ConnectException){
//                    Toast.makeText(requireActivity(), resources.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
//                }else {
//                    Toast.makeText(requireActivity(),throwable.message , Toast.LENGTH_SHORT).show()
//                }
                false
            })
    }

    //getCategory api
    private fun callGetCategoryApi(merchantCategoryId: String) {
        /**
         *
         * params : getCategory()
         *
         * optional :
         *
         * */
        //Todo:commented
//        locationManager.triggerLocation(object : LocationManager.LocationListener {
//            override fun onLocationAvailable(latLng: LatLng) {
//                if (latLng != null) {
//
        showLoader()
        val apiRequestParams = ApiRequestParams()
        //load restaurants by default for first time
        apiRequestParams.merchant_category_id = merchantCategoryId
        appPreferences.putString("merchantCategoryId", merchantCategoryId)
        //Todo:commented
        //apiRequestParams.latitude = 24.7136.toString()
        //apiRequestParams.longitude = 46.6753.toString()
        Log.i("clicked", "hit api: " + merchantCategoryId)
        authViewModel.getCategory(apiRequestParams)
        //   locationManager.stop()
//                }
//            }
//
//            override fun onFail(status: LocationManager.LocationListener.Status) {
//                showAlertDialog(resources.getString(R.string.label_we_need_location_permission_for_get_nearest_store), object : BaseActivity.DialogOkListener {
//                    override fun onClick() {
//                        callGetCategoryApi(merchantCategoryId)
//                    }
//                }, getString(R.string.label_ok))
//            }
//        })

    }

    //getProfile api
    private fun callGetProfileApi() {
        /**
         *
         * params : getProfile(user_id)
         *
         * optional :
         *
         * */
        val apiRequestParams = ApiRequestParams()
        apiRequestParams.user_id = session.userId
        authViewModel.getProfile(apiRequestParams)
    }

    //getTopSlider api
    private fun callGetTopSliderApi() {
        /**
         *
         * params : getTopSlider()
         *
         * optional :
         *
         * */
        //       val apiRequestParams = ApiRequestParams()
        //       apiRequestParams.user_id = session.userId
        authViewModel.getTopSlider()
    }

    //getMerchantCategoryList api
    private fun callGetMerchantCategoryListApi() {
        /**
         *
         * params : getMerchantCategoryList()
         *
         * optional :
         *
         * */
        val apiRequestParams = ApiRequestParams()
//        apiRequestParams.user_id = session.userId
        authViewModel.getMerchantCategoryList(apiRequestParams)
    }


//    private fun showAlertDialog(message: String){
//        if(activity != null) {
//            val alertBox = AlertDialog.Builder(activity)
//            alertBox.setTitle(resources.getString(R.string.warning))
//            alertBox.setMessage(message)
//            alertBox.setIcon(R.mipmap.ic_launcher_round)
//
//            alertBox.setPositiveButton(resources.getString(R.string.cancel)) { args0, args1 ->
//                args0.cancel()
//                this@HomeFragment.requireActivity().finish()
//            }
//            alertBox.setNegativeButton(resources.getString(R.string.retry)) { args0, args1 ->
//                showLoader()
//                bindData()
//            }
//            val dialog = alertBox.create()
//            dialog.setCanceledOnTouchOutside(false)
//            dialog.show()
//            var b = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
//            b.setTextColor(Color.parseColor("#FFFFFF"))
//            var NegBu = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
//            NegBu.setTextColor(Color.parseColor("#FFFFFF"))
//            val params = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//            )
//            params.setMargins(20, 0, 20, 0)
//            NegBu.setLayoutParams(params)
//        }
//    }


}