package com.coming.customer.ui.home.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coming.customer.BuildConfig
import com.coming.customer.R
import com.coming.customer.apiparams.ApiRequestParams
import com.coming.customer.core.AppPreferences
import com.coming.customer.core.LocationManager
import com.coming.customer.data.pojo.*
import com.coming.customer.data.repository.UserRepository
import com.coming.customer.exception.ServerException
import com.coming.customer.ui.auth.activity.AuthActivity
import com.coming.customer.ui.home.AddOnAdapter
import com.coming.customer.ui.home.DishSizeAdapter
import com.coming.customer.ui.home.StoreDetailsUpdateCounterInf
import com.coming.customer.ui.payment.StoreDetailsActivity
import com.coming.customer.ui.viewmodel.AuthViewModel
import com.coming.customer.util.loadDrawableCircle
import com.coming.customer.util.twoDecimal
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.throdle.exception.AuthenticationException
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.home_dialog_add_dish.*
import kotlinx.android.synthetic.main.store_details_activity.*
import java.net.ConnectException
import java.net.ProtocolException

@AndroidEntryPoint
class AddDishDialogFragment : DialogFragment(), View.OnClickListener, AddOnAdapter.OnItemClick {

    lateinit var storeDetailsUpdateCounterInf: StoreDetailsUpdateCounterInf
    lateinit var locationManager: LocationManager

    fun newInstance(mStoreDetailsUpdateCounterInf: StoreDetailsUpdateCounterInf, mLocationManager: LocationManager) = AddDishDialogFragment().apply {
        storeDetailsUpdateCounterInf = mStoreDetailsUpdateCounterInf
        locationManager = mLocationManager
    }

    private var compositeDisposable: CompositeDisposable? = null
    lateinit var appPreferences: AppPreferences
    var selectedItemPrice: String = "0"
    var selectedExtraItemPrice = 0.0

    lateinit var animate: Animation
    private val authViewModel by viewModels<AuthViewModel>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.home_dialog_add_dish, container, false)
    }

    override fun onResume() {
        super.onResume()

        val calculatedWidth = resources.displayMetrics.widthPixels * 90 / 100
        val calculatedHeight = resources.displayMetrics.heightPixels * 90 / 100

        val params = dialog?.window?.attributes
        params?.width = calculatedWidth
        params?.height = calculatedHeight
        dialog?.window?.apply {
            attributes = params
            //  setBackgroundDrawableResource(R.drawable.bg_rounded_rect_18dp)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        appPreferences = AppPreferences(requireContext())
        setupListeners()
        setupRecyclerView()
        compositeDisposable = CompositeDisposable()
        callItemDetailApi()
    }

    private fun setupRecyclerView() {
        recyclerViewSizes.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        sizeAdapter = DishSizeAdapter()
        recyclerViewSizes.adapter = sizeAdapter
        sizeAdapter?.selectAmount = { amount ->
            selectedItemPrice = amount
            updateButtonPrice()
        }
    }

    var sizeAdapter: DishSizeAdapter? = null

    @SuppressLint("ResourceType")
    private fun updateDetails() {
        var addOnAdapter: AddOnAdapter? = null
//        when {
//            appPreferences.getString(AppCommon.LANGUAGE) == AppCommon.EN -> {
        itemDetails?.let {
            it.itemDetail?.let {
                it.itemImage?.let {
                    if (imageViewDish != null)
                        imageViewDish.loadDrawableCircle(it, R.drawable.sign_up_avatar, 100, 100)
                }
            }
            it.itemDetail?.let {
                it.itemName?.let {
                    if (textViewDishName != null)
                        textViewDishName.text = it
                }
            }
            it.itemDetail?.let {
                it.itemDescription?.let {
                    if (textViewIngredients != null)
                        textViewIngredients.text = it
                }
            }
            it.itemDetail?.let { detail ->
                detail.calories?.let {
                    if (it != "0")
                        textViewCalorie.visibility = View.VISIBLE
                    if (textViewCalorie != null)
                        textViewCalorie.text = it + " " + resources.getString(R.string.label_calories)
                }
            }
            it.itemDetail?.let {
                it.itemPrice?.let {
                    if (textViewDishPrice != null)
                        textViewDishPrice.text = resources.getString(R.string.label_sar) + " " + it.twoDecimal()
                }
            }
            //it.itemDetail?.let { it.itemPrice?.let { textViewDishTotal.text = resources.getString(R.string.label_sar) + " " + it } }
            if (it.itemDetail?.additionDetail == null || it.itemDetail?.additionDetail.equals("") || it.itemDetail?.additionDetail.size == 0) {
                //textViewLabelExtras.visibility = View.GONE
//                        textViewLabelUpto.visibility = View.GONE
            }
        }
//            }
//            appPreferences.getString(AppCommon.LANGUAGE) == AppCommon.AR -> {
//                itemDetails?.let {
//                    it.itemDetail?.let { it.itemImage?.let { imageViewDish.loadUrl(it) } }
//                    it.itemDetail?.let { it.itemName?.let { textViewDishName.text = it } }
//                    it.itemDetail?.let { it.itemDescription?.let { textViewIngredients.text = it } }
//                    it.itemDetail?.let { it.calories?.let { textViewCalorie.text = getEnglishNumberToArabicNumber(it,false) + " " + resources.getString(R.string.label_calories) } }
//                    it.itemDetail?.let { it.itemPrice?.let { textViewDishPrice.text = getEnglishNumberToArabicNumber(it,false)+" "+resources.getString(R.string.label_sar)} }
//               //     it.itemDetail?.let { it.itemPrice?.let { textViewDishTotal.text = getEnglishNumberToArabicNumber(it,false)+" "+resources.getString(R.string.label_sar)} }
//
//                }
//            }
//        }


        itemDetails?.let {
            it.itemDetail?.let { it.additionDetail?.let { addOnAdapter = AddOnAdapter(it, this) } }
        }

        val addOnLayoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        recyclerViewExtras.adapter = addOnAdapter
        recyclerViewExtras.layoutManager = addOnLayoutManager


        itemDetails?.let {
            it.itemDetail?.let {
                if (it.sizeDetail == null || it.sizeDetail.equals("") || it.sizeDetail.size == 0) {
                    textViewLabelSize.visibility = View.GONE
                    selectedItemPrice = it.itemPrice?.twoDecimal().toString()
                    updateButtonPrice()
                } else {
                    it.sizeDetail?.let {
                        sizeAdapter?.submitList(it)
                        if (it.size > 0) {
                            it[0].isSelected = true
                            selectedItemPrice = it[0].sizePrice?.twoDecimal().toString()
                        }
                        updateButtonPrice()
                    }
                }
            }
        }


        linearAddToCart.visibility = View.VISIBLE

    }


    override fun onExtraItemClicked() {
        selectedExtraItemPrice = 0.0
        //extras array
        itemDetails?.let {
            it.itemDetail?.let {
                it.additionDetail?.let {
                    for (i in 0 until it.size) {
                        for (t in 0 until (it[i].items?.size!!)) {
                            if (it[i]?.items?.get(t)?.isSelected!!) {
                                it[i].items?.get(t)?.additionPrice?.let {
                                    selectedExtraItemPrice += it.toFloat()
                                }
                            }
                        }
                    }
                }
            }
        }

        updateButtonPrice()
    }

    fun updateButtonPrice() {
        val curruntQty = tvDishQuantity.text.toString().trim().toInt()
        val totalPrice = selectedExtraItemPrice.toFloat() + selectedItemPrice.toFloat()
        // textViewDishTotal.text = resources.getString(R.string.label_sar) + (totalPrice * curruntQty).toString()
        textViewDishPrice.text = resources.getString(R.string.label_sar) + (totalPrice * curruntQty).toString()
//        when {
//            appPreferences.getString(AppCommon.LANGUAGE) == AppCommon.EN -> {
//                textViewDishTotal.text = resources.getString(R.string.label_sar) + (totalPrice * curruntQty).toString()
//
//                textViewDishPrice.text = resources.getString(R.string.label_sar) + (totalPrice * curruntQty).toString()
//            }
//            appPreferences.getString(AppCommon.LANGUAGE) == AppCommon.AR -> {
//                textViewDishTotal.text = getEnglishNumberToArabicNumber((totalPrice * curruntQty).toString())+resources.getString(R.string.label_sar)
//
//                textViewDishPrice.text = getEnglishNumberToArabicNumber((totalPrice * curruntQty).toString())+resources.getString(R.string.label_sar)
//
//            }
//        }

//        textViewDishPrice.text = resources.getString(R.string.label_sar) +selectedItemPrice.toFloat().toString()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.setCanceledOnTouchOutside(true)
    }

    private fun setupListeners() {
        imageViewClose.setOnClickListener(this)
        buttonPlus.setOnClickListener(this)
        buttonMinus.setOnClickListener(this)
        linearAddToCart.setOnClickListener(this)
    }

    private fun incrementQuantity() {
        var currentQuantity = tvDishQuantity.text.toString().toInt()
        tvDishQuantity.text = "${++currentQuantity}"
    }

    private fun decrementQuantity() {
        var currentQuantity = tvDishQuantity.text.toString().toInt()
        if (currentQuantity > 1) {

            tvDishQuantity.text = "${--currentQuantity}"
        }
    }

    var latitude = 0.0
    var longitude = 0.0

    override fun onClick(view: View) {
        when (view.id) {
            R.id.imageViewClose -> {
                dismiss()
            }

            R.id.buttonPlus -> {
                incrementQuantity()
                updateButtonPrice()
            }

            R.id.buttonMinus -> {
                decrementQuantity()
                updateButtonPrice()
            }

            R.id.linearAddToCart -> {
//                locationManager.triggerLocation(object : LocationManager.LocationListener {
//                    override fun onLocationAvailable(latLng: LatLng) {
//                        if (latLng != null) {
                latitude = appPreferences.getString("lati").toDouble()
                longitude = appPreferences.getString("longi").toDouble()

                if (userId != null && userId.length != 0) {
                    //callAddToCartApi()
                    var totalExtraPrice = 0
                    var mandatory = 0
                    //extras array
                    itemDetails?.let {
                        it.itemDetail?.let {
                            it.additionDetail?.let {
                                for (i in 0 until it.size) {
                                    if (it[i].isOptional?.toInt() == 0) {
                                        mandatory += it[i].maxCount?.toInt()!!
                                        for (t in 0 until (it[i].items?.size!!)) {
                                            if (it[i]?.items?.get(t)?.isSelected!!) {
                                                totalExtraPrice += 1
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Log.e("totalExtraPrice", "totalExtraPrice : " + totalExtraPrice + " mandatory : " + mandatory)
                    if (totalExtraPrice in 1..mandatory || mandatory == 0) {
                        callAddToCartApi()
                    } else {
                        showToast(resources.getString(R.string.validations_mandatory_choices))
                    }
                } else {
//                    showSnackBar(resources.getString(R.string.authentication_required_please_login))
                    storeDetailsUpdateCounterInf.onAuthenticated()
                }

//                var cartDialog = CartDialogFragment()
//                cartDialog.show(childFragmentManager, CartDialogFragment::class.simpleName)

//                            locationManager.stop()
//                        }
//                    }
//
//                    override fun onFail(status: LocationManager.LocationListener.Status) {
//                        showSnackBar(resources.getString(R.string.label_we_need_location_permission_for_get_nearest_store))
//                    }
//                })
            }
        }
    }

    /*private fun getDishSizes(): ArrayList<DishSize> = ArrayList<DishSize>().apply {
        add(DishSize(1, "Small", "SAR 15.00"))
        add(DishSize(2, "Medium", "SAR 20.00"))
        add(DishSize(3, "Large", "SAR 25.00"))
    }

    private fun getDishAddons(): ArrayList<DishSize> = ArrayList<DishSize>().apply {
        add(DishSize(1, "Tomatoes", "SAR 5.00"))
        add(DishSize(2, "Cheese", "SAR 10.00"))
        add(DishSize(3, "Onions", "SAR 5.00"))
        add(DishSize(4, "Olives", "SAR 10.00"))
        add(DishSize(5, "Mushrooms", "SAR 5.00"))
        add(DishSize(6, "Pickles", "SAR 2.00"))
    }*/


    var disposableItemDetail: Disposable? = null
    var disposableAddToCar: Disposable? = null
    var disposableCleanCart: Disposable? = null
    lateinit var repository: UserRepository
    var userId: String = ""

    var itemDetailItem: ItemDetailItem? = null
    var itemDetails: ItemDetails? = null
    var store: Store? = null

    private fun callItemDetailApi() {
        disposableItemDetail?.dispose()
        //item_id,  optional : user_id
        val apiRequestParams = ApiRequestParams()
        apiRequestParams.item_id = itemDetailItem?.id
        repository.itemDetail(apiRequestParams)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<DataWrapper<ItemDetails>> {
                override fun onSuccess(data: DataWrapper<ItemDetails>) {
                    when (data.responseBody?.responseCode) {
                        null -> {
                            when (data.throwable?.message) {
                                "Authentication Exception" -> {
                                    Toast.makeText(requireActivity(), resources.getString(R.string.invalid_access), Toast.LENGTH_SHORT).show()
                                    appPreferences.clearAll()
                                    appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                                    storeDetailActivity.loadActivity(AuthActivity::class.java).byFinishingAll().start()
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
                            //showSnackBar(data.responseBody.message!!)
                            data.responseBody.data?.let {
                                itemDetails = it
                            }
                            updateDetails()
                        }
                    }
                }

                override fun onSubscribe(d: Disposable) {
                    disposableItemDetail = d
                    compositeDisposable?.add(d)
                }

                override fun onError(e: Throwable) {
                    if (e is ServerException) {
                        e.message?.let {
                            showToast(it)
                        }
                    }
                    if (e is AuthenticationException) {
                        Toast.makeText(requireActivity(), resources.getString(R.string.invalid_access), Toast.LENGTH_SHORT).show()
                        appPreferences.clearAll()
                        appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                        storeDetailActivity.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                    } else if (e is ProtocolException || e is ConnectException) {
                        Toast.makeText(requireActivity(), resources.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

    lateinit var storeDetailActivity: StoreDetailsActivity
    private fun callAddToCartApi() {
        disposableAddToCar?.dispose()

        //user_id,merchant_id,item_id,quantity,item_size_id  optional : addition_detail
        val apiRequestParams = ApiRequestParams()

        var itemDetailsSize = ""
        //size array
        itemDetails?.let {
            it.itemDetail?.let {
                it.sizeDetail?.let {
                    for (i in 0 until it.size) {
                        if (it[i].isSelected) {
                            if (itemDetailsSize.length == 0) {
                                itemDetailsSize += it[i].id
                            } else {
                                itemDetailsSize += "," + it[i].id
                            }
                        }
                    }
                }
            }
        }
        var itemDetailsExtras = ""
        //extras array
        itemDetails?.let {
            it.itemDetail?.let {
                it.additionDetail?.let {
                    for (i in 0 until it.size) {
                        for (t in 0 until (it[i].items?.size!!)) {
                            if (it[i]?.items?.get(t)?.isSelected!!) {
                                if (itemDetailsExtras.length == 0) {
                                    itemDetailsExtras += it[i].items?.get(t)?.id
                                } else {
                                    itemDetailsExtras += "," + it[i].items?.get(t)?.id
                                }
                            }
                        }
                    }
                }
            }
        }

//        if (itemDetailsSize.length == 0) {
//            showSnackBar("Please select size")
//        }
//        else {
        if (itemDetailsExtras.length != 0) {
            apiRequestParams.addition_detail = itemDetailsExtras
        }
        apiRequestParams.item_size_id = itemDetailsSize
        apiRequestParams.user_id = userId
        itemDetails?.let {
            it.itemDetail?.let {
                it.merchantId?.let {
                    apiRequestParams.merchant_id = it
                }
                it.id?.let {
                    apiRequestParams.item_id = it
                }
            }
        }
        apiRequestParams.quantity = tvDishQuantity.text.toString().trim()

        apiRequestParams.branch_id = store?.branchId
        apiRequestParams.address_lat = latitude.toString()
        apiRequestParams.address_lng = longitude.toString()
        repository.addToCart(apiRequestParams)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<DataWrapper<CartDetails>> {
                override fun onSuccess(data: DataWrapper<CartDetails>) {
                    when (data.responseBody?.responseCode) {
                        null -> {
                            when (data.throwable?.message) {
                                "Authentication Exception" -> {
                                    Toast.makeText(requireActivity(), resources.getString(R.string.invalid_access), Toast.LENGTH_SHORT).show()
                                    appPreferences.clearAll()
                                    appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                                    storeDetailActivity.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                                }
                                "Failed to connect to /${BuildConfig.BASE_URL}:8507" -> {
                                    Toast.makeText(requireActivity(), resources.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
                                }
                                else -> {
                                    Toast.makeText(requireActivity(), data.throwable?.message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        2 -> {
                            data.responseBody.message?.let {
                                showToast(it)
                            }
                        }
                        1 -> {
//
                            try {
                                Handler().postDelayed({
                                    dismiss()
                                    data.responseBody.message?.let {
                                        showToast(it)
                                    }
                                    storeDetailsUpdateCounterInf.onUpdateCounter(data.responseBody.data?.totalItem.toString())
                                    YoYo.with(Techniques.Shake)
                                        .duration(1000)
                                        .repeat(0)
                                        .playOn(storeDetailActivity.textViewCartCounter)
                                    YoYo.with(Techniques.Shake)
                                        .duration(1000)
                                        .repeat(0)
                                        .playOn(storeDetailActivity.imageViewCart)
                                }, 100)
                                // animate.showAnimation(storeDetailsUpdateCounterInf,data.responseBody.data?.totalItem.toString(),data.responseBody.message)
//                                        Handler().postDelayed({
//
//                                        },100)
                            } catch (e: Exception) {

                            }

                        }
                        0 -> {
                            data.responseBody.message?.let {
                                showToast(it)
                                // showAlertDialog(it + "\n")

                            }
                        }
                        5 -> {
                            data.responseBody.message?.let {
                                //showSnackBar(it)
                                showAlertDialog(getString(R.string.singleton_merchant_purchasing) + "\n")

                            }
                        }
                    }
                }

                override fun onSubscribe(d: Disposable) {
                    disposableAddToCar = d
//                    compositeDisposable?.add(d)
                }

                override fun onError(e: Throwable) {
                    if (e is ServerException) {
                        showToast(e.message!!)
                    }
                    if (e is AuthenticationException) {
                        Toast.makeText(requireActivity(), resources.getString(R.string.invalid_access), Toast.LENGTH_SHORT).show()
                        appPreferences.clearAll()
                        appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                        storeDetailActivity.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                    } else if (e is ProtocolException || e is ConnectException) {
                        Toast.makeText(requireActivity(), resources.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }


    private fun showAlertDialog(message: String) {
        if (activity != null) {
            val alertBox = AlertDialog.Builder(activity)
            alertBox.setTitle(resources.getString(R.string.warning))
            alertBox.setMessage(message)
            alertBox.setIcon(R.drawable.ic_warning_black_24dp)

            alertBox.setPositiveButton(resources.getString(R.string.cancel)) { args0, args1 ->
                args0.cancel()

            }
            alertBox.setNegativeButton(resources.getString(R.string.clear)) { args0, args1 ->
                Log.e("showAlertDialog", "showAlertDialog: inside")
                callGetCleanCart()
                try {
                    Handler().postDelayed(Runnable {
                        callAddToCartApi()
                        dismiss()
                    }, 700)
                } catch (e: Exception) {

                }
            }
            val dialog = alertBox.create()
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
            var b = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            b.setTextColor(Color.parseColor("#FFFFFF"))
            var NegBu = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            NegBu.setTextColor(Color.parseColor("#FFFFFF"))
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(20, 0, 20, 0)
            NegBu.setLayoutParams(params)
        }
    }

    //getCleanCart api
    private fun callGetCleanCart() {
        disposableCleanCart?.dispose()

        repository.cleanCart()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<DataWrapper<CartDetails>> {
                override fun onSuccess(data: DataWrapper<CartDetails>) {
                    when (data.responseBody?.responseCode) {
                        null -> {
                            when (data.throwable?.message) {
                                "Authentication Exception" -> {
                                    Toast.makeText(requireActivity(), resources.getString(R.string.invalid_access), Toast.LENGTH_SHORT).show()
                                    appPreferences.clearAll()
                                    appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                                    storeDetailActivity.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                                }
                                "Failed to connect to /${BuildConfig.BASE_URL}:8507" -> {
                                    Toast.makeText(requireActivity(), resources.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
                                }
                                else -> {
                                    Toast.makeText(requireActivity(), data.throwable?.message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        2 -> {
                            try {
                                data.responseBody.message?.let {
                                    showToast(it)
                                }

                                data.responseBody.data?.let {
                                    showToast(it.toString())
                                }

                                data.responseBody.data?.cartDetail?.let {
                                    showToast(it.toString())
                                }

//                                updateDetails() TODO unknown useCase
                            } catch (e: Exception) {

                            }
                        }
                    }
                }

                override fun onSubscribe(d: Disposable) {
                    disposableCleanCart = d
                }

                override fun onError(e: Throwable) {
                    if (e is ServerException) {
                        e.message?.let {
                            showToast(it)
                        }
                    }
                    if (e is AuthenticationException) {
                        Toast.makeText(requireActivity(), resources.getString(R.string.invalid_access), Toast.LENGTH_SHORT).show()
                        appPreferences.clearAll()
                        appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                        storeDetailActivity.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                    } else if (e is ProtocolException || e is ConnectException) {
                        Toast.makeText(requireActivity(), resources.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

    fun showToast(message: String) {
        Toast.makeText(this.storeDetailActivity, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        compositeDisposable?.clear()
        super.onDestroyView()
    }
}
