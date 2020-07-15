package me.shedaniel.clothconfig2.forge.gui.entries;

import me.shedaniel.clothconfig2.forge.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.forge.api.Tooltip;
import me.shedaniel.math.Point;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Optional;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public abstract class TooltipListEntry<T> extends AbstractConfigListEntry<T> {
    
    @Nullable private Supplier<Optional<ITextComponent[]>> tooltipSupplier;
    
    @ApiStatus.Internal
    @Deprecated
    public TooltipListEntry(ITextComponent fieldName, @Nullable Supplier<Optional<ITextComponent[]>> tooltipSupplier) {
        this(fieldName, tooltipSupplier, false);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public TooltipListEntry(ITextComponent fieldName, @Nullable Supplier<Optional<ITextComponent[]>> tooltipSupplier, boolean requiresRestart) {
        super(fieldName, requiresRestart);
        this.tooltipSupplier = tooltipSupplier;
    }
    
    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        if (isMouseInside(mouseX, mouseY, x, y, entryWidth, entryHeight)) {
            Optional<ITextComponent[]> tooltip = getTooltip();
            if (tooltip.isPresent() && tooltip.get().length > 0)
                addTooltip(Tooltip.of(new Point(mouseX, mouseY), tooltip.get()));
        }
    }
    
    public boolean isMouseInside(int mouseX, int mouseY, int x, int y, int entryWidth, int entryHeight) {
        return mouseX >= x && mouseY >= y && mouseX <= x + entryWidth && mouseY <= y + entryHeight && getParent().isMouseOver(mouseX, mouseY);
    }
    
    public Optional<ITextComponent[]> getTooltip() {
        if (tooltipSupplier != null)
            return tooltipSupplier.get();
        return Optional.empty();
    }
    
    @Nullable
    public Supplier<Optional<ITextComponent[]>> getTooltipSupplier() {
        return tooltipSupplier;
    }
    
    public void setTooltipSupplier(@Nullable Supplier<Optional<ITextComponent[]>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
    }
    
}
