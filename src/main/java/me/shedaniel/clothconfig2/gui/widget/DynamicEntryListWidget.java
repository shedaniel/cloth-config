package me.shedaniel.clothconfig2.gui.widget;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.clothconfig2.api.ScissorsHandler;
import me.shedaniel.math.Rectangle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public abstract class DynamicEntryListWidget<E extends DynamicEntryListWidget.Entry<E>> extends AbstractParentElement implements Drawable {
    protected static final int DRAG_OUTSIDE = -2;
    protected final MinecraftClient client;
    private final List<E> entries = new Entries();
    public int width;
    public int height;
    public int top;
    public int bottom;
    public int right;
    public int left;
    protected boolean verticallyCenter = true;
    protected int yDrag = -2;
    protected boolean visible = true;
    protected boolean renderSelection;
    protected int headerHeight;
    protected double scroll;
    protected boolean scrolling;
    protected E selectedItem;
    protected Identifier backgroundLocation;
    
    public DynamicEntryListWidget(MinecraftClient client, int width, int height, int top, int bottom, Identifier backgroundLocation) {
        this.client = client;
        this.width = width;
        this.height = height;
        this.top = top;
        this.bottom = bottom;
        this.left = 0;
        this.right = width;
        this.backgroundLocation = backgroundLocation;
    }
    
    public void setRenderSelection(boolean boolean_1) {
        this.visible = boolean_1;
    }
    
    protected void setRenderHeader(boolean boolean_1, int headerHeight) {
        this.renderSelection = boolean_1;
        this.headerHeight = headerHeight;
        if (!boolean_1)
            this.headerHeight = 0;
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
    
    public final List<E> children() {
        return this.entries;
    }
    
    protected final void clearItems() {
        this.entries.clear();
    }
    
    protected E getItem(int index) {
        return this.children().get(index);
    }
    
    protected int addItem(E item) {
        this.entries.add(item);
        return this.entries.size() - 1;
    }
    
    protected int getItemCount() {
        return this.children().size();
    }
    
    protected boolean isSelected(int index) {
        return Objects.equals(this.getSelectedItem(), this.children().get(index));
    }
    
    protected final E getItemAtPosition(double mouseX, double mouseY) {
        int listMiddleX = this.left + this.width / 2;
        int minX = listMiddleX - this.getItemWidth() / 2;
        int maxX = listMiddleX + this.getItemWidth() / 2;
        int currentY = MathHelper.floor(mouseY - (double) this.top) - this.headerHeight + (int) this.getScroll() - 4;
        int itemY = 0;
        int itemIndex = -1;
        for (int i = 0; i < entries.size(); i++) {
            E item = getItem(i);
            itemY += item.getItemHeight();
            if (itemY > currentY) {
                itemIndex = i;
                break;
            }
        }
        return mouseX < (double) this.getScrollbarPosition() && mouseX >= minX && mouseX <= maxX && itemIndex >= 0 && currentY >= 0 && itemIndex < this.getItemCount() ? this.children().get(itemIndex) : null;
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
        for (E entry : entries) {
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
    
    protected void renderHeader(int int_1, int int_2, Tessellator tessellator) {
    }
    
    protected void drawBackground() {
    }
    
    protected void renderDecorations(int int_1, int int_2) {
    }
    
    @Deprecated
    protected void renderBackBackground(BufferBuilder buffer, Tessellator tessellator) {
        this.client.getTextureManager().bindTexture(backgroundLocation);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        float float_2 = 32.0F;
        buffer.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        buffer.vertex(this.left, this.bottom, 0.0D).texture(this.left / 32.0F, ((this.bottom + (int) this.getScroll()) / 32.0F)).color(32, 32, 32, 255).next();
        buffer.vertex(this.right, this.bottom, 0.0D).texture(this.right / 32.0F, ((this.bottom + (int) this.getScroll()) / 32.0F)).color(32, 32, 32, 255).next();
        buffer.vertex(this.right, this.top, 0.0D).texture(this.right / 32.0F, ((this.top + (int) this.getScroll()) / 32.0F)).color(32, 32, 32, 255).next();
        buffer.vertex(this.left, this.top, 0.0D).texture(this.left / 32.0F, ((this.top + (int) this.getScroll()) / 32.0F)).color(32, 32, 32, 255).next();
        tessellator.draw();
    }
    
    @SuppressWarnings("deprecation")
    public void render(int mouseX, int mouseY, float delta) {
        this.drawBackground();
        int scrollbarPosition = this.getScrollbarPosition();
        int int_4 = scrollbarPosition + 6;
        RenderSystem.disableLighting();
        RenderSystem.disableFog();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        renderBackBackground(buffer, tessellator);
        int rowLeft = this.getRowLeft();
        int startY = this.top + 4 - (int) this.getScroll();
        if (this.renderSelection)
            this.renderHeader(rowLeft, startY, tessellator);
        ScissorsHandler.INSTANCE.scissor(new Rectangle(left, top, width, bottom - top));
        this.renderList(rowLeft, startY, mouseX, mouseY, delta);
        ScissorsHandler.INSTANCE.removeLastScissor();
        RenderSystem.disableDepthTest();
        this.renderHoleBackground(0, this.top, 255, 255);
        this.renderHoleBackground(this.bottom, this.height, 255, 255);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(770, 771, 0, 1);
        RenderSystem.disableAlphaTest();
        RenderSystem.shadeModel(7425);
        RenderSystem.disableTexture();
        buffer.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        buffer.vertex(this.left, this.top + 4, 0.0D).texture(0, 1).color(0, 0, 0, 0).next();
        buffer.vertex(this.right, this.top + 4, 0.0D).texture(1, 1).color(0, 0, 0, 0).next();
        buffer.vertex(this.right, this.top, 0.0D).texture(1, 0).color(0, 0, 0, 255).next();
        buffer.vertex(this.left, this.top, 0.0D).texture(0, 0).color(0, 0, 0, 255).next();
        tessellator.draw();
        buffer.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        buffer.vertex(this.left, this.bottom, 0.0D).texture(0, 1).color(0, 0, 0, 255).next();
        buffer.vertex(this.right, this.bottom, 0.0D).texture(1, 1).color(0, 0, 0, 255).next();
        buffer.vertex(this.right, this.bottom - 4, 0.0D).texture(1, 0).color(0, 0, 0, 0).next();
        buffer.vertex(this.left, this.bottom - 4, 0.0D).texture(0, 0).color(0, 0, 0, 0).next();
        tessellator.draw();
        int maxScroll = this.getMaxScroll();
        renderScrollBar(tessellator, buffer, maxScroll, scrollbarPosition, int_4);
        
        this.renderDecorations(mouseX, mouseY);
        RenderSystem.enableTexture();
        RenderSystem.shadeModel(7424);
        RenderSystem.enableAlphaTest();
        RenderSystem.disableBlend();
    }
    
    @SuppressWarnings("deprecation")
    protected void renderScrollBar(Tessellator tessellator, BufferBuilder buffer, int maxScroll, int scrollbarPositionMinX, int scrollbarPositionMaxX) {
        if (maxScroll > 0) {
            int int_9 = ((this.bottom - this.top) * (this.bottom - this.top)) / this.getMaxScrollPosition();
            int_9 = MathHelper.clamp(int_9, 32, this.bottom - this.top - 8);
            int int_10 = (int) this.getScroll() * (this.bottom - this.top - int_9) / maxScroll + this.top;
            if (int_10 < this.top) {
                int_10 = this.top;
            }
            
            buffer.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            buffer.vertex(scrollbarPositionMinX, this.bottom, 0.0D).texture(0, 1).color(0, 0, 0, 255).next();
            buffer.vertex(scrollbarPositionMaxX, this.bottom, 0.0D).texture(1, 1).color(0, 0, 0, 255).next();
            buffer.vertex(scrollbarPositionMaxX, this.top, 0.0D).texture(1, 0).color(0, 0, 0, 255).next();
            buffer.vertex(scrollbarPositionMinX, this.top, 0.0D).texture(0, 0).color(0, 0, 0, 255).next();
            tessellator.draw();
            buffer.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            buffer.vertex(scrollbarPositionMinX, int_10 + int_9, 0.0D).texture(0, 1).color(128, 128, 128, 255).next();
            buffer.vertex(scrollbarPositionMaxX, int_10 + int_9, 0.0D).texture(1, 1).color(128, 128, 128, 255).next();
            buffer.vertex(scrollbarPositionMaxX, int_10, 0.0D).texture(1, 0).color(128, 128, 128, 255).next();
            buffer.vertex(scrollbarPositionMinX, int_10, 0.0D).texture(0, 0).color(128, 128, 128, 255).next();
            tessellator.draw();
            buffer.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            buffer.vertex(scrollbarPositionMinX, (int_10 + int_9 - 1), 0.0D).texture(0, 1).color(192, 192, 192, 255).next();
            buffer.vertex((scrollbarPositionMaxX - 1), (int_10 + int_9 - 1), 0.0D).texture(1, 1).color(192, 192, 192, 255).next();
            buffer.vertex((scrollbarPositionMaxX - 1), int_10, 0.0D).texture(1, 0).color(192, 192, 192, 255).next();
            buffer.vertex(scrollbarPositionMinX, int_10, 0.0D).texture(0, 0).color(192, 192, 192, 255).next();
            tessellator.draw();
        }
    }
    
    protected void centerScrollOn(E item) {
        double d = (this.bottom - this.top) / -2d;
        for (int i = 0; i < this.children().indexOf(item) && i < this.getItemCount(); i++)
            d += getItem(i).getItemHeight();
        this.capYPosition(d);
    }
    
    protected void ensureVisible(E item) {
        int rowTop = this.getRowTop(this.children().indexOf(item));
        int int_2 = rowTop - this.top - 4 - item.getItemHeight();
        if (int_2 < 0)
            this.scroll(int_2);
        int int_3 = this.bottom - rowTop - item.getItemHeight() * 2;
        if (int_3 < 0)
            this.scroll(-int_3);
    }
    
    protected void scroll(int int_1) {
        this.capYPosition(this.getScroll() + (double) int_1);
        this.yDrag = -2;
    }
    
    public double getScroll() {
        return this.scroll;
    }
    
    public void capYPosition(double double_1) {
        this.scroll = MathHelper.clamp(double_1, 0.0D, this.getMaxScroll());
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
    
    public boolean mouseReleased(double double_1, double double_2, int int_1) {
        if (this.getFocused() != null) {
            this.getFocused().mouseReleased(double_1, double_2, int_1);
        }
        
        return false;
    }
    
    public boolean mouseDragged(double double_1, double double_2, int int_1, double double_3, double double_4) {
        if (super.mouseDragged(double_1, double_2, int_1, double_3, double_4)) {
            return true;
        } else if (int_1 == 0 && this.scrolling) {
            if (double_2 < (double) this.top) {
                this.capYPosition(0.0D);
            } else if (double_2 > (double) this.bottom) {
                this.capYPosition(this.getMaxScroll());
            } else {
                double double_5 = Math.max(1, this.getMaxScroll());
                int int_2 = this.bottom - this.top;
                int int_3 = MathHelper.clamp((int) ((float) (int_2 * int_2) / (float) this.getMaxScrollPosition()), 32, int_2 - 8);
                double double_6 = Math.max(1.0D, double_5 / (double) (int_2 - int_3));
                this.capYPosition(this.getScroll() + double_4 * double_6);
            }
            
            return true;
        } else {
            return false;
        }
    }
    
    public boolean mouseScrolled(double double_1, double double_2, double double_3) {
        for (E entry : entries) {
            if (entry.mouseScrolled(double_1, double_2, double_3)) {
                return true;
            }
        }
        this.capYPosition(this.getScroll() - double_3 * (double) (getMaxScroll() / getItemCount()) / 2.0D);
        return true;
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
    
    protected void moveSelection(int int_1) {
        if (!this.children().isEmpty()) {
            int int_2 = this.children().indexOf(this.getSelectedItem());
            int int_3 = MathHelper.clamp(int_2 + int_1, 0, this.getItemCount() - 1);
            E itemListWidget$Item_1 = this.children().get(int_3);
            this.selectItem(itemListWidget$Item_1);
            this.ensureVisible(itemListWidget$Item_1);
        }
        
    }
    
    public boolean isMouseOver(double double_1, double double_2) {
        return double_2 >= (double) this.top && double_2 <= (double) this.bottom && double_1 >= (double) this.left && double_1 <= (double) this.right;
    }
    
    protected void renderList(int startX, int startY, int int_3, int int_4, float float_1) {
        int itemCount = this.getItemCount();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        
        for (int renderIndex = 0; renderIndex < itemCount; ++renderIndex) {
            E item = this.getItem(renderIndex);
            int itemY = startY + headerHeight;
            for (int i = 0; i < entries.size() && i < renderIndex; i++)
                itemY += entries.get(i).getItemHeight();
            int itemHeight = item.getItemHeight() - 4;
            int itemWidth = this.getItemWidth();
            int itemMinX, itemMaxX;
            if (this.visible && this.isSelected(renderIndex)) {
                itemMinX = this.left + this.width / 2 - itemWidth / 2;
                itemMaxX = itemMinX + itemWidth;
                RenderSystem.disableTexture();
                float float_2 = this.isFocused() ? 1.0F : 0.5F;
                RenderSystem.color4f(float_2, float_2, float_2, 1.0F);
                buffer.begin(7, VertexFormats.POSITION);
                buffer.vertex(itemMinX, itemY + itemHeight + 2, 0.0D).next();
                buffer.vertex(itemMaxX, itemY + itemHeight + 2, 0.0D).next();
                buffer.vertex(itemMaxX, itemY - 2, 0.0D).next();
                buffer.vertex(itemMinX, itemY - 2, 0.0D).next();
                tessellator.draw();
                RenderSystem.color4f(0.0F, 0.0F, 0.0F, 1.0F);
                buffer.begin(7, VertexFormats.POSITION);
                buffer.vertex(itemMinX + 1, itemY + itemHeight + 1, 0.0D).next();
                buffer.vertex(itemMaxX - 1, itemY + itemHeight + 1, 0.0D).next();
                buffer.vertex(itemMaxX - 1, itemY - 1, 0.0D).next();
                buffer.vertex(itemMinX + 1, itemY - 1, 0.0D).next();
                tessellator.draw();
                RenderSystem.enableTexture();
            }
            
            int y = this.getRowTop(renderIndex);
            int x = this.getRowLeft();
            DiffuseLighting.disable();
            renderItem(item, renderIndex, y, x, itemWidth, itemHeight, int_3, int_4, this.isMouseOver(int_3, int_4) && Objects.equals(this.getItemAtPosition(int_3, int_4), item), float_1);
        }
        
    }
    
    protected void renderItem(E item, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
        item.render(index, y, x, entryWidth, entryHeight, mouseX, mouseY, isSelected, delta);
    }
    
    protected int getRowLeft() {
        return this.left + this.width / 2 - this.getItemWidth() / 2 + 2;
    }
    
    protected int getRowTop(int index) {
        int integer = top + 4 - (int) this.getScroll() + headerHeight;
        for (int i = 0; i < entries.size() && i < index; i++)
            integer += entries.get(i).getItemHeight();
        return integer;
    }
    
    protected boolean isFocused() {
        return false;
    }
    
    @SuppressWarnings("deprecation")
    protected void renderHoleBackground(int int_1, int int_2, int int_3, int int_4) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        this.client.getTextureManager().bindTexture(backgroundLocation);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        float float_1 = 32.0F;
        buffer.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        buffer.vertex(this.left, int_2, 0.0D).texture(0, ((float) int_2 / 32.0F)).color(64, 64, 64, int_4).next();
        buffer.vertex(this.left + this.width, int_2, 0.0D).texture(((float) this.width / 32.0F), ((float) int_2 / 32.0F)).color(64, 64, 64, int_4).next();
        buffer.vertex(this.left + this.width, int_1, 0.0D).texture(((float) this.width / 32.0F), ((float) int_1 / 32.0F)).color(64, 64, 64, int_3).next();
        buffer.vertex(this.left, int_1, 0.0D).texture(0, ((float) int_1 / 32.0F)).color(64, 64, 64, int_3).next();
        tessellator.draw();
    }
    
    protected E remove(int int_1) {
        E itemListWidget$Item_1 = this.entries.get(int_1);
        return this.removeEntry(this.entries.get(int_1)) ? itemListWidget$Item_1 : null;
    }
    
    protected boolean removeEntry(E itemListWidget$Item_1) {
        boolean boolean_1 = this.entries.remove(itemListWidget$Item_1);
        if (boolean_1 && itemListWidget$Item_1 == this.getSelectedItem()) {
            this.selectItem(null);
        }
        
        return boolean_1;
    }
    
    public static final class SmoothScrollingSettings {
        public static final double CLAMP_EXTENSION = 200;
        
        private SmoothScrollingSettings() {}
    }
    
    @Environment(EnvType.CLIENT)
    public abstract static class Entry<E extends Entry<E>> extends DrawableHelper implements Element {
        @Deprecated DynamicEntryListWidget<E> parent;
        
        public Entry() {
        }
        
        public abstract void render(int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta);
        
        public boolean isMouseOver(double double_1, double double_2) {
            return Objects.equals(this.parent.getItemAtPosition(double_1, double_2), this);
        }
        
        public DynamicEntryListWidget<E> getParent() {
            return parent;
        }
        
        public void setParent(DynamicEntryListWidget<E> parent) {
            this.parent = parent;
        }
        
        public abstract int getItemHeight();
        
        @Deprecated
        public int getMorePossibleHeight() {
            return -1;
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

