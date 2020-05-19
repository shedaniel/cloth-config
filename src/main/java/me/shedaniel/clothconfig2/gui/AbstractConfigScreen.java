package me.shedaniel.clothconfig2.gui;

import me.shedaniel.clothconfig2.api.AbstractConfigEntry;
import me.shedaniel.clothconfig2.api.ConfigScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;

public abstract class AbstractConfigScreen extends Screen implements ConfigScreen {
    private boolean legacyEdited = false;
    protected boolean legacyRequiresRestart = false;
    private final Identifier backgroundLocation;
    
    protected AbstractConfigScreen(Text title, Identifier backgroundLocation) {
        super(title);
        this.backgroundLocation = backgroundLocation;
    }
    
    @Override
    public Identifier getBackgroundLocation() {
        return backgroundLocation;
    }
    
    @Override
    public boolean isRequiresRestart() {
        if (legacyRequiresRestart) return true;
        for (List<AbstractConfigEntry<?>> entries : getCategorizedEntries().values()) {
            for (AbstractConfigEntry<?> entry : entries) {
                if (entry.isRequiresRestart()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public abstract Map<Text, List<AbstractConfigEntry<?>>> getCategorizedEntries();
    
    @Override
    public boolean isEdited() {
        if (legacyEdited) return true;
        for (List<AbstractConfigEntry<?>> entries : getCategorizedEntries().values()) {
            for (AbstractConfigEntry<?> entry : entries) {
                if (entry.isEdited()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Override #isEdited please
     */
    @Override
    @Deprecated
    public void setEdited(boolean edited) {
        this.legacyEdited = edited;
    }
    
    /**
     * Override #isEdited please
     */
    @Override
    @Deprecated
    public void setEdited(boolean edited, boolean legacyRequiresRestart) {
        setEdited(edited);
        if (!this.legacyRequiresRestart && legacyRequiresRestart)
            this.legacyRequiresRestart = legacyRequiresRestart;
    }
}
