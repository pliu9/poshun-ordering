package com.poshun.ordering.application;

import static com.poshun.ordering.domain.OrderStatus.SUBMITTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.poshun.ordering.domain.CustomerId;
import com.poshun.ordering.domain.MinimumOrderQuantityNotMet;
import com.poshun.ordering.domain.Money;
import com.poshun.ordering.domain.ProductId;
import com.poshun.ordering.domain.PurchaseOrder;
import com.poshun.ordering.domain.Quantity;

class PlaceOrderTest {

    private static final Currency USD = Currency.getInstance("USD");
    private static final Instant NOW = Instant.parse("2026-08-18T12:00:00Z");

    @Test
    void placesAndSavesAnOrder() {
        var repository = new RecordingOrderRepository();
        var customerId = new CustomerId(UUID.randomUUID());
        var productId = new ProductId(UUID.randomUUID());
        var product = new ProductCatalog.Product(
                new Money(new BigDecimal("12.50"), USD),
                new Quantity(5));
        ProductCatalog productCatalog = (id, currency) -> product;
        var placeOrder = new PlaceOrder(
                repository,
                productCatalog,
                Clock.fixed(NOW, ZoneOffset.UTC));
        var command = new PlaceOrderCommand(
                customerId,
                USD,
                List.of(new PlaceOrderCommand.Line(
                        productId,
                        new Quantity(10))));

        var orderId = placeOrder.execute(command);

        var savedOrder = repository.savedOrder();
        assertThat(savedOrder).isNotNull();
        assertThat(savedOrder.id()).isEqualTo(orderId);
        assertThat(savedOrder.customerId()).isEqualTo(customerId);
        assertThat(savedOrder.createdAt()).isEqualTo(NOW);
        assertThat(savedOrder.status()).isEqualTo(SUBMITTED);
        assertThat(savedOrder.lines()).hasSize(1);
        assertThat(savedOrder.total()).isEqualTo(
                new Money(new BigDecimal("125.00"), USD));
    }

    @Test
    void doesNotSaveAnOrderWhenQuantityIsBelowMinimum() {
        var repository = new RecordingOrderRepository();
        var productId = new ProductId(UUID.randomUUID());
        var product = new ProductCatalog.Product(
                new Money(new BigDecimal("12.50"), USD),
                new Quantity(10));
        ProductCatalog productCatalog = (id, currency) -> product;
        var placeOrder = new PlaceOrder(
                repository,
                productCatalog,
                Clock.fixed(NOW, ZoneOffset.UTC));
        var command = new PlaceOrderCommand(
                new CustomerId(UUID.randomUUID()),
                USD,
                List.of(new PlaceOrderCommand.Line(
                        productId,
                        new Quantity(5))));

        assertThatThrownBy(() -> placeOrder.execute(command))
                .isInstanceOf(MinimumOrderQuantityNotMet.class);
        assertThat(repository.savedOrder()).isNull();
    }

    private static final class RecordingOrderRepository implements OrderRepository {

        private PurchaseOrder savedOrder;

        @Override
        public void save(PurchaseOrder order) {
            savedOrder = order;
        }

        PurchaseOrder savedOrder() {
            return savedOrder;
        }
    }
}
