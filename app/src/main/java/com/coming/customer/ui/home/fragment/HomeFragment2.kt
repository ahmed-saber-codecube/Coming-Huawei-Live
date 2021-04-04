package com.coming.customer.ui.home.fragment

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.coming.customer.R
import com.coming.customer.data.pojo.DeliveryType
import com.coming.customer.data.pojo.HomeMerchantSlider
import com.coming.customer.data.pojo.MerchantCategory
import com.coming.customer.ui.base.BaseFragment
import com.coming.customer.ui.home.DeliveryTypeAdapter
import com.coming.customer.ui.home.HomePagerAdapter
import com.coming.customer.ui.home.OnDeliveryTypeSelectedListener
import com.coming.customer.ui.isolated.IsolatedActivity
import com.coming.customer.ui.manager.ActivityStarter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.home_fragment_2.*
import java.util.*

@AndroidEntryPoint
class HomeFragment2 : BaseFragment(), View.OnClickListener, OnDeliveryTypeSelectedListener, HomePagerAdapter.OnSliderClicked {

    var homeMerchantSlider = ArrayList<HomeMerchantSlider>()
    var homeSliderPagerAdapter: HomePagerAdapter? = null

    var merchantCategory = ArrayList<MerchantCategory>()
    var adapter: DeliveryTypeAdapter? = null

    private var selectedChipIndex = 1
    private var chipsMap: HashMap<Int, Chip> = HashMap()

    override fun bindData() {
        setupListeners()
        setupViewPager()
        setupRecyclerViewOrderTypes()
        setupViewPagerProviders()
        prepareChipsMap()
        setupChipGroupCategory()
    }


    private fun setupListeners() {
        imageViewLocation.setOnClickListener(this)
        imageViewSearch.setOnClickListener(this)
    }

    private fun setupViewPager() {
        homeSliderPagerAdapter = HomePagerAdapter(homeMerchantSlider, this)
        viewPagerHome.adapter = homeSliderPagerAdapter

//        TabLayoutMediator(tabLayoutHome, viewPagerHome) { _, _ -> }.attach()
    }

    override fun onMerchantOpen(homeMerchantSlider: HomeMerchantSlider) {

    }

    private fun setupViewPagerProviders() {
//        val pagerAdapter = ProvidersPagerAdapter(requireActivity(),4)
//        viewPagerProviders.adapter = pagerAdapter

        viewPagerProviders.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                chipGroupCategory.check(chipsMap[position + 1]?.id!!)
            }
        })
    }

    private fun prepareChipsMap() {
        chipGroupCategory.removeView(chipAll)
        chipsMap.apply {
            put(1, chipAll)
            put(2, chipBuilder("Fast Food", R.drawable.ic_category))
            put(3, chipBuilder("Dessert", R.drawable.ic_donut))
            put(4, chipBuilder("Coffee", R.drawable.ic_coffee))
        }
    }

    private fun setupChipGroupCategory() {
        for ((key, chip) in chipsMap) {
            chipGroupCategory.addView(chip)
        }
        chipGroupCategory.check(chipsMap[1]!!.id)
    }

    private fun chipBuilder(chipTitle: String, chipIcon: Int): Chip {
        val chip = Chip(requireContext())
        chip.setChipDrawable(ChipDrawable.createFromAttributes(requireContext(), null, 0, R.style.BaseChip))
        chip.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.color_chip_text))
        chip.text = chipTitle
        chip.chipIcon = ContextCompat.getDrawable(requireContext(), chipIcon)
        chip.chipIconTint = ContextCompat.getColorStateList(requireContext(), R.color.color_chip_text)
        chip.isChipIconVisible = true
        return chip
    }

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


    override fun onDeliverTypeSelected(adapterPosition: Int, merchantCategory: MerchantCategory) {
        navigator.loadActivity(IsolatedActivity::class.java)
            .addBundle(Bundle().apply {
                putSerializable(
                    ActivityStarter.ACTIVITY_FIRST_PAGE,
                    SpecialOrderFragment::class.java
                )
            })
            .start()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.imageViewLocation -> {
                navigator.loadActivity(IsolatedActivity::class.java)
                    .addBundle(Bundle().apply {
                        putSerializable(ActivityStarter.ACTIVITY_FIRST_PAGE, PickLocationFragment::class.java)
                    })
                    .start()
            }

            R.id.imageViewSearch -> {
                navigator.load(SearchFragment::class.java)
                    .replace(true)
            }
        }
    }

    override fun createLayout(): Int = R.layout.home_fragment_2

}