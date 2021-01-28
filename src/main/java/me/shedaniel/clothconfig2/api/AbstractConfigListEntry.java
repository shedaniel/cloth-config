package me.shedaniel.clothconfig2.api;

import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import me.shedaniel.math.Rectangle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public abstract class AbstractConfigListEntry<T> extends AbstractConfigEntry<T> {
    private Component fieldName;
    private boolean editable = true;
    private boolean requiresRestart;
    
    public AbstractConfigListEntry(Component fieldName, boolean requiresRestart) {
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
    
    public Rectangle getEntryArea(int x, int y, int entryWidth, int entryHeight) {
        return new Rectangle(getParent().left, y, getParent().right - getParent().left, getItemHeight() - 4);
    }
    
    public boolean isMouseInside(int mouseX, int mouseY, int x, int y, int entryWidth, int entryHeight) {
        return getParent().isMouseOver(mouseX, mouseY) && getEntryArea(x, y, entryWidth, entryHeight).contains(mouseX, mouseY);
    }
    
    @Override
    public void render(PoseStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        if (isMouseInside(mouseX, mouseY, x, y, entryWidth, entryHeight)) {
            Rectangle area = getEntryArea(x, y, entryWidth, entryHeight);
            if (getParent() instanceof ClothConfigScreen.ListWidget)
                ((ClothConfigScreen.ListWidget<AbstractConfigEntry<T>>) getParent()).thisTimeTarget = area;
        }
    }
    
    @Override
    public Component getFieldName() {
        return fieldName;
    }
}
