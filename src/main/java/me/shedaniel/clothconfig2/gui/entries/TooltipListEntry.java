package me.shedaniel.clothconfig2.gui.entries;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.Tooltip;
import me.shedaniel.math.Point;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public abstract class TooltipListEntry<T> extends AbstractConfigListEntry<T> {
    
    @Nullable private Supplier<Optional<Text[]>> tooltipSupplier;
    
    @ApiStatus.Internal
    @Deprecated
    public TooltipListEntry(Text fieldName, @Nullable Supplier<Optional<Text[]>> tooltipSupplier) {
        this(fieldName, tooltipSupplier, false);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public TooltipListEntry(Text fieldName, @Nullable Supplier<Optional<Text[]>> tooltipSupplier, boolean requiresRestart) {
        super(fieldName, requiresRestart);
        this.tooltipSupplier = tooltipSupplier;
    }
    
    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
        if (isMouseInside(mouseX, mouseY, x, y, entryWidth, entryHeight)) {
            Optional<Text[]> tooltip = getTooltip(mouseX, mouseY);
            if (tooltip.isPresent() && tooltip.get().length > 0)
                addTooltip(Tooltip.of(new Point(mouseX, mouseY), tooltip.get()));
        }
    }
    
    public Optional<Text[]> getTooltip() {
        if (tooltipSupplier != null)
            return tooltipSupplier.get();
        return Optional.empty();
    }
    
    public Optional<Text[]> getTooltip(int mouseX, int mouseY) {
        return getTooltip();
    }
    
    @Nullable
    public Supplier<Optional<Text[]>> getTooltipSupplier() {
        return tooltipSupplier;
    }
    
    public void setTooltipSupplier(@Nullable Supplier<Optional<Text[]>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
    }
    
}
