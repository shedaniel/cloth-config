package me.shedaniel.clothconfig2.gui.entries;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.AbstractParentElement;

import java.util.Optional;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public abstract class BaseListCell extends AbstractParentElement {
    
    private Supplier<Optional<String>> errorSupplier;
    
    public final int getPreferredTextColor() {
        return getConfigError().isPresent() ? 16733525 : 14737632;
    }
    
    public final Optional<String> getConfigError() {
        if (errorSupplier != null && errorSupplier.get().isPresent())
            return errorSupplier.get();
        return getError();
    }
    
    public void setErrorSupplier(Supplier<Optional<String>> errorSupplier) {
        this.errorSupplier = errorSupplier;
    }
    
    public abstract Optional<String> getError();
    
    public abstract int getCellHeight();
    
    public abstract void render(int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta);
    
}