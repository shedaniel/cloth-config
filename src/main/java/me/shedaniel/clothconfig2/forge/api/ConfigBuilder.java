package me.shedaniel.clothconfig2.forge.api;

import me.shedaniel.clothconfig2.forge.impl.ConfigBuilderImpl;
import me.shedaniel.clothconfig2.forge.impl.ConfigEntryBuilderImpl;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public interface ConfigBuilder {
    
    static ConfigBuilder create() {
        return new ConfigBuilderImpl();
    }
    
    ConfigBuilder setFallbackCategory(ConfigCategory fallbackCategory);
    
    Screen getParentScreen();
    
    ConfigBuilder setParentScreen(Screen parent);
    
    ITextComponent getTitle();
    
    ConfigBuilder setTitle(ITextComponent title);
    
    boolean isEditable();
    
    ConfigBuilder setEditable(boolean editable);
    
    ConfigCategory getOrCreateCategory(ITextComponent categoryKey);
    
    ConfigBuilder removeCategory(ITextComponent categoryKey);
    
    ConfigBuilder removeCategoryIfExists(ITextComponent categoryKey);
    
    boolean hasCategory(ITextComponent category);
    
    ConfigBuilder setShouldTabsSmoothScroll(boolean shouldTabsSmoothScroll);
    
    boolean isTabsSmoothScrolling();
    
    ConfigBuilder setShouldListSmoothScroll(boolean shouldListSmoothScroll);
    
    boolean isListSmoothScrolling();
    
    ConfigBuilder setDoesConfirmSave(boolean confirmSave);
    
    boolean doesConfirmSave();
    
    /**
     * This feature has been removed.
     */
    @Deprecated
    default ConfigBuilder setDoesProcessErrors(boolean processErrors) {
        return this;
    }
    
    /**
     * This feature has been removed.
     */
    @Deprecated
    default boolean doesProcessErrors() {
        return false;
    }
    
    ResourceLocation getDefaultBackgroundTexture();
    
    ConfigBuilder setDefaultBackgroundTexture(ResourceLocation texture);
    
    Runnable getSavingRunnable();
    
    ConfigBuilder setSavingRunnable(Runnable runnable);
    
    Consumer<Screen> getAfterInitConsumer();
    
    ConfigBuilder setAfterInitConsumer(Consumer<Screen> afterInitConsumer);
    
    default ConfigBuilder alwaysShowTabs() {
        return setAlwaysShowTabs(true);
    }
    
    void setGlobalized(boolean globalized);
    
    void setGlobalizedExpanded(boolean globalizedExpanded);
    
    boolean isAlwaysShowTabs();
    
    ConfigBuilder setAlwaysShowTabs(boolean alwaysShowTabs);
    
    ConfigBuilder setTransparentBackground(boolean transparentBackground);
    
    default ConfigBuilder transparentBackground() {
        return setTransparentBackground(true);
    }
    
    default ConfigBuilder solidBackground() {
        return setTransparentBackground(false);
    }
    
    @Deprecated
    default ConfigEntryBuilderImpl getEntryBuilder() {
        return (ConfigEntryBuilderImpl) entryBuilder();
    }
    
    default ConfigEntryBuilder entryBuilder() {
        return ConfigEntryBuilderImpl.create();
    }
    
    Screen build();
    
}
