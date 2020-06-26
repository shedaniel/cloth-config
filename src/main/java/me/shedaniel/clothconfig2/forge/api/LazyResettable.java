package me.shedaniel.clothconfig2.forge.api;

import java.util.Objects;
import java.util.function.Supplier;

public final class LazyResettable<T> implements Supplier<T> {
    private final Supplier<T> supplier;
    private T value = null;
    private boolean supplied = false;
    
    public LazyResettable(Supplier<T> supplier) {
        this.supplier = supplier;
    }
    
    @Override
    public T get() {
        if (!supplied) {
            this.value = supplier.get();
            this.supplied = true;
        }
        return value;
    }
    
    public void reset() {
        this.supplied = false;
        this.value = null;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        LazyResettable<?> that = (LazyResettable<?>) o;
        return Objects.equals(get(), that.get());
    }
    
    @Override
    public int hashCode() {
        T value = get();
        return value != null ? value.hashCode() : 0;
    }
}
