package com.poshun.ordering.application;

import java.util.Currency;
import java.util.List;
import java.util.Objects;

import com.poshun.ordering.domain.CustomerId;
import com.poshun.ordering.domain.ProductId;
import com.poshun.ordering.domain.Quantity;

public record PlaceOrderCommand(
        CustomerId customerId,
        Currency currency,
        List<Line> lines) {

    public PlaceOrderCommand {
        Objects.requireNonNull(customerId, "customerId must not be null");
        Objects.requireNonNull(currency, "currency must not be null");
        lines = List.copyOf(lines);
    }

    public record Line(ProductId productId, Quantity quantity) {

        public Line {
            Objects.requireNonNull(productId, "productId must not be null");
            Objects.requireNonNull(quantity, "quantity must not be null");
        }
    }
}
