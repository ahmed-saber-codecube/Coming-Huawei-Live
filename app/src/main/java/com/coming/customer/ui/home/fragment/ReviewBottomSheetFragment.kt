package com.coming.customer.ui.home.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import androidx.fragment.app.DialogFragment
import com.coming.customer.R
import com.coming.customer.ui.home.RatingListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.home_bottom_sheet_rating.*

class ReviewBottomSheetFragment(var ratingListener: RatingListener) : BottomSheetDialogFragment(), View.OnClickListener {

    private var storeCheckboxes: ArrayList<CheckedTextView> = ArrayList()
    private var checkedStoreId: Int = 0

    private var driverCheckboxes: ArrayList<CheckedTextView> = ArrayList()
    private var checkedDriverId: Int = 0

    var storeRating = "0"
    var driverRating = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.home_bottom_sheet_rating, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCheckBoxes()
        setupListeners()

        /*dialog?.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
        }*/
    }

    private fun setupCheckBoxes() {
        driverCheckboxes.apply {
            add(textViewTerribleDriver)
            add(textViewBadDriver)
            add(textViewOkayDriver)
            add(textViewGoodDriver)
            add(textViewGreatDriver)
        }

        storeCheckboxes.apply {
            add(textViewTerribleStore)
            add(textViewBadStore)
            add(textViewOkayStore)
            add(textViewGoodStore)
            add(textViewGreatStore)
        }
    }

    private fun setupListeners() {
        buttonDone.setOnClickListener(this)
        buttonCancel.setOnClickListener(this)

        driverCheckboxes.forEach {
            it.setOnClickListener { checkedTextView ->
                checkedDriverId = checkedTextView.id

                driverCheckboxes.forEach { checkedTextView_ ->
                    checkedTextView_.isChecked = checkedTextView_.id == checkedDriverId
                    if (checkedTextView_.id == checkedDriverId) {
                        when (checkedTextView_.text.trim()) {
                            resources.getString(R.string.text_terrible).trim() -> {
                                driverRating = "1"
                            }
                            resources.getString(R.string.text_bad).trim() -> {
                                driverRating = "2"
                            }
                            resources.getString(R.string.text_okay).trim() -> {
                                driverRating = "3"
                            }
                            resources.getString(R.string.text_good).trim() -> {
                                driverRating = "4"
                            }
                            resources.getString(R.string.text_great).trim() -> {
                                driverRating = "5"
                            }
                        }
                    }
                }
            }
        }

        storeCheckboxes.forEach {
            it.setOnClickListener { checkedTextView ->
                checkedStoreId = checkedTextView.id

                storeCheckboxes.forEach { checkedTextView_ ->
                    checkedTextView_.isChecked = checkedTextView_.id == checkedStoreId
                    if (checkedTextView_.id == checkedStoreId) {
                        when (checkedTextView_.text.trim()) {
                            resources.getString(R.string.text_terrible).trim() -> {
                                storeRating = "1"
                            }
                            resources.getString(R.string.text_bad).trim() -> {
                                storeRating = "2"
                            }
                            resources.getString(R.string.text_okay).trim() -> {
                                storeRating = "3"
                            }
                            resources.getString(R.string.text_good).trim() -> {
                                storeRating = "4"
                            }
                            resources.getString(R.string.text_great).trim() -> {
                                storeRating = "5"
                            }
                        }
                    }
                }
            }
        }
    }

    var doneRatingListener = object : RatingListener {
        override fun onRating(driverRating: String, storeRating: String, doneRatingListener: RatingListener) {
            dismiss()
        }

        override fun onDoneRating() {
            dismiss()
        }

    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.buttonCancel -> {
                dismiss()
            }
            R.id.buttonDone -> {
                Log.e("--> ", "driverRating : $driverRating")
                Log.e("--> ", "storeRating : $storeRating")
                ratingListener.onRating(driverRating, storeRating, doneRatingListener)
            }
        }
    }


}