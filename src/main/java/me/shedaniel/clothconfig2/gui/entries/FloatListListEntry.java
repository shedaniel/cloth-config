package me.shedaniel.clothconfig2.gui.entries;

import net.minecraft.client.resource.language.I18n;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@ApiStatus.Internal
public class FloatListListEntry extends AbstractTextFieldListListEntry<Float, FloatListListEntry.FloatListCell, FloatListListEntry> {

    private float minimum, maximum;

    @Deprecated
    public FloatListListEntry(String fieldName, List<Float> value, boolean defaultExpanded, Supplier<Optional<String[]>> tooltipSupplier, Consumer<List<Float>> saveConsumer, Supplier<List<Float>> defaultValue, String resetButtonKey) {
        this(fieldName, value, defaultExpanded, tooltipSupplier, saveConsumer, defaultValue, resetButtonKey, false);
    }

    @Deprecated
    public FloatListListEntry(String fieldName, List<Float> value, boolean defaultExpanded, Supplier<Optional<String[]>> tooltipSupplier, Consumer<List<Float>> saveConsumer, Supplier<List<Float>> defaultValue, String resetButtonKey, boolean requiresRestart) {
        this(fieldName, value, defaultExpanded, tooltipSupplier, saveConsumer, defaultValue, resetButtonKey, requiresRestart, true, true);
    }

    public FloatListListEntry(
            String fieldName,
            List<Float> value,
            boolean defaultExpanded,
            Supplier<Optional<String[]>> tooltipSupplier,
            Consumer<List<Float>> saveConsumer,
            Supplier<List<Float>> defaultValue,
            String resetButtonKey,
            boolean requiresRestart,
            boolean deleteButtonEnabled,
            boolean insertInFront
    ) {
        super(fieldName, value, defaultExpanded, tooltipSupplier, saveConsumer, defaultValue, resetButtonKey, requiresRestart, deleteButtonEnabled, insertInFront, FloatListCell::new);
        this.minimum = Float.NEGATIVE_INFINITY;
        this.maximum = Float.POSITIVE_INFINITY;
    }

    public FloatListListEntry setMaximum(float maximum) {
        this.maximum = maximum;
        return this;
    }

    public FloatListListEntry setMinimum(float minimum) {
        this.minimum = minimum;
        return this;
    }

    @Override
    public FloatListListEntry self() {
        return this;
    }

    public static class FloatListCell extends AbstractTextFieldListListEntry.AbstractTextFieldListCell<Float, FloatListCell, FloatListListEntry> {

        public FloatListCell(Float value, FloatListListEntry listListEntry) {
            super(value, listListEntry);
        }

        @Nullable
        @Override
        protected Float substituteDefault(@Nullable Float value) {
            if (value == null)
                return 0f;
            else
                return value;
        }

        @Override
        protected boolean isValidText(@NotNull String text) {
            return text.chars().allMatch(c -> Character.isDigit(c) || c == '-' || c == '.');
        }

        public Float getValue() {
            try {
                return Float.valueOf(widget.getText());
            } catch (NumberFormatException e) {
                return 0f;
            }
        }

        @Override
        public Optional<String> getError() {
            try {
                float i = Float.parseFloat(widget.getText());
                if (i > listListEntry.maximum)
                    return Optional.of(I18n.translate("text.cloth-config.error.too_large", listListEntry.maximum));
                else if (i < listListEntry.minimum)
                    return Optional.of(I18n.translate("text.cloth-config.error.too_small", listListEntry.minimum));
            } catch (NumberFormatException ex) {
                return Optional.of(I18n.translate("text.cloth-config.error.not_valid_number_float"));
            }
            return Optional.empty();
        }
    }
}
