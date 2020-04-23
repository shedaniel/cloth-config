package me.shedaniel.clothconfig2.api;

import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import me.shedaniel.clothconfig2.gui.widget.DynamicElementListWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.Optional;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public abstract class AbstractConfigEntry<T> extends DynamicElementListWidget.ElementEntry<AbstractConfigEntry<T>> {
    private ClothConfigScreen screen;
    private Supplier<Optional<Text>> errorSupplier;
    
    public abstract boolean isRequiresRestart();
    
    public abstract void setRequiresRestart(boolean requiresRestart);
    
    public abstract Text getFieldName();
    
    public abstract T getValue();
    
    public final Optional<Text> getConfigError() {
        if (errorSupplier != null && errorSupplier.get().isPresent())
            return errorSupplier.get();
        return getError();
    }
    
    public void lateRender(MatrixStack matrices, int mouseX, int mouseY, float delta) {}
    
    public void setErrorSupplier(Supplier<Optional<Text>> errorSupplier) {
        this.errorSupplier = errorSupplier;
    }
    
    public Optional<Text> getError() {
        return Optional.empty();
    }
    
    public abstract Optional<T> getDefaultValue();
    
    public final ClothConfigScreen.ListWidget getParent() {
        return screen.listWidget;
    }
    
    public final ClothConfigScreen getScreen() {
        return screen;
    }
    
    public void updateSelected(boolean isSelected) {}
    
    @Deprecated
    public final void setScreen(ClothConfigScreen screen) {
        this.screen = screen;
    }
    
    public abstract void save();
    
    @Override
    public int getItemHeight() {
        return 24;
    }
}
