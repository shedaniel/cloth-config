package me.shedaniel.clothconfig2.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.Expandable;
import me.shedaniel.clothconfig2.api.TabbedConfigScreen;
import me.shedaniel.clothconfig2.gui.AbstractConfigScreen;
import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import me.shedaniel.clothconfig2.gui.GlobalizedClothConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
@ApiStatus.Internal
public class ConfigBuilderImpl implements ConfigBuilder {
    private Runnable savingRunnable;
    private Screen parent;
    private Component title = new TranslatableComponent("text.cloth-config.config");
    private boolean globalized = false;
    private boolean globalizedExpanded = true;
    private boolean editable = true;
    private boolean tabsSmoothScroll = true;
    private boolean listSmoothScroll = true;
    private boolean doesConfirmSave = true;
    private boolean transparentBackground = false;
    private ResourceLocation defaultBackground = GuiComponent.BACKGROUND_LOCATION;
    private Consumer<Screen> afterInitConsumer = screen -> {};
    private final Map<Component, ConfigCategory> categoryMap = Maps.newLinkedHashMap();
    private Component fallbackCategory = null;
    private boolean alwaysShowTabs = false;
    
    @ApiStatus.Internal
    public ConfigBuilderImpl() {
        
    }
    
    @Override
    public void setGlobalized(boolean globalized) {
        this.globalized = globalized;
    }
    
    @Override
    public void setGlobalizedExpanded(boolean globalizedExpanded) {
        this.globalizedExpanded = globalizedExpanded;
    }
    
    @Override
    public boolean isAlwaysShowTabs() {
        return alwaysShowTabs;
    }
    
    @Override
    public ConfigBuilder setAlwaysShowTabs(boolean alwaysShowTabs) {
        this.alwaysShowTabs = alwaysShowTabs;
        return this;
    }
    
    @Override
    public ConfigBuilder setTransparentBackground(boolean transparentBackground) {
        this.transparentBackground = transparentBackground;
        return this;
    }
    
    @Override
    public boolean hasTransparentBackground() {
        return transparentBackground;
    }
    
    @Override
    public ConfigBuilder setAfterInitConsumer(Consumer<Screen> afterInitConsumer) {
        this.afterInitConsumer = afterInitConsumer;
        return this;
    }
    
    @Override
    public ConfigBuilder setFallbackCategory(ConfigCategory fallbackCategory) {
        this.fallbackCategory = Objects.requireNonNull(fallbackCategory).getCategoryKey();
        return this;
    }
    
    @Override
    public Screen getParentScreen() {
        return parent;
    }
    
    @Override
    public ConfigBuilder setParentScreen(Screen parent) {
        this.parent = parent;
        return this;
    }
    
    @Override
    public Component getTitle() {
        return title;
    }
    
    @Override
    public ConfigBuilder setTitle(Component title) {
        this.title = title;
        return this;
    }
    
    @Override
    public boolean isEditable() {
        return editable;
    }
    
    @Override
    public ConfigBuilder setEditable(boolean editable) {
        this.editable = editable;
        return this;
    }
    
    @Override
    public ConfigCategory getOrCreateCategory(Component categoryKey) {
        if (categoryMap.containsKey(categoryKey))
            return categoryMap.get(categoryKey);
        if (fallbackCategory == null)
            fallbackCategory = categoryKey;
        return categoryMap.computeIfAbsent(categoryKey, key -> new ConfigCategoryImpl(this, categoryKey));
    }
    
    @Override
    public ConfigBuilder removeCategory(Component category) {
        if (categoryMap.containsKey(category) && fallbackCategory.equals(category))
            fallbackCategory = null;
        if (!categoryMap.containsKey(category))
            throw new NullPointerException("Category doesn't exist!");
        categoryMap.remove(category);
        return this;
    }
    
    @Override
    public ConfigBuilder removeCategoryIfExists(Component category) {
        if (categoryMap.containsKey(category) && fallbackCategory.equals(category))
            fallbackCategory = null;
        categoryMap.remove(category);
        return this;
    }
    
    @Override
    public boolean hasCategory(Component category) {
        return categoryMap.containsKey(category);
    }
    
    @Override
    public ConfigBuilder setShouldTabsSmoothScroll(boolean shouldTabsSmoothScroll) {
        this.tabsSmoothScroll = shouldTabsSmoothScroll;
        return this;
    }
    
    @Override
    public boolean isTabsSmoothScrolling() {
        return tabsSmoothScroll;
    }
    
    @Override
    public ConfigBuilder setShouldListSmoothScroll(boolean shouldListSmoothScroll) {
        this.listSmoothScroll = shouldListSmoothScroll;
        return this;
    }
    
    @Override
    public boolean isListSmoothScrolling() {
        return listSmoothScroll;
    }
    
    @Override
    public ConfigBuilder setDoesConfirmSave(boolean confirmSave) {
        this.doesConfirmSave = confirmSave;
        return this;
    }
    
    @Override
    public boolean doesConfirmSave() {
        return doesConfirmSave;
    }
    
    @Override
    public ResourceLocation getDefaultBackgroundTexture() {
        return defaultBackground;
    }
    
    @Override
    public ConfigBuilder setDefaultBackgroundTexture(ResourceLocation texture) {
        this.defaultBackground = texture;
        return this;
    }
    
    @Override
    public ConfigBuilder setSavingRunnable(Runnable runnable) {
        this.savingRunnable = runnable;
        return this;
    }
    
    @Override
    public Consumer<Screen> getAfterInitConsumer() {
        return afterInitConsumer;
    }
    
    @Override
    public Screen build() {
        if (categoryMap.isEmpty() || fallbackCategory == null)
            throw new NullPointerException("There cannot be no categories or fallback category!");
        AbstractConfigScreen screen;
        if (globalized) {
            screen = new GlobalizedClothConfigScreen(parent, title, categoryMap, defaultBackground);
        } else {
            screen = new ClothConfigScreen(parent, title,  categoryMap, defaultBackground);
        }
        screen.setSavingRunnable(savingRunnable);
        screen.setEditable(editable);
        screen.setFallbackCategory(fallbackCategory);
        screen.setTransparentBackground(transparentBackground);
        screen.setAlwaysShowTabs(alwaysShowTabs);
        screen.setConfirmSave(doesConfirmSave);
        screen.setAfterInitConsumer(afterInitConsumer);
        if (screen instanceof Expandable)
            ((Expandable) screen).setExpanded(globalizedExpanded);
        return screen;
    }
    
    @Override
    public Runnable getSavingRunnable() {
        return savingRunnable;
    }
    
}
