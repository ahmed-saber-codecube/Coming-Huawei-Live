package com.coming.customer.ui.auth.fragment

/**
 * Created by hlink on 6/7/18.
 */

import android.text.Editable
import android.text.TextWatcher
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coming.customer.R
import com.coming.customer.data.pojo.Country
import com.coming.customer.ui.auth.CountryAdapter
import com.coming.customer.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.auth_fragment_select_country_code.*
import java.util.*

/**
 * Created by hlink on 8/1/18.
 */
@AndroidEntryPoint
class CountryCodePickerFragment : BaseFragment() {

    internal var selectedCode = ""
    private var onCountrySelectListener: OnCountrySelectListener? = null
    val countries = ArrayList<Country>()
    val tempCountries = ArrayList<Country>()
    val handler = android.os.Handler()
    lateinit var countryAdapter: CountryAdapter

    override fun createLayout(): Int = R.layout.auth_fragment_select_country_code

    override fun bindData() {
        toolbar.showToolbar(false)

        hideKeyBoard()

        countries.addAll(Country.countryList)

        tempCountries.addAll(Country.countryList)

        countryAdapter = CountryAdapter(requireActivity(), countries, object : CountryAdapter.CallBack {
            override fun onItemClick(position: Int) {
                if (onCountrySelectListener != null)
                    onCountrySelectListener?.onCountrySelect(countries[position].dialCode)
            }
        })

        textViewClear.setOnClickListener {
            onCountrySelectListener?.onCountrySelect("")
        }

        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

                Observable.fromIterable(tempCountries).filter { orgLocal -> (orgLocal.name!!.toUpperCase().contains(charSequence.toString().toUpperCase()) || orgLocal.dialCode!!.contains(charSequence.toString())) }.toList().subscribe(Consumer {
                    countries.clear()
                    countries.addAll(it)
                    if (countryAdapter != null) {
                        countryAdapter.notifyDataSetChanged()
                    }

                })

            }

            override fun afterTextChanged(editable: Editable) {}
        })

        recyclerViewMyActivitySession!!.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recyclerViewMyActivitySession!!.adapter = countryAdapter

        imageViewBack.setOnClickListener {
            navigator.goBack()
        }
    }


    fun setOnCountrySelectListener(onCountrySelectListener: OnCountrySelectListener) {
        this.onCountrySelectListener = onCountrySelectListener
    }


    interface OnCountrySelectListener {

        fun onCountrySelect(countryCode: String?)

    }

}