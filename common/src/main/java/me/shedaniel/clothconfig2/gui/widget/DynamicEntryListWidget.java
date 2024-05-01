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

package me.shedaniel.clothconfig2.gui.widget;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import me.shedaniel.clothconfig2.api.*;
import me.shedaniel.math.Rectangle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.*;
import java.util.function.Predicate;

@Environment(EnvType.CLIENT)
public abstract class DynamicEntryListWidget<E extends DynamicEntryListWidget.Entry<E>> extends AbstractContainerEventHandler implements Renderable, NarratableEntry {
    public static final ResourceLocation VERTICAL_HEADER_SEPARATOR = new ResourceLocation("cloth-config2:textures/gui/vertical_header_separator.png");
    public static final ResourceLocation VERTICAL_FOOTER_SEPARATOR = new ResourceLocation("cloth-config2:textures/gui/vertical_footer_separator.png");
    protected static final int DRAG_OUTSIDE = -2;
    protected final Minecraft client;
    private final List<E> entries = new Entries();
    private float totalTicks = 1.0f;
    private List<E> visibleEntries = Collections.emptyList();
    public int width;
    public int height;
    public int top;
    public int bottom;
    public int right;
    public int left;
    protected boolean verticallyCenter = true;
    protected int yDrag = -2;
    protected boolean selectionVisible = true;
    protected boolean renderSelection;
    protected int headerHeight;
    protected double scroll;
    protected boolean scrolling;
    @Nullable
    protected E hoveredItem;
    protected E selectedItem;
    protected ResourceLocation backgroundLocation;
    
    public DynamicEntryListWidget(Minecraft client, int width, int height, int top, int bottom, ResourceLocation backgroundLocation) {
        this.client = client;
        this.width = width;
        this.height = height;
        this.top = top;
        this.bottom = bottom;
        this.left = 0;
        this.right = width;
        this.backgroundLocation = backgroundLocation;
    }
    
    /**
     * Get all visible children. I.e. hidden config entries are filtered out.
     * 
     * <p> Note: this isn't thread safe, since the visible children list is
     * updated when calling {@link #tickList()}.
     * 
     * @return an unmodifiable {@link List} of visible entries
     */
    @ApiStatus.Experimental
    public List<E> visibleChildren() {
        return this.visibleEntries;
    }
    
    private void updateVisibleChildren() {
        this.visibleEntries = this.children().stream()
                .filter(HideableWidget::isDisplayed)
                .toList();
    }
    
    public void setRenderSelection(boolean boolean_1) {
        this.selectionVisible = boolean_1;
    }
    
    protected void setRenderHeader(boolean boolean_1, int headerHeight) {
        this.renderSelection = boolean_1;
        this.headerHeight = headerHeight;
        if (!boolean_1)
            this.headerHeight = 0;
    }
    
    public NarrationPriority narrationPriority() {
        if (this.isFocused()) {
            return NarrationPriority.FOCUSED;
        } else {
            return this.hoveredItem != null ? NarrationPriority.HOVERED : NarrationPriority.NONE;
        }
    }
    
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
        E entry = this.hoveredItem;
        if (entry != null) {
            entry.updateNarration(narrationElementOutput.nest());
            this.narrateListElementPosition(narrationElementOutput, entry);
        } else {
            E entry2 = this.getFocused();
            if (entry2 != null) {
                entry2.updateNarration(narrationElementOutput.nest());
                this.narrateListElementPosition(narrationElementOutput, entry2);
            }
        }
        
        narrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("narration.component_list.usage"));
    }
    
    protected void narrateListElementPosition(NarrationElementOutput narrationElementOutput, E entry) {
        List<E> list = this.visibleChildren();
        if (list.size() > 1) {
            int i = list.indexOf(entry);
            if (i != -1) {
                narrationElementOutput.add(NarratedElementType.POSITION, Component.translatable("narrator.position.list", i + 1, list.size()));
            }
        }
    }
    
    public int getItemWidth() {
        return 220;
    }
    
    public E getSelectedItem() {
        return this.selectedItem;
    }
    
    public void selectItem(E item) {
        this.selectedItem = item;
    }
    
    public E getFocused() {
        return (E) super.getFocused();
    }
    
    public List<E> children() {
        return this.entries;
    }
    
    protected final void clearItems() {
        this.children().clear();
    }
    
    protected E getItem(int index) {
        return this.visibleChildren().get(index);
    }
    
    protected int addItem(E item) {
        this.children().add(item);
        return this.children().size() - 1;
    }
    
    protected int getItemCount() {
        return this.visibleChildren().size();
    }
    
    protected boolean isSelected(int index) {
        return Objects.equals(this.getSelectedItem(), this.getItem(index));
    }
    
    protected final E getItemAtPosition(double mouseX, double mouseY) {
        int listMiddleX = this.left + this.width / 2;
        int minX = listMiddleX - this.getItemWidth() / 2;
        int maxX = listMiddleX + this.getItemWidth() / 2;
        int currentY = Mth.floor(mouseY - (double) this.top) - this.headerHeight + (int) this.getScroll() - 4;
        
        // Check if we can return early
        if ((double) this.getScrollbarPosition() <= mouseX) {
            return null;
        } else if (mouseX < minX) {
            return null;
        } else if (mouseX > maxX) {
            return null;
        } else if (currentY < 0) {
            return null;
        }
        
        // Otherwise look for the selected item
        E itemAtPosition = null;
        int itemY = 0;
        for (E item : visibleChildren()) {
            itemY += item.getItemHeight();
            if (itemY > currentY) {
                itemAtPosition = item;
                break;
            }
        }
        return itemAtPosition;
    }
    
    public void updateSize(int width, int height, int top, int bottom) {
        this.width = width;
        this.height = height;
        this.top = top;
        this.bottom = bottom;
        this.left = 0;
        this.right = width;
    }
    
    public void setLeftPos(int left) {
        this.left = left;
        this.right = left + this.width;
    }
    
    protected int getMaxScrollPosition() {
        List<Integer> list = new ArrayList<>();
        int i = headerHeight;
        for (E entry : visibleChildren()) {
            i += entry.getItemHeight();
            if (entry.getMorePossibleHeight() >= 0) {
                list.add(i + entry.getMorePossibleHeight());
            }
        }
        list.add(i);
        return list.stream().max(Integer::compare).orElse(0);
    }
    
    protected void clickedHeader(int int_1, int int_2) {
    }
    
    public void tickList() {
        this.updateVisibleChildren();
        for (E child : this.children()) {
           child.tick();
        }
    }
    
    protected void renderHeader(GuiGraphics graphics, int rowLeft, int startY, Tesselator tessellator) {
    }
    
    protected void drawBackground() {
    }
    
    protected void renderDecorations(GuiGraphics graphics, int mouseX, int mouseY) {
    }
    
    @Deprecated
    protected void renderBackBackground(GuiGraphics graphics, BufferBuilder buffer, Tesselator tessellator) {
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, backgroundLocation);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        Matrix4f matrix = graphics.pose().last().pose();
        float float_2 = 32.0F;
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        buffer.vertex(matrix, this.left, this.bottom, 0.0F).uv(this.left / 32.0F, ((this.bottom + (int) this.getScroll()) / 32.0F)).color(32, 32, 32, 255).endVertex();
        buffer.vertex(matrix, this.right, this.bottom, 0.0F).uv(this.right / 32.0F, ((this.bottom + (int) this.getScroll()) / 32.0F)).color(32, 32, 32, 255).endVertex();
        buffer.vertex(matrix, this.right, this.top, 0.0F).uv(this.right / 32.0F, ((this.top + (int) this.getScroll()) / 32.0F)).color(32, 32, 32, 255).endVertex();
        buffer.vertex(matrix, this.left, this.top, 0.0F).uv(this.left / 32.0F, ((this.top + (int) this.getScroll()) / 32.0F)).color(32, 32, 32, 255).endVertex();
        tessellator.end();
    }
    
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        this.totalTicks += delta;
        if (this.totalTicks >= 1.0f) {
            this.totalTicks = this.totalTicks % 1.0f;
            this.tickList();
        }
        
        this.drawBackground();
        int scrollbarPosition = this.getScrollbarPosition();
        int int_4 = scrollbarPosition + 6;
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        renderBackBackground(graphics, buffer, tesselator);
        int rowLeft = this.getRowLeft();
        int startY = this.top + 4 - (int) this.getScroll();
        if (this.renderSelection)
            this.renderHeader(graphics, rowLeft, startY, tesselator);
        ScissorsHandler.INSTANCE.scissor(new Rectangle(left, top, width, bottom - top));
        this.renderList(graphics, rowLeft, startY, mouseX, mouseY, delta);
        ScissorsHandler.INSTANCE.removeLastScissor();
        RenderSystem.disableDepthTest();
        this.renderHoleBackground(graphics, 0, this.top, 255, 255);
        this.renderHoleBackground(graphics, this.bottom, this.height, 255, 255);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        graphics.blit(CreateWorldScreen.HEADER_SEPARATOR, this.left, this.top - 2, 0.0F, 0.0F, this.width, 2, 32, 2);
        graphics.blit(CreateWorldScreen.FOOTER_SEPARATOR, this.left, this.bottom, 0.0F, 0.0F, this.width, 2, 32, 2);
        RenderSystem.disableBlend();
        
        int maxScroll = this.getMaxScroll();
        renderScrollBar(graphics, tesselator, buffer, maxScroll, scrollbarPosition, int_4);
        
        this.renderDecorations(graphics, mouseX, mouseY);
        RenderSystem.disableBlend();
    }
    
    protected void renderScrollBar(GuiGraphics graphics, Tesselator tessellator, BufferBuilder buffer, int maxScroll, int scrollbarPositionMinX, int scrollbarPositionMaxX) {
        if (maxScroll > 0) {
            int int_9 = ((this.bottom - this.top) * (this.bottom - this.top)) / this.getMaxScrollPosition();
            int_9 = Mth.clamp(int_9, 32, this.bottom - this.top - 8);
            int int_10 = (int) this.getScroll() * (this.bottom - this.top - int_9) / maxScroll + this.top;
            if (int_10 < this.top) {
                int_10 = this.top;
            }
            
            graphics.fill(scrollbarPositionMinX, this.top, scrollbarPositionMaxX, this.bottom, 0xff000000);
            graphics.fill(scrollbarPositionMinX, int_10, scrollbarPositionMaxX, int_10 + int_9, 0xff808080);
            graphics.fill(scrollbarPositionMinX, int_10, scrollbarPositionMaxX - 1, int_10 + int_9 - 1, 0xffc0c0c0);
        }
    }
    
    protected void centerScrollOn(E item) {
        List<E> children = this.visibleChildren();
        double halfway = (this.bottom - this.top) / -2d;
        int itemIndex = children.indexOf(item);
        int i = 0;
        for (E elm : children) {
            if (i++ >= itemIndex) {
                break;
            }
            halfway += elm.getItemHeight();
        }
        this.capYPosition(halfway);
    }
    
    protected void ensureVisible(E item) {
        ensureVisible((int) this.getScroll() - top - headerHeight - 4 + this.getRowTop(this.visibleChildren().indexOf(item)), item.getItemHeight());
    }
    
    public void ensureVisible(int rowTop, int itemHeight) {
        int rowBottom = rowTop + itemHeight;
        double scroll = getScroll();
        // ensure visible with scroll(..)
        if (rowTop < scroll) {
            this.capYPosition(rowTop);
        } else if (rowBottom > scroll + this.height) {
            this.capYPosition(rowBottom);
        }
    }
    
    protected void scroll(int int_1) {
        this.capYPosition(this.getScroll() + (double) int_1);
        this.yDrag = -2;
    }
    
    public double getScroll() {
        return this.scroll;
    }
    
    public void capYPosition(double double_1) {
        this.scroll = Mth.clamp(double_1, 0.0F, this.getMaxScroll());
    }
    
    protected int getMaxScroll() {
        return Math.max(0, this.getMaxScrollPosition() - (this.bottom - this.top - 4));
    }
    
    public int getScrollBottom() {
        return (int) this.getScroll() - this.height - this.headerHeight;
    }
    
    protected void updateScrollingState(double double_1, double double_2, int int_1) {
        this.scrolling = int_1 == 0 && double_1 >= (double) this.getScrollbarPosition() && double_1 < (double) (this.getScrollbarPosition() + 6);
    }
    
    protected int getScrollbarPosition() {
        return this.width / 2 + 124;
    }
    
    public boolean mouseClicked(double double_1, double double_2, int int_1) {
        this.updateScrollingState(double_1, double_2, int_1);
        if (!this.isMouseOver(double_1, double_2)) {
            return false;
        } else {
            E item = this.getItemAtPosition(double_1, double_2);
            if (item != null) {
                if (item.mouseClicked(double_1, double_2, int_1)) {
                    this.setFocused(item);
                    this.setDragging(true);
                    return true;
                }
            } else if (int_1 == 0) {
                this.clickedHeader((int) (double_1 - (double) (this.left + this.width / 2 - this.getItemWidth() / 2)), (int) (double_2 - (double) this.top) + (int) this.getScroll() - 4);
                return true;
            }
            
            return this.scrolling;
        }
    }
    
    public ScreenRectangle getRectangle() {
        return new ScreenRectangle(this.left, this.top, this.right - this.left, this.bottom - this.top);
    }
    
    public void setFocused(@Nullable GuiEventListener guiEventListener) {
        super.setFocused(guiEventListener);
        int i = this.entries.indexOf(guiEventListener);
        if (i >= 0) {
            E entry = this.entries.get(i);
            this.selectItem(entry);
            if (this.client.getLastInputType().isKeyboard()) {
                this.ensureVisible(entry);
            }
        }
    }
    
    @Nullable
    protected E nextEntry(ScreenDirection screenDirection) {
        return this.nextEntry(screenDirection, (entry) -> {
            return true;
        });
    }
    
    @Nullable
    protected E nextEntry(ScreenDirection screenDirection, Predicate<E> predicate) {
        return this.nextEntry(screenDirection, predicate, this.getSelectedItem());
    }
    
    @Nullable
    protected E nextEntry(ScreenDirection screenDirection, Predicate<E> predicate, @Nullable E entry) {
        int var10000 = switch (screenDirection) {
            case RIGHT, LEFT -> 0;
            case UP -> -1;
            case DOWN -> 1;
        };
    
        if (!this.children().isEmpty() && var10000 != 0) {
            int j;
            if (entry == null) {
                j = var10000 > 0 ? 0 : this.children().size() - 1;
            } else {
                j = this.children().indexOf(entry) + var10000;
            }
            
            for(int k = j; k >= 0 && k < this.entries.size(); k += var10000) {
                E entry2 = this.entries.get(k);
                if (predicate.test(entry2)) {
                    return entry2;
                }
            }
        }
        
        return null;
    }
    
    public boolean mouseReleased(double double_1, double double_2, int int_1) {
        if (this.getFocused() != null) {
            this.getFocused().mouseReleased(double_1, double_2, int_1);
        }
        
        return false;
    }
    
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            return true;
        } else if (button == 0 && this.scrolling) {
            if (mouseY < (double) this.top) {
                this.capYPosition(0.0F);
            } else if (mouseY > (double) this.bottom) {
                this.capYPosition(this.getMaxScroll());
            } else {
                double double_5 = Math.max(1, this.getMaxScroll());
                int int_2 = this.bottom - this.top;
                int int_3 = Mth.clamp((int) ((float) (int_2 * int_2) / (float) this.getMaxScrollPosition()), 32, int_2 - 8);
                double double_6 = Math.max(1.0D, double_5 / (double) (int_2 - int_3));
                this.capYPosition(this.getScroll() + deltaY * double_6);
            }
            
            return true;
        } else {
            return false;
        }
    }
    
    public boolean mouseScrolled(double double_1, double double_2, double amountX, double amountY) {
        for (E entry : visibleChildren()) {
            if (entry.mouseScrolled(double_1, double_2, amountX, amountY)) {
                return true;
            }
        }
        this.capYPosition(this.getScroll() - amountY * (double) (getMaxScroll() / getItemCount()) / 2.0D);
        return amountY != 0;
    }
    
    public boolean keyPressed(int int_1, int int_2, int int_3) {
        if (super.keyPressed(int_1, int_2, int_3)) {
            return true;
        } else if (int_1 == 264) {
            this.moveSelection(1);
            return true;
        } else if (int_1 == 265) {
            this.moveSelection(-1);
            return true;
        } else {
            return false;
        }
    }
    
    protected void moveSelection(int shift) {
        List<E> children = this.visibleChildren();
        if (children.isEmpty()) {
            return;
        }
        int selected = children.indexOf(this.getSelectedItem());
        int index = Mth.clamp(selected + shift, 0, this.getItemCount() - 1);
        E item = this.getItem(index);
        this.selectItem(item);
        this.ensureVisible(item);
    }
    
    public boolean isMouseOver(double double_1, double double_2) {
        return double_2 >= (double) this.top && double_2 <= (double) this.bottom && double_1 >= (double) this.left && double_1 <= (double) this.right;
    }
    
    protected void renderList(GuiGraphics graphics, int startX, int startY, int mouseX, int mouseY, float delta) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        
        hoveredItem = this.isMouseOver(mouseX, mouseY) ? this.getItemAtPosition(mouseX, mouseY) : null;
        
        int heights = 0; // itemHeight accumulator
        int renderIndex = 0; // index is passed to render methods
        for (E item : visibleChildren()) {
            int itemY = startY + headerHeight + heights;
            int itemHeight = item.getItemHeight() - 4;
            int itemWidth = this.getItemWidth();
            boolean itemHovered = Objects.equals(this.hoveredItem, item);
            
            if (this.selectionVisible && Objects.equals(this.selectedItem, item)) {
                int itemMinX = this.left + (this.width - itemWidth) / 2;
                int itemMaxX = this.left + (this.width + itemWidth) / 2;
                graphics.fill(itemMinX, itemY - 2, itemMaxX, itemY + itemHeight + 2, this.isFocused() ? 0xffffffff : 0xff808080);
                graphics.fill(itemMinX + 1, itemY - 1, itemMaxX - 1, itemY + itemHeight + 1, 0xff000000);
            }
            
            // Finally, call render
            int y = this.getRowTop(renderIndex);
            int x = this.getRowLeft();
            renderItem(graphics, item, renderIndex, y, x, itemWidth, itemHeight, mouseX, mouseY, itemHovered, delta);
            
            // Update counter and accumulator
            heights += item.getItemHeight();
            renderIndex++;
        }
    }
    
    protected void renderItem(GuiGraphics graphics, E item, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
        item.render(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isSelected, delta);
    }
    
    protected int getRowLeft() {
        return this.left + this.width / 2 - this.getItemWidth() / 2 + 2;
    }
    
    public int getRowTop(int index) {
        int top = this.top + 4 - (int) this.getScroll() + headerHeight;
        int i = 0;
        for (E item : visibleChildren()) {
            if (index <= i++) {
                break;
            }
            top += item.getItemHeight(); 
        }
        return top;
    }
    
    @Override
    public boolean isFocused() {
        return false;
    }
    
    protected void renderHoleBackground(GuiGraphics graphics, int y1, int y2, int alpha1, int alpha2) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        Matrix4f matrix = graphics.pose().last().pose();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, backgroundLocation);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        buffer.vertex(matrix, this.left, y2, 0.0F).uv(0, ((float) y2 / 32.0F)).color(64, 64, 64, alpha2).endVertex();
        buffer.vertex(matrix, this.left + this.width, y2, 0.0F).uv(((float) this.width / 32.0F), ((float) y2 / 32.0F)).color(64, 64, 64, alpha2).endVertex();
        buffer.vertex(matrix, this.left + this.width, y1, 0.0F).uv(((float) this.width / 32.0F), ((float) y1 / 32.0F)).color(64, 64, 64, alpha1).endVertex();
        buffer.vertex(matrix, this.left, y1, 0.0F).uv(0, ((float) y1 / 32.0F)).color(64, 64, 64, alpha1).endVertex();
        tesselator.end();
    }
    
    protected E remove(int index) {
        E item = this.getItem(index);
        return this.removeEntry(item) ? item : null;
    }
    
    protected boolean removeEntry(E entry) {
        boolean removed = this.children().remove(entry);
        if (removed && entry == this.getSelectedItem()) {
            this.selectItem(null);
        }
        
        return removed;
    }
    
    public static final class SmoothScrollingSettings {
        public static final double CLAMP_EXTENSION = 200;
        
        private SmoothScrollingSettings() {}
    }
    
    @Environment(EnvType.CLIENT)
    public abstract static class Entry<E extends Entry<E>> implements GuiEventListener, TickableWidget, HideableWidget, DisableableWidget {
        @Deprecated DynamicEntryListWidget<E> parent;
        @Nullable
        private NarratableEntry lastNarratable;
        @Nullable
        protected Requirement enableRequirement = null;
        @Nullable
        protected Requirement displayRequirement = null;
        protected boolean enabled = true;
        protected boolean displayed = true;
        
        public Entry() {
        }
        
        public abstract void render(GuiGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta);
        
        public boolean isMouseOver(double double_1, double double_2) {
            return Objects.equals(this.parent.getItemAtPosition(double_1, double_2), this);
        }
        
        public DynamicEntryListWidget<E> getParent() {
            return parent;
        }
        
        public void setParent(DynamicEntryListWidget<E> parent) {
            this.parent = parent;
        }
        
        @Override
        public boolean isEnabled() {
            return isDisplayed() && enabled;
        }
        
        @Override
        public boolean isDisplayed() {
            return displayed;
        }
        
        @Override
        public void setRequirement(@Nullable Requirement requirement) {
            this.enableRequirement = requirement;
        }
        
        @Override
        public @Nullable Requirement getRequirement() {
            return enableRequirement;
        }
        
        @Override
        public void setDisplayRequirement(@Nullable Requirement requirement) {
            this.displayRequirement = requirement;
        }
        
        @Override
        public @Nullable Requirement getDisplayRequirement() {
            return displayRequirement;
        }
        
        public abstract int getItemHeight();
        
        @Deprecated
        public int getMorePossibleHeight() {
            return -1;
        }
        
        public abstract List<? extends NarratableEntry> narratables();
        
        @Override
        public void tick() {
            // Check requirements
            enabled = getRequirement() == null || getRequirement().check();
            displayed = getDisplayRequirement() == null || getDisplayRequirement().check();
        }
        
        void updateNarration(NarrationElementOutput narrationElementOutput) {
            List<? extends NarratableEntry> list = this.narratables();
            Screen.NarratableSearchResult narratableSearchResult = Screen.findNarratableWidget(list, this.lastNarratable);
            if (narratableSearchResult != null) {
                if (narratableSearchResult.priority.isTerminal()) {
                    this.lastNarratable = narratableSearchResult.entry;
                }
                
                if (list.size() > 1) {
                    narrationElementOutput.add(NarratedElementType.POSITION, Component.translatable("narrator.position.object_list", narratableSearchResult.index + 1, list.size()));
                    if (narratableSearchResult.priority == NarrationPriority.FOCUSED) {
                        narrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("narration.component_list.usage"));
                    }
                }
                
                narratableSearchResult.entry.updateNarration(narrationElementOutput.nest());
            }
            
        }
    }
    
    @Environment(EnvType.CLIENT)
    class Entries extends AbstractList<E> {
        private final ArrayList<E> items;
        
        private Entries() {
            this.items = Lists.newArrayList();
        }
        
        @Override
        public void clear() {
            items.clear();
        }
        
        @Override
        public E get(int int_1) {
            return this.items.get(int_1);
        }
        
        @Override
        public int size() {
            return this.items.size();
        }
        
        @Override
        public E set(int int_1, E itemListWidget$Item_1) {
            E itemListWidget$Item_2 = this.items.set(int_1, itemListWidget$Item_1);
            itemListWidget$Item_1.parent = DynamicEntryListWidget.this;
            return itemListWidget$Item_2;
        }
        
        @Override
        public void add(int int_1, E itemListWidget$Item_1) {
            this.items.add(int_1, itemListWidget$Item_1);
            itemListWidget$Item_1.parent = DynamicEntryListWidget.this;
        }
        
        @Override
        public E remove(int int_1) {
            return this.items.remove(int_1);
        }
    }
}

