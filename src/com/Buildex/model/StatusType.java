package com.Buildex.model;

public enum StatusType {
    PAID("Paid"),
    PENDING("Pending");

    private final String value;

    StatusType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}