package me.shedaniel.clothconfig2.forge.api;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.shedaniel.clothconfig2.forge.gui.AbstractConfigScreen;
import me.shedaniel.clothconfig2.forge.gui.ClothConfigScreen;
import me.shedaniel.clothconfig2.forge.gui.widget.DynamicElementListWidget;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractConfigEntry<T> extends DynamicElementListWidget.ElementEntry<AbstractConfigEntry<T>> implements ReferenceProvider<T> {
    private AbstractConfigScreen screen;
    private Supplier<Optional<ITextComponent>> errorSupplier;
    @Nullable
    private List<ReferenceProvider<?>> referencableEntries = null;
    
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public final void setReferencableEntries(@Nullable List<AbstractConfigEntry<?>> referencableEntries) {
        setReferenceProviderEntries(referencableEntries.stream().map(AbstractConfigEntry::provideReferenceEntry).collect(Collectors.toList()));
    }
    
    public final void setReferenceProviderEntries(@Nullable List<ReferenceProvider<?>> referencableEntries) {
        this.referencableEntries = referencableEntries;
    }
    
    public void requestReferenceRebuilding() {
        AbstractConfigScreen configScreen = getConfigScreen();
        if (configScreen instanceof ReferenceBuildingConfigScreen) {
            ((ReferenceBuildingConfigScreen) configScreen).requestReferenceRebuilding();
        }
    }
    
    @Override
    public @NotNull AbstractConfigEntry<T> provideReferenceEntry() {
        return this;
    }
    
    @Nullable
    @ApiStatus.Internal
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public final List<AbstractConfigEntry<?>> getReferencableEntries() {
        return referencableEntries.stream().map(ReferenceProvider::provideReferenceEntry).collect(Collectors.toList());
    }
    
    @Nullable
    @ApiStatus.Internal
    public final List<ReferenceProvider<?>> getReferenceProviderEntries() {
        return referencableEntries;
    }
    
    public abstract boolean isRequiresRestart();
    
    public abstract void setRequiresRestart(boolean requiresRestart);
    
    public abstract ITextComponent getFieldName();
    
    public ITextComponent getDisplayedFieldName() {
        IFormattableTextComponent text = getFieldName().deepCopy();
        boolean hasError = getConfigError().isPresent();
        boolean isEdited = isEdited();
        if (hasError)
            text = text.func_240699_a_(TextFormatting.RED);
        if (isEdited)
            text = text.func_240699_a_(TextFormatting.ITALIC);
        if (!hasError && !isEdited)
            text = text.func_240699_a_(TextFormatting.GRAY);
        return text;
    }
    
    public abstract T getValue();
    
    public final Optional<ITextComponent> getConfigError() {
        if (errorSupplier != null && errorSupplier.get().isPresent())
            return errorSupplier.get();
        return getError();
    }
    
    public void lateRender(MatrixStack matrices, int mouseX, int mouseY, float delta) {}
    
    public void setErrorSupplier(Supplier<Optional<ITextComponent>> errorSupplier) {
        this.errorSupplier = errorSupplier;
    }
    
    public Optional<ITextComponent> getError() {
        return Optional.empty();
    }
    
    public abstract Optional<T> getDefaultValue();
    
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    @Nullable
    public final ClothConfigScreen getScreen() {
        if (screen instanceof ClothConfigScreen)
            return (ClothConfigScreen) screen;
        return null;
    }
    
    @Nullable
    public final AbstractConfigScreen getConfigScreen() {
        return screen;
    }
    
    public final void addTooltip(@NotNull Tooltip tooltip) {
        screen.addTooltip(tooltip);
    }
    
    public void updateSelected(boolean isSelected) {}
    
    @ApiStatus.Internal
    public final void setScreen(AbstractConfigScreen screen) {
        this.screen = screen;
    }
    
    public abstract void save();
    
    public boolean isEdited() {
        return getConfigError().isPresent();
    }
    
    @Override
    public int getItemHeight() {
        return 24;
    }
    
    public int getInitialReferenceOffset() {
        return 0;
    }
}
