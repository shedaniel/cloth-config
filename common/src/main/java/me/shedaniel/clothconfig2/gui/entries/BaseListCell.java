package me.shedaniel.clothconfig2.gui.entries;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.network.chat.Component;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Optional;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public abstract class BaseListCell extends AbstractContainerEventHandler {
    private Supplier<Optional<Component>> errorSupplier;
    
    public final int getPreferredTextColor() {
        return getConfigError().isPresent() ? 16733525 : 14737632;
    }
    
    public final Optional<Component> getConfigError() {
        if (errorSupplier != null && errorSupplier.get().isPresent())
            return errorSupplier.get();
        return getError();
    }
    
    public void setErrorSupplier(Supplier<Optional<Component>> errorSupplier) {
        this.errorSupplier = errorSupplier;
    }
    
    public abstract Optional<Component> getError();
    
    public abstract int getCellHeight();
    
    public abstract void render(PoseStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta);
    
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