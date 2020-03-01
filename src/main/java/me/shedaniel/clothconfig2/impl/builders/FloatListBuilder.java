package me.shedaniel.clothconfig2.impl.builders;

import me.shedaniel.clothconfig2.gui.entries.FloatListListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class FloatListBuilder extends FieldBuilder<List<Float>, FloatListListEntry> {
    
    protected Function<Float, Optional<String>> cellErrorSupplier;
    private Consumer<List<Float>> saveConsumer = null;
    private Function<List<Float>, Optional<String[]>> tooltipSupplier = list -> Optional.empty();
    private final List<Float> value;
    private boolean expanded = false;
    private Float min = null, max = null;
    private Function<FloatListListEntry, FloatListListEntry.FloatListCell> createNewInstance;
    private String addTooltip = I18n.translate("text.cloth-config.list.add"), removeTooltip = I18n.translate("text.cloth-config.list.remove");
    private boolean deleteButtonEnabled = true, insertInFront = true;
    
    public FloatListBuilder(String resetButtonKey, String fieldNameKey, List<Float> value) {
        super(resetButtonKey, fieldNameKey);
        this.value = value;
    }
    
    public Function<Float, Optional<String>> getCellErrorSupplier() {
        return cellErrorSupplier;
    }
    
    public FloatListBuilder setCellErrorSupplier(Function<Float, Optional<String>> cellErrorSupplier) {
        this.cellErrorSupplier = cellErrorSupplier;
        return this;
    }
    
    public FloatListBuilder setDeleteButtonEnabled(boolean deleteButtonEnabled) {
        this.deleteButtonEnabled = deleteButtonEnabled;
        return this;
    }
    
    public FloatListBuilder setErrorSupplier(Function<List<Float>, Optional<String>> errorSupplier) {
        this.errorSupplier = errorSupplier;
        return this;
    }
    
    public FloatListBuilder setInsertInFront(boolean insertInFront) {
        this.insertInFront = insertInFront;
        return this;
    }
    
    public FloatListBuilder setAddButtonTooltip(String addTooltip) {
        this.addTooltip = addTooltip;
        return this;
    }
    
    public FloatListBuilder setRemoveButtonTooltip(String removeTooltip) {
        this.removeTooltip = removeTooltip;
        return this;
    }
    
    public FloatListBuilder requireRestart() {
        requireRestart(true);
        return this;
    }
    
    public FloatListBuilder setCreateNewInstance(Function<FloatListListEntry, FloatListListEntry.FloatListCell> createNewInstance) {
        this.createNewInstance = createNewInstance;
        return this;
    }
    
    public FloatListBuilder setExpanded(boolean expanded) {
        this.expanded = expanded;
        return this;
    }
    
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public FloatListBuilder setExpended(boolean expanded) {
        return setExpanded(expanded);
    }
    
    public FloatListBuilder setSaveConsumer(Consumer<List<Float>> saveConsumer) {
        this.saveConsumer = saveConsumer;
        return this;
    }
    
    public FloatListBuilder setDefaultValue(Supplier<List<Float>> defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }
    
    public FloatListBuilder setMin(float min) {
        this.min = min;
        return this;
    }
    
    public FloatListBuilder setMax(float max) {
        this.max = max;
        return this;
    }
    
    public FloatListBuilder removeMin() {
        this.min = null;
        return this;
    }
    
    public FloatListBuilder removeMax() {
        this.max = null;
        return this;
    }
    
    public FloatListBuilder setDefaultValue(List<Float> defaultValue) {
        this.defaultValue = () -> defaultValue;
        return this;
    }
    
    public FloatListBuilder setTooltipSupplier(Supplier<Optional<String[]>> tooltipSupplier) {
        this.tooltipSupplier = list -> tooltipSupplier.get();
        return this;
    }
    
    public FloatListBuilder setTooltipSupplier(Function<List<Float>, Optional<String[]>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
        return this;
    }
    
    public FloatListBuilder setTooltip(Optional<String[]> tooltip) {
        this.tooltipSupplier = list -> tooltip;
        return this;
    }
    
    public FloatListBuilder setTooltip(String... tooltip) {
        this.tooltipSupplier = list -> Optional.ofNullable(tooltip);
        return this;
    }
    
    @NotNull
    @Override
    public FloatListListEntry build() {
        FloatListListEntry entry = new FloatListListEntry(getFieldNameKey(), value, expanded, null, saveConsumer, defaultValue, getResetButtonKey(), isRequireRestart(), deleteButtonEnabled, insertInFront);
        if (min != null)
            entry.setMinimum(min);
        if (max != null)
            entry.setMaximum(max);
        if (createNewInstance != null)
            entry.setCreateNewInstance(createNewInstance);
        entry.setCellErrorSupplier(cellErrorSupplier);
        entry.setTooltipSupplier(() -> tooltipSupplier.apply(entry.getValue()));
        entry.setAddTooltip(addTooltip);
        entry.setRemoveTooltip(removeTooltip);
        if (errorSupplier != null)
            entry.setErrorSupplier(() -> errorSupplier.apply(entry.getValue()));
        return entry;
    }
    
}
