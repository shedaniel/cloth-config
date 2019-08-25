package me.shedaniel.clothconfig2.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class ConfigBuilderImpl implements ConfigBuilder {
    
    private Runnable savingRunnable;
    private Screen parent;
    private String title = "text.cloth-config.config";
    private boolean editable = true;
    private boolean tabsSmoothScroll = true;
    private boolean listSmoothScroll = true;
    private boolean doesProcessErrors = true;
    private boolean doesConfirmSave = true;
    private Identifier defaultBackground = DrawableHelper.BACKGROUND_LOCATION;
    private Consumer<Screen> afterInitConsumer = screen -> {};
    private Map<String, Identifier> categoryBackground = Maps.newHashMap();
    private Map<String, List<Pair<String, Object>>> dataMap = Maps.newLinkedHashMap();
    private String fallbackCategory = null;
    
    @Deprecated
    public ConfigBuilderImpl() {
    
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
    public String getTitle() {
        return title;
    }
    
    @Override
    public ConfigBuilder setTitle(String title) {
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
    public ConfigCategory getOrCreateCategory(String categoryKey) {
        if (dataMap.containsKey(categoryKey))
            return new ConfigCategoryImpl(categoryKey, identifier -> {
                categoryBackground.put(categoryKey, identifier);
            }, () -> dataMap.get(categoryKey));
        dataMap.put(categoryKey, Lists.newArrayList());
        if (fallbackCategory == null)
            fallbackCategory = categoryKey;
        return new ConfigCategoryImpl(categoryKey, identifier -> {
            categoryBackground.put(categoryKey, identifier);
        }, () -> dataMap.get(categoryKey));
    }
    
    @Override
    public ConfigBuilder removeCategory(String category) {
        if (dataMap.containsKey(category) && fallbackCategory.equals(category))
            fallbackCategory = null;
        dataMap.remove(category);
        return this;
    }
    
    @Override
    public ConfigBuilder removeCategoryIfExists(String category) {
        if (dataMap.containsKey(category) && fallbackCategory.equals(category))
            fallbackCategory = null;
        if (dataMap.containsKey(category))
            dataMap.remove(category);
        return this;
    }
    
    @Override
    public boolean hasCategory(String category) {
        return dataMap.containsKey(category);
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
    public ConfigBuilder setDoesProcessErrors(boolean processErrors) {
        this.doesProcessErrors = processErrors;
        return this;
    }
    
    @Override
    public boolean doesProcessErrors() {
        return doesProcessErrors;
    }
    
    @Override
    public Identifier getDefaultBackgroundTexture() {
        return defaultBackground;
    }
    
    @Override
    public ConfigBuilder setDefaultBackgroundTexture(Identifier texture) {
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
        if (dataMap.isEmpty() || fallbackCategory == null)
            throw new NullPointerException("There cannot be no categories or fallback category!");
        ClothConfigScreen screen = new ClothConfigScreen(parent, I18n.translate(title), dataMap, doesConfirmSave, doesProcessErrors, listSmoothScroll, defaultBackground, categoryBackground) {
            @Override
            public void onSave(Map<String, List<Pair<String, Object>>> o) {
                if (savingRunnable != null)
                    savingRunnable.run();
            }
            
            @Override
            public boolean isEditable() {
                return editable;
            }
            
            @Override
            protected void init() {
                super.init();
                afterInitConsumer.accept(this);
            }
            
            @Override
            public String getFallbackCategory() {
                return fallbackCategory;
            }
        };
        screen.setSmoothScrollingTabs(tabsSmoothScroll);
        return screen;
    }
    
    @Override
    public Runnable getSavingRunnable() {
        return savingRunnable;
    }
    
}
