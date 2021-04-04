package com.coming.customer.ui.payment;


public interface PaymentStatusRequestListener {
    void onErrorOccurred();

    void onPaymentStatusReceived(String paymentStatus);
}
