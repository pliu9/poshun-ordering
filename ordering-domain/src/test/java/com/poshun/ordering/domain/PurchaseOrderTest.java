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

    @Test
    void submitsAnOrderWithAtLeastOneLine() {
        var order = newOrder();
        order.addLine(productId(), new Quantity(5), new Money(new BigDecimal("12.50"), USD), new Quantity(5));

        order.submit();

        assertThat(order.status()).isEqualTo(OrderStatus.SUBMITTED);
    }

    @Test
    void rejectsSubmittingAnEmptyOrder() {
        var order = newOrder();

        assertThatThrownBy(order::submit)
                .isInstanceOf(EmptyOrderCannotBeSubmitted.class)
                .hasMessage("an empty order cannot be submitted");
    }

    @Test
    void rejectsAddingLineAfterSubmission() {
        var order = newOrder();
        order.addLine(productId(), new Quantity(5), new Money(new BigDecimal("12.50"), USD), new Quantity(5));
        order.submit();

        assertThatThrownBy(() -> order.addLine(
                productId(), new Quantity(5), new Money(new BigDecimal("12.50"), USD), new Quantity(5)))
                .isInstanceOf(IllegalStateException.class).hasMessage("only draft status can be modified.");
    }

    @Test
    void NoSubmittingAnOrderTwice(){
        var order = newOrder();
        order.addLine(productId(), new Quantity(5), new Money(new BigDecimal("12.50"), USD), new Quantity(5));
        order.submit();

        assertThatThrownBy(order::submit)
                .isInstanceOf(IllegalStateException.class).hasMessage("only draft status can be modified.");

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
