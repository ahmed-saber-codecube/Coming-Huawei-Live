package com.coming.customer.ui.payment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.coming.customer.R;
import com.coming.customer.ui.base.BaseActivity;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.huawei.hms.wallet.constant.WalletPassConstant;
import com.oppwa.mobile.connect.checkout.dialog.CheckoutActivity;
import com.oppwa.mobile.connect.checkout.dialog.GooglePayHelper;
import com.oppwa.mobile.connect.checkout.meta.CheckoutSettings;
import com.oppwa.mobile.connect.checkout.meta.CheckoutSkipCVVMode;
import com.oppwa.mobile.connect.exception.PaymentError;
import com.oppwa.mobile.connect.provider.Connect;
import com.oppwa.mobile.connect.provider.Transaction;
import com.oppwa.mobile.connect.provider.TransactionType;

import java.util.LinkedHashSet;


/**
 * Represents a base activity for making the payments with mobile sdk.
 * This activity handles payment callbacks.
 */

public class BasePaymentActivity extends BaseActivity implements CheckoutIdRequestListener, PaymentStatusRequestListener {

    private static final String STATE_RESOURCE_PATH = "STATE_RESOURCE_PATH";

    protected String resourcePath;
    private String totalAmount;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MAHER", BasePaymentActivity.class.getName());
        if (savedInstanceState != null) {
            resourcePath = savedInstanceState.getString(STATE_RESOURCE_PATH);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e("BasePaymentActivity", "OnnewIntent");
        Log.e("BasePaymentActivity", "OnnewIntent" + resourcePath);
        Log.e("BasePaymentActivity", "OnnewIntent" + hasCallbackScheme(intent));
        setIntent(intent);

        /* Check if the intent contains the callback scheme. */
        if (resourcePath != null && hasCallbackScheme(intent)) {
            requestPaymentStatus(resourcePath);
        }
    }

    /**
     * Returns <code>true</code> if the Intent contains one of the predefined schemes for the app.
     *
     * @param intent the incoming intent
     * @return <code>true</code> if the Intent contains one of the predefined schemes for the app;
     * <code>false</code> otherwise
     */
    protected boolean hasCallbackScheme(Intent intent) {
        String scheme = intent.getScheme();

        return getString(R.string.checkout_ui_callback_scheme).equals(scheme) ||
                getString(R.string.payment_button_callback_scheme).equals(scheme) ||
                getString(R.string.custom_ui_callback_scheme).equals(scheme);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_RESOURCE_PATH, resourcePath);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /* Override onActivityResult to get notified when the checkout process is done. */
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("onActivityResult", "resultCode" + resultCode);
        Log.e("onActivityResult", "data" + data);
        if (requestCode == CheckoutActivity.REQUEST_CODE_CHECKOUT) {
            switch (resultCode) {
                case CheckoutActivity.RESULT_OK:
                    /* Transaction completed. */
                    Transaction transaction = data.getParcelableExtra(
                            CheckoutActivity.CHECKOUT_RESULT_TRANSACTION);

                    resourcePath = data.getStringExtra(
                            CheckoutActivity.CHECKOUT_RESULT_RESOURCE_PATH);

                    /* Check the transaction type. */
                    if (transaction.getTransactionType() == TransactionType.SYNC) {
                        /* Check the status of synchronous transaction. */
                        requestPaymentStatus(resourcePath);
                    } else {
                        /* Asynchronous transaction is processed in the onNewIntent(). */
                        hideLoader();
                    }

                    break;
                case CheckoutActivity.RESULT_CANCELED:
                    hideLoader();
                    break;
                case CheckoutActivity.RESULT_ERROR:
                    hideLoader();
                    PaymentError error = data.getParcelableExtra(
                            CheckoutActivity.CHECKOUT_RESULT_ERROR);

//                    showToast(getString(R.string.error_message));
//                    showToast(error.getErrorInfo());

                    Log.e("onActivityResult : ", "errr : " + error.getErrorMessage());
                    Log.e("onActivityResult : ", "errr : " + error.getErrorInfo());
                    Log.e("onActivityResult : ", "errr : " + error.getErrorCode().toString());
            }
        }
    }

    protected void requestCheckoutId(String total, String userId, String cardType) {
        showLoader();

        totalAmount = total;
        new CheckoutIdRequestAsyncTask(this, this).execute(total, userId, cardType);
    }

    @Override
    public void onCheckoutIdReceived(String checkoutId) {
        hideLoader();

        Log.e("onCheckoutIdReceived", "checkoutId" + checkoutId);

        if (checkoutId == null) {
            showToast(getString(R.string.error_message));
        }
    }

    @Override
    public void onErrorOccurred() {
        hideLoader();
        showToast(getString(R.string.error_message));
        Log.e("onErrorOccurred", "");

    }

    @Override
    public void onPaymentStatusReceived(String paymentStatus) {
        hideLoader();
        Log.e("onPaymentStatusReceived", "" + paymentStatus);

        if ("OK".equals(paymentStatus)) {
//            showToast(getString(R.string.message_successful_payment));

            return;
        }

//        showToast(getString(R.string.message_unsuccessful_payment));
    }

    protected void requestPaymentStatus(String resourcePath) {
        showLoader();
        Log.e("requestPaymentStatus", "" + resourcePath);

        new PaymentStatusRequestAsyncTask(this).execute(resourcePath);
        checkBookingPaymentStatus(this);
    }


    //call API
    void checkBookingPaymentStatus(BasePaymentActivity p) {
        if (p != null) {
            if (p instanceof StoreDetailsActivity) {
                ((StoreDetailsActivity) p).checkPaymentStatus();
            }
        }
    }


    /**
     * Creates the new instance of {@link CheckoutSettings}
     * to instantiate the {@link CheckoutActivity}.
     *
     * @param checkoutId    the received checkout id
     * @param paymentBrands
     * @return the new instance of {@link CheckoutSettings}
     */
    protected CheckoutSettings createCheckoutSettings(String checkoutId, String callbackScheme, LinkedHashSet<String> paymentBrands) {
        return new CheckoutSettings(checkoutId, paymentBrands,
                Connect.ProviderMode.LIVE)
                //   Connect.ProviderMode.TEST)
                .setSkipCVVMode(CheckoutSkipCVVMode.FOR_STORED_CARDS)
                .setShopperResultUrl(callbackScheme + "://callback")
                .setGooglePayPaymentDataRequest(getGooglePayRequest());
    }

    private PaymentDataRequest getGooglePayRequest() {
        return GooglePayHelper.preparePaymentDataRequestBuilder(
                totalAmount,
                Constants.Config.CURRENCY,
                Constants.MERCHANT_ID,
                getPaymentMethodsForGooglePay(),
                getDefaultCardNetworksForGooglePay()
        ).build();
    }

    private Integer[] getPaymentMethodsForGooglePay() {
        return new Integer[]{
                WalletPassConstant.PAYMENT_METHOD_CARD,
                WalletPassConstant.PAYMENT_METHOD_TOKENIZED_CARD
        };
    }

    private Integer[] getDefaultCardNetworksForGooglePay() {
        return new Integer[]{
                WalletPassConstant.CARD_NETWORK_VISA,
                WalletPassConstant.CARD_NETWORK_MASTERCARD,
                WalletPassConstant.CARD_NETWORK_AMEX,
        };
    }

    @Override
    public int findFragmentPlaceHolder() {
        return 0;
    }

    @Override
    public int findContentView() {
        return R.layout.store_details_activity;
    }

}
