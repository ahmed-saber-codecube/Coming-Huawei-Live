package com.coming.customer.ui.drawer.fragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coming.customer.R
import com.coming.customer.core.AppCommon
import com.coming.customer.core.Common
import com.coming.customer.data.pojo.GetOrderDetails
import com.coming.customer.data.pojo.chat.ChatMessage
import com.coming.customer.ui.base.BaseFragment
import com.coming.customer.ui.drawer.ChatAdapter
import com.coming.customer.ui.isolated.IsolatedActivity
import com.coming.customer.util.DateTimeUtility
import com.huawei.hmf.tasks.OnSuccessListener
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.home_fragment_chat.*
import java.util.*

@AndroidEntryPoint
class ChatFragment : BaseFragment(), View.OnClickListener, View.OnTouchListener, TextWatcher, ChatAdapter.OnItemClickListener {

    /*Firebase chat*/
    lateinit var dbBaseActivity: FirebaseFirestore
    var anonymousId: String = ""
    lateinit var recentChatId: String
    lateinit var date: String
    lateinit var recentChatIdTemp: String
    var liveChatListener: ListenerRegistration? = null
    var chatMessagesList = ArrayList<ChatMessage>()
    private lateinit var chatAdapter: ChatAdapter
    var profileImageName = ""
    /*End Firebase chat*/


    var getOrderDetails: GetOrderDetails? = null
    var driverId = ""
    var driverName = ""
    var driverimage = ""

    private val parent: IsolatedActivity get() = requireActivity() as IsolatedActivity


    override fun bindData() {
        getIntentData()
        setupListeners()
        setupRecyclerViewChat()

        //Create object of database
        dbBaseActivity = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        dbBaseActivity.firestoreSettings = settings

        //getRecent Chat id
//        anonymousId = bundle?.getString("anonymousId").toString()
        recentChatId = "U" + session?.user?.id + "D" + driverId

        if (recentChatId.isEmpty()) {
//            createRecentChatId()
        } else {
            //set chat live listener
            setLiveChatListener()
        }

    }

    private fun getIntentData() {
        if (arguments != null && arguments?.containsKey(AppCommon.ORDER_DETAILS)!!) {
            getOrderDetails = arguments?.getSerializable(AppCommon.ORDER_DETAILS) as GetOrderDetails
            driverId = getOrderDetails?.driverDetail?.id.toString()
            driverName = getOrderDetails?.driverDetail?.username.toString()
            driverimage = getOrderDetails?.driverDetail?.driverImage.toString()
//            recentChatUniqId = getRecentChatUniqId(driverId)
        } else if (arguments != null && arguments?.containsKey(AppCommon.DRIVER_ID)!!) {
            driverId = arguments?.getString(AppCommon.DRIVER_ID).toString()
            driverName = arguments?.getString(AppCommon.DRIVER_USER_NAME).toString()
            driverimage = arguments?.getString(AppCommon.DRIVER_IMAGE).toString()
        }
    }

    override fun onResume() {
        super.onResume()
        parent.apply {
            showToolbar(true)
            setToolbarTitle(R.string.title_live_chat)
        }
    }

    private fun setupListeners() {
        buttonSendMessage.setOnClickListener(this)
        buttonSendImage.setOnClickListener(this)
    }

    private fun setupRecyclerViewChat() {
        chatAdapter = ChatAdapter(chatMessagesList, this)
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        recyclerView.adapter = chatAdapter
        recyclerView.layoutManager = layoutManager
    }

    override fun onItemClick(pos: Int) {

    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.buttonSendMessage -> {
                sendMessageToUser()
            }
            R.id.buttonSendImage -> {
                Dexter.withActivity(activity)
                    .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(object : PermissionListener {
                        override fun onPermissionGranted(response: PermissionGrantedResponse) {
                            openFilePicker()
                        }

                        override fun onPermissionDenied(response: PermissionDeniedResponse) {
                            // check for permanent denial of permission
                            if (response.isPermanentlyDenied) {
//                                showToastLong("Please enable read external storage permission")
                                showSettingsDialogForImage()
                            }
                        }

                        override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) {
                            token.continuePermissionRequest()
                        }
                    }).check()
            }
        }
    }

    override fun createLayout(): Int = R.layout.home_fragment_chat


    //Firebase chat Send message

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        android.os.Handler().postDelayed({
            if (chatMessagesList.size > 1)
                recyclerView.smoothScrollToPosition(chatAdapter.itemCount - 1)
        }, 500)
        return false
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (s.toString().trim().isNotEmpty()) {
            if (chatAdapter.itemCount > 1)
                recyclerView.scrollToPosition(chatAdapter.itemCount - 1)
        }
    }

    override fun afterTextChanged(s: Editable?) {

    }

    private fun sendMessageToUser() {
        val message = editTextMessage.getText().toString().trim()
        if (!message.isEmpty()) {
            addDataChat(message)
            editTextMessage.setText("")
        } else {
            Toast.makeText(context, resources.getString(R.string.validations_please_enter_message), Toast.LENGTH_SHORT).show()
        }

    }

    //image selection for chat
    private fun openFilePicker() {
        val filePickerIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }


        try {
            startActivityForResult(
                Intent.createChooser(filePickerIntent, resources.getString(R.string.label_select_an_image_to_upload)),
                AppCommon.RC_SELECT_IMAGE
            )
        } catch (e: Exception) {

        }
    }

    private fun showSettingsDialogForImage() {
        val dialog = AlertDialog.Builder(activity, R.style.AlertDialogCustom)
            .setTitle(resources.getString(R.string.label_need_permissions))
            .setMessage(resources.getString(R.string.label_this_app_needs_permission_to_use_this_feature_you_can_grant_them_in_app_settings))
            .setCancelable(false)
            .setPositiveButton(resources.getString(R.string.label_goto_settings)) { dialog, which ->
                dialog.cancel()
                openSettings()
            }.setNegativeButton(resources.getString(R.string.label_cancel)) { dialog, which -> dialog.cancel() }.create()

        dialog.setOnShowListener { dialogInterface: DialogInterface? ->
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.colorPrimary))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.colorPrimary))
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(resources.getColor(R.color.colorTransferant))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(resources.getColor(R.color.colorTransferant))
        }
        dialog.show()
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", context?.applicationInfo?.packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                AppCommon.RC_SELECT_IMAGE -> {
                    val uri = data?.data

                    if (uri != null) {
                        showLoader()
//                        uploads3(S3Utils.CHAT_IMAGE, File(FileUtils.getPath(activity, uri)), "", object : OnImageCompleteListener {
//                            override fun toComplete(fileName: String) {
//                                hideLoader()
////                                profileImageName = S3Utils.IMAGE_PATH + "coming/chat_image/" + fileName
////                                addImageDataChat(profileImageName)
//                            }
//
//                            override fun toCompleteList(imagesList: List<String>) {
//                                hideLoader()
//                            }
//
//                            override fun onFailed() {
//                                hideLoader()
//                            }
//                        })

                    }
                }
            }
        }
    }

    //end image selection for chat

    private fun addDataChat(message: String) {

        val chat = HashMap<String, Any>()
        chat[Common.Chat.SENDER_ID] = "U".plus(session.userId)
        chat[Common.Chat.RECEIVER_ID] = "D".plus("driverId")
        chat[Common.Chat.RECENT_CHAT_ID] = recentChatId
        chat[Common.Chat.CHAT_READ] = false
        chat[Common.Chat.MESSAGE] = message
        chat[Common.Chat.MESSAGE_TYPE] = "T"
        chat[Common.Chat.SERVER_TIME] = FieldValue.serverTimestamp()
        val time = Calendar.getInstance().time
        Log.d("tag", "Time" + time)
        date = DateTimeUtility.format(time, "dd-MM-yyyy HH:mm:ss")
        chat[Common.Chat.CHAT_TIME] = date
        dbBaseActivity.collection(Common.Chat.CHAT_COLLECTION_NM).add(chat)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.e("::::::Chat", "Data Add successfully");
                    //                appCompatEditTextChatMessage.setFocusable(true);
                    //                makeChatReadFalse();
                    addDataRecentChat(message)
                }
            }
    }

    private fun addImageDataChat(message: String) {

        val chat = HashMap<String, Any>()
        chat[Common.Chat.SENDER_ID] = "U".plus(session.userId)
        chat[Common.Chat.RECEIVER_ID] = driverId
        chat[Common.Chat.RECENT_CHAT_ID] = recentChatId
        chat[Common.Chat.CHAT_READ] = false
        chat[Common.Chat.MESSAGE] = ""
        chat[Common.Chat.CHAT_IMAGE] = message
        chat[Common.Chat.MESSAGE_TYPE] = "M"
        chat[Common.Chat.SERVER_TIME] = FieldValue.serverTimestamp()
        val time = Calendar.getInstance().time
        Log.d("tag", "Time" + time)
        date = DateTimeUtility.format(time, "dd-MM-yyyy HH:mm:ss")
        chat[Common.Chat.CHAT_TIME] = date
        dbBaseActivity.collection(Common.Chat.CHAT_COLLECTION_NM).add(chat)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.e("::::::Chat", "Data Add successfully");
                    //                appCompatEditTextChatMessage.setFocusable(true);
                    //                makeChatReadFalse();
                    addImageDataRecentChat(message)
                }
            }
    }

    private fun addImageDataRecentChat(message: String) {

        dbBaseActivity.collection(Common.Chat.CHAT_COLLECTION_NM)
            .whereEqualTo(Common.Chat.RECENT_CHAT_ID, recentChatId)
            .whereEqualTo(Common.Chat.CHAT_READ, false)
            .whereEqualTo(Common.Chat.RECEIVER_ID, "D$driverId")
            .get()
            .addOnSuccessListener { documentSnapshots ->
                val chatCount = documentSnapshots.size()
                val recentChat = HashMap<String, Any>()
                recentChat[Common.RecentChat.CHAT_COUNT] = chatCount.toString()
                recentChat[Common.RecentChat.CHAT_TIME] = date
                recentChat[Common.RecentChat.MESSAGE] = ""
                recentChat[Common.RecentChat.CHAT_IMAGE] = message
                recentChat[Common.RecentChat.MESSAGE_TYPE] = "M"
                recentChat[Common.RecentChat.RECEIVER_ID] = "D$driverId"
                recentChat[Common.RecentChat.RECEIVER_IMAGE] = "$driverimage"
                recentChat[Common.RecentChat.RECEIVER_NAME] = "$driverName"
                recentChat[Common.RecentChat.SENDER_ID] = "U".plus(session.userId)
                recentChat[Common.RecentChat.SENDER_NAME] = session.user?.firstname.plus(" ").plus(session.user?.lastname).plus(" ").plus(session.user?.username)
                recentChat[Common.RecentChat.SENDER_IMAGE] = session.user?.profileImage.toString()
                recentChat[Common.RecentChat.IS_BLOCKED] = false
                recentChat[Common.Chat.SERVER_TIME] = FieldValue.serverTimestamp()
                dbBaseActivity.collection(Common.RecentChat.RECENT_CHAT_COLLECTION_NM)
                    .document(recentChatId).set(recentChat, SetOptions.merge())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            //                                Log.e(":::::: RecentChat", "Data Updated successfully");
                            //                                  addDataChat();
                            //                                  makeChatReadFalse();
                            //                                setChatCount();
                        }
                    }
            }


    }

    private fun addDataRecentChat(message: String) {
        dbBaseActivity.collection(Common.Chat.CHAT_COLLECTION_NM)
            .whereEqualTo(Common.Chat.RECENT_CHAT_ID, recentChatId)
            .whereEqualTo(Common.Chat.CHAT_READ, false)
            .whereEqualTo(Common.Chat.RECEIVER_ID, "D$driverId")
            .get()
            .addOnSuccessListener { documentSnapshots ->
                val chatCount = documentSnapshots.size()
                val recentChat = HashMap<String, Any>()
                recentChat[Common.RecentChat.CHAT_COUNT] = chatCount.toString()
                recentChat[Common.RecentChat.CHAT_TIME] = date
                recentChat[Common.RecentChat.MESSAGE] = message
                recentChat[Common.RecentChat.MESSAGE_TYPE] = "T"
                recentChat[Common.RecentChat.RECEIVER_ID] = "D$driverId"
                recentChat[Common.RecentChat.RECEIVER_IMAGE] = "$driverimage"
                recentChat[Common.RecentChat.RECEIVER_NAME] = "$driverName"
                recentChat[Common.RecentChat.SENDER_ID] = "U".plus(session.userId)
                recentChat[Common.RecentChat.SENDER_NAME] = session.user?.firstname.plus(" ").plus(session.user?.lastname).plus(" ").plus(session.user?.username)
                recentChat[Common.RecentChat.SENDER_IMAGE] = session.user?.profileImage.toString()
                recentChat[Common.RecentChat.IS_BLOCKED] = false
                recentChat[Common.Chat.SERVER_TIME] = FieldValue.serverTimestamp()
                dbBaseActivity.collection(Common.RecentChat.RECENT_CHAT_COLLECTION_NM)
                    .document(recentChatId).set(recentChat, SetOptions.merge())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            //                                Log.e(":::::: RecentChat", "Data Updated successfully");
                            //                                  addDataChat();
                            //                                  makeChatReadFalse();
                            //                                setChatCount();
                        }
                    }
            }

    }

    fun isRecentIdExists(recentChatIdTemp: String, callBack: OnSuccessListener<DocumentSnapshot>) {

        dbBaseActivity.collection(Common.RecentChat.RECENT_CHAT_COLLECTION_NM)
            .document(recentChatIdTemp)
            .get()
            .addOnSuccessListener(callBack)

    }

    fun createRecentChatId() {

        if (Integer.parseInt(session.userId) < Integer.parseInt(driverId)) {
            recentChatIdTemp = String.format(
                "#%s#%s#%s",
                session.userId,
                driverId,
                session.userId
            )
        } else {
            recentChatIdTemp = String.format(
                "#%s#%s#%s",
                driverId,
                session.userId,
                session.userId
            )
        }


        //getRecentChatId contains ###

        isRecentIdExists(recentChatIdTemp,
            OnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    recentChatId = recentChatIdTemp
                    setLiveChatListener()
                } else {
                    if (Integer.parseInt(session.userId) < Integer.parseInt(driverId)) {
                        recentChatIdTemp = String.format(
                            "#%s#%s#%s",
                            session.userId,
                            driverId,
                            driverId
                        )
                    } else {
                        recentChatIdTemp = String.format(
                            "#%s#%s#%s",
                            driverId,
                            session.userId,
                            driverId
                        )
                    }

                    isRecentIdExists(recentChatIdTemp,
                        OnSuccessListener { documentSnapshot ->
                            if (documentSnapshot.exists()) {
                                recentChatId = recentChatIdTemp
                                setLiveChatListener()
                            } else {
                                if (Integer.parseInt(session.userId) < Integer.parseInt(
                                        driverId
                                    )
                                ) {
                                    recentChatIdTemp = String.format(
                                        "#%s#%s#%s",
                                        session.userId,
                                        driverId,
                                        session.userId
                                    )
                                } else {
                                    recentChatIdTemp = String.format(
                                        "#%s#%s#%s",
                                        driverId,
                                        session.userId,
                                        session.userId
                                    )
                                }
                                recentChatId = recentChatIdTemp
                                addRecentChaiId(recentChatId)

                            }
                        })

                }
            })

    }

    fun addRecentChaiId(recentChatId: String) {

        val recentChat = HashMap<String, Any>()
        /*    if (bundle.getString("isFriend").equals("1")) {
            recentChat.put(Common.RecentChat.ANONYMOUS_ID, "");
        } else {*/
        recentChat[Common.RecentChat.ANONYMOUS_ID] = anonymousId
        //        }
        recentChat[Common.RecentChat.CHAT_COUNT] = ""
        recentChat[Common.RecentChat.CHAT_TIME] = ""
        recentChat[Common.RecentChat.MESSAGE] = ""
        recentChat[Common.RecentChat.MESSAGE_TYPE] = ""
        recentChat[Common.RecentChat.RECEIVER_ID] = ""
        recentChat[Common.RecentChat.RECEIVER_IMAGE] = ""
        recentChat[Common.RecentChat.RECEIVER_NAME] = ""
        recentChat[Common.RecentChat.SENDER_ID] = ""
        recentChat[Common.RecentChat.SENDER_NAME] = ""
        recentChat[Common.RecentChat.SENDER_IMAGE] = ""
        recentChat[Common.RecentChat.IS_BLOCKED] = false


        dbBaseActivity.collection(Common.RecentChat.RECENT_CHAT_COLLECTION_NM)
            .document(recentChatId).set(recentChat).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //                Log.e(":::::: RecentChat", "Document Id Add successfully");
                    setLiveChatListener()
                }
            }
    }

    fun setLiveChatListener() {
        liveChatListener = dbBaseActivity.collection(Common.Chat.CHAT_COLLECTION_NM)
            .whereEqualTo(Common.Chat.RECENT_CHAT_ID, recentChatId)
            .orderBy(Common.Chat.CHAT_TIME, Query.Direction.ASCENDING)
            .addSnapshotListener(addChatLiveListener())
    }

    /* private fun setChatPeference(isChatStatus: Boolean, senderIdS: String) {
         appPreferences.putBoolean("isChatScreenD", isChatStatus)
         appPreferences.putString("recentChatId", senderIdS)
     }*/

    private fun addChatLiveListener(): EventListener<QuerySnapshot> {

        return EventListener { documentSnapshots, e ->
            if (e != null) {
                //                    Log.w("Listen failed.", e);
                return@EventListener
            }

            if (documentSnapshots!!.documentChanges.size == 0) {

            }

            for (document in documentSnapshots.documentChanges) {
                when (document.type) {
                    DocumentChange.Type.ADDED -> {

                        //make chat read false if receiver id = my id and chat read = false
                        if (document.document.get(Common.Chat.CHAT_READ) == false && document.document.get(Common.Chat.RECEIVER_ID) == "U".plus(session.userId)) {
                            if (isResumed) {
                                //Log.e("Isresume", String.valueOf(isResumed()));
                                updateChatRead(document.document.id)
                            }
                        }
                        if (document.document.get(Common.Chat.SENDER_ID) == "U".plus(session.userId)) {
                            val chatMessage = ChatMessage()
                            chatMessage.type = ChatMessage.SENDER
                            chatMessage.message = document.document.get(Common.Chat.MESSAGE) as String
                            chatMessage.messageId = document.document.id
                            chatMessage.image = session.user?.profileImage
                            chatMessage.time = document.document.get(Common.Chat.CHAT_TIME) as String

                            if (document.document.get(Common.Chat.MESSAGE_TYPE) != null) {
                                chatMessage.messageType = document.document.get(Common.Chat.MESSAGE_TYPE) as String
                            } else {
                            }
                            if (document.document.get(Common.Chat.CHAT_IMAGE) != null) {
                                chatMessage.chatImage = document.document.get(Common.Chat.CHAT_IMAGE) as String
                            } else {

                            }

                            chatMessagesList.add(chatMessage)
                        } else {
                            val chatMessage = ChatMessage()
                            chatMessage.type = ChatMessage.RECEIVER
                            chatMessage.messageId = document.document.id
                            chatMessage.message = document.document.get(Common.Chat.MESSAGE) as String

                            if (document.document.get(Common.Chat.MESSAGE_TYPE) != null) {
                                chatMessage.messageType = document.document.get(Common.Chat.MESSAGE_TYPE) as String
                            } else {
                            }
                            if (document.document.get(Common.Chat.CHAT_IMAGE) != null) {
                                chatMessage.chatImage = document.document.get(Common.Chat.CHAT_IMAGE) as String
                            } else {
                            }

                            if (anonymousId.isEmpty() || anonymousId == "U".plus(session.userId)) {
                                chatMessage.image = "$driverimage"
                                //chatMessage.image = session.getUser().profileImage;
                            } else {
                                chatMessage.image = ""
                            }
                            chatMessage.time = document.document.get(Common.Chat.CHAT_TIME) as String
                            chatMessagesList.add(chatMessage)
                        }
                        chatAdapter.notifyDataSetChanged()
                        if (recyclerView != null) {
                            recyclerView.smoothScrollToPosition(chatAdapter.itemCount - 1)
                        }
                    }
                    DocumentChange.Type.MODIFIED -> {

                        //make chat read false if receiver id = my id and chat read = false
                        if (document.document.get(Common.Chat.CHAT_READ) == false && document.document.get(Common.Chat.RECEIVER_ID) == "U".plus(session.userId)) {
                            if (isResumed) {
                                //Log.e("Isresume", String.valueOf(isResumed()));
                                updateChatRead(document.document.id)
                            }
                        }

                        for (i in chatMessagesList.indices)
                            if (chatMessagesList.get(i).messageId.equals(document.document.id)) {
                                if (document.document.get(Common.Chat.SENDER_ID) == "U".plus(session.userId)) {
                                    chatMessagesList.get(i).type = ChatMessage.SENDER
                                    chatMessagesList.get(i).messageId = document.document.id
                                    chatMessagesList.get(i).message = document.document.get(Common.Chat.MESSAGE) as String
                                    chatMessagesList.get(i).image = session.user?.profileImage
                                    chatMessagesList.get(i).time = document.document.get(Common.Chat.CHAT_TIME) as String

                                    if (document.document.get(Common.Chat.MESSAGE_TYPE) != null) {
                                        chatMessagesList.get(i).messageType = document.document.get(Common.Chat.MESSAGE_TYPE) as String
                                    } else {
                                    }
                                    if (document.document.get(Common.Chat.CHAT_IMAGE) != null) {
                                        chatMessagesList.get(i).chatImage = document.document.get(Common.Chat.CHAT_IMAGE) as String
                                    } else {
                                    }

                                    //chatMessagesList.add(chatMessage);
                                } else {
                                    chatMessagesList.get(i).type = ChatMessage.RECEIVER
                                    chatMessagesList.get(i).messageId = document.document.id
                                    chatMessagesList.get(i).message = document.document.get(Common.Chat.MESSAGE) as String
                                    if (anonymousId.isEmpty()) {
                                        chatMessagesList.get(i).image = "$driverimage"
                                    } else {
                                        chatMessagesList.get(i).image = ""
                                    }
                                    chatMessagesList.get(i).time = document.document.get(Common.Chat.CHAT_TIME) as String

                                    if (document.document.get(Common.Chat.MESSAGE_TYPE) != null) {
                                        chatMessagesList.get(i).messageType = document.document.get(Common.Chat.MESSAGE_TYPE) as String
                                    } else {
                                    }
                                    if (document.document.get(Common.Chat.CHAT_IMAGE) != null) {
                                        chatMessagesList.get(i).chatImage = document.document.get(Common.Chat.CHAT_IMAGE) as String
                                    } else {
                                    }

                                    //chatMessagesList.add(chatMessage);
                                }
                            }
                        chatAdapter.notifyDataSetChanged()
                        if (recyclerView != null) {
                            recyclerView.smoothScrollToPosition(chatAdapter.itemCount - 1)
                        }
                    }
                    DocumentChange.Type.REMOVED -> {
                        //makeChatReadFalse();
                        for (i in chatMessagesList.indices) {
                            if (chatMessagesList.get(i).messageId.equals(document.document.id)) {
                                chatMessagesList.removeAt(i)
                            }
                        }
                        chatAdapter.notifyDataSetChanged()
                        if (recyclerView != null) {
                            recyclerView.smoothScrollToPosition(chatAdapter.itemCount - 1)
                        }
                    }
                }
            }
        }
    }

    private fun updateChatRead(documentId: String) {
        //        Log.e("update read:", "enter in function");
        val chatRead = HashMap<String, Any>()
        chatRead[Common.Chat.CHAT_READ] = true
        dbBaseActivity.collection(Common.Chat.CHAT_COLLECTION_NM)
            .document(documentId)
            .set(chatRead, SetOptions.merge())
            .addOnSuccessListener {
                //                        Log.e(Common.Chat.CHAT_COLLECTION_NM, "Success Update chat read");
//                setChatCount()
            }
            .addOnFailureListener {
                //                        Log.e(Common.Chat.CHAT_COLLECTION_NM, "Success Update chat read");
            }
    }

}