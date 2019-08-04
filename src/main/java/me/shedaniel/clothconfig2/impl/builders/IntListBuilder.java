package me.shedaniel.clothconfig2.impl.builders;

import me.shedaniel.clothconfig2.gui.entries.BaseListEntry;
import me.shedaniel.clothconfig2.gui.entries.IntegerListListEntry;
import net.minecraft.client.resource.language.I18n;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class IntListBuilder extends FieldBuilder<List<Integer>, IntegerListListEntry> {
    
    private Consumer<List<Integer>> saveConsumer = null;
    private Function<List<Integer>, Optional<String[]>> tooltipSupplier = list -> Optional.empty();
    private List<Integer> value;
    private boolean expended = false;
    private Integer min = null, max = null;
    private Function<BaseListEntry, IntegerListListEntry.IntegerListCell> createNewInstance;
    private String addTooltip = I18n.translate("text.cloth-config.list.add"), removeTooltip = I18n.translate("text.cloth-config.list.remove");
    private boolean deleteButtonEnabled = true, insertInFront = true;
    
    public IntListBuilder(String resetButtonKey, String fieldNameKey, List<Integer> value) {
        super(resetButtonKey, fieldNameKey);
        this.value = value;
    }
    
    public IntListBuilder setDeleteButtonEnabled(boolean deleteButtonEnabled) {
        this.deleteButtonEnabled = deleteButtonEnabled;
        return this;
    }
    
    public IntListBuilder setInsertInFront(boolean insertInFront) {
        this.insertInFront = insertInFront;
        return this;
    }
    
    public IntListBuilder setAddButtonTooltip(String addTooltip) {
        this.addTooltip = addTooltip;
        return this;
    }
    
    public IntListBuilder setRemoveButtonTooltip(String removeTooltip) {
        this.removeTooltip = removeTooltip;
        return this;
    }
    
    public IntListBuilder requireRestart() {
        requireRestart(true);
        return this;
    }
    
    public IntListBuilder setCreateNewInstance(Function<BaseListEntry, IntegerListListEntry.IntegerListCell> createNewInstance) {
        this.createNewInstance = createNewInstance;
        return this;
    }
    
    public IntListBuilder setExpended(boolean expended) {
        this.expended = expended;
        return this;
    }
    
    public IntListBuilder setSaveConsumer(Consumer<List<Integer>> saveConsumer) {
        this.saveConsumer = saveConsumer;
        return this;
    }
    
    public IntListBuilder setDefaultValue(Supplier<List<Integer>> defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }
    
    public IntListBuilder setMin(int min) {
        this.min = min;
        return this;
    }
    
    public IntListBuilder setMax(int max) {
        this.max = max;
        return this;
    }
    
    public IntListBuilder removeMin() {
        this.min = null;
        return this;
    }
    
    public IntListBuilder removeMax() {
        this.max = null;
        return this;
    }
    
    public IntListBuilder setDefaultValue(List<Integer> defaultValue) {
        this.defaultValue = () -> defaultValue;
        return this;
    }
    
    public IntListBuilder setTooltipSupplier(Function<List<Integer>, Optional<String[]>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
        return this;
    }
    
    public IntListBuilder setTooltipSupplier(Supplier<Optional<String[]>> tooltipSupplier) {
        this.tooltipSupplier = list -> tooltipSupplier.get();
        return this;
    }
    
    public IntListBuilder setTooltip(Optional<String[]> tooltip) {
        this.tooltipSupplier = list -> tooltip;
        return this;
    }
    
    public IntListBuilder setTooltip(String... tooltip) {
        this.tooltipSupplier = list -> Optional.ofNullable(tooltip);
        return this;
    }
    
    @Override
    public IntegerListListEntry build() {
        IntegerListListEntry entry = new IntegerListListEntry(getFieldNameKey(), value, expended, null, saveConsumer, defaultValue, getResetButtonKey(), isRequireRestart()) {
            @Override
            public boolean isDeleteButtonEnabled() {
                return deleteButtonEnabled;
            }
            
            @Override
            public boolean insertInFront() {
                return insertInFront;
            }
        };
        if (min != null)
            entry.setMinimum(min);
        if (max != null)
            entry.setMaximum(max);
        if (createNewInstance != null)
            entry.setCreateNewInstance(createNewInstance);
        entry.setTooltipSupplier(() -> tooltipSupplier.apply(entry.getValue()));
        entry.setAddTooltip(addTooltip);
        entry.setAddTooltip(removeTooltip);
        return entry;
    }
    
}