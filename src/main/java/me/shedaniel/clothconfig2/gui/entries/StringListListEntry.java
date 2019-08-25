package me.shedaniel.clothconfig2.gui.entries;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.TextFieldWidget;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class StringListListEntry extends BaseListEntry<String, StringListListEntry.StringListCell> {
    
    private Function<String, Optional<String>> cellErrorSupplier;
    
    @Deprecated
    public StringListListEntry(String fieldName, List<String> value, boolean defaultExpended, Supplier<Optional<String[]>> tooltipSupplier, Consumer<List<String>> saveConsumer, Supplier<List<String>> defaultValue, String resetButtonKey) {
        this(fieldName, value, defaultExpended, tooltipSupplier, saveConsumer, defaultValue, resetButtonKey, false);
    }
    
    @Deprecated
    public StringListListEntry(String fieldName, List<String> value, boolean defaultExpended, Supplier<Optional<String[]>> tooltipSupplier, Consumer<List<String>> saveConsumer, Supplier<List<String>> defaultValue, String resetButtonKey, boolean requiresRestart) {
        super(fieldName, tooltipSupplier, defaultValue, baseListEntry -> new StringListCell("", (StringListListEntry) baseListEntry), saveConsumer, resetButtonKey, requiresRestart);
        for(String str : value)
            cells.add(new StringListCell(str, this));
        this.widgets.addAll(cells);
        expended = defaultExpended;
    }
    
    public Function<String, Optional<String>> getCellErrorSupplier() {
        return cellErrorSupplier;
    }
    
    public void setCellErrorSupplier(Function<String, Optional<String>> cellErrorSupplier) {
        this.cellErrorSupplier = cellErrorSupplier;
    }
    
    @Override
    public List<String> getValue() {
        return cells.stream().map(cell -> cell.widget.getText()).collect(Collectors.toList());
    }
    
    @Override
    protected StringListCell getFromValue(String value) {
        return new StringListCell(value, this);
    }
    
    public static class StringListCell extends BaseListCell {
        
        private TextFieldWidget widget;
        private boolean isSelected;
        private StringListListEntry listListEntry;
        
        public StringListCell(String value, StringListListEntry listListEntry) {
            this.listListEntry = listListEntry;
            this.setErrorSupplier(() -> listListEntry.cellErrorSupplier == null ? Optional.empty() : listListEntry.getCellErrorSupplier().apply(widget.getText()));
            widget = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, 100, 18, "") {
                @Override
                public void render(int int_1, int int_2, float float_1) {
                    boolean f = isFocused();
                    setFocused(isSelected);
                    widget.setEditableColor(getPreferredTextColor());
                    super.render(int_1, int_2, float_1);
                    setFocused(f);
                }
            };
            widget.setMaxLength(999999);
            widget.setHasBorder(false);
            widget.setText(value);
            widget.setChangedListener(s -> {
                if (!value.contentEquals(s))
                    listListEntry.getScreen().setEdited(true, listListEntry.isRequiresRestart());
            });
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
            widget.setIsEditable(listListEntry.isEditable());
            this.isSelected = isSelected;
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
