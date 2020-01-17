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
public class FloatListListEntry extends AbstractListListEntry<Float, FloatListListEntry.FloatListCell, FloatListListEntry> {

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

    @Override
    public List<Float> getValue() {
        return cells.stream().map(FloatListCell::getValue).collect(Collectors.toList());
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

    @Override
    protected FloatListCell getFromValue(Float value) {
        return new FloatListCell(value, this);
    }

    public static class FloatListCell extends AbstractListListEntry.AbstractListCell<Float, FloatListCell, FloatListListEntry> {

        private TextFieldWidget widget;

        public FloatListCell(Float value, FloatListListEntry listListEntry) {
            super(value, listListEntry);

            if (value == null)
                value = 0f;
            Float finalValue = value;

            this.setErrorSupplier(() -> Optional.ofNullable(listListEntry.cellErrorSupplier).flatMap(fn -> fn.apply(this.getValue())));
            widget = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, 100, 18, "");
            widget.setTextPredicate(s -> s.chars().allMatch(c -> Character.isDigit(c) || c == '-' || c == '.'));
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
