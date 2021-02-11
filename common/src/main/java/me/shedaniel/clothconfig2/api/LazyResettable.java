/*
 * This file is part of Cloth Config.
 * Copyright (C) 2020 - 2021 shedaniel
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package me.shedaniel.clothconfig2.api;

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
