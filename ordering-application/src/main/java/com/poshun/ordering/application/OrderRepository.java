package com.poshun.ordering.application;

import com.poshun.ordering.domain.PurchaseOrder;

public interface OrderRepository {

    void save(PurchaseOrder order);
}
