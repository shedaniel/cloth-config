package me.shedaniel.clothconfig2.forge.gui.entries;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.mojang.blaze3d.matrix.MatrixStack;
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
@OnlyIn(Dist.CLIENT)
public abstract class AbstractTextFieldListListEntry<T, C extends AbstractTextFieldListListEntry.AbstractTextFieldListCell<T, C, SELF>, SELF extends AbstractTextFieldListListEntry<T, C, SELF>> extends AbstractListListEntry<T, C, SELF> {
    
    @ApiStatus.Internal
    public AbstractTextFieldListListEntry(ITextComponent fieldName, List<T> value, boolean defaultExpanded, Supplier<Optional<ITextComponent[]>> tooltipSupplier, Consumer<List<T>> saveConsumer, Supplier<List<T>> defaultValue, ITextComponent resetButtonKey, boolean requiresRestart, boolean deleteButtonEnabled, boolean insertInFront, BiFunction<T, SELF, C> createNewCell) {
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
        private boolean isSelected;
        
        public AbstractTextFieldListCell(@Nullable T value, OUTER_SELF listListEntry) {
            super(value, listListEntry);
            
            final T finalValue = substituteDefault(value);
            
            widget = new TextFieldWidget(Minecraft.getInstance().fontRenderer, 0, 0, 100, 18, NarratorChatListener.EMPTY) {
                @Override
                public void func_230430_a_(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                    setFocused2(isSelected);
                    super.func_230430_a_(matrices, mouseX, mouseY, delta);
                }
            };
            widget.setValidator(this::isValidText);
            widget.setMaxStringLength(Integer.MAX_VALUE);
            widget.setEnableBackgroundDrawing(false);
            widget.setText(Objects.toString(finalValue));
            widget.setResponder(s -> {
                widget.setTextColor(getPreferredTextColor());
            });
        }
        
        @Override
        public void updateSelected(boolean isSelected) {
            this.isSelected = isSelected;
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
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
            widget.func_230991_b_(entryWidth - 12);
            widget.field_230690_l_ = x;
            widget.field_230691_m_ = y + 1;
            widget.setEnabled(listListEntry.isEditable());
            widget.func_230430_a_(matrices, mouseX, mouseY, delta);
            if (isSelected && listListEntry.isEditable())
                func_238467_a_(matrices, x, y + 12, x + entryWidth - 12, y + 13, getConfigError().isPresent() ? 0xffff5555 : 0xffe0e0e0);
        }
        
        @Override
        public List<? extends IGuiEventListener> func_231039_at__() {
            return Collections.singletonList(widget);
        }
    }
    
}
