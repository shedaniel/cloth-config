package me.shedaniel.forge.clothconfig2.api;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractConfigListEntry<T> extends AbstractConfigEntry<T> {
    private String fieldName;
    private boolean editable = true;
    private boolean requiresRestart;
    
    public AbstractConfigListEntry(String fieldName, boolean requiresRestart) {
        this.fieldName = fieldName;
        this.requiresRestart = requiresRestart;
    }
    
    @Override
    public boolean isRequiresRestart() {
        return requiresRestart;
    }
    
    @Override
    public void setRequiresRestart(boolean requiresRestart) {
        this.requiresRestart = requiresRestart;
    }
    
    public boolean isEditable() {
        return getScreen().isEditable() && editable;
    }
    
    public void setEditable(boolean editable) {
        this.editable = editable;
    }
    
    public final int getPreferredTextColor() {
        return getConfigError().isPresent() ? 16733525 : 16777215;
    }
    
    @Override
    public String getFieldName() {
        return fieldName;
    }
}
