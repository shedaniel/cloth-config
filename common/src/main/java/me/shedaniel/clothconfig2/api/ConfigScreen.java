package me.shedaniel.clothconfig2.api;

import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

public interface ConfigScreen {
    void setSavingRunnable(@Nullable Runnable savingRunnable);
    
    void setAfterInitConsumer(@Nullable Consumer<Screen> afterInitConsumer);
    
    ResourceLocation getBackgroundLocation();
    
    boolean isRequiresRestart();
    
    boolean isEdited();
    
    /**
     * Override #isEdited please
     */
    @Deprecated
    void setEdited(boolean edited);
    
    /**
     * Override #isEdited please
     */
    @Deprecated
    void setEdited(boolean edited, boolean legacyRequiresRestart);
    
    void saveAll(boolean openOtherScreens);
    
    void addTooltip(Tooltip tooltip);
}
