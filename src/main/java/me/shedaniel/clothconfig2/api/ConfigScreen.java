package me.shedaniel.clothconfig2.api;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface ConfigScreen {
    void setSavingRunnable(@Nullable Runnable savingRunnable);
    
    void setAfterInitConsumer(@Nullable Consumer<Screen> afterInitConsumer);
    
    Identifier getBackgroundLocation();
    
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
