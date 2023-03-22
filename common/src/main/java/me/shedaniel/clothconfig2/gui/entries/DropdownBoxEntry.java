/*
 * This file is part of Cloth Config.
 * Copyright (C) 2020 - 2021 shedaniel
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package me.shedaniel.clothconfig2.gui.entries;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import me.shedaniel.clothconfig2.ClothConfigInitializer;
import me.shedaniel.clothconfig2.api.ScissorsHandler;
import me.shedaniel.clothconfig2.api.ScrollingContainer;
import me.shedaniel.math.Rectangle;
import me.shedaniel.math.impl.PointHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static me.shedaniel.clothconfig2.api.ScrollingContainer.handleScrollingPosition;

@SuppressWarnings("deprecation")
@Environment(EnvType.CLIENT)
public class DropdownBoxEntry<T> extends TooltipListEntry<T> {
    protected Button resetButton;
    protected SelectionElement<T> selectionElement;
    @NotNull private final Supplier<T> defaultValue;
    private boolean suggestionMode = true;
    
    @ApiStatus.Internal
    @Deprecated
    public DropdownBoxEntry(Component fieldName, @NotNull Component resetButtonKey, @Nullable Supplier<Optional<Component[]>> tooltipSupplier, boolean requiresRestart, @Nullable Supplier<T> defaultValue, @Nullable Consumer<T> saveConsumer, @Nullable Iterable<T> selections, @NotNull SelectionTopCellElement<T> topRenderer, @NotNull SelectionCellCreator<T> cellCreator) {
        super(fieldName, tooltipSupplier, requiresRestart);
        this.defaultValue = defaultValue;
        this.saveCallback = saveConsumer;
        this.resetButton = new Button(0, 0, Minecraft.getInstance().font.width(resetButtonKey) + 6, 20, resetButtonKey, widget -> {
            selectionElement.topRenderer.setValue(defaultValue.get());
        });
        this.selectionElement = new SelectionElement<>(this, new Rectangle(0, 0, 150, 20), new DefaultDropdownMenuElement<>(selections == null ? ImmutableList.of() : ImmutableList.copyOf(selections)), topRenderer, cellCreator);
    }
    
    @Override
    public void render(PoseStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
        Window window = Minecraft.getInstance().getWindow();
        this.resetButton.active = isEditable() && getDefaultValue().isPresent() && (!defaultValue.get().equals(getValue()) || getConfigError().isPresent());
        this.resetButton.y = y;
        this.selectionElement.active = isEditable();
        this.selectionElement.bounds.y = y;
        Component displayedFieldName = getDisplayedFieldName();
        if (Minecraft.getInstance().font.isBidirectional()) {
            Minecraft.getInstance().font.drawShadow(matrices, displayedFieldName.getVisualOrderText(), window.getGuiScaledWidth() - x - Minecraft.getInstance().font.width(displayedFieldName), y + 6, getPreferredTextColor());
            this.resetButton.x = x;
            this.selectionElement.bounds.x = x + resetButton.getWidth() + 1;
        } else {
            Minecraft.getInstance().font.drawShadow(matrices, displayedFieldName.getVisualOrderText(), x, y + 6, getPreferredTextColor());
            this.resetButton.x = x + entryWidth - resetButton.getWidth();
            this.selectionElement.bounds.x = x + entryWidth - 150 + 1;
        }
        this.selectionElement.bounds.width = 150 - resetButton.getWidth() - 4;
        resetButton.render(matrices, mouseX, mouseY, delta);
        selectionElement.render(matrices, mouseX, mouseY, delta);
    }
    
    @Override
    public boolean isEdited() {
        return this.selectionElement.topRenderer.isEdited();
    }
    
    public boolean isSuggestionMode() {
        return suggestionMode;
    }
    
    public void setSuggestionMode(boolean suggestionMode) {
        this.suggestionMode = suggestionMode;
    }
    
    @Override
    public void updateSelected(boolean isSelected) {
        selectionElement.topRenderer.isSelected = isSelected;
        selectionElement.menu.isSelected = isSelected;
    }
    
    @NotNull
    public ImmutableList<T> getSelections() {
        return selectionElement.menu.getSelections();
    }
    
    @Override
    public T getValue() {
        return selectionElement.getValue();
    }
    
    @Deprecated
    public SelectionElement<T> getSelectionElement() {
        return selectionElement;
    }
    
    @Override
    public Optional<T> getDefaultValue() {
        return defaultValue == null ? Optional.empty() : Optional.ofNullable(defaultValue.get());
    }
    
    @Override
    public List<? extends GuiEventListener> children() {
        return Lists.newArrayList(selectionElement, resetButton);
    }
    
    @Override
    public List<? extends NarratableEntry> narratables() {
        return Collections.singletonList(resetButton);
    }
    
    @Override
    public Optional<Component> getError() {
        return selectionElement.topRenderer.getError();
    }
    
    @Override
    public void lateRender(PoseStack matrices, int mouseX, int mouseY, float delta) {
        selectionElement.lateRender(matrices, mouseX, mouseY, delta);
    }
    
    @Override
    public int getMorePossibleHeight() {
        return selectionElement.getMorePossibleHeight();
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return selectionElement.mouseScrolled(mouseX, mouseY, amount);
    }
    
    public static class SelectionElement<R> extends AbstractContainerEventHandler implements Widget {
        protected Rectangle bounds;
        protected boolean active;
        protected SelectionTopCellElement<R> topRenderer;
        protected DropdownBoxEntry<R> entry;
        protected DropdownMenuElement<R> menu;
        protected boolean dontReFocus = false;
        
        public SelectionElement(DropdownBoxEntry<R> entry, Rectangle bounds, DropdownMenuElement<R> menu, SelectionTopCellElement<R> topRenderer, SelectionCellCreator<R> cellCreator) {
            this.bounds = bounds;
            this.entry = entry;
            this.menu = Objects.requireNonNull(menu);
            this.menu.entry = entry;
            this.menu.cellCreator = Objects.requireNonNull(cellCreator);
            this.menu.initCells();
            this.topRenderer = Objects.requireNonNull(topRenderer);
            this.topRenderer.entry = entry;
        }
        
        @Override
        public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
            fill(matrices, bounds.x, bounds.y, bounds.x + bounds.width, bounds.y + bounds.height, topRenderer.isSelected ? -1 : -6250336);
            fill(matrices, bounds.x + 1, bounds.y + 1, bounds.x + bounds.width - 1, bounds.y + bounds.height - 1, -16777216);
            topRenderer.render(matrices, mouseX, mouseY, bounds.x, bounds.y, bounds.width, bounds.height, delta);
            if (menu.isExpanded())
                menu.render(matrices, mouseX, mouseY, bounds, delta);
        }
        
        @Deprecated
        public SelectionTopCellElement<R> getTopRenderer() {
            return topRenderer;
        }
        
        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
            if (menu.isExpanded())
                return menu.mouseScrolled(mouseX, mouseY, amount);
            return false;
        }
        
        public void lateRender(PoseStack matrices, int mouseX, int mouseY, float delta) {
            if (menu.isExpanded())
                menu.lateRender(matrices, mouseX, mouseY, delta);
        }
        
        public int getMorePossibleHeight() {
            if (menu.isExpanded())
                return menu.getHeight();
            return -1;
        }
        
        public R getValue() {
            return topRenderer.getValue();
        }
        
        @Override
        public List<? extends GuiEventListener> children() {
            return Lists.newArrayList(topRenderer, menu);
        }
        
        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            dontReFocus = false;
            boolean b = super.mouseClicked(mouseX, mouseY, button);
            if (dontReFocus) {
                setFocused(null);
                dontReFocus = false;
            }
            return b;
        }
    }
    
    public static abstract class DropdownMenuElement<R> extends AbstractContainerEventHandler {
        @Deprecated @NotNull private SelectionCellCreator<R> cellCreator;
        @Deprecated @NotNull private DropdownBoxEntry<R> entry;
        private boolean isSelected;
        
        @NotNull
        public SelectionCellCreator<R> getCellCreator() {
            return cellCreator;
        }
        
        @NotNull
        public final DropdownBoxEntry<R> getEntry() {
            return entry;
        }
        
        @NotNull
        public abstract ImmutableList<R> getSelections();
        
        public abstract void initCells();
        
        public abstract void render(PoseStack matrices, int mouseX, int mouseY, Rectangle rectangle, float delta);
        
        public abstract void lateRender(PoseStack matrices, int mouseX, int mouseY, float delta);
        
        public abstract int getHeight();
        
        public final boolean isExpanded() {
            return isSelected && this.getEntry().getFocused() == this.getEntry().selectionElement;
        }
        
        public final boolean isSuggestionMode() {
            return entry.isSuggestionMode();
        }
        
        @Override
        public abstract List<SelectionCellElement<R>> children();
    }
    
    public static class DefaultDropdownMenuElement<R> extends DropdownMenuElement<R> {
        @NotNull protected ImmutableList<R> selections;
        @NotNull protected List<SelectionCellElement<R>> cells;
        @NotNull protected List<SelectionCellElement<R>> currentElements;
        protected Component lastSearchKeyword = Component.empty();
        protected Rectangle lastRectangle;
        protected boolean scrolling;
        protected double scroll, target;
        protected long start;
        protected long duration;
        
        public DefaultDropdownMenuElement(@NotNull ImmutableList<R> selections) {
            this.selections = selections;
            this.cells = Lists.newArrayList();
            this.currentElements = Lists.newArrayList();
        }
        
        public double getMaxScroll() {
            return getCellCreator().getCellHeight() * currentElements.size();
        }
        
        protected double getMaxScrollPosition() {
            return Math.max(0, this.getMaxScroll() - (getHeight()));
        }
        
        @Override
        @NotNull
        public ImmutableList<R> getSelections() {
            return selections;
        }
        
        @Override
        public void initCells() {
            for (R selection : getSelections()) {
                cells.add(getCellCreator().create(selection));
            }
            for (SelectionCellElement<R> cell : cells) {
                cell.entry = getEntry();
            }
            search();
        }
        
        public void search() {
            if (isSuggestionMode()) {
                currentElements.clear();
                String keyword = this.lastSearchKeyword.getString().toLowerCase();
                for (SelectionCellElement<R> cell : cells) {
                    Component key = cell.getSearchKey();
                    if (key == null || key.getString().toLowerCase().contains(keyword))
                        currentElements.add(cell);
                }
                if (!keyword.isEmpty()) {
                    Comparator<SelectionCellElement<?>> c = Comparator.comparingDouble(i -> i.getSearchKey() == null ? Double.MAX_VALUE : similarity(i.getSearchKey().getString(), keyword));
                    currentElements.sort(c.reversed());
                }
                scrollTo(0, false);
            } else {
                currentElements.clear();
                currentElements.addAll(cells);
            }
        }
        
        protected int editDistance(String s1, String s2) {
            s1 = s1.toLowerCase();
            s2 = s2.toLowerCase();
            
            int[] costs = new int[s2.length() + 1];
            for (int i = 0; i <= s1.length(); i++) {
                int lastValue = i;
                for (int j = 0; j <= s2.length(); j++) {
                    if (i == 0)
                        costs[j] = j;
                    else {
                        if (j > 0) {
                            int newValue = costs[j - 1];
                            if (s1.charAt(i - 1) != s2.charAt(j - 1))
                                newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                            costs[j - 1] = lastValue;
                            lastValue = newValue;
                        }
                    }
                }
                if (i > 0)
                    costs[s2.length()] = lastValue;
            }
            return costs[s2.length()];
        }
        
        protected double similarity(String s1, String s2) {
            String longer = s1, shorter = s2;
            if (s1.length() < s2.length()) { // longer should always have greater length
                longer = s2;
                shorter = s1;
            }
            int longerLength = longer.length();
            if (longerLength == 0) {
                return 1.0; /* both strings are zero length */
            }
            return (longerLength - editDistance(longer, shorter)) / (double) longerLength;
        }
        
        @Override
        public void render(PoseStack matrices, int mouseX, int mouseY, Rectangle rectangle, float delta) {
            if (!getEntry().selectionElement.topRenderer.getSearchTerm().equals(lastSearchKeyword)) {
                lastSearchKeyword = getEntry().selectionElement.topRenderer.getSearchTerm();
                search();
            }
            updatePosition(delta);
            lastRectangle = rectangle.clone();
            lastRectangle.translate(0, -1);
        }
        
        private void updatePosition(float delta) {
            double[] target = {this.target};
            scroll = handleScrollingPosition(target, scroll, getMaxScrollPosition(), delta, start, duration);
            this.target = target[0];
        }
        
        @Override
        public void lateRender(PoseStack matrices, int mouseX, int mouseY, float delta) {
            int last10Height = getHeight();
            int cWidth = getCellCreator().getCellWidth();
            fill(matrices, lastRectangle.x, lastRectangle.y + lastRectangle.height, lastRectangle.x + cWidth, lastRectangle.y + lastRectangle.height + last10Height + 1, isExpanded() ? -1 : -6250336);
            fill(matrices, lastRectangle.x + 1, lastRectangle.y + lastRectangle.height + 1, lastRectangle.x + cWidth - 1, lastRectangle.y + lastRectangle.height + last10Height, -16777216);
            matrices.pushPose();
            matrices.translate(0, 0, 300f);
            
            ScissorsHandler.INSTANCE.scissor(new Rectangle(lastRectangle.x, lastRectangle.y + lastRectangle.height + 1, cWidth - 6, last10Height - 1));
            double yy = lastRectangle.y + lastRectangle.height - scroll;
            for (SelectionCellElement<R> cell : currentElements) {
                if (yy + getCellCreator().getCellHeight() >= lastRectangle.y + lastRectangle.height && yy <= lastRectangle.y + lastRectangle.height + last10Height + 1)
                    cell.render(matrices, mouseX, mouseY, lastRectangle.x, (int) yy, getMaxScrollPosition() > 6 ? getCellCreator().getCellWidth() - 6 : getCellCreator().getCellWidth(), getCellCreator().getCellHeight(), delta);
                else
                    cell.dontRender(matrices, delta);
                yy += getCellCreator().getCellHeight();
            }
            ScissorsHandler.INSTANCE.removeLastScissor();
            
            if (currentElements.isEmpty()) {
                Font textRenderer = Minecraft.getInstance().font;
                Component text = Component.translatable("text.cloth-config.dropdown.value.unknown");
                textRenderer.drawShadow(matrices, text.getVisualOrderText(), lastRectangle.x + getCellCreator().getCellWidth() / 2f - textRenderer.width(text) / 2f, lastRectangle.y + lastRectangle.height + 3, -1);
            }
            
            if (getMaxScrollPosition() > 6) {
                RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
                RenderSystem.disableTexture();
                int scrollbarPositionMinX = lastRectangle.x + getCellCreator().getCellWidth() - 6;
                int scrollbarPositionMaxX = scrollbarPositionMinX + 6;
                int height = (int) (((last10Height) * (last10Height)) / this.getMaxScrollPosition());
                height = Mth.clamp(height, 32, last10Height - 8);
                height -= Math.min((scroll < 0 ? (int) -scroll : scroll > getMaxScrollPosition() ? (int) scroll - getMaxScrollPosition() : 0), height * .95);
                height = Math.max(10, height);
                int minY = (int) Math.min(Math.max((int) scroll * (last10Height - height) / getMaxScrollPosition() + (lastRectangle.y + lastRectangle.height + 1), (lastRectangle.y + lastRectangle.height + 1)), (lastRectangle.y + lastRectangle.height + 1 + last10Height) - height);
                
                int bottomc = new Rectangle(scrollbarPositionMinX, minY, scrollbarPositionMaxX - scrollbarPositionMinX, height).contains(PointHelper.ofMouse()) ? 168 : 128;
                int topc = new Rectangle(scrollbarPositionMinX, minY, scrollbarPositionMaxX - scrollbarPositionMinX, height).contains(PointHelper.ofMouse()) ? 222 : 172;
                
                Tesselator tesselator = Tesselator.getInstance();
                BufferBuilder buffer = tesselator.getBuilder();
                
                // Bottom
                buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
                buffer.vertex(scrollbarPositionMinX, minY + height, 0.0D).color(bottomc, bottomc, bottomc, 255).endVertex();
                buffer.vertex(scrollbarPositionMaxX, minY + height, 0.0D).color(bottomc, bottomc, bottomc, 255).endVertex();
                buffer.vertex(scrollbarPositionMaxX, minY, 0.0D).color(bottomc, bottomc, bottomc, 255).endVertex();
                buffer.vertex(scrollbarPositionMinX, minY, 0.0D).color(bottomc, bottomc, bottomc, 255).endVertex();
                
                // Top
                buffer.vertex(scrollbarPositionMinX, (minY + height - 1), 0.0D).color(topc, topc, topc, 255).endVertex();
                buffer.vertex((scrollbarPositionMaxX - 1), (minY + height - 1), 0.0D).color(topc, topc, topc, 255).endVertex();
                buffer.vertex((scrollbarPositionMaxX - 1), minY, 0.0D).color(topc, topc, topc, 255).endVertex();
                buffer.vertex(scrollbarPositionMinX, minY, 0.0D).color(topc, topc, topc, 255).endVertex();
                tesselator.end();
                RenderSystem.enableTexture();
            }
            matrices.popPose();
        }
        
        @Override
        public int getHeight() {
            return Math.max(Math.min(getCellCreator().getDropBoxMaxHeight(), (int) getMaxScroll()), 14);
        }
        
        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            return isExpanded() && mouseX >= lastRectangle.x && mouseX <= lastRectangle.x + getCellCreator().getCellWidth() && mouseY >= lastRectangle.y + lastRectangle.height && mouseY <= lastRectangle.y + lastRectangle.height + getHeight() + 1;
        }
        
        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            if (!isExpanded())
                return false;
            if (button == 0 && this.scrolling) {
                if (mouseY < (double) lastRectangle.y + lastRectangle.height) {
                    scrollTo(0, false);
                } else if (mouseY > (double) lastRectangle.y + lastRectangle.height + getHeight()) {
                    scrollTo(getMaxScrollPosition(), false);
                } else {
                    double double_5 = Math.max(1, this.getMaxScrollPosition());
                    int int_2 = getHeight();
                    int int_3 = Mth.clamp((int) ((float) (int_2 * int_2) / (float) this.getMaxScrollPosition()), 32, int_2 - 8);
                    double double_6 = Math.max(1.0D, double_5 / (double) (int_2 - int_3));
                    this.offset(deltaY * double_6, false);
                }
                target = Mth.clamp(target, 0, getMaxScrollPosition());
                return true;
            }
            return false;
        }
        
        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
            if (isMouseOver(mouseX, mouseY)) {
                offset(ClothConfigInitializer.getScrollStep() * -amount, true);
                return true;
            }
            return false;
        }
        
        protected void updateScrollingState(double double_1, double double_2, int int_1) {
            this.scrolling = isExpanded() && lastRectangle != null && int_1 == 0 && double_1 >= (double) lastRectangle.x + getCellCreator().getCellWidth() - 6 && double_1 < (double) (lastRectangle.x + getCellCreator().getCellWidth());
        }
        
        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!isExpanded())
                return false;
            updateScrollingState(mouseX, mouseY, button);
            return super.mouseClicked(mouseX, mouseY, button) || scrolling;
        }
        
        public void offset(double value, boolean animated) {
            scrollTo(target + value, animated);
        }
        
        public void scrollTo(double value, boolean animated) {
            scrollTo(value, animated, ClothConfigInitializer.getScrollDuration());
        }
        
        public void scrollTo(double value, boolean animated, long duration) {
            target = ScrollingContainer.clampExtension(value, getMaxScrollPosition());
            
            if (animated) {
                start = System.currentTimeMillis();
                this.duration = duration;
            } else
                scroll = target;
        }
        
        @Override
        public List<SelectionCellElement<R>> children() {
            return currentElements;
        }
    }
    
    public static abstract class SelectionCellCreator<R> {
        public abstract SelectionCellElement<R> create(R selection);
        
        public abstract int getCellHeight();
        
        public abstract int getDropBoxMaxHeight();
        
        public int getCellWidth() {
            return 132;
        }
    }
    
    public static class DefaultSelectionCellCreator<R> extends SelectionCellCreator<R> {
        protected Function<R, Component> toTextFunction;
        
        public DefaultSelectionCellCreator(Function<R, Component> toTextFunction) {
            this.toTextFunction = toTextFunction;
        }
        
        public DefaultSelectionCellCreator() {
            this(r -> Component.literal(r.toString()));
        }
        
        @Override
        public SelectionCellElement<R> create(R selection) {
            return new DefaultSelectionCellElement<>(selection, toTextFunction);
        }
        
        @Override
        public int getCellHeight() {
            return 14;
        }
        
        @Override
        public int getDropBoxMaxHeight() {
            return getCellHeight() * 7;
        }
    }
    
    public static abstract class SelectionCellElement<R> extends AbstractContainerEventHandler {
        @SuppressWarnings("NotNullFieldNotInitialized") @Deprecated @NotNull private DropdownBoxEntry<R> entry;
        
        @NotNull
        public final DropdownBoxEntry<R> getEntry() {
            return entry;
        }
        
        public abstract void render(PoseStack matrices, int mouseX, int mouseY, int x, int y, int width, int height, float delta);
        
        public abstract void dontRender(PoseStack matrices, float delta);
        
        @Nullable
        public abstract Component getSearchKey();
        
        @Nullable
        public abstract R getSelection();
    }
    
    public static class DefaultSelectionCellElement<R> extends SelectionCellElement<R> {
        protected R r;
        protected int x;
        protected int y;
        protected int width;
        protected int height;
        protected boolean rendering;
        protected Function<R, Component> toTextFunction;
        
        public DefaultSelectionCellElement(R r, Function<R, Component> toTextFunction) {
            this.r = r;
            this.toTextFunction = toTextFunction;
        }
        
        @Override
        public void render(PoseStack matrices, int mouseX, int mouseY, int x, int y, int width, int height, float delta) {
            rendering = true;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            boolean b = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
            if (b)
                fill(matrices, x + 1, y + 1, x + width - 1, y + height - 1, -15132391);
            Minecraft.getInstance().font.drawShadow(matrices, toTextFunction.apply(r).getVisualOrderText(), x + 6, y + 3, b ? 16777215 : 8947848);
        }
        
        @Override
        public void dontRender(PoseStack matrices, float delta) {
            rendering = false;
        }
        
        @Nullable
        @Override
        public Component getSearchKey() {
            return toTextFunction.apply(r);
        }
        
        @Nullable
        @Override
        public R getSelection() {
            return r;
        }
        
        @Override
        public List<? extends GuiEventListener> children() {
            return Collections.emptyList();
        }
        
        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            boolean b = rendering && mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
            if (b) {
                getEntry().selectionElement.topRenderer.setValue(r);
                getEntry().selectionElement.setFocused(null);
                getEntry().selectionElement.dontReFocus = true;
                return true;
            }
            return false;
        }
    }
    
    public static abstract class SelectionTopCellElement<R> extends AbstractContainerEventHandler {
        @Deprecated private DropdownBoxEntry<R> entry;
        protected boolean isSelected = false;
        
        public abstract R getValue();
        
        public abstract void setValue(R value);
        
        public abstract Component getSearchTerm();
        
        public boolean isEdited() {
            return getConfigError().isPresent();
        }
        
        public abstract Optional<Component> getError();
        
        public final Optional<Component> getConfigError() {
            return entry.getConfigError();
        }
        
        public DropdownBoxEntry<R> getParent() {
            return entry;
        }
        
        public final boolean hasConfigError() {
            return getConfigError().isPresent();
        }
        
        public final boolean hasError() {
            return getError().isPresent();
        }
        
        public final int getPreferredTextColor() {
            return getConfigError().isPresent() ? 16733525 : 16777215;
        }
        
        public final boolean isSuggestionMode() {
            return getParent().isSuggestionMode();
        }
        
        public void selectFirstRecommendation() {
            List<SelectionCellElement<R>> children = getParent().selectionElement.menu.children();
            for (SelectionCellElement<R> child : children) {
                if (child.getSelection() != null) {
                    setValue(child.getSelection());
                    getParent().selectionElement.setFocused(null);
                    break;
                }
            }
        }
        
        public abstract void render(PoseStack matrices, int mouseX, int mouseY, int x, int y, int width, int height, float delta);
    }
    
    public static class DefaultSelectionTopCellElement<R> extends SelectionTopCellElement<R> {
        protected EditBox textFieldWidget;
        protected Function<String, R> toObjectFunction;
        protected Function<R, Component> toTextFunction;
        protected final R original;
        protected R value;
        
        public DefaultSelectionTopCellElement(R value, Function<String, R> toObjectFunction, Function<R, Component> toTextFunction) {
            this.original = Objects.requireNonNull(value);
            this.value = Objects.requireNonNull(value);
            this.toObjectFunction = Objects.requireNonNull(toObjectFunction);
            this.toTextFunction = Objects.requireNonNull(toTextFunction);
            textFieldWidget = new EditBox(Minecraft.getInstance().font, 0, 0, 148, 18, Component.empty()) {
                @Override
                public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
                    setFocused(isSuggestionMode() && isSelected && DefaultSelectionTopCellElement.this.getParent().getFocused() == DefaultSelectionTopCellElement.this.getParent().selectionElement && DefaultSelectionTopCellElement.this.getParent().selectionElement.getFocused() == DefaultSelectionTopCellElement.this && DefaultSelectionTopCellElement.this.getFocused() == this);
                    super.render(matrices, mouseX, mouseY, delta);
                }
                
                @Override
                public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
                    if (keyCode == 257 || keyCode == 335) {
                        DefaultSelectionTopCellElement.this.selectFirstRecommendation();
                        return true;
                    }
                    return isSuggestionMode() && super.keyPressed(keyCode, scanCode, modifiers);
                }
                
                @Override
                public boolean charTyped(char chr, int keyCode) {
                    return isSuggestionMode() && super.charTyped(chr, keyCode);
                }
            };
            textFieldWidget.setBordered(false);
            textFieldWidget.setMaxLength(999999);
            textFieldWidget.setValue(toTextFunction.apply(value).getString());
        }
        
        @Override
        public boolean isEdited() {
            return super.isEdited() || !getValue().equals(original);
        }
        
        @Override
        public void render(PoseStack matrices, int mouseX, int mouseY, int x, int y, int width, int height, float delta) {
            textFieldWidget.x = x + 4;
            textFieldWidget.y = y + 6;
            textFieldWidget.setWidth(width - 8);
            textFieldWidget.setEditable(getParent().isEditable());
            textFieldWidget.setTextColor(getPreferredTextColor());
            textFieldWidget.render(matrices, mouseX, mouseY, delta);
        }
        
        @Override
        public R getValue() {
            if (hasError())
                return value;
            return toObjectFunction.apply(textFieldWidget.getValue());
        }
        
        @Override
        public void setValue(R value) {
            textFieldWidget.setValue(toTextFunction.apply(value).getString());
            textFieldWidget.moveCursorTo(0);
        }
        
        @Override
        public Component getSearchTerm() {
            return Component.literal(textFieldWidget.getValue());
        }
        
        @Override
        public Optional<Component> getError() {
            if (toObjectFunction.apply(textFieldWidget.getValue()) != null)
                return Optional.empty();
            return Optional.of(Component.literal("Invalid Value!"));
        }
        
        @Override
        public List<? extends GuiEventListener> children() {
            return Collections.singletonList(textFieldWidget);
        }
    }
}
