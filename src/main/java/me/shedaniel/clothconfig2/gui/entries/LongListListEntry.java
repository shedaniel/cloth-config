package me.shedaniel.clothconfig2.gui.entries;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@ApiStatus.Internal
public class LongListListEntry extends AbstractListListEntry<Long, LongListListEntry.LongListCell, LongListListEntry> {

    private long minimum, maximum;

    @Deprecated
    public LongListListEntry(String fieldName, List<Long> value, boolean defaultExpanded, Supplier<Optional<String[]>> tooltipSupplier, Consumer<List<Long>> saveConsumer, Supplier<List<Long>> defaultValue, String resetButtonKey) {
        this(fieldName, value, defaultExpanded, tooltipSupplier, saveConsumer, defaultValue, resetButtonKey, false);
    }

    @Deprecated
    public LongListListEntry(String fieldName, List<Long> value, boolean defaultExpanded, Supplier<Optional<String[]>> tooltipSupplier, Consumer<List<Long>> saveConsumer, Supplier<List<Long>> defaultValue, String resetButtonKey, boolean requiresRestart) {
        this(fieldName, value, defaultExpanded, tooltipSupplier, saveConsumer, defaultValue, resetButtonKey, requiresRestart, true, true);
    }

    public LongListListEntry(String fieldName, List<Long> value, boolean defaultExpanded, Supplier<Optional<String[]>> tooltipSupplier, Consumer<List<Long>> saveConsumer, Supplier<List<Long>> defaultValue, String resetButtonKey, boolean requiresRestart, boolean deleteButtonEnabled, boolean insertInFront) {
        super(fieldName, value, defaultExpanded, tooltipSupplier, saveConsumer, defaultValue, resetButtonKey, requiresRestart, deleteButtonEnabled, insertInFront, LongListCell::new);
        this.minimum = Long.MIN_VALUE;
        this.maximum = Long.MAX_VALUE;
    }

    @Override
    public List<Long> getValue() {
        return cells.stream().map(LongListCell::getValue).collect(Collectors.toList());
    }

    public LongListListEntry setMaximum(long maximum) {
        this.maximum = maximum;
        return this;
    }

    public LongListListEntry setMinimum(long minimum) {
        this.minimum = minimum;
        return this;
    }

    @Override
    public LongListListEntry self() {
        return this;
    }

    @Override
    protected LongListCell getFromValue(Long value) {
        return new LongListCell(value, this);
    }

    public static class LongListCell extends AbstractListListEntry.AbstractListCell<Long, LongListCell, LongListListEntry> {

        private TextFieldWidget widget;

        public LongListCell(Long value, LongListListEntry listListEntry) {
            super(value, listListEntry);

            if (value == null)
                value = 0L;
            Long finalValue = value;

            this.setErrorSupplier(() -> Optional.ofNullable(listListEntry.cellErrorSupplier).flatMap(fn -> fn.apply(this.getValue())));
            widget = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, 100, 18, "");
            widget.setTextPredicate(s -> s.chars().allMatch(c -> Character.isDigit(c) || c == '-'));
            widget.setMaxLength(Integer.MAX_VALUE);
            widget.setHasBorder(false);
            widget.setText(value.toString());
            widget.setChangedListener(s -> {
                widget.setEditableColor(getPreferredTextColor());
                if (!Objects.equals(s, finalValue.toString())) {
                    this.listListEntry.getScreen().setEdited(true, this.listListEntry.isRequiresRestart());
                }
            });
        }

        public Long getValue() {
            try {
                return Long.valueOf(widget.getText());
            } catch (NumberFormatException e) {
                return 0L;
            }
        }

        @Override
        public Optional<String> getError() {
            try {
                long l = Long.parseLong(widget.getText());
                if (l > listListEntry.maximum)
                    return Optional.of(I18n.translate("text.cloth-config.error.too_large", listListEntry.maximum));
                else if (l < listListEntry.minimum)
                    return Optional.of(I18n.translate("text.cloth-config.error.too_small", listListEntry.minimum));
            } catch (NumberFormatException ex) {
                return Optional.of(I18n.translate("text.cloth-config.error.not_valid_number_long"));
            }
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
