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
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
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
    private Text title = new TranslatableText("text.cloth-config.config");
    private boolean globalized = false;
    private boolean globalizedExpanded = true;
    private boolean editable = true;
    private boolean tabsSmoothScroll = true;
    private boolean listSmoothScroll = true;
    private boolean doesConfirmSave = true;
    private boolean transparentBackground = false;
    private Identifier defaultBackground = DrawableHelper.BACKGROUND_TEXTURE;
    private Consumer<Screen> afterInitConsumer = screen -> {};
    private final Map<Text, Identifier> categoryBackground = Maps.newHashMap();
    private final Map<Text, List<Object>> dataMap = Maps.newLinkedHashMap();
    private final Map<Text, ConfigCategory> categoryMap = Maps.newHashMap();
    private Text fallbackCategory = null;
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
    public Text getTitle() {
        return title;
    }
    
    @Override
    public ConfigBuilder setTitle(Text title) {
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
    public ConfigCategory getOrCreateCategory(Text categoryKey) {
        if (categoryMap.containsKey(categoryKey))
            return categoryMap.get(categoryKey);
        dataMap.put(categoryKey, Lists.newArrayList());
        if (fallbackCategory == null)
            fallbackCategory = categoryKey;
        return categoryMap.computeIfAbsent(categoryKey, key -> new ConfigCategoryImpl(categoryKey, identifier -> {
            if (transparentBackground)
                throw new IllegalStateException("Cannot set category background if screen is using transparent background.");
            categoryBackground.put(categoryKey, identifier);
        }, () -> dataMap.get(categoryKey), () -> removeCategory(categoryKey)));
    }
    
    @Override
    public ConfigBuilder removeCategory(Text category) {
        if (dataMap.containsKey(category) && fallbackCategory.equals(category))
            fallbackCategory = null;
        if (!dataMap.containsKey(category))
            throw new NullPointerException("Category doesn't exist!");
        dataMap.remove(category);
        return this;
    }
    
    @Override
    public ConfigBuilder removeCategoryIfExists(Text category) {
        if (dataMap.containsKey(category) && fallbackCategory.equals(category))
            fallbackCategory = null;
        dataMap.remove(category);
        return this;
    }
    
    @Override
    public boolean hasCategory(Text category) {
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
        AbstractConfigScreen screen;
        if (globalized) {
            screen = new GlobalizedClothConfigScreen(parent, title, dataMap, defaultBackground);
        } else {
            screen = new ClothConfigScreen(parent, title, dataMap, categoryMap, defaultBackground);
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
        if (screen instanceof TabbedConfigScreen)
            categoryBackground.forEach(((TabbedConfigScreen) screen)::registerCategoryBackground);
        return screen;
    }
    
    @Override
    public Runnable getSavingRunnable() {
        return savingRunnable;
    }
    
}
