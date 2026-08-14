package com.poshun.ordering.domain;

public final class EmptyOrderCannotBeSubmitted extends RuntimeException {
    public EmptyOrderCannotBeSubmitted() {
        super("an empty order cannot be submitted");
    }
}
