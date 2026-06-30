package org.example.payment;

import org.example.model.PaymentResult;

public class PaypalPayment extends PaymentMethod {
    private final String email;

    public PaypalPayment(String email) {
        super("PayPal");
        this.email = email;
    }

    @Override
    public PaymentResult processPayment(double amount) {
        if (email == null || email.isBlank() || !email.contains("@")) {
            return new PaymentResult(false, "Valid PayPal email is required.");
        }

        return new PaymentResult(true, "Paid " + amount + " using PayPal");
    }
}
