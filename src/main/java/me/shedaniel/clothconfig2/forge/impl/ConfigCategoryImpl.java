package me.shedaniel.clothconfig2.forge.impl;

import me.shedaniel.clothconfig2.forge.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.forge.api.ConfigCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class ConfigCategoryImpl implements ConfigCategory {
    
    private final Supplier<List<Object>> listSupplier;
    private final Consumer<ResourceLocation> backgroundConsumer;
    private final Runnable destroyCategory;
    private final ITextComponent categoryKey;
    
    ConfigCategoryImpl(ITextComponent categoryKey, Consumer<ResourceLocation> backgroundConsumer, Supplier<List<Object>> listSupplier, Runnable destroyCategory) {
        this.listSupplier = listSupplier;
        this.backgroundConsumer = backgroundConsumer;
        this.categoryKey = categoryKey;
        this.destroyCategory = destroyCategory;
    }
    
    @Override
    public ITextComponent getCategoryKey() {
        return categoryKey;
    }
    
    @Override
    public List<Object> getEntries() {
        return listSupplier.get();
    }
    
    @Override
    public ConfigCategory addEntry(AbstractConfigListEntry entry) {
        listSupplier.get().add(entry);
        return this;
    }
    
    @Override
    public ConfigCategory setCategoryBackground(ResourceLocation identifier) {
        backgroundConsumer.accept(identifier);
        return this;
    }
    
    @Override
    public void removeCategory() {
        destroyCategory.run();
    }
    
}
