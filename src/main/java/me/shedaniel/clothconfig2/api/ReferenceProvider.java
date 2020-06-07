package me.shedaniel.clothconfig2.api;

public interface ReferenceProvider<T> {
    AbstractConfigEntry<T> provideReferenceEntry();
}
