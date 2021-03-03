package me.shedaniel.clothconfig2.gui.entries;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * A config entry list consisting of bounded values that use one
 * {@link AbstractSliderListCell} per entry.
 *
 * Any bounded values that can be respresented as a {@code double} can be
 * listed using this entry list by implementing a specialized subclass of
 * {@link AbstractSliderListCell}.
 *
 * @param <T>    the configuration object type
 * @param <C>    the cell type
 * @param <SELF> the "curiously recurring template pattern" type parameter
 * @see AbstractListListEntry
 */
@Environment(EnvType.CLIENT)
public abstract class AbstractSliderListEntry<T, C extends AbstractSliderListEntry.AbstractSliderListCell<T, C, SELF>, SELF extends AbstractSliderListEntry<T, C, SELF>> extends AbstractListListEntry<T, C, SELF> {
    private static final ResourceLocation WIDGETS_TEX = new ResourceLocation("textures/gui/widgets.png");

    protected final T minimum, maximum, cellDefaultValue;
    protected Function<T, Component> textGetter;

    public AbstractSliderListEntry(Component fieldName, T minimum, T maximum, List<T> value, boolean defaultExpanded, Supplier<Optional<Component[]>> tooltipSupplier, Consumer<List<T>> saveConsumer, Supplier<List<T>> defaultValue, T cellDefaultValue, Component resetButtonKey, boolean requiresRestart, boolean deleteButtonEnabled, boolean insertInFront, BiFunction<T, SELF, C> createNewCell) {
        super(fieldName, value, defaultExpanded, tooltipSupplier, saveConsumer, defaultValue, resetButtonKey, requiresRestart, deleteButtonEnabled, insertInFront, createNewCell);

        this.minimum = requireNonNull(minimum);
        this.maximum = requireNonNull(maximum);
        this.cellDefaultValue = requireNonNull(cellDefaultValue);
    }

    public SELF setTextGetter(Function<T, Component> textGetter) {
        this.textGetter = textGetter;
        this.cells.forEach(c -> c.sliderWidget.updateMessage());
        return self();
    }

    /**
     * A config entry within a parent {@link AbstractSliderListEntry}
     * containing a single bounded value with an {@link AbstractSliderButton}
     * for user display and input.
     *
     * Any bounded value that can be respresented as a {@code double} can be
     * listed by subclassing this class and its parent {@link AbstractSliderListEntry},
     * implementing the {@link #getValueForSlider()} and
     * {@link #setValueFromSlider(double)} methods.
     *
     * @param <T>          the configuration object type
     * @param <SELF>       the "curiously recurring template pattern" type parameter for this class
     * @param <OUTER_SELF> the "curiously recurring template pattern" type parameter for the outer class
     * @see AbstractSliderListEntry
     */
    public abstract static class AbstractSliderListCell<T, SELF extends AbstractSliderListEntry.AbstractSliderListCell<T, SELF, OUTER_SELF>, OUTER_SELF extends AbstractSliderListEntry<T, SELF, OUTER_SELF>> extends AbstractListListEntry.AbstractListCell<T, SELF, OUTER_SELF> {
        protected final Slider sliderWidget;
        private boolean isSelected;

        public AbstractSliderListCell(T value, OUTER_SELF listListEntry) {
            super(value, listListEntry);
            this.sliderWidget = new Slider(0, 0, 152, 20, 0);
        }

        protected abstract double getValueForSlider();

        protected abstract void setValueFromSlider(double value);

        protected void syncValueToSlider() {
            sliderWidget.syncValueFromCell();
        }

        protected Component getValueForMessage() {
            if (listListEntry.textGetter == null) {
                return null;
            } else {
                return listListEntry.textGetter.apply(getValue());
            }
        }

        @Override
        public void onAdd() {
            syncValueToSlider();
            sliderWidget.updateMessage();
        }

        @Override
        public void updateSelected(boolean isSelected) {
            this.isSelected = isSelected;
        }

        @Override
        public Optional<Component> getError() {
            return Optional.empty();
        }

        @Override
        public int getCellHeight() {
            return 22;
        }

        @Override
        public void render(PoseStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isFocusedCell, float delta) {
            sliderWidget.x = x;
            sliderWidget.y = y;
            sliderWidget.setWidth(entryWidth - 12);
            sliderWidget.active = listListEntry.isEditable();
            sliderWidget.render(matrices, mouseX, mouseY, delta);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return Collections.singletonList(sliderWidget);
        }

        private class Slider extends AbstractSliderButton {
            protected Slider(int x, int y, int width, int height, double value) {
                super(x, y, width, height, NarratorChatListener.NO_TITLE, value);
            }

            protected void syncValueFromCell() {
                this.value = AbstractSliderListCell.this.getValueForSlider();
                updateMessage();
            }

            @Override
            public void updateMessage() {
                if (AbstractSliderListCell.this.listListEntry.textGetter != null) {
                    setMessage(AbstractSliderListCell.this.getValueForMessage());
                }
            }

            @Override
            protected void applyValue() {
                AbstractSliderListCell.this.setValueFromSlider(value);
            }

            @Override
            public boolean keyPressed(int keyCode, int int_2, int int_3) {
                if (!listListEntry.isEditable())
                    return false;
                return super.keyPressed(keyCode, int_2, int_3);
            }

            @Override
            public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY) {
                if (!listListEntry.isEditable())
                    return false;
                return super.mouseDragged(mouseX, mouseY, mouseButton, deltaX, deltaY);
            }

            @Override
            protected void renderBg(PoseStack matrices, Minecraft client, int mouseX, int mouseY) {
                /*
                 * If the width is greater than 200, then fill in the gap in the middle with more button bg
                 */
                int gap = width - 200;
                if (gap > 0) {
                    client.getTextureManager().bind(WIDGETS_TEX);

                    int offset = 100;
                    do {
                        blit(matrices, x + offset, y, 1, 46 + 0 * 20, Math.min(gap, 198), height);

                        offset += 198;
                        gap -= 198;
                    } while (gap > 0);
                }

                // Note: the non-error highlight color here is a bit darker
                // than the normal highlight of 0xffe0e0e0 to let the scrubber stand out
                if (isSelected && listListEntry.isEditable())
                    fill(matrices, x, y + 19, x + width, y + 20, getConfigError().isPresent() ? 0xffff5555 : 0xffa0a0a0);

                // Render the scrubber on top of anything we've drawn
                super.renderBg(matrices, client, mouseX, mouseY);
            }
        }
    }
}
