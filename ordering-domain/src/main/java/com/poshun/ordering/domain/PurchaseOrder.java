package com.poshun.ordering.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Objects;

public final class PurchaseOrder {
    private final OrderId id;
    private final CustomerId customerId;
    private final Currency currency;
    private final Instant createdAt;
    private final List<OrderLine> lines = new ArrayList<>();
    private OrderStatus status;

    private PurchaseOrder(OrderId id, CustomerId customerId, Currency currency, Instant createdAt) {
        this.id = Objects.requireNonNull(id);
        this.customerId = Objects.requireNonNull(customerId);
        this.currency = Objects.requireNonNull(currency);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.status = OrderStatus.DRAFT;
    }

    public static PurchaseOrder create(OrderId id, CustomerId customerId, Currency currency, Instant createdAt) {
        return new PurchaseOrder(id, customerId, currency, createdAt);
    }

    public void addLine(ProductId productId, Quantity quantity, Money unitPrice, Quantity minimumOrderQuantity) {
        if (quantity.value() < minimumOrderQuantity.value()) {
            throw new MinimumOrderQuantityNotMet(productId, minimumOrderQuantity, quantity);
        }
        if (!currency.equals(unitPrice.currency())) {
            throw new IllegalArgumentException("line currency must match order currency");
        }
        lines.add(new OrderLine(productId, quantity, unitPrice));
    }

    public Money total() {
        return lines.stream()
                .map(OrderLine::subtotal)
                .reduce(Money.zero(currency), Money::add);
    }

    public void submit() {
        if (lines.isEmpty()) {
            throw new EmptyOrderCannotBeSubmitted();
        }
        status = OrderStatus.SUBMITTED;
    }

    public OrderId id() { return id; }
    public CustomerId customerId() { return customerId; }
    public Instant createdAt() { return createdAt; }
    public OrderStatus status() { return status; }
    public List<OrderLine> lines() { return List.copyOf(lines); }
}
