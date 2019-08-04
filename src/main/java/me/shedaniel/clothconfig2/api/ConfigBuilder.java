package me.shedaniel.clothconfig2.api;

import me.shedaniel.clothconfig2.impl.ConfigBuilderImpl;
import me.shedaniel.clothconfig2.impl.ConfigEntryBuilderImpl;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public interface ConfigBuilder {
    
    @SuppressWarnings("deprecation")
    public static ConfigBuilder create() {
        return new ConfigBuilderImpl();
    }
    
    /**
     * @deprecated Use {@link ConfigBuilder#create()}
     */
    @Deprecated
    public static ConfigBuilder create(Screen parent, String title) {
        return create().setParentScreen(parent).setTitle(title);
    }
    
    Screen getParentScreen();
    
    ConfigBuilder setParentScreen(Screen parent);
    
    String getTitle();
    
    ConfigBuilder setTitle(String title);
    
    boolean isEditable();
    
    ConfigBuilder setEditable(boolean editable);
    
    ConfigCategory getOrCreateCategory(String categoryKey);
    
    ConfigBuilder removeCategory(String categoryKey);
    
    ConfigBuilder removeCategoryIfExists(String categoryKey);
    
    boolean hasCategory(String category);
    
    ConfigBuilder setShouldTabsSmoothScroll(boolean shouldTabsSmoothScroll);
    
    boolean isTabsSmoothScrolling();
    
    ConfigBuilder setShouldListSmoothScroll(boolean shouldListSmoothScroll);
    
    boolean isListSmoothScrolling();
    
    ConfigBuilder setDoesConfirmSave(boolean confirmSave);
    
    boolean doesConfirmSave();
    
    ConfigBuilder setDoesProcessErrors(boolean processErrors);
    
    boolean doesProcessErrors();
    
    Identifier getDefaultBackgroundTexture();
    
    ConfigBuilder setDefaultBackgroundTexture(Identifier texture);
    
    Runnable getSavingRunnable();
    
    ConfigBuilder setSavingRunnable(Runnable runnable);
    
    Consumer<Screen> getAfterInitConsumer();
    
    ConfigBuilder setAfterInitConsumer(Consumer<Screen> afterInitConsumer);
    
    default ConfigEntryBuilderImpl getEntryBuilder() {
        return ConfigEntryBuilderImpl.create();
    }
    
    Screen build();
    
}
