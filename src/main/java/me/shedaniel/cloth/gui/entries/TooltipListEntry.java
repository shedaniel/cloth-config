package me.shedaniel.cloth.gui.entries;

import me.shedaniel.cloth.gui.ClothConfigScreen;
import me.shedaniel.clothconfig.gui.QueuedTooltip;

import java.awt.*;
import java.util.Optional;
import java.util.function.Supplier;

public abstract class TooltipListEntry extends ClothConfigScreen.ListEntry {
    
    private Supplier<Optional<String[]>> tooltipSupplier;
    
    public TooltipListEntry(String fieldName, Supplier<Optional<String[]>> tooltipSupplier) {
        super(fieldName);
        this.tooltipSupplier = tooltipSupplier;
    }
    
    @Override
    public void render(int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
        if (isMouseInside(mouseX, mouseY, x, y, entryWidth, entryHeight)) {
            Optional<String[]> tooltip = getTooltip();
            if (tooltip.isPresent() && tooltip.get().length > 0)
                getScreen().queueTooltip(QueuedTooltip.create(new Point(mouseX, mouseY), tooltip.get()));
        }
    }
    
    public boolean isMouseInside(int mouseX, int mouseY, int x, int y, int entryWidth, int entryHeight) {
        return mouseX >= x && mouseY >= y && mouseX <= x + entryWidth && mouseY <= y + entryHeight && getParent().isMouseOver(mouseX, mouseY);
    }
    
    public Optional<String[]> getTooltip() {
        if (tooltipSupplier != null)
            return tooltipSupplier.get();
        return Optional.empty();
    }
    
    public Supplier<Optional<String[]>> getTooltipSupplier() {
        return tooltipSupplier;
    }
    
    public void setTooltipSupplier(Supplier<Optional<String[]>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
    }
    
}
