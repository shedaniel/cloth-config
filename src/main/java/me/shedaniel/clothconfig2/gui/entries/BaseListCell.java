package me.shedaniel.clothconfig2.gui.entries;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.Optional;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public abstract class BaseListCell extends AbstractParentElement {
    private Supplier<Optional<Text>> errorSupplier;
    
    public final int getPreferredTextColor() {
        return getConfigError().isPresent() ? 16733525 : 14737632;
    }
    
    public final Optional<Text> getConfigError() {
        if (errorSupplier != null && errorSupplier.get().isPresent())
            return errorSupplier.get();
        return getError();
    }
    
    public void setErrorSupplier(Supplier<Optional<Text>> errorSupplier) {
        this.errorSupplier = errorSupplier;
    }
    
    public abstract Optional<Text> getError();
    
    public abstract int getCellHeight();
    
    public abstract void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta);
    
    public void updateSelected(boolean isSelected) {}
    
    public boolean isRequiresRestart() {
        return false;
    }
    
    public boolean isEdited() {
        return getConfigError().isPresent();
    }
}