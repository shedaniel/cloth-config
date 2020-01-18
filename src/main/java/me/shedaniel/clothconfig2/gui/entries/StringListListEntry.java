package me.shedaniel.clothconfig2.gui.entries;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@ApiStatus.Internal
public class StringListListEntry extends AbstractTextFieldListListEntry<String, StringListListEntry.StringListCell, StringListListEntry> {

    @Deprecated
    public StringListListEntry(String fieldName, List<String> value, boolean defaultExpanded, Supplier<Optional<String[]>> tooltipSupplier, Consumer<List<String>> saveConsumer, Supplier<List<String>> defaultValue, String resetButtonKey) {
        this(fieldName, value, defaultExpanded, tooltipSupplier, saveConsumer, defaultValue, resetButtonKey, false);
    }

    @Deprecated
    public StringListListEntry(String fieldName, List<String> value, boolean defaultExpanded, Supplier<Optional<String[]>> tooltipSupplier, Consumer<List<String>> saveConsumer, Supplier<List<String>> defaultValue, String resetButtonKey, boolean requiresRestart) {
        this(fieldName, value, defaultExpanded, tooltipSupplier, saveConsumer, defaultValue, resetButtonKey, requiresRestart, true, true);
    }

    public StringListListEntry(String fieldName, List<String> value, boolean defaultExpanded, Supplier<Optional<String[]>> tooltipSupplier, Consumer<List<String>> saveConsumer, Supplier<List<String>> defaultValue, String resetButtonKey, boolean requiresRestart, boolean deleteButtonEnabled, boolean insertInFront) {
        super(fieldName, value, defaultExpanded, tooltipSupplier, saveConsumer, defaultValue, resetButtonKey, requiresRestart, deleteButtonEnabled, insertInFront, StringListCell::new);
    }

    @Override
    public StringListListEntry self() {
        return this;
    }

    public static class StringListCell extends AbstractTextFieldListListEntry.AbstractTextFieldListCell<String, StringListCell, StringListListEntry> {

        public StringListCell(String value, StringListListEntry listListEntry) {
            super(value, listListEntry);
        }

        @Nullable
        @Override
        protected String substituteDefault(@Nullable String value) {
            if (value == null)
                return "";
            else
                return value;
        }

        @Override
        protected boolean isValidText(@NotNull String text) {
            return true;
        }

        @Override
        public String getValue() {
            return widget.getText();
        }

        @Override
        public Optional<String> getError() {
            return Optional.empty();
        }

    }

}
