package me.shedaniel.clothconfig2.api;

import org.jetbrains.annotations.Nullable;

public interface ValueHolder<T> {
    /**
     * Get the value held by this Value Holder.
     * 
     * <p>Depending on the implementation, this method may or may not be {@link Nullable}.
     * 
     * @return the current value.
     */
    T getValue();
}
