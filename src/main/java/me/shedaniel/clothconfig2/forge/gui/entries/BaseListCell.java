package me.shedaniel.clothconfig2.forge.gui.entries;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class BaseListCell extends FocusableGui {
    private Supplier<Optional<ITextComponent>> errorSupplier;
    
    public final int getPreferredTextColor() {
        return getConfigError().isPresent() ? 16733525 : 14737632;
    }
    
    public final Optional<ITextComponent> getConfigError() {
        if (errorSupplier != null && errorSupplier.get().isPresent())
            return errorSupplier.get();
        return getError();
    }
    
    public void setErrorSupplier(Supplier<Optional<ITextComponent>> errorSupplier) {
        this.errorSupplier = errorSupplier;
    }
    
    public abstract Optional<ITextComponent> getError();
    
    public abstract int getCellHeight();
    
    public abstract void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta);
    
    public void updateSelected(boolean isSelected) {}
    
    public boolean isRequiresRestart() {
        return false;
    }
    
    public boolean isEdited() {
        return getConfigError().isPresent();
    }
    
    public void onAdd() {}
    
    public void onDelete() {}
}