package com.coming.customer.ui.home.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.ItemTouchHelper
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
import com.coming.customer.ui.home.CartAdapter
import com.coming.customer.ui.home.HomeUpdateCounterInf
import com.coming.customer.ui.home.StoreDetailsUpdateCounterInf
import com.coming.customer.ui.home.activity.HomeActivity
import com.coming.customer.ui.payment.StoreDetailsActivity
import com.coming.customer.util.twoDecimal
import com.google.android.material.snackbar.Snackbar
import com.throdle.exception.AuthenticationException
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.home_dialog_cart.*
import java.net.ConnectException
import java.net.ProtocolException
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class CartDialogFragment : DialogFragment(), View.OnClickListener, CompoundButton.OnCheckedChangeListener, CartAdapter.OnCartChangeListener {
    @Inject
    lateinit var locationManager: LocationManager

    companion object {
        var storeDetailsUpdateCounterInf: StoreDetailsUpdateCounterInf? = null
        var homeDetailsUpdateCounterInf: HomeUpdateCounterInf? = null

        //FIXME: use liveModel observer
        fun newInstance(updateCounterInfStore: StoreDetailsUpdateCounterInf?, UpdateCounterInfHome: HomeUpdateCounterInf?) = CartDialogFragment().apply {
            storeDetailsUpdateCounterInf = updateCounterInfStore
            homeDetailsUpdateCounterInf = UpdateCounterInfHome
        }
    }

    internal var progressDialog: AlertDialog? = null

    var promoCode: PromoCode? = null

    var cartDetails: CartDetails? = null

    val cartDetail = ArrayList<CartDetailItem>()

    var disposable: Disposable? = null

    @Inject
    lateinit var repository: UserRepository
    var userId: String = ""
    var homeActivity: HomeActivity? = null
    var storeDetailActivity: StoreDetailsActivity? = null

    private var compositeDisposable: CompositeDisposable? = null

    var cartDialog: CartDialogFragment? = null

    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.home_dialog_cart, container, false)
    }

    override fun onResume() {
        super.onResume()

        val calculatedWidth = resources.displayMetrics.widthPixels * 90 / 100
        val calculatedHeight = resources.displayMetrics.heightPixels * 80 / 100

        val params = dialog?.window?.attributes
        params?.width = calculatedWidth
        params?.height = calculatedHeight

        dialog?.window?.apply {
//            attributes = params
            setBackgroundDrawableResource(R.drawable.img_bg_cart)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        compositeDisposable = CompositeDisposable()
        setupListeners()
        setupRecyclerViewCart()
        setupSwipeToDelete()
        setupPromocodeUI()
        callViewCartApi()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupPromocodeUI() {
        layout_promo.setEndIconOnClickListener {
            if (editTextPromoCode.text.toString().trim().isNotEmpty()) {
                hideKeyBoard()
                callCheckPromocodeApi()
            } else {
                showSnackBar(resources.getString(R.string.validations_please_enter_promocode))
            }
        }

        editTextPromoCode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (editTextPromoCode.text.toString().trim().isEmpty()) {
                    promoCode = null

                    textViewLabelDiscount.visibility = View.GONE
                    textViewDiscount.visibility = View.GONE

                    hideKeyBoard()
                    callCheckPromocodeApi()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.setCanceledOnTouchOutside(true)
    }

    private fun setupListeners() {
        buttonBack.setOnClickListener(this)
        buttonCheckout.setOnClickListener(this)
        checkBoxNote.setOnCheckedChangeListener(this)
        checkBoxPromoCode.setOnCheckedChangeListener(this)
    }

    private fun setupRecyclerViewCart() {
        cartAdapter = CartAdapter(cartDetail, this)
        val cartLayoutManager = LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)

        recyclerViewCart.adapter = cartAdapter
        recyclerViewCart.layoutManager = cartLayoutManager
    }

    private fun setupSwipeToDelete() {
        /*val itemTouchHelperCallBack = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                cartAdapter.removeItem(viewHolder.adapterPosition)
                if (cartAdapter.getCartItemCount() == 0) {
                    dismiss()
                } else {
                    Log.e("POS ", " : " + viewHolder.adapterPosition)
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallBack)
        itemTouchHelper.attachToRecyclerView(recyclerViewCart)*/

        var itemTouchHelperDirectionRight = ItemTouchHelper.RIGHT
        var itemTouchHelperDirectionLeft = ItemTouchHelper.LEFT
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, itemTouchHelperDirectionRight or itemTouchHelperDirectionLeft) {

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return true
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                if (viewHolder != null) {
//                    val foregroundView: View = (viewHolder as CartAdapter.CartItemViewHolder).viewForeground!!
//                    ItemTouchHelper.Callback.getDefaultUIUtil().onSelected(foregroundView)
                }
            }

            override fun onChildDrawOver(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder?, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
//                val foregroundView: View = (viewHolder as PlaceCustomOrderAddQtyEffectAdapter.ViewHolder).viewForeground!!
//                ItemTouchHelper.Callback.getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive)
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
//                val foregroundView: View = (viewHolder as PlaceCustomOrderAddQtyEffectAdapter.ViewHolder).viewForeground!!
//                ItemTouchHelper.Callback.getDefaultUIUtil().clearView(foregroundView)
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
//                val foregroundView: View = (viewHolder as PlaceCustomOrderAddQtyEffectAdapter.ViewHolder).viewForeground!!
//                ItemTouchHelper.Callback.getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive)
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                // cartAdapter.removeItem(viewHolder.adapterPosition)
                // cartDetailItem = cartDetail[position]
                callRemoveItemFromCartApi(cartDetail[position])
            }

            override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
                return super.convertToAbsoluteDirection(flags, layoutDirection)
            }

        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(recyclerViewCart)

    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        when (buttonView.id) {
            R.id.checkBoxNote -> {
                if (isChecked) {
                    editTextNote.visibility = View.VISIBLE
                } else {
                    editTextNote.visibility = View.GONE
                }
            }

            R.id.checkBoxPromoCode -> {
                if (isChecked) {
                    layout_promo.visibility = View.VISIBLE
                } else {
                    layout_promo.visibility = View.GONE
                }
            }

        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.buttonBack -> {
                Handler().postDelayed(Runnable {
                    dismiss()
                }, 10)

            }

            R.id.buttonCheckout -> {
                val placeOrderFragment = PlaceOrderDialogFragment().newInstance(storeDetailsUpdateCounterInf, homeDetailsUpdateCounterInf)
                placeOrderFragment.userId = userId
                placeOrderFragment.promoCode = promoCode
                placeOrderFragment.orderNote = editTextNote.text.toString().trim()
                placeOrderFragment.cartDetails = cartDetails
                placeOrderFragment.placeOrderFragment = placeOrderFragment
                placeOrderFragment.cartDialog = cartDialog
                placeOrderFragment.show(childFragmentManager, PlaceOrderDialogFragment::class.simpleName)

            }
        }
    }

    private fun getDishSizes(): ArrayList<DishSize> = ArrayList<DishSize>().apply {
        add(DishSize(1, "Small", "$15.00"))
        add(DishSize(2, "Medium", "$20.00"))
        add(DishSize(3, "Large", "$25.00"))
    }

    private fun getDishAddons(): ArrayList<DishSize> = ArrayList<DishSize>().apply {
        add(DishSize(1, "Tomatoes", "$5.00"))
        add(DishSize(2, "Cheese", "$10.00"))
        add(DishSize(3, "Onions", "$5.00"))
        add(DishSize(4, "Olives", "$10.00"))
        add(DishSize(5, "Mushrooms", "$5.00"))
        add(DishSize(6, "Pickles", "$2.00"))
    }

    fun updateDetails() {
        cartDetails?.let {
            if (textViewSubtotal != null)
                textViewSubtotal.text = resources.getString(R.string.label_currency) + " " + it.subTotal?.twoDecimal()
            textViewDelivery.text = resources.getString(R.string.label_currency) + " " + it.deliveryCost?.twoDecimal()
            textViewTax.text = resources.getString(R.string.label_currency) + " " + it.tax?.twoDecimal()
            textViewTotal.text = resources.getString(R.string.label_currency) + " " + it.grandTotal?.twoDecimal()

            it.promocodeAmount?.let {
                textViewLabelDiscount.visibility = View.VISIBLE
                textViewDiscount.visibility = View.VISIBLE
                textViewDiscount.text = resources.getString(R.string.label_currency_minus) + " " + it
            } ?: run {
                textViewLabelDiscount.visibility = View.GONE
                textViewDiscount.visibility = View.GONE
            }
        }
        promoCode
    }

    private var disposableViewCart: Disposable? = null
    private fun callViewCartApi() {
        disposableViewCart?.dispose()

        //user_id,  optional : promocode
        val apiRequestParams = ApiRequestParams()
        //apiRequestParams.user_id = userId

        promoCode?.let {
            apiRequestParams.promocode = it.promocode
        }

        try {

            repository.viewCart(apiRequestParams)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<DataWrapper<CartDetails>> {
                    override fun onSuccess(data: DataWrapper<CartDetails>) {
                        if (data.responseBody?.responseCode == null) {
                            when (data.throwable?.message) {
                                "Authentication Exception" -> {
                                    Toast.makeText(requireActivity(), resources.getString(R.string.invalid_access), Toast.LENGTH_SHORT).show()
                                    AppPreferences(requireActivity()).clearAll()
                                    AppPreferences(requireActivity()).putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                                    if (homeActivity != null) {
                                        homeActivity!!.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                                    } else {
                                        storeDetailActivity!!.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                                    }
                                }
                                "Failed to connect to /${BuildConfig.BASE_URL}:8507" -> {
                                    Toast.makeText(requireActivity(), resources.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
                                }
                                else -> {
                                    Toast.makeText(requireActivity(), data.throwable?.message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else if (data.responseBody?.responseCode == 1 && data.responseBody?.data?.showCheckout?.toBoolean() == true) {
                            try {
                                buttonCheckout.visibility = View.VISIBLE
                                data.responseBody.data?.let {
                                    cartDetails = it
                                    it.totalItem?.let {
                                        if (storeDetailsUpdateCounterInf != null) {
                                            storeDetailsUpdateCounterInf?.onUpdateCounter(it)
                                        } else {
                                            homeDetailsUpdateCounterInf?.onUpdateCounter(it)
                                        }
                                    }
                                }
                                cartDetail.clear()
                                data.responseBody.data?.cartDetail?.let {
                                    cartDetail.addAll(it)
                                }
                                cartAdapter.notifyDataSetChanged()
                                updateDetails()
                            } catch (e: Exception) {

                            }
                        } else if (data.responseBody?.responseCode == 1 && data.responseBody?.data?.showCheckout?.toBoolean() == false) {
                            try {
                                buttonCheckout.visibility = View.INVISIBLE
                                textViewLabelDelivery.visibility = View.INVISIBLE
                                textViewDelivery.visibility = View.INVISIBLE
                                textViewLabelTax.visibility = View.INVISIBLE
                                textViewTax.visibility = View.INVISIBLE
                                textViewLabelDiscount.visibility = View.INVISIBLE
                                textViewDiscount.visibility = View.INVISIBLE
                                textViewLabelTotal.visibility = View.INVISIBLE
                                textViewTotal.visibility = View.INVISIBLE
                                textViewLabelNote.visibility = View.GONE
                                checkBoxNote.visibility = View.GONE
                                textViewLabelPromoCode.visibility = View.GONE
                                checkBoxPromoCode.visibility = View.GONE
                                separator2.visibility = View.GONE
                                textViewMessage.text = resources.getString(R.string.distance_is_too_far)
                                data.responseBody.data?.let {
                                    cartDetails = it
                                    it.totalItem?.let {
                                        if (storeDetailsUpdateCounterInf != null) {
                                            storeDetailsUpdateCounterInf?.onUpdateCounter(it)
                                        } else {
                                            homeDetailsUpdateCounterInf?.onUpdateCounter(it)
                                        }
                                    }
                                }
                                cartDetail.clear()
                                data.responseBody.data?.cartDetail?.let {
                                    cartDetail.addAll(it)
                                }
                                cartAdapter.notifyDataSetChanged()
                                updateDetails()
                            } catch (e: Exception) {

                            }

                        } else if (data.responseBody?.responseCode == 2) {
                            try {
                                if (storeDetailsUpdateCounterInf != null) {
                                    storeDetailsUpdateCounterInf?.onUpdateCounter("0")
                                } else {
                                    homeDetailsUpdateCounterInf?.onUpdateCounter("0")
                                }
                                val emptyCartDialog = EmptyCartDialogFragment()
                                emptyCartDialog.show(requireActivity().supportFragmentManager, EmptyCartDialogFragment::class.simpleName)
//                                data.responseBody.message?.let {
//                                    showSnackBar(it)
//                                }
                                //    Handler().postDelayed(Runnable {
                                dismiss()
                                //    }, 100)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        }
                    }

                    override fun onSubscribe(d: Disposable) {
                        disposableViewCart = d
                        compositeDisposable?.add(d)
                    }

                    override fun onError(e: Throwable) {
                        if (e is ServerException) {
                            e.message?.let {
                                showSnackBar(it)
                            }
                        }
                        if (e is AuthenticationException) {
                            Toast.makeText(requireActivity(), resources.getString(R.string.invalid_access), Toast.LENGTH_SHORT).show()
                            AppPreferences(requireActivity()).clearAll()
                            AppPreferences(requireActivity()).putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                            if (homeActivity != null) {
                                homeActivity!!.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                            } else {
                                storeDetailActivity!!.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                            }
                        } else if (e is ProtocolException || e is ConnectException) {
                            Toast.makeText(requireActivity(), resources.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
                        }
                    }
                })

        } catch (e: Exception) {

        }
    }

    private var disposablePlusUpdate: Disposable? = null
    override fun onPlusUpdate(cartDetailItem: CartDetailItem) {
        disposablePlusUpdate?.dispose()
        //user_id,merchant_id,item_id,quantity,item_size_id  optional : addition_detail
        val apiRequestParams = ApiRequestParams()


        var itemDetailsExtras = ""
        //extras array
        cartDetailItem?.additionDetail?.let {
            for (i in 0 until it.size) {
//                for (t in 0 until (it[i].items?.size!!)) {
                if (itemDetailsExtras.isEmpty()) {
                    itemDetailsExtras += it[i].id
                } else {
                    itemDetailsExtras += "," + it[i].id
                }
//                }
            }
        }


        apiRequestParams.addition_detail = itemDetailsExtras

        apiRequestParams.user_id = userId
        apiRequestParams.merchant_id = cartDetailItem.merchantId
        apiRequestParams.item_id = cartDetailItem.itemId
        apiRequestParams.item_size_id = cartDetailItem.itemSizeId
        apiRequestParams.quantity = cartDetailItem.quantity

        repository.increaseItemToCart(apiRequestParams)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<DataWrapper<CartDetails>> {
                override fun onSuccess(data: DataWrapper<CartDetails>) {
                    when (data.responseBody?.responseCode) {
                        null -> {
                            when (data.throwable?.message) {
                                "Authentication Exception" -> {
                                    Toast.makeText(requireActivity(), resources.getString(R.string.invalid_access), Toast.LENGTH_SHORT).show()
                                    AppPreferences(requireActivity()).clearAll()
                                    AppPreferences(requireActivity()).putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                                    if (homeActivity != null) {
                                        homeActivity!!.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                                    } else {
                                        storeDetailActivity!!.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                                    }
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
                            data.responseBody.message?.let {
                                showSnackBar(it)
                            }
                            data.responseBody.data?.let {
                                cartDetails = it
                            }
                            cartDetail.clear()
                            callViewCartApi()
                            data.responseBody.data?.cartDetail?.let {
                                cartDetail.addAll(it)
                            }
                            cartAdapter.notifyDataSetChanged()
                            updateDetails()
                        }

                    }
                }

                override fun onSubscribe(d: Disposable) {
                    disposablePlusUpdate = d
                    compositeDisposable?.add(d)
                }

                override fun onError(e: Throwable) {
                    if (e is ServerException) {
                        e.message?.let { showSnackBar(it) }
                    }
                    if (e is AuthenticationException) {
                        Toast.makeText(requireActivity(), resources.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
                        AppPreferences(requireActivity()).clearAll()
                        AppPreferences(requireActivity()).putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                        if (homeActivity != null) {
                            homeActivity!!.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                        } else {
                            storeDetailActivity!!.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                        }
                    } else if (e is ProtocolException || e is ConnectException) {
                        Toast.makeText(requireActivity(), resources.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
                    }
                }
            })


    }

    private var disposableMinusUpdate: Disposable? = null

    override fun onMinusUpdate(cartDetailItem: CartDetailItem) {
        disposableMinusUpdate?.dispose()

        //user_id,merchant_id,item_id,quantity,item_size_id  optional : addition_detail
        val apiRequestParams = ApiRequestParams()


        var itemDetailsExtras = ""
        //extras array
        cartDetailItem?.additionDetail?.let {
            for (i in 0 until it.size) {
                //  for (t in 0 until (it[i].items?.size!!)) {
                if (itemDetailsExtras.isEmpty()) {
                    itemDetailsExtras += it[i].id
                } else {
                    itemDetailsExtras += "," + it[i].id
                }
                //}
            }
        }
        apiRequestParams.addition_detail = itemDetailsExtras

        apiRequestParams.user_id = userId
        apiRequestParams.merchant_id = cartDetailItem.merchantId
        apiRequestParams.item_id = cartDetailItem.itemId
        apiRequestParams.item_size_id = cartDetailItem.itemSizeId
        apiRequestParams.quantity = cartDetailItem.quantity

        repository.decreaseItemToCart(apiRequestParams)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<DataWrapper<CartDetails>> {
                override fun onSuccess(data: DataWrapper<CartDetails>) {
                    when (data.responseBody?.responseCode) {
                        null -> {
                            when (data.throwable?.message) {
                                "Authentication Exception" -> {
                                    Toast.makeText(requireActivity(), resources.getString(R.string.invalid_access), Toast.LENGTH_SHORT).show()
                                    AppPreferences(requireActivity()).clearAll()
                                    AppPreferences(requireActivity()).putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                                    if (homeActivity != null) {
                                        homeActivity!!.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                                    } else {
                                        storeDetailActivity!!.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                                    }
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
                            data.responseBody.message?.let {
                                showSnackBar(it)
                            }
                            data.responseBody.data?.let {
                                cartDetails = it
                            }
                            cartDetail.clear()
                            callViewCartApi()
                            data.responseBody.data?.cartDetail?.let {
                                cartDetail.addAll(it)
                            }
                            cartAdapter.notifyDataSetChanged()
                            updateDetails()
                        }

                    }
                }

                override fun onSubscribe(d: Disposable) {
                    disposableMinusUpdate = d
                    compositeDisposable?.add(d)
                }

                override fun onError(e: Throwable) {
                    if (e is ServerException) {
                        e.message?.let { showSnackBar(it) }
                    }
                    if (e is AuthenticationException) {
                        Toast.makeText(requireActivity(), resources.getString(R.string.invalid_access), Toast.LENGTH_SHORT).show()
                        AppPreferences(requireActivity()).clearAll()
                        AppPreferences(requireActivity()).putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                        if (homeActivity != null) {
                            homeActivity!!.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                        } else {
                            storeDetailActivity!!.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                        }
                    } else if (e is ProtocolException || e is ConnectException) {
                        Toast.makeText(requireActivity(), resources.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
                    }
                }
            })


    }

    override fun onRemoveUpdate(cartDetailItem: CartDetailItem) {
        callRemoveItemFromCartApi(cartDetailItem)
    }

    private var disposableRemoveItemFromCart: Disposable? = null
    private fun callRemoveItemFromCartApi(cartDetailItem: CartDetailItem) {
        disposableRemoveItemFromCart?.dispose()

        //item_id, user_id,  optional :
        val apiRequestParams = ApiRequestParams()
        // apiRequestParams.user_id = cartDetailItem?.userId
        apiRequestParams.item_id = cartDetailItem?.itemId
        apiRequestParams.item_size_id = cartDetailItem?.itemSizeId
        var itemDetailsExtras = ""
        cartDetailItem?.additionDetail?.let {
            for (i in 0 until it.size) {
//                for (t in 0 until (it[i].items?.size!!)) {
                if (itemDetailsExtras.isEmpty()) {
                    itemDetailsExtras += it[i].id
                } else {
                    itemDetailsExtras += "," + it[i].id
                }
//                }
            }
        }

        Log.e("itemDetailsExtras", "callRemoveItemFromCartApi: " + itemDetailsExtras)
        apiRequestParams.addition_detail = itemDetailsExtras
        repository.removeItemFromCart(apiRequestParams)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<DataWrapper<CartDetails>> {
                override fun onSuccess(data: DataWrapper<CartDetails>) {
                    when (data.responseBody?.responseCode) {
                        null -> {
                            when (data.throwable?.message) {
                                "Authentication Exception" -> {
                                    Toast.makeText(requireActivity(), resources.getString(R.string.invalid_access), Toast.LENGTH_SHORT).show()
                                    AppPreferences(requireActivity()).clearAll()
                                    AppPreferences(requireActivity()).putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                                    if (homeActivity != null) {
                                        homeActivity!!.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                                    } else {
                                        storeDetailActivity!!.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                                    }
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
                            try {
                                data.responseBody.message?.let {
                                    showSnackBar(it)
                                }

                                data.responseBody.data?.let {
                                    cartDetails = it
                                }
                                cartDetail.clear()
                                data.responseBody.data?.cartDetail?.let {
                                    cartDetail.addAll(it)
                                }
                                cartAdapter.notifyDataSetChanged()
                                updateDetails()
                                callViewCartApi()
                            } catch (e: Exception) {

                            }
                        }
                        2 -> {
                            data.responseBody.message?.let {
                                showSnackBar(it)
                            }
                        }
                    }
                }

                override fun onSubscribe(d: Disposable) {
                    disposableRemoveItemFromCart = d
                    compositeDisposable?.add(d)
                }

                override fun onError(e: Throwable) {
                    if (e is ServerException) {
                        e.message?.let {
                            showSnackBar(it)
                        }
                    }
                    if (e is AuthenticationException) {
                        Toast.makeText(requireActivity(), resources.getString(R.string.invalid_access), Toast.LENGTH_SHORT).show()
                        AppPreferences(requireActivity()).clearAll()
                        AppPreferences(requireActivity()).putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                        if (homeActivity != null) {
                            homeActivity!!.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                        } else {
                            storeDetailActivity!!.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                        }
                    } else if (e is ProtocolException || e is ConnectException) {
                        Toast.makeText(requireActivity(), resources.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

    private fun hideKeyBoard() {
        val view = this.editTextPromoCode
        if (view != null) {
            val inputManager = editTextPromoCode.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    private var disposableCheckPromocode: Disposable? = null
    private fun callCheckPromocodeApi() {
        disposableCheckPromocode?.dispose()

        //user_id,promocode,  optional :
        val apiRequestParams = ApiRequestParams()
        apiRequestParams.user_id = userId

        cartDetails?.let {
            apiRequestParams.amount = it.subTotal
        }


        apiRequestParams.promocode = editTextPromoCode.text.toString().trim()
        showLoader()
        repository.checkPromocode(apiRequestParams)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<DataWrapper<PromoCode>> {
                override fun onSuccess(data: DataWrapper<PromoCode>) {
                    hideLoader()
                    when (data.responseBody?.responseCode) {
                        null -> {
                            when (data.throwable?.message) {
                                "Authentication Exception" -> {
                                    Toast.makeText(requireActivity(), resources.getString(R.string.invalid_access), Toast.LENGTH_SHORT).show()
                                    AppPreferences(requireActivity()).clearAll()
                                    AppPreferences(requireActivity()).putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                                    if (homeActivity != null) {
                                        homeActivity!!.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                                    } else {
                                        storeDetailActivity!!.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                                    }
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
                            data.responseBody.message?.let {
                                showSnackBar(it)
                            }
                            data.responseBody.data?.let {
                                promoCode = it
                                callViewCartApi()
                            }
                        }
                        0 -> {
                            data.responseBody.message?.let {
                                showSnackBar(it)
                            }
                            promoCode = null
                            callViewCartApi()
                        }
                        else -> {
                            data.responseBody?.message?.let {
                                showSnackBar(it)
                            }
                        }
                    }
                }

                override fun onSubscribe(d: Disposable) {
                    disposableCheckPromocode = d
                    compositeDisposable?.add(d)
                }

                override fun onError(e: Throwable) {
                    hideLoader()
                    if (e is ServerException) {
                        e.message?.let {
                            showSnackBar(it)
                        }
                    }
                    if (e is AuthenticationException) {
                        Toast.makeText(requireActivity(), resources.getString(R.string.invalid_access), Toast.LENGTH_SHORT).show()
                        AppPreferences(requireActivity()).clearAll()
                        AppPreferences(requireActivity()).putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                        if (homeActivity != null) {
                            homeActivity!!.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                        } else {
                            storeDetailActivity!!.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                        }
                    } else if (e is ProtocolException || e is ConnectException) {
                        Toast.makeText(requireActivity(), resources.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

    fun showLoader() {
        if (progressDialog != null) {
            progressDialog!!.show()
        } else {
            setUpDialog()
            progressDialog!!.show()
        }
    }

    fun hideLoader() {
        if (progressDialog != null && progressDialog!!.isShowing) {
            progressDialog!!.dismiss()
        }
    }

    private fun setUpDialog() {

        val dialogBuilder = context?.let { AlertDialog.Builder(it, R.style.alertDialog) }
        val dialogView = LayoutInflater.from(context).inflate(R.layout.progress_bar_layout, null, false)
        dialogBuilder?.setView(dialogView)
        dialogBuilder?.setCancelable(false)
        progressDialog = dialogBuilder?.create()

    }

    private fun showSnackBar(message: String) {
        try {
            val snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT)
            snackbar.setActionTextColor(this.resources.getColor(R.color.colorTextWhite))
            //  snackbar.setAction("Ok") { snackbar.dismiss() }
            val snackView = snackbar.view
            val textView = snackView.findViewById<TextView>(R.id.snackbar_text)
            textView.maxLines = 4
            textView.setTextColor(this.resources.getColor(R.color.colorTextWhite))
            //      textView.typeface = ResourcesCompat.getFont(this, R.font.gill_sans_regular)

            //      snackView.setBackgroundResource(R.drawable.snackbar_gradirent)
            snackbar.show()
        } catch (e: Exception) {

        }
    }

    override fun onDestroyView() {
        compositeDisposable?.clear()
        super.onDestroyView()
    }
}