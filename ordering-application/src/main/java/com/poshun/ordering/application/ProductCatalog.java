package com.poshun.ordering.application;

import java.util.Currency;
import java.util.Objects;

import com.poshun.ordering.domain.Money;
import com.poshun.ordering.domain.ProductId;
import com.poshun.ordering.domain.Quantity;

public interface ProductCatalog {

    Product getProduct(ProductId productId, Currency currency);

    record Product(
            Money unitPrice,
            Quantity minimumOrderQuantity) {

        public Product {
            Objects.requireNonNull(unitPrice, "unitPrice must not be null");
            Objects.requireNonNull(
                    minimumOrderQuantity,
                    "minimumOrderQuantity must not be null");
        }
    }
}
