package com.coming.customer.ui.home.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.coming.customer.core.AppCommon;
import com.coming.customer.ui.payment.CheckoutIdRequestListener;


/**
 * Represents an async task to request a checkout id from the server.
 */
public class HomeCheckoutIdRequestAsyncTask extends AsyncTask<String, Void, String> {

    private CheckoutIdRequestListener listener;
    private Context context;

    public HomeCheckoutIdRequestAsyncTask(HomeBasePaymentActivity basePaymentActivity, CheckoutIdRequestListener listener) {
        this.listener = listener;
        this.context = basePaymentActivity;
    }

    @Override
    protected String doInBackground(String... params) {
        if (params.length != 3) {
            return null;
        }


        Log.e("tag", "requestCheckoutId::::" + getCheckoutId());
        return getCheckoutId();
    }

    private String getCheckoutId() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AppCommon.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(AppCommon.CHECKOUT_ID, "en");
    }

    @Override
    protected void onPostExecute(String checkoutId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AppCommon.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String checkoutIddddd = sharedPreferences.getString(AppCommon.CHECKOUT_ID, "en");
        Log.e("tag", "listnerr : " + listener);
        Log.e("tag", "listnerrcheckoutId : " + checkoutId);
        Log.e("tag", "listnerrcheckoutIddddd : " + checkoutIddddd);

        if (listener != null) {
            listener.onCheckoutIdReceived(checkoutIddddd);
        }
    }

}