package com.coming.customer.util
//
//import android.content.Context
//import android.util.Log
//
//import com.amazonaws.auth.AWSCredentials
//import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
//import com.amazonaws.regions.Region
//import com.amazonaws.regions.Regions
//import com.amazonaws.services.s3.AmazonS3Client
//import com.coming.customer.BuildConfig
//import com.coming.customer.R
//import com.google.firebase.remoteconfig.FirebaseRemoteConfig
//
///**
// * Created by hlink on 6/6/18.
// */
//
//object S3Utils {
//
//    // We only need one instance of the clients and credentials provider
//    private var sS3Client: AmazonS3Client? = null
//    //    private static CognitoCachingCredentialsProvider sCredProvider;
//
//    private var sTransferUtility: TransferUtility? = null
//
//    private var awsCredentials: AWSCredentials? = null
//
//    private var firebaseRemoteConfig: FirebaseRemoteConfig? = null
//
//
//    val IMAGE_PATH = "https://cominglive.s3.eu-central-1.amazonaws.com/"
//
//    val MY_BUCKET = "cominglive/"/*TODO set s3bucket url*/
//
//    val PROFILE_IMAGE = MY_BUCKET + "driver_image"
//
//    val CHAT_IMAGE = MY_BUCKET + "coming/chat_image"
//
//    val CATEGORY = MY_BUCKET + "category"
//
//    val DOCUMENT = MY_BUCKET + "document"
//
//    val SERVICE = MY_BUCKET + "service"
//
//    val PORTFOLIO = MY_BUCKET + "portfolio"
//
//    //val HOST = "$MY_BUCKET/host"
//
//    //val HOST_IMAGE = "$HOST/profile_image"
//
//    //val PROPERTY_IMAGE = "$HOST/property"
//
//    /**
//     * Gets an instance of CognitoCachingCredentialsProvider which is
//     * constructed using the given Context.
//     *
//     * @param context An Context instance.
//     * @return A default credential provider.
//     *//*
//    private static CognitoCachingCredentialsProvider getCredProvider(Context context) {
//        if (sCredProvider == null) {
//            sCredProvider = new CognitoCachingCredentialsProvider(
//                    context.getApplicationContext(),
//                    Constants.COGNITO_POOL_ID,
//                    Regions.fromName(Constants.COGNITO_POOL_REGION));
//        }
//        return sCredProvider;
//    }
//*/
//
//    /**
//     * Gets an instance of a S3 client which is constructed using the given
//     * Context.
//     *
//     * @param context An Context instance.
//     * @return A default S3 client.
//     */
//    fun getS3Client(context: Context): AmazonS3Client {
//
//        if (firebaseRemoteConfig == null) {
////            getFirebaseRemoteConfig()
//        }
//
//        if (sS3Client == null) {
//
//            awsCredentials = object : AWSCredentials {
//                override fun getAWSAccessKeyId(): String {
//                    return BuildConfig.AWSACKEY /*TODO set s3bucket AWS AcceskeyID */
////                    return getFirebaseConfigValue(AppCommon.AWSACKEY)
//                }
//
//                override fun getAWSSecretKey(): String {
//                    return BuildConfig.AWSSCKEY /*TODO set s3bucket AWS secret key*/
////                    return getFirebaseConfigValue(AppCommon.AWSSCKEY)
//                }
//            }
//
//            sS3Client = AmazonS3Client(awsCredentials)
//            sS3Client!!.setRegion(Region.getRegion(Regions.EU_CENTRAL_1))
//        }
//        return sS3Client as AmazonS3Client
//    }
//
//    /**
//     * Gets an instance of the TransferUtility which is constructed using the
//     * given Context
//     *
//     * @param context
//     * @return a TransferUtility instance
//     */
//    fun getTransferUtility(context: Context): TransferUtility {
//        if (sTransferUtility == null) {
//            sTransferUtility = TransferUtility(
//                getS3Client(context.applicationContext),
//                context.applicationContext
//            )
//        }
//
//        return sTransferUtility as TransferUtility
//    }
//
//    fun getFirebaseRemoteConfig() {
//        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
//
//        firebaseRemoteConfig?.setDefaults(R.xml.default_config_local)
//
//        firebaseRemoteConfig?.fetch()?.addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                firebaseRemoteConfig?.activateFetched()
//            } else {
//                getFirebaseRemoteConfig()
//            }
//        }
//    }
//
//
//    fun getFirebaseConfigValue(key: String): String {
//        if (firebaseRemoteConfig == null) {
//            getFirebaseRemoteConfig()
//            return ""
//        } else {
//            return firebaseRemoteConfig!!.getString(key)
//        }
//
//    }
//
//}
