package com.coming.customer.ui.base

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.coming.customer.R
import com.coming.customer.core.AppPreferences
import com.coming.customer.core.Common
import com.coming.customer.core.LocationManager
import com.coming.customer.core.Session
import com.coming.customer.data.pojo.AppConstants
import com.coming.customer.ui.auth.activity.AuthActivity
import com.coming.customer.ui.manager.Navigator
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Created by hlink21 on 25/4/16.
 */
abstract class BaseFragment : Fragment() {
    @Inject
    lateinit var appPreferences: AppPreferences

    @Inject
    lateinit var navigator: Navigator

    protected lateinit var toolbar: HasToolbar

    @Inject
    lateinit var locationManager: LocationManager

    companion object {
        var latitudeLocation: Double = 0.00
        var longtudeLocation: Double = 0.00

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(createLayout(), container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bindData()
        locationManager.setActivity(requireActivity() as AppCompatActivity)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (activity is HasToolbar)
            toolbar = activity as HasToolbar
    }


    fun hideKeyBoard() {
        if (activity is BaseActivity) {
            (activity as BaseActivity).hideKeyboard()
        }
    }

    fun showKeyBoard() {
        if (activity is BaseActivity) {
            (activity as BaseActivity).showKeyboard()
        }
    }


    fun <T : BaseFragment> getParentFragment(targetFragment: Class<T>): T? {
        if (parentFragment == null) return null
        try {
            return targetFragment.cast(parentFragment)
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }

        return null
    }

    open fun onShow() {

    }

    open fun onBackActionPerform(): Boolean {
        return true
    }

    open fun onViewClick(view: View) {

    }

    fun showAlertDialog(message: String, dialogOkListener: BaseActivity.DialogOkListener? = null, neutralText: String = "Ok") {
        navigator.showAlertDialog(message, getString(R.string.label_ok), dialogOkListener)
    }

    fun showToastShort(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    fun showLoader() {

        if (activity is BaseActivity) {
            (activity as BaseActivity).showLoader()
        }
    }

    fun showSettingsDialog() {
        val dialog = AlertDialog.Builder(activity, R.style.AlertDialogCustom)
            .setTitle(resources.getString(R.string.label_to_access_this_you_need_to_login))
            .setMessage(resources.getString(R.string.label_do_you_want_to_login_now))
            .setCancelable(false)
            .setPositiveButton(resources.getString(R.string.label_yes)) { dialog, which ->
                appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                appPreferences.clearAll()
                navigator.loadActivity(AuthActivity::class.java).byFinishingCurrent().start()
            }.setNegativeButton(resources.getString(R.string.label_not_now)) { dialog, which ->
                dialog.cancel()
            }.create()

        dialog.setOnShowListener { dialogInterface: DialogInterface? ->
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.colorPrimary))
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isAllCaps = false
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.colorPrimary))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).isAllCaps = false
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(resources.getColor(R.color.colorTransferant))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(resources.getColor(R.color.colorTransferant))
        }
        dialog.show()
    }


    fun hideLoader() {
        if (activity is BaseActivity) {
            (activity as BaseActivity).hideLoader()
        }
    }

    fun showToastLong(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    interface OnImageCompleteListener {
        fun toComplete(fileName: String)

        fun toCompleteList(imagesList: List<String>)

        fun onFailed()
    }

//    private var transferUtility: TransferUtility? = null

    lateinit var callBackUploadImages: OnImageCompleteListener

    val randomFileName: String
        get() {

            val SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"

            val salt = StringBuilder()
            val rnd = Random()
            while (salt.length < 15) {
                val index = (rnd.nextFloat() * SALTCHARS.length).toInt()
                salt.append(SALTCHARS[index])
            }

            return salt.toString()

        }

    fun uploads3(objectKey: String, originalFile: File, fileName: String, onImageCompleteListener: OnImageCompleteListener) {

        this.callBackUploadImages = onImageCompleteListener

        if (originalFile.name.isEmpty() || originalFile.name.trim { it <= ' ' } == "") {
            callBackUploadImages.toComplete("")
            return
        }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())

        val finalFileName = fileName + "_" + randomFileName + originalFile.absolutePath.substring(originalFile.absolutePath.lastIndexOf("."))

//        transferUtility = S3Utils.getTransferUtility(requireActivity())
//
//        val observer = transferUtility!!.upload(objectKey, finalFileName, originalFile, CannedAccessControlList.PublicReadWrite)
//
//        observer.setTransferListener(object : TransferListener {
//            override fun onStateChanged(id: Int, state: TransferState) {
//                Log.e("AMZON", "onStateChanged: " + id + ", " + state);
//                if (state == TransferState.IN_PROGRESS) {
//
//                } else if (state == TransferState.COMPLETED) {
//
//                    Log.e("Uploded path", objectKey)
//
//
//
//                    callBackUploadImages.toComplete(finalFileName)
//
//                } else if (state == TransferState.FAILED) {
//                    callBackUploadImages.onFailed()
//
//
//                }
//            }
//
//            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
//                val percentage = (bytesCurrent / bytesTotal * 100).toInt()
//                Log.e("AMZON_PROGRESS_CHANGE", String.format("onProgressChanged: %d, total: %d, current: %d", id, bytesTotal, bytesCurrent));
//            }
//
//            override fun onError(id: Int, ex: Exception) {
//                Log.e("AMZONERROR", "Error during upload: " + id + ex.toString());
//                Log.e("AMZONERROR",  id.toString() );
//                showToastLong(ex.toString())
//            }
//        })
    }


//    public fun onError(throwable: Throwable) {
////        Log.e(javaClass.simpleName, "Error From Base framework ", throwable)
//
//
//        hideKeyBoard()
//        try {
//            when (throwable) {
//                is ConnectException -> {
//                    navigator.showAlertDialog(resources.getString(R.string.connection_exception))
//                }
//                /*is ServerException -> {
//                    navigator.showAlertDialog(throwable.message.toString())
//                }
//                is ConnectException -> {
//                    navigator.showAlertDialog(resources.getString(R.string.connection_exception))
//                }
//                is AuthenticationException -> {
//                    appPreferences.clearAll()
//                    appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
//                    navigator.loadActivity(AuthActivity::class.java).byFinishingAll().start()
//                }
//                is ApplicationException -> {
////                    navigator.showAlertDialog(throwable.message)
////                    showToastLong(throwable.message)
//                }
//                is SocketTimeoutException -> {
//                    navigator.showAlertDialog(resources.getString(R.string.socket_time_out_exception))
//                }
//                else -> {
//                    navigator.showAlertDialog(throwable.message.toString())
////                    Log.e("error", throwable.message.toString())
////                    showMessage(R.string.other_exception)
//                }*/
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//    }


    protected abstract fun createLayout(): Int
    protected abstract fun bindData()

    @Inject
    lateinit var session: Session


    //Firebase chat

    private var dbBaseActivity: FirebaseFirestore? = null

    fun setDataToFirebase() {
        var data = session?.user
        val username = data?.firstname.plus(" ").plus(data?.lastname).plus(" ").plus(data?.username)
        val profileImage = data?.profileImage
        val phoneNum = data?.phone
        val email = data?.email
        val deviceToken = data?.deviceToken
        val id = data?.id
        //firebase
        dbBaseActivity = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        dbBaseActivity?.setFirestoreSettings(settings)
        val user = HashMap<String, Any>()
        user[Common.User.USER_NAME] = username
        user[Common.User.USER_IMAGE] = profileImage.toString()
        user[Common.User.PHONE] = phoneNum.toString()
        user[Common.User.EMAIL] = email.toString()
        user[Common.User.IS_ONLINE] = true
        user[Common.User.DEVICE_TYPE] = Session.DEVICE_TYPE
        user[Common.User.DEVICE_TOKEN] = deviceToken.toString()

        dbBaseActivity?.collection(Common.User.USER_COLLECTION_NM)?.document("U".plus(id))?.set(user)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.e(":::::: User", "Data Add successfully");
            } else {
                val e = task.exception
                Log.e("TAG", "Exception" + e?.localizedMessage)

            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationManager.onRequestPermissionsResult(requestCode, permissions as Array<String>, grantResults)
    }


}
