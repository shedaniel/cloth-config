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
public class IntegerListListEntry extends AbstractTextFieldListListEntry<Integer, IntegerListListEntry.IntegerListCell, IntegerListListEntry> {

    private int minimum, maximum;

    @Deprecated
    public IntegerListListEntry(String fieldName, List<Integer> value, boolean defaultExpanded, Supplier<Optional<String[]>> tooltipSupplier, Consumer<List<Integer>> saveConsumer, Supplier<List<Integer>> defaultValue, String resetButtonKey) {
        this(fieldName, value, defaultExpanded, tooltipSupplier, saveConsumer, defaultValue, resetButtonKey, false);
    }

    @Deprecated
    public IntegerListListEntry(String fieldName, List<Integer> value, boolean defaultExpanded, Supplier<Optional<String[]>> tooltipSupplier, Consumer<List<Integer>> saveConsumer, Supplier<List<Integer>> defaultValue, String resetButtonKey, boolean requiresRestart) {
        this(fieldName, value, defaultExpanded, tooltipSupplier, saveConsumer, defaultValue, resetButtonKey, requiresRestart, true, true);
    }

    public IntegerListListEntry(
            String fieldName,
            List<Integer> value,
            boolean defaultExpanded,
            Supplier<Optional<String[]>> tooltipSupplier,
            Consumer<List<Integer>> saveConsumer,
            Supplier<List<Integer>> defaultValue,
            String resetButtonKey,
            boolean requiresRestart,
            boolean deleteButtonEnabled,
            boolean insertInFront
    ) {
        super(fieldName, value, defaultExpanded, tooltipSupplier, saveConsumer, defaultValue, resetButtonKey, requiresRestart, deleteButtonEnabled, insertInFront, IntegerListCell::new);
        this.minimum = Integer.MIN_VALUE;
        this.maximum = Integer.MAX_VALUE;
    }

    public IntegerListListEntry setMaximum(int maximum) {
        this.maximum = maximum;
        return this;
    }

    public IntegerListListEntry setMinimum(int minimum) {
        this.minimum = minimum;
        return this;
    }

    @Override
    public IntegerListListEntry self() {
        return this;
    }

    public static class IntegerListCell extends AbstractTextFieldListListEntry.AbstractTextFieldListCell<Integer, IntegerListCell, IntegerListListEntry> {

        public IntegerListCell(Integer value, IntegerListListEntry listListEntry) {
            super(value, listListEntry);
        }

        @Nullable
        @Override
        protected Integer substituteDefault(@Nullable Integer value) {
            if (value == null)
                return 0;
            else
                return value;
        }

        @Override
        protected boolean isValidText(@NotNull String text) {
            return text.chars().allMatch(c -> Character.isDigit(c) || c == '-');
        }

        public Integer getValue() {
            try {
                return Integer.valueOf(widget.getText());
            } catch (NumberFormatException e) {
                return 0;
            }
        }

        @Override
        public Optional<String> getError() {
            try {
                int i = Integer.parseInt(widget.getText());
                if (i > listListEntry.maximum)
                    return Optional.of(I18n.translate("text.cloth-config.error.too_large", listListEntry.maximum));
                else if (i < listListEntry.minimum)
                    return Optional.of(I18n.translate("text.cloth-config.error.too_small", listListEntry.minimum));
            } catch (NumberFormatException ex) {
                return Optional.of(I18n.translate("text.cloth-config.error.not_valid_number_int"));
            }
            return Optional.empty();
        }
    }

}
