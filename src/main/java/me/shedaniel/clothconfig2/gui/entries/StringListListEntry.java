package me.shedaniel.clothconfig2.gui.entries;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@ApiStatus.Internal
public class StringListListEntry extends AbstractListListEntry<String, StringListListEntry.StringListCell, StringListListEntry> {

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
    public List<String> getValue() {
        return cells.stream().map(cell -> cell.widget.getText()).collect(Collectors.toList());
    }

    @Override
    public StringListListEntry self() {
        return this;
    }

    @Override
    protected StringListCell getFromValue(String value) {
        return new StringListCell(value, this);
    }

    public static class StringListCell extends AbstractListListEntry.AbstractListCell<String, StringListCell, StringListListEntry> {

        private TextFieldWidget widget;

        public StringListCell(String value, StringListListEntry listListEntry) {
            super(value, listListEntry);

            if (value == null)
                value = "";
            String finalValue = value;

            this.setErrorSupplier(() -> Optional.ofNullable(listListEntry.cellErrorSupplier).flatMap(fn -> fn.apply(this.getValue())));
            widget = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, 100, 18, "");
            widget.setMaxLength(Integer.MAX_VALUE);
            widget.setHasBorder(false);
            widget.setText(value);
            widget.setChangedListener(s -> {
                widget.setEditableColor(getPreferredTextColor());
                if (!Objects.equals(s, finalValue)) {
                    this.listListEntry.getScreen().setEdited(true, this.listListEntry.isRequiresRestart());
                }
            });
        }

        @Override
        public String getValue() {
            return widget.getText();
        }

        @Override
        public Optional<String> getError() {
            return Optional.empty();
        }

        @Override
        public int getCellHeight() {
            return 20;
        }

        @Override
        public void render(int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
            widget.setWidth(entryWidth - 12);
            widget.x = x;
            widget.y = y + 1;
            widget.setEditable(listListEntry.isEditable());
            widget.render(mouseX, mouseY, delta);
            if (isSelected && listListEntry.isEditable())
                fill(x, y + 12, x + entryWidth - 12, y + 13, getConfigError().isPresent() ? 0xffff5555 : 0xffe0e0e0);
        }

        @Override
        public List<? extends Element> children() {
            return Collections.singletonList(widget);
        }

    }

}
