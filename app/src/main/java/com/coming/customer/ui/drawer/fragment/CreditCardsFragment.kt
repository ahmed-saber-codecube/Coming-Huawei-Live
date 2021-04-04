package com.coming.customer.ui.drawer.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coming.customer.R
import com.coming.customer.apiparams.ApiRequestParams
import com.coming.customer.data.pojo.AppConstants
import com.coming.customer.data.pojo.GetCardList
import com.coming.customer.exception.ServerException
import com.coming.customer.ui.auth.activity.AuthActivity
import com.coming.customer.ui.base.BaseFragment
import com.coming.customer.ui.drawer.CreditCardAdapter
import com.coming.customer.ui.isolated.IsolatedActivity
import com.coming.customer.ui.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.home_fragment_credit_cards.*

@AndroidEntryPoint
class CreditCardsFragment : BaseFragment(), View.OnClickListener, CreditCardAdapter.OnCardActivity {

    val getCardList = ArrayList<GetCardList>()
    var adapter: CreditCardAdapter? = null

    var removeGetCardList: GetCardList? = null

    private val parent: IsolatedActivity get() = requireActivity() as IsolatedActivity

    override fun bindData() {
        setupListeners()
        setupRecyclerViewCreditCards()
    }

    override fun onResume() {
        super.onResume()
        parent.apply { showToolbar(false) }

        getCardList.clear()
        adapter?.notifyDataSetChanged()
        callGetCardsApi()
    }

    private fun setupListeners() {
        textViewAddCard.setOnClickListener(this)
        textViewLabelOptions.setOnClickListener(this)
    }

    private fun setupRecyclerViewCreditCards() {
        adapter = CreditCardAdapter(getCardList, this)
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        recyclerViewCards.adapter = adapter
        recyclerViewCards.layoutManager = layoutManager
    }


    private fun updateUI(dataNotFoundMessage: String = resources.getString(R.string.label_data_not_found)) {
        if (getCardList.size == 0) {
            textViewAddCard.visibility = View.VISIBLE
            textViewNoDataFound.visibility = View.VISIBLE
            textViewNoDataFound.text = dataNotFoundMessage
            recyclerViewCards.visibility = View.GONE
        } else {
            textViewAddCard.visibility = View.GONE
            textViewNoDataFound.visibility = View.GONE
            recyclerViewCards.visibility = View.VISIBLE
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.textViewAddCard -> {
                navigator.load(AddCardFragment::class.java).replace(true)
            }

            R.id.textViewLabelOptions -> {
                navigator.goBack()
            }
        }
    }

    override fun onRemoveCard(getCardList: GetCardList) {
        removeGetCardList = getCardList
        callDeleteCardApi()
    }

    override fun onAddCard() {
        navigator.load(AddCardFragment::class.java).replace(true)
    }

    override fun createLayout(): Int = R.layout.home_fragment_credit_cards

    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setLiveData()
        super.onCreate(savedInstanceState)
    }

    private fun setLiveData() {
        //getCards live data source
        authViewModel.getCardsLiveData.observe(this,
            { responseBody ->
                hideLoader()
//                showToastLong(responseBody.message)
                if (responseBody.responseCode == 1 && responseBody.data != null) {
                    getCardList.clear()
                    responseBody.data?.let {
                        getCardList.addAll(it)
                    }
                    adapter?.notifyDataSetChanged()
                }
                updateUI("")
            },
            { throwable ->
                hideLoader()
                updateUI("")
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
                        throwable.message?.let {
                            showToastLong(it)
                            updateUI(it)
                        }
                    } else if (throwable.code == 3 || throwable.code == 11) {
                        Toast.makeText(requireActivity(), resources.getString(R.string.inactive_account), Toast.LENGTH_SHORT).show()
                        appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                        navigator.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                    }
                }
                false
            })

        //deleteCard live data source
        authViewModel.deleteCardLiveData.observe(this,
            { responseBody ->
                hideLoader()
                showToastLong(responseBody.message)
                if (responseBody.responseCode == 1) {
                    getCardList.clear()
                    adapter?.notifyDataSetChanged()
                    callGetCardsApi()
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
                        throwable.message?.let { showToastLong(it) }
                    } else if (throwable.code == 3 || throwable.code == 11) {
                        Toast.makeText(requireActivity(), resources.getString(R.string.inactive_account), Toast.LENGTH_SHORT).show()
                        appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                        navigator.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                    }
                }
                false
            })

    }

    //getCards api
    private fun callGetCardsApi() {
        /**
         *
         * params : getCards(user_id)
         *
         * optional :
         *
         * */

        showLoader()
        val apiRequestParams = ApiRequestParams()
        apiRequestParams.user_id = session.user?.id
        authViewModel.getCards(apiRequestParams)
    }

    //deleteCard api
    private fun callDeleteCardApi() {
        /**
         *
         * params : deleteCard(card_id)
         *
         * optional :
         *
         * */

        showLoader()
        val apiRequestParams = ApiRequestParams()
        apiRequestParams.card_id = removeGetCardList?.id
        authViewModel.deleteCard(apiRequestParams)
    }


}