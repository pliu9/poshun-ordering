package com.poshun.ordering.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PurchaseOrderTest {
    private static final Currency USD = Currency.getInstance("USD");

    @Test
    void calculatesTheOrderTotal() {
        var order = newOrder();

        order.addLine(productId(), new Quantity(10), new Money(new BigDecimal("12.50"), USD), new Quantity(5));
        order.addLine(productId(), new Quantity(2), new Money(new BigDecimal("8.00"), USD), new Quantity(1));

        assertThat(order.total()).isEqualTo(new Money(new BigDecimal("141.00"), USD));
    }

    @Test
    void rejectsAQuantityBelowTheProductsMinimum() {
        var order = newOrder();
        var productId = productId();

        assertThatThrownBy(() -> order.addLine(
                productId,
                new Quantity(4),
                new Money(new BigDecimal("12.50"), USD),
                new Quantity(5)))
                .isInstanceOfSatisfying(MinimumOrderQuantityNotMet.class, error -> {
                    assertThat(error.productId()).isEqualTo(productId);
                    assertThat(error.minimum()).isEqualTo(new Quantity(5));
                    assertThat(error.requested()).isEqualTo(new Quantity(4));
                });
    }

    private PurchaseOrder newOrder() {
        return PurchaseOrder.create(
                OrderId.newId(),
                new CustomerId(UUID.randomUUID()),
                USD,
                Instant.parse("2026-08-13T12:00:00Z"));
    }

    private ProductId productId() {
        return new ProductId(UUID.randomUUID());
    }
}

