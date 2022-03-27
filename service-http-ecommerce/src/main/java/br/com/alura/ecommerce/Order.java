package br.com.alura.ecommerce;

import java.math.BigDecimal;

public class Order {

    private final String orderId;
    private final BigDecimal amount;

    public Order(String orderId, BigDecimal amount) {
        this.orderId = orderId;
        this.amount = amount;
    }
}
