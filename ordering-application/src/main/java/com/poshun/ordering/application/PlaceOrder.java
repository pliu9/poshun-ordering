package com.poshun.ordering.application;

import java.time.Clock;
import java.util.Objects;

import com.poshun.ordering.domain.OrderId;
import com.poshun.ordering.domain.PurchaseOrder;

public final class PlaceOrder {

    private final OrderRepository orderRepository;
    private final ProductCatalog productCatalog;
    private final Clock clock;

    public PlaceOrder(
            OrderRepository orderRepository,
            ProductCatalog productCatalog,
            Clock clock) {
        this.orderRepository = Objects.requireNonNull(orderRepository);
        this.productCatalog = Objects.requireNonNull(productCatalog);
        this.clock = Objects.requireNonNull(clock);
    }

    public OrderId execute(PlaceOrderCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        var order = PurchaseOrder.create(
                OrderId.newId(),
                command.customerId(),
                command.currency(),
                clock.instant());

        for (var line : command.lines()) {
            var product = productCatalog.getProduct(
                    line.productId(),
                    command.currency());

            order.addLine(
                    line.productId(),
                    line.quantity(),
                    product.unitPrice(),
                    product.minimumOrderQuantity());
        }

        order.submit();
        orderRepository.save(order);

        return order.id();
    }
}
