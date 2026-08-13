package com.poshun.ordering.domain;

public final class MinimumOrderQuantityNotMet extends RuntimeException {
    private final ProductId productId;
    private final Quantity minimum;
    private final Quantity requested;

    public MinimumOrderQuantityNotMet(ProductId productId, Quantity minimum, Quantity requested) {
        super("product requires at least %d units; %d requested".formatted(minimum.value(), requested.value()));
        this.productId = productId;
        this.minimum = minimum;
        this.requested = requested;
    }

    public ProductId productId() { return productId; }
    public Quantity minimum() { return minimum; }
    public Quantity requested() { return requested; }
}

