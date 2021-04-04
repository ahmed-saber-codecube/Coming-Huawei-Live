package com.coming.customer.ui.manager

import com.coming.customer.ui.base.BaseActivity
import com.coming.customer.ui.base.BaseFragment


/**
 * Created by hlink21 on 29/5/17.
 */

interface Navigator {

    fun <T : BaseFragment> load(tClass: Class<T>): FragmentActionPerformer<T>

    fun loadActivity(aClass: Class<out BaseActivity>): ActivityBuilder

    fun <T : BaseFragment> loadActivity(aClass: Class<out BaseActivity>, pageTClass: Class<T>): ActivityBuilder

    fun goBack()

    fun finish()

    fun showAlertDialog(message: String, neutralText: String = "Ok", dialogOkListener: BaseActivity.DialogOkListener? = null)

}
