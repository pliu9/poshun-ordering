package com.poshun.ordering.domain;

public record Quantity(int value) {
    public Quantity {
        if (value < 1) {
            throw new IllegalArgumentException("quantity must be greater than zero");
        }
    }
}

