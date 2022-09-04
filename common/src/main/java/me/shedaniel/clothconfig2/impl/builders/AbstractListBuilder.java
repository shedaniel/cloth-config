package me.shedaniel.clothconfig2.impl.builders;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public abstract class AbstractListBuilder<T, A extends AbstractConfigListEntry, SELF extends AbstractListBuilder<T, A, SELF>> extends AbstractFieldBuilder<List<T>, A, SELF> {
    protected Function<T, Optional<Component>> cellErrorSupplier;
    private boolean expanded = false;
    private Component addTooltip = Component.translatable("text.cloth-config.list.add");
    private Component removeTooltip = Component.translatable("text.cloth-config.list.remove");
    private boolean deleteButtonEnabled = true, insertInFront = false;
    
    protected AbstractListBuilder(Component resetButtonKey, Component fieldNameKey) {
        super(resetButtonKey, fieldNameKey);
    }
    
    public Function<T, Optional<Component>> getCellErrorSupplier() {
        return cellErrorSupplier;
    }
    
    public SELF setCellErrorSupplier(Function<T, Optional<Component>> cellErrorSupplier) {
        this.cellErrorSupplier = cellErrorSupplier;
        return (SELF) this;
    }
    
    public SELF setDeleteButtonEnabled(boolean deleteButtonEnabled) {
        this.deleteButtonEnabled = deleteButtonEnabled;
        return (SELF) this;
    }
    
    public SELF setInsertInFront(boolean insertInFront) {
        this.insertInFront = insertInFront;
        return (SELF) this;
    }
    
    public SELF setAddButtonTooltip(Component addTooltip) {
        this.addTooltip = addTooltip;
        return (SELF) this;
    }
    
    public SELF setRemoveButtonTooltip(Component removeTooltip) {
        this.removeTooltip = removeTooltip;
        return (SELF) this;
    }
    
    public SELF setExpanded(boolean expanded) {
        this.expanded = expanded;
        return (SELF) this;
    }
    
    public boolean isExpanded() {
        return expanded;
    }
    
    public Component getAddTooltip() {
        return addTooltip;
    }
    
    public Component getRemoveTooltip() {
        return removeTooltip;
    }
    
    public boolean isDeleteButtonEnabled() {
        return deleteButtonEnabled;
    }
    
    public boolean isInsertInFront() {
        return insertInFront;
    }
}
