package fr.univcotedazur.simpletcfs.exceptions;

import java.io.Serializable;

public class UnknownOrderId extends Exception {

    private String orderId;

    public UnknownOrderId(String id) {
        orderId = id;
    }

    public UnknownOrderId() {
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}