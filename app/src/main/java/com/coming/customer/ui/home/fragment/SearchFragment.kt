package com.coming.customer.ui.home.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.coming.customer.R
import com.coming.customer.apiparams.ApiRequestParams
import com.coming.customer.core.Session
import com.coming.customer.data.pojo.AppConstants
import com.coming.customer.data.pojo.CategoryList
import com.coming.customer.data.pojo.Store
import com.coming.customer.exception.ServerException
import com.coming.customer.ui.HomeCategoryAdapter
import com.coming.customer.ui.auth.activity.AuthActivity
import com.coming.customer.ui.base.BaseFragment
import com.coming.customer.ui.home.OnStoreSelectedListener
import com.coming.customer.ui.home.ProvidersPagerAdapter
import com.coming.customer.ui.isolated.IsolatedActivity
import com.coming.customer.ui.payment.StoreDetailsActivity
import com.coming.customer.ui.viewmodel.AuthViewModel
import com.coming.customer.util.extentions.textChanges
import com.google.android.material.chip.Chip
import com.throdle.exception.AuthenticationException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.home_fragment_search.*
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.net.ConnectException
import java.net.ProtocolException
import java.util.*

@AndroidEntryPoint
class SearchFragment : BaseFragment(), View.OnClickListener, OnStoreSelectedListener, HomeCategoryAdapter.OnItemClick {

    //var categoryAdapter: HomeCategoryAdapter? = null

    // val categoryArrayList = ArrayList<CategoryList>()

    private val fragmentList = arrayListOf<ProvidersFragment>()

    var pagerAdapter: ProvidersPagerAdapter? = null

    private var chipsMap: HashMap<Int, Chip> = HashMap()

    private val parent: IsolatedActivity get() = requireActivity() as IsolatedActivity

    override fun bindData() {
        setupListeners()
//        setupChipGroupCategory()
        //getCategory api call

        //  setupRecyclearview()

        callGetCategoryApi()
        setupSearchEditText()
        setLiveData()
//        prepareChipsMap()
//        setupChipGroupCategory()
//        setupViewPagerProviders()
    }

//    private fun setupRecyclearview() {
////        private val confirmationCartAdapter by lazy { BookingRequiestDetailsAdapter(myBookingArrayList, this,session) }
//        categoryAdapter = HomeCategoryAdapter(categoryArrayList, this)
//        recyclerViewCategory.adapter = categoryAdapter
//        recyclerViewCategory.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
//    }

    override fun onItemClicked(position: Int) {
        viewPagerProviders.currentItem = position
//        if (fragmentList.size != 0) {
//            fragmentList[viewPagerProviders.currentItem].onSearch("")
//        }
    }

    override fun onResume() {
        super.onResume()
        parent.apply {
            showToolbar(false)
        }
    }

    private fun setupListeners() {
        imageViewBack.setOnClickListener(this)
    }

    private fun setupViewPagerProviders() {
//        pagerAdapter = ProvidersPagerAdapter(requireActivity(), fragmentList)
//        viewPagerProviders.adapter = pagerAdapter
        pagerAdapter = ProvidersPagerAdapter(childFragmentManager, fragmentList)
        viewPagerProviders.adapter = pagerAdapter

//        viewPagerProviders?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
//            override fun onPageScrollStateChanged(state: Int) {
//            }
//
//            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
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

        /*  viewPagerProviders.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
              override fun onPageSelected(position: Int) {
                  chipGroupCategory.check(chipsMap[position + 1]?.id!!)
              }
          })*/
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
//        // chip.chipIcon = ContextCompat.getDrawable(requireContext(), chipIcon)
//        chip.chipIconTint = ContextCompat.getColorStateList(requireContext(), R.color.color_chip_text)
//        chip.isChipIconVisible = true
//        return chip
//    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupSearchEditText() {
        /*editTextSearch.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyBoard()
                if (fragmentList.size != 0) {
                    fragmentList[viewPagerProviders.currentItem].onSearch(editTextSearch.text.toString().trim())
                }
                return@OnEditorActionListener true
            }
            false
        })*/
        editTextSearch.textChanges()
            .onEach {
                if (fragmentList.size != 0) {
                    Log.e("clicked", "afterTextChanged: " + it)
                    fragmentList[viewPagerProviders.currentItem].onSearch(it.toString())
                }
            }
            .launchIn(lifecycleScope)

//        editTextSearch.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(p0: Editable?) {
//
//            }
//
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//        })

        layout_search.setEndIconOnClickListener {
            editTextSearch.setText("")
            if (fragmentList.size != 0) {
                fragmentList[viewPagerProviders.currentItem].onSearch(editTextSearch.text.toString().trim())
            }
        }
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.imageViewBack -> {
                navigator.goBack()
            }
        }
    }

    override fun onStoreSelected(store: Store) {
        /*navigator.loadActivity(IsolatedPaymentGetawayActivity::class.java)
            .addBundle(Bundle().apply { putSerializable(ActivityStarter.ACTIVITY_FIRST_PAGE, StoreDetailFragment::class.java) })
            .start()*/
        navigator.loadActivity(StoreDetailsActivity::class.java).addBundle(Bundle().apply {
            /*putSerializable(ActivityStarter.ACTIVITY_FIRST_PAGE, StoreDetailFragment::class.java)*/
            putSerializable(Session.DATA, store)
        }).start()
    }

    override fun createLayout(): Int = R.layout.home_fragment_search


    private val authViewModel by viewModels<AuthViewModel>()


    private fun setLiveData() {
        //GetCategory live data source
        authViewModel.getCategoryLiveData.observe(this,
            { responseBody ->
                hideLoader()
                //showToastLong(responseBody.message)
                if (responseBody.responseCode == 1 && responseBody.data != null) {
                    fragmentList.clear()
                    fragmentList.add(ProvidersFragment().newInstance(CategoryList("", "", resources.getString(R.string.label_all), "", "", "", "", true, "")))
                    responseBody.data?.let {
                        // categoryArrayList.clear()
                        // categoryArrayList.add(CategoryList("", "", resources.getString(R.string.label_all), "", "", "", "", true, ""))
                        // categoryArrayList.addAll(it)
//                        chipGroupCategory.removeView(chipAll)
//                        chipGroupCategory.addView(chipAll)
//                        chipsMap.put(1, chipAll)
//                        for (i in 0 until it.size) {
//                            fragmentList.add(ProvidersFragment(it[i]))
//                            chipsMap.put(i + 2, chipBuilder(it[i].categoryName.toString()))
//                        }

                        pagerAdapter?.notifyDataSetChanged()

                        // categoryAdapter?.notifyDataSetChanged()

                        setupViewPagerProviders()
                        //setupChipGroupCategory()
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
                        fragmentList.clear()
                        fragmentList.add(ProvidersFragment().newInstance(CategoryList("", "", resources.getString(R.string.label_all), "", "", "", "", true, "")))
                        pagerAdapter?.notifyDataSetChanged()
                        setupViewPagerProviders()
                        // throwable.message?.let { showToastLong(it) }
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

    //getCategory api
    private fun callGetCategoryApi() {
        /**
         *
         * params : getCategory()
         *
         * optional :
         *
         * */
        showLoader()
        val apiRequestParams = ApiRequestParams()
        apiRequestParams.merchant_category_id = appPreferences.getString("merchantCategoryId")

        authViewModel.getCategory(apiRequestParams)
    }
}