package me.shedaniel.clothconfig2.impl;

import com.google.common.collect.Lists;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class ConfigCategoryImpl implements ConfigCategory {
    private final ConfigBuilder builder;
    private final List<Object> data;
    @Nullable
    private ResourceLocation background;
    private final Component categoryKey;
    @Nullable
    private Supplier<Optional<FormattedText[]>> description = Optional::empty;
    
    ConfigCategoryImpl(ConfigBuilder builder, Component categoryKey) {
        this.builder = builder;
        this.data = Lists.newArrayList();
        this.categoryKey = categoryKey;
    }
    
    @Override
    public Component getCategoryKey() {
        return categoryKey;
    }
    
    @Override
    public List<Object> getEntries() {
        return data;
    }
    
    @Override
    public ConfigCategory addEntry(AbstractConfigListEntry entry) {
        data.add(entry);
        return this;
    }
    
    @Override
    public ConfigCategory setCategoryBackground(ResourceLocation identifier) {
        if (builder.hasTransparentBackground())
            throw new IllegalStateException("Cannot set category background if screen is using transparent background.");
        background = identifier;
        return this;
    }
    
    @Override
    public void removeCategory() {
        builder.removeCategory(categoryKey);
    }
    
    @Override
    public void setBackground(@Nullable ResourceLocation background) {
        this.background = background;
    }
    
    @Override
    @Nullable
    public ResourceLocation getBackground() {
        return background;
    }
    
    @Nullable
    @Override
    public Supplier<Optional<FormattedText[]>> getDescription() {
        return description;
    }
    
    @Override
    public void setDescription(@Nullable Supplier<Optional<FormattedText[]>> description) {
        this.description = description;
    }
}
