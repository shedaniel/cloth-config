package me.shedaniel.clothconfig2.impl;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ConfigCategoryImpl implements ConfigCategory {
    
    private Supplier<List<Pair<String, Object>>> listSupplier;
    private Consumer<Identifier> backgroundConsumer;
    
    public ConfigCategoryImpl(Consumer<Identifier> backgroundConsumer, Supplier<List<Pair<String, Object>>> listSupplier) {
        this.listSupplier = listSupplier;
        this.backgroundConsumer = backgroundConsumer;
    }
    
    @Override
    public List<Object> getEntries() {
        return listSupplier.get().stream().map(Pair::getRight).collect(Collectors.toList());
    }
    
    @Override
    public ConfigCategory addEntry(AbstractConfigListEntry entry) {
        listSupplier.get().add(new Pair<>(entry.getFieldName(), entry));
        return this;
    }
    
    @Override
    public ConfigCategory setCategoryBackground(Identifier identifier) {
        backgroundConsumer.accept(identifier);
        return this;
    }
    
}
