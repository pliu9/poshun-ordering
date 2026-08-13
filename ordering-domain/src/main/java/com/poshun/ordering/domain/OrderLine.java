package com.poshun.ordering.domain;

import java.util.Objects;

public record OrderLine(ProductId productId, Quantity quantity, Money unitPrice) {
    public OrderLine {
        Objects.requireNonNull(productId, "productId must not be null");
        Objects.requireNonNull(quantity, "quantity must not be null");
        Objects.requireNonNull(unitPrice, "unitPrice must not be null");
    }

    public Money subtotal() {
        return unitPrice.multiply(quantity);
    }
}

