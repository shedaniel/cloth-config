package me.shedaniel.clothconfig2.gui.entries;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * This class represents config entry lists that use one {@link TextFieldWidget} per entry.
 *
 * @param <T>    the configuration object type
 * @param <C>    the cell type
 * @param <SELF> the "curiously recurring template pattern" type parameter
 * @see AbstractListListEntry
 */
@ApiStatus.Internal
@Environment(EnvType.CLIENT)
public abstract class AbstractTextFieldListListEntry<T, C extends AbstractTextFieldListListEntry.AbstractTextFieldListCell<T, C, SELF>, SELF extends AbstractTextFieldListListEntry<T, C, SELF>> extends AbstractListListEntry<T, C, SELF> {
    
    public AbstractTextFieldListListEntry(String fieldName, List<T> value, boolean defaultExpanded, Supplier<Optional<String[]>> tooltipSupplier, Consumer<List<T>> saveConsumer, Supplier<List<T>> defaultValue, String resetButtonKey, boolean requiresRestart, boolean deleteButtonEnabled, boolean insertInFront, BiFunction<T, SELF, C> createNewCell) {
        super(fieldName, value, defaultExpanded, tooltipSupplier, saveConsumer, defaultValue, resetButtonKey, requiresRestart, deleteButtonEnabled, insertInFront, createNewCell);
    }
    
    /**
     * @param <T>           the configuration object type
     * @param <SELF>        the "curiously recurring template pattern" type parameter for this class
     * @param <OUTER_SELF>> the "curiously recurring template pattern" type parameter for the outer class
     * @see AbstractTextFieldListListEntry
     */
    @ApiStatus.Internal
    public static abstract class AbstractTextFieldListCell<T, SELF extends AbstractTextFieldListCell<T, SELF, OUTER_SELF>, OUTER_SELF extends AbstractTextFieldListListEntry<T, SELF, OUTER_SELF>> extends AbstractListListEntry.AbstractListCell<T, SELF, OUTER_SELF> {
        
        protected TextFieldWidget widget;
        
        public AbstractTextFieldListCell(@Nullable T value, OUTER_SELF listListEntry) {
            super(value, listListEntry);
            
            final T finalValue = substituteDefault(value);
            
            widget = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, 100, 18, "");
            widget.setTextPredicate(this::isValidText);
            widget.setMaxLength(Integer.MAX_VALUE);
            widget.setHasBorder(false);
            widget.setText(Objects.toString(finalValue));
            widget.setChangedListener(s -> {
                widget.setEditableColor(getPreferredTextColor());
                if (listListEntry.getScreen() != null && !Objects.equals(s, Objects.toString(finalValue))) {
                    this.listListEntry.getScreen().setEdited(true, this.listListEntry.isRequiresRestart());
                }
            });
        }
        
        /**
         * Allows subclasses to substitute default values.
         *
         * @param value the (possibly null) value to substitute
         * @return a substitution
         */
        @Nullable
        protected abstract T substituteDefault(@Nullable T value);
        
        /**
         * Tests if the text entered is valid. If not, the text is not changed.
         *
         * @param text the text to test
         * @return {@code true} if the text may be changed, {@code false} to prevent the change
         */
        protected abstract boolean isValidText(@NotNull String text);
        
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
