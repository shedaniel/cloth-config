package me.shedaniel.clothconfig2.gui.entries;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class DoubleListListEntry extends AbstractTextFieldListListEntry<Double, DoubleListListEntry.DoubleListCell, DoubleListListEntry> {
    
    private double minimum, maximum;
    
    @ApiStatus.Internal
    @Deprecated
    public DoubleListListEntry(String fieldName, List<Double> value, boolean defaultExpanded, Supplier<Optional<String[]>> tooltipSupplier, Consumer<List<Double>> saveConsumer, Supplier<List<Double>> defaultValue, String resetButtonKey) {
        this(fieldName, value, defaultExpanded, tooltipSupplier, saveConsumer, defaultValue, resetButtonKey, false);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public DoubleListListEntry(String fieldName, List<Double> value, boolean defaultExpanded, Supplier<Optional<String[]>> tooltipSupplier, Consumer<List<Double>> saveConsumer, Supplier<List<Double>> defaultValue, String resetButtonKey, boolean requiresRestart) {
        this(fieldName, value, defaultExpanded, tooltipSupplier, saveConsumer, defaultValue, resetButtonKey, requiresRestart, true, true);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public DoubleListListEntry(String fieldName, List<Double> value, boolean defaultExpanded, Supplier<Optional<String[]>> tooltipSupplier, Consumer<List<Double>> saveConsumer, Supplier<List<Double>> defaultValue, String resetButtonKey, boolean requiresRestart, boolean deleteButtonEnabled, boolean insertInFront) {
        super(fieldName, value, defaultExpanded, tooltipSupplier, saveConsumer, defaultValue, resetButtonKey, requiresRestart, deleteButtonEnabled, insertInFront, DoubleListCell::new);
        this.minimum = Double.NEGATIVE_INFINITY;
        this.maximum = Double.POSITIVE_INFINITY;
    }
    
    public DoubleListListEntry setMaximum(Double maximum) {
        this.maximum = maximum;
        return this;
    }
    
    public DoubleListListEntry setMinimum(Double minimum) {
        this.minimum = minimum;
        return this;
    }
    
    @Override
    public DoubleListListEntry self() {
        return this;
    }
    
    public static class DoubleListCell extends AbstractTextFieldListListEntry.AbstractTextFieldListCell<Double, DoubleListCell, DoubleListListEntry> {
        
        public DoubleListCell(Double value, final DoubleListListEntry listListEntry) {
            super(value, listListEntry);
        }
        
        @Nullable
        @Override
        protected Double substituteDefault(@Nullable Double value) {
            if (value == null)
                return 0d;
            else
                return value;
        }
        
        @Override
        protected boolean isValidText(@NotNull String text) {
            return text.chars().allMatch(c -> Character.isDigit(c) || c == '-' || c == '.');
        }
        
        public Double getValue() {
            try {
                return Double.valueOf(widget.getText());
            } catch (NumberFormatException e) {
                return 0d;
            }
        }
        
        @Override
        public Optional<String> getError() {
            try {
                double i = Double.parseDouble(widget.getText());
                if (i > listListEntry.maximum)
                    return Optional.of(I18n.translate("text.cloth-config.error.too_large", listListEntry.maximum));
                else if (i < listListEntry.minimum)
                    return Optional.of(I18n.translate("text.cloth-config.error.too_small", listListEntry.minimum));
            } catch (NumberFormatException ex) {
                return Optional.of(I18n.translate("text.cloth-config.error.not_valid_number_double"));
            }
            return Optional.empty();
        }
        
    }
    
}
