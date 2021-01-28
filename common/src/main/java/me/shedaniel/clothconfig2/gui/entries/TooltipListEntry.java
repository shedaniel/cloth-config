package me.shedaniel.clothconfig2.gui.entries;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.Tooltip;
import me.shedaniel.math.Point;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Optional;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public abstract class TooltipListEntry<T> extends AbstractConfigListEntry<T> {
    
    @Nullable private Supplier<Optional<Component[]>> tooltipSupplier;
    
    @ApiStatus.Internal
    @Deprecated
    public TooltipListEntry(Component fieldName, @Nullable Supplier<Optional<Component[]>> tooltipSupplier) {
        this(fieldName, tooltipSupplier, false);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public TooltipListEntry(Component fieldName, @Nullable Supplier<Optional<Component[]>> tooltipSupplier, boolean requiresRestart) {
        super(fieldName, requiresRestart);
        this.tooltipSupplier = tooltipSupplier;
    }
    
    @Override
    public void render(PoseStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
        if (isMouseInside(mouseX, mouseY, x, y, entryWidth, entryHeight)) {
            Optional<Component[]> tooltip = getTooltip(mouseX, mouseY);
            if (tooltip.isPresent() && tooltip.get().length > 0)
                addTooltip(Tooltip.of(new Point(mouseX, mouseY), tooltip.get()));
        }
    }
    
    public Optional<Component[]> getTooltip() {
        if (tooltipSupplier != null)
            return tooltipSupplier.get();
        return Optional.empty();
    }
    
    public Optional<Component[]> getTooltip(int mouseX, int mouseY) {
        return getTooltip();
    }
    
    @Nullable
    public Supplier<Optional<Component[]>> getTooltipSupplier() {
        return tooltipSupplier;
    }
    
    public void setTooltipSupplier(@Nullable Supplier<Optional<Component[]>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
    }
    
}
