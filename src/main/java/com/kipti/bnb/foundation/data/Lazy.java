package com.kipti.bnb.foundation.data;

import java.util.function.Supplier;

public final class Lazy<T> implements Supplier<T> {
    private final Supplier<T> factory;
    private T value;
    private boolean resolved;

    private Lazy(final Supplier<T> factory) {
        this.factory = factory;
    }

    public static <T> Lazy<T> of(final Supplier<T> factory) {
        return new Lazy<>(factory);
    }

    @Override
    public T get() {
        if (!resolved) {
            value = factory.get();
            resolved = true;
        }
        return value;
    }
}
