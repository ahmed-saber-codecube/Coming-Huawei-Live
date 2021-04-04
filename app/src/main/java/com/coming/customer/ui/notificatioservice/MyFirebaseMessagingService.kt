package com.coming.customer.ui.notificatioservice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.coming.customer.BuildConfig
import com.coming.customer.R
import com.coming.customer.core.AppCommon
import com.coming.customer.ui.home.activity.HomeActivity
//import com.google.firebase.iid.FirebaseInstanceId
import com.huawei.hms.push.HmsMessageService
import com.huawei.hms.push.RemoteMessage
import com.google.gson.Gson
import java.util.*


class MyFirebaseMessagingService : HmsMessageService() {

    private val uniqeId: Int
        get() {
            val time = Date().time
            val tmpStr = time.toString()
            val last4Str = tmpStr.substring(tmpStr.length - 5)
            return Integer.valueOf(last4Str)
        }

    lateinit var notificationMap: MutableMap<String, String>


    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
       // val refreshedToken = FirebaseInstanceId.getInstance()
       // Log.e(TAG, "Refreshed token: $refreshedToken");
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. GetReviewData messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. GetReviewData messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.e(TAG, "From: " + remoteMessage.from!!)
        Log.e(TAG, "PAYLOAD :: " + remoteMessage.dataOfMap.toString())

        /* CHAT NOTIFICATION PAYLOAD//{post_activity_id=145, tag=AppliedDirect, body=Lucy Roy has been applied direct on your post, title=crowth, role=J}
        //{"title":"undefined sent you a message","message":"G  du in fghwgve","sender_id":"23","recent_chat_id":"@6@23@","order_id":"0","tag":"new_chat_message"}
         */

        // Check if message contains a data payload.
        if (remoteMessage.dataOfMap.isNotEmpty()) {
            Log.e(TAG, "Message data payload: " + remoteMessage.dataOfMap)

            notificationMap = remoteMessage.dataOfMap

            if (notificationMap.containsKey(AppCommon.NotificationParam.TAG)) {

//                val strNotification = notificationMap[AppCommon.NotificationParam.MESSAGE]

//                val notification = Gson().fromJson(strNotification, NotificationModel::class.java)

//                val data = JSONObject(notificationMap[AppCommon.NotificationParam.MESSAGE])

                val gson = Gson()
                val MapData = gson.toJson(notificationMap)
                val notification = Gson().fromJson(MapData, NotificationModel::class.java)

                /* if (!data.toString().isNullOrEmpty()) {
 //                    val message = Gson().fromJson(data.toString(), Message::class.java)
                     val notification = NotificationModel(
                         notificationMap[AppCommon.NotificationParam.TAG].toString()
                         , notificationMap[AppCommon.NotificationParam.ORDER_SUMMARY_ID].toString()
                         , notificationMap[AppCommon.NotificationParam.TITLE].toString()
                         , notificationMap[AppCommon.NotificationParam.MESSAGE].toString()
                         , notificationMap[AppCommon.NotificationParam.ORDER_ID].toString()
                     )



                 }*/

                handleNotification(notification)

            } else {

                /* val notification = Notification()
                 notification.tag = notificationMap[AppCommon.NotificationParam.TAG]
                 notification.title = notificationMap[AppCommon.NotificationParam.TITLE]
                 notification.body = notificationMap[AppCommon.NotificationParam.BODY]

                 handleNotification(notification)*/

            }

            if (remoteMessage.dataOfMap[AppCommon.NotificationParam.TAG].equals(AppCommon.NotificationTag.CHAT)) {
                //chat notification


/*
                val notification = NotificationModel()
                notification.time = notificationMap[AppCommon.NotificationParam.TIME]!!
                notification.message = notificationMap[AppCommon.NotificationParam.MESSAGE]
                        notificationMap[AppCommon.NotificationParam.SENDER_IMAGE]
                handleNotification(notification)
*/

            } else {
                //simple notification

/*
                val notification = NotificationModel()
                notification.tag = notificationMap[AppCommon.NotificationParam.TAG]
                notification.title = notificationMap[AppCommon.NotificationParam.TITLE]
                notification.message = notificationMap[AppCommon.NotificationParam.BODY]
                notification.post_activity_id =
                        notificationMap[AppCommon.NotificationParam.post_activity_id]
                notification.role = notificationMap[AppCommon.NotificationParam.role]

                handleNotification(notification)
*/

            }

        }


        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

    }

    private fun handleNotification(notification: NotificationModel) {
//        val sharedPreferences = getSharedPreferences(Common.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        if (notification.body != null) {
            val intent = Intent(this, HomeActivity::class.java)

            Log.e("notification_tag", "::" + notification.body)

            /*val broadCastIntent = Intent(AppCommon.UPDATE_IRDER_DETAILS)
            broadCastIntent.putExtra(AppCommon.ORDER_ID, notification.order_id)
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadCastIntent);*/

            if (true
//                notification.tag == AppCommon.NotificationTag.PLACE_ORDER_ACCEPTED ||
//                notification.tag == AppCommon.NotificationTag.PLACE_ORDER_REJECTED ||
//                notification.tag == AppCommon.NotificationTag.PLACE_ORDER_PACKED ||
//                notification.tag == AppCommon.NotificationTag.PLACE_ORDER_CANCELLED ||
//                notification.tag == AppCommon.NotificationTag.PLACE_ORDER_DELIVERED ||
//                notification.tag == AppCommon.NotificationTag.PLACE_ORDER_ON_THE_WAY ||
//                notification.tag == AppCommon.NotificationTag.PLACE_ORDER_CONFIRMED
//                || notification.NotificationId == AppCommon.NotificationTag.CANCEL_ORDER
//                || notification.NotificationId == AppCommon.NotificationTag.ORDER_DELIVERED
//                || notification.NotificationId == AppCommon.NotificationTag.TRACKING_NOTIFICATION
//                || notification.NotificationId == AppCommon.NotificationTag.DISCOUNT
//                || notification.NotificationId == AppCommon.NotificationTag.COMBO
//                || notification.NotificationId == AppCommon.NotificationTag.REVIEW_REQUEST
//                || notification.NotificationId == AppCommon.NotificationTag.RETURN_REQUEST
//                || notification.NotificationId == AppCommon.NotificationTag.ITEM_NOT_AVAILABLE
//                || notification.NotificationId == AppCommon.NotificationTag.CONTACT_US
//                || notification.NotificationId == AppCommon.NotificationTag.CHAT
            ) {

                var requestCode = 0

                when (notification.tag) {
//                    AppCommon.NotificationTag.PLACE_ORDER_ACCEPTED -> {
//                        requestCode = 1
//                    }
//                    AppCommon.NotificationTag.PLACE_ORDER_REJECTED -> {
//                        requestCode = 1
//                    }
//                    AppCommon.NotificationTag.PLACE_ORDER_PACKED -> {
//                        requestCode = 1
//                    }
//                    AppCommon.NotificationTag.PLACE_ORDER_CANCELLED -> {
//                        requestCode = 1
//                    }
//                    AppCommon.NotificationTag.PLACE_ORDER_DELIVERED -> {
//                        requestCode = 1
//                    }
//                    AppCommon.NotificationTag.PLACE_ORDER_ON_THE_WAY -> {
//                        requestCode = 1
//                    }
//                    AppCommon.NotificationTag.PLACE_ORDER_CONFIRMED -> {
//                        requestCode = 1
//                    }

                    AppCommon.NotificationTag.ACCEPT_ORDER -> {
                        //sendBroadcast(broadCastIntent)
                        val intentBC = Intent("filter_string")
                        intentBC.putExtra("key", notification.order_id)
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intentBC)
                    }

                    AppCommon.NotificationTag.ORDER_ACCEPT_BY_DRIVER -> {
                        //sendBroadcast(broadCastIntent)
                        val intentBC = Intent("filter_string")
                        intentBC.putExtra("key", notification.order_id)
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intentBC)
                    }

                    AppCommon.NotificationTag.CANCEL_ORDER -> {
                        requestCode = 2
                    }
                    AppCommon.NotificationTag.ORDER_DELIVERED -> {
                        requestCode = 3
                    }
                    AppCommon.NotificationTag.TRACKING_NOTIFICATION -> {
                        requestCode = 4
                    }
                    AppCommon.NotificationTag.DISCOUNT -> {
                        requestCode = 5
                    }
                    AppCommon.NotificationTag.COMBO -> {
                        requestCode = 6
                    }
                    AppCommon.NotificationTag.REVIEW_REQUEST -> {
                        requestCode = 7
                    }
                    AppCommon.NotificationTag.RETURN_REQUEST -> {
                        requestCode = 8
                    }
                    AppCommon.NotificationTag.ITEM_NOT_AVAILABLE -> {
                        requestCode = 9
                    }
                    AppCommon.NotificationTag.CONTACT_US -> {
                        requestCode = 10
                    }
                    AppCommon.NotificationTag.CHAT -> {
                        requestCode = 11
                    }
                }

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.putExtra("notification", notification)

                val pendingIntent = PendingIntent.getActivity(
                    this, requestCode,
                    intent, PendingIntent.FLAG_ONE_SHOT
                )

                if (notification.tag.equals(AppCommon.NotificationTag.CHAT)) {

/*
                    if (ChatFragment.mReceiverId != notification.sender_id) {
                        sendFirebaseChatNotification(
                                notification.message!!,
                                pendingIntent, notification.title!!
                        )
                    }
*/

                } else {

                    /* if (BaseActivity.isActive) {
                         // send broadcast if app is active
                         val broadcastIntent = Intent(ACTION_APP_NOTIFICATION)
                         intent.extras?.let { broadcastIntent.putExtras(it) }
                         sendBroadcast(broadcastIntent)
                     }*/

                    sendNotification(notification.body!!, pendingIntent, notification!!.title)

                }

            }
        }
    }


    private fun sendNotification(messageBody: String, pendingIntent: PendingIntent, title: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(notificationManager)
        }
        val notificationBuilder = NotificationCompat.Builder(
            this,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) AppCommon.CHANNEL_ID else BuildConfig.APPLICATION_ID
        )

        val notification = notificationBuilder
            .setSmallIcon(R.drawable.ic_stat_ic_notification)
            .setAutoCancel(true)
            .setContentTitle(title)
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageBody))
            .setContentIntent(pendingIntent)
            .setColor(Color.parseColor("#49E684"))
            .setLargeIcon(
                BitmapFactory.decodeResource(applicationContext.resources, R.drawable.ic_stat_ic_notification)
            )
            .setDefaults(NotificationManagerCompat.IMPORTANCE_HIGH)
            .setPriority(NotificationManagerCompat.IMPORTANCE_HIGH)
            .setContentText(messageBody).build()


        notificationManager.notify(uniqeId, notification)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(notificationManager: NotificationManager) {
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val att = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .build()

        // create android channel

        // create android channel
        val androidChannel = NotificationChannel(AppCommon.CHANNEL_ID, AppCommon.CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
        // Sets whether notifications posted to this channel should display notification lights
        // Sets whether notifications posted to this channel should display notification lights
        androidChannel.enableLights(true)
        // Sets whether notification posted to this channel should vibrate.
        // Sets whether notification posted to this channel should vibrate.
        androidChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
        androidChannel.enableVibration(true)
        androidChannel.setSound(defaultSoundUri, att)
        // Sets the notification light color for notifications posted to this channel
        //androidChannel.setLightColor(Color.GREEN);
        // Sets whether notifications posted to this channel appear on the lockscreen or not
        // Sets the notification light color for notifications posted to this channel
        //androidChannel.setLightColor(Color.GREEN);
        // Sets whether notifications posted to this channel appear on the lockscreen or not
        androidChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

        notificationManager.createNotificationChannel(androidChannel)
    }


    private fun sendFirebaseChatNotification(
        messageBody: String,
        pendingIntent: PendingIntent,
        title: String
    ) {

        val notificationBuilder = NotificationCompat.Builder(
            this,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) AppCommon.CHANNEL_ID else BuildConfig.APPLICATION_ID
        )

        val notification = notificationBuilder
            .setSmallIcon(R.drawable.ic_stat_ic_notification)
            .setAutoCancel(true)
            .setContentTitle(title)
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageBody))
            .setContentIntent(pendingIntent)
//            .setSound(Uri.parse("android.resource://" + packageName + "/" + R.raw.push_sound))
            .setColor(Color.parseColor("#49E684"))
            .setLargeIcon(
                BitmapFactory.decodeResource(applicationContext.resources, R.drawable.ic_stat_ic_notification)
            )
            .setDefaults(NotificationManagerCompat.IMPORTANCE_HIGH)
            .setPriority(NotificationManagerCompat.IMPORTANCE_HIGH)
            .setContentText(messageBody).build()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(uniqeId, notification)

    }

    companion object {

        val ACTION_PUSH = "MyFirebaseMsgService_ActionPush"
        val RC_common = 0
        val RC_booking_complete = 5
        private val TAG = "MyFirebaseMsgService"
        val ACTION_PUSH_TWILIO = "MyFirebaseMsgService_ActionPushTwilio"
        val ACTION_BLOCK_UNBLOCK = "MyFirebaseMsgService_ActionBLOCKUNBLOCK"
        val ACTION_DETAILS = "MyFirebaseMsgService_ActionDetails"
        val ACTION_RATE = "MyFirebaseMsgService_ActionRATE"
        val ACTION_BADGE_COUNT = "MyFirebaseMsgService_ActionPush"
        val ACTION_PAYMENT_ACCEPT_DECLINE = "MyFirebaseMsgService_ActionACCEPT_DECLINE"
        val ACTION_APP_NOTIFICATION = "KanggoAppNotification"
    }

}
