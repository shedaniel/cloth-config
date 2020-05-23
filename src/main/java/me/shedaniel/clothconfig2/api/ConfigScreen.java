package me.shedaniel.clothconfig2.api;

import net.minecraft.util.Identifier;

public interface ConfigScreen {
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
