package me.shedaniel.clothconfig2.api;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public abstract class AbstractConfigListEntry<T> extends AbstractConfigEntry<T> {
    private Text fieldName;
    private boolean editable = true;
    private boolean requiresRestart;
    
    public AbstractConfigListEntry(Text fieldName, boolean requiresRestart) {
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
        return getConfigScreen().isEditable() && editable;
    }
    
    public void setEditable(boolean editable) {
        this.editable = editable;
    }
    
    public final int getPreferredTextColor() {
        return getConfigError().isPresent() ? 16733525 : 16777215;
    }
    
    @Override
    public Text getFieldName() {
        return fieldName;
    }
}
