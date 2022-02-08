/*
 * The smooth scrolling code is partially taken from osu-framework.
 * <p>
 * Copyright (c) 2020 ppy Pty Ltd <contact@ppy.sh>.
 * Copyright (c) 2018, 2019, 2020 shedaniel.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package me.shedaniel.clothconfig2.api.scroll;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import me.shedaniel.clothconfig2.ClothConfigInitializer;
import me.shedaniel.clothconfig2.api.animator.NumberAnimator;
import me.shedaniel.clothconfig2.api.animator.ValueAnimator;
import me.shedaniel.clothconfig2.gui.widget.DynamicEntryListWidget;
import me.shedaniel.math.Rectangle;
import me.shedaniel.math.impl.PointHelper;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;

public abstract class ScrollingContainer {
    private final NumberAnimator<Double> scroll = ValueAnimator.ofDouble();
    private boolean draggingScrollBar = false;
    private long scrollDuration = ClothConfigInitializer.getScrollDuration();
    
    public ScrollingContainer() {
    }
    
    public abstract Rectangle getBounds();
    
    public Rectangle getScissorBounds() {
        Rectangle bounds = getBounds();
        if (hasScrollBar()) {
            return new Rectangle(bounds.x, bounds.y, bounds.width - 6, bounds.height);
        }
        return bounds;
    }
    
    public int getScrollBarX(int maxX) {
        return hasScrollBar() ? maxX - 6 : maxX;
    }
    
    public boolean hasScrollBar() {
        return getMaxScrollHeight() > getBounds().height;
    }
    
    public abstract int getMaxScrollHeight();
    
    public final double scrollAmount() {
        return scroll.value();
    }
    
    public final int scrollAmountInt() {
        return (int) Math.round(scroll.value());
    }
    
    public final double scrollTarget() {
        return scroll.target();
    }
    
    public void setScrollDuration(long scrollDuration) {
        this.scrollDuration = scrollDuration;
    }
    
    public final int getMaxScroll() {
        return Math.max(0, getMaxScrollHeight() - getBounds().height);
    }
    
    public final double clamp(double v) {
        return this.clamp(v, 200.0D);
    }
    
    public final double clamp(double v, double clampExtension) {
        return Mth.clamp(v, -clampExtension, (double) this.getMaxScroll() + clampExtension);
    }
    
    public final void offset(double value, boolean animated) {
        scrollTo(scroll.target() + value, animated);
    }
    
    public final void scrollTo(double value, boolean animated) {
        scrollTo(value, animated, scrollDuration);
    }
    
    public final void scrollTo(double value, boolean animated, long duration) {
        if (animated) {
            scroll.setTo(value, duration);
        } else {
            scroll.setAs(value);
        }
    }
    
    public void updatePosition(float delta) {
        scroll.setTarget(handleBounceBack(this.scrollTarget(), this.scrollAmount(), this.getMaxScroll(), delta));
        this.scroll.update(delta);
    }
    
    public static double handleBounceBack(double target, double maxScroll, float delta) {
        return handleBounceBack(target, maxScroll, delta, ClothConfigInitializer.getBounceBackMultiplier());
    }
    
    public static double handleBounceBack(double target, double maxScroll, float delta, double bounceBackMultiplier) {
        if (bounceBackMultiplier >= 0) {
            target = clampExtension(target, maxScroll);
            if (target < 0) {
                target -= target * (1 - bounceBackMultiplier) * delta / 3;
            } else if (target > maxScroll) {
                target = (target - maxScroll) * (1 - (1 - bounceBackMultiplier) * delta / 3) + maxScroll;
            }
        } else
            target = clampExtension(target, maxScroll, 0);
        return target;
    }
    
    public static double clampExtension(double value, double maxScroll) {
        return clampExtension(value, maxScroll, DynamicEntryListWidget.SmoothScrollingSettings.CLAMP_EXTENSION);
    }
    
    public static double clampExtension(double v, double maxScroll, double clampExtension) {
        return Mth.clamp(v, -clampExtension, maxScroll + clampExtension);
    }
    
    public void renderScrollBar() {
        renderScrollBar(0, 1, 1);
    }
    
    public void renderScrollBar(int background, float alpha, float scrollBarAlphaOffset) {
        if (hasScrollBar()) {
            Rectangle bounds = getBounds();
            int maxScroll = getMaxScroll();
            int height = bounds.height * bounds.height / getMaxScrollHeight();
            height = Mth.clamp(height, 32, bounds.height);
            height -= Math.min((scrollAmount() < 0 ? (int) -scrollAmount() : scrollAmount() > maxScroll ? (int) scrollAmount() - maxScroll : 0), height * .95);
            height = Math.max(10, height);
            int minY = Math.min(Math.max((int) scrollAmount() * (bounds.height - height) / maxScroll + bounds.y, bounds.y), bounds.getMaxY() - height);
            
            int scrollbarPositionMinX = getScrollBarX(bounds.getMaxX());
            int scrollbarPositionMaxX = scrollbarPositionMinX + 6;
            boolean hovered = (new Rectangle(scrollbarPositionMinX, minY, scrollbarPositionMaxX - scrollbarPositionMinX, height)).contains(PointHelper.ofMouse());
            float bottomC = (hovered ? .67f : .5f) * scrollBarAlphaOffset;
            float topC = (hovered ? .87f : .67f) * scrollBarAlphaOffset;
            
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.disableAlphaTest();
            RenderSystem.blendFuncSeparate(770, 771, 1, 0);
            RenderSystem.shadeModel(7425);
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder buffer = tesselator.getBuilder();
            buffer.begin(7, DefaultVertexFormat.POSITION_COLOR);
            {
                float a = (background >> 24 & 255) / 255.0F;
                float r = (background >> 16 & 255) / 255.0F;
                float g = (background >> 8 & 255) / 255.0F;
                float b = (background & 255) / 255.0F;
                buffer.vertex(scrollbarPositionMinX, bounds.getMaxY(), 0.0D).color(r, g, b, a).endVertex();
                buffer.vertex(scrollbarPositionMaxX, bounds.getMaxY(), 0.0D).color(r, g, b, a).endVertex();
                buffer.vertex(scrollbarPositionMaxX, bounds.y, 0.0D).color(r, g, b, a).endVertex();
                buffer.vertex(scrollbarPositionMinX, bounds.y, 0.0D).color(r, g, b, a).endVertex();
            }
            buffer.vertex(scrollbarPositionMinX, minY + height, 0.0D).color(bottomC, bottomC, bottomC, alpha).endVertex();
            buffer.vertex(scrollbarPositionMaxX, minY + height, 0.0D).color(bottomC, bottomC, bottomC, alpha).endVertex();
            buffer.vertex(scrollbarPositionMaxX, minY, 0.0D).color(bottomC, bottomC, bottomC, alpha).endVertex();
            buffer.vertex(scrollbarPositionMinX, minY, 0.0D).color(bottomC, bottomC, bottomC, alpha).endVertex();
            buffer.vertex(scrollbarPositionMinX, (minY + height - 1), 0.0D).color(topC, topC, topC, alpha).endVertex();
            buffer.vertex((scrollbarPositionMaxX - 1), (minY + height - 1), 0.0D).color(topC, topC, topC, alpha).endVertex();
            buffer.vertex((scrollbarPositionMaxX - 1), minY, 0.0D).color(topC, topC, topC, alpha).endVertex();
            buffer.vertex(scrollbarPositionMinX, minY, 0.0D).color(topC, topC, topC, alpha).endVertex();
            tesselator.end();
            RenderSystem.disableBlend();
            RenderSystem.enableTexture();
        }
    }
    
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
        return mouseDragged(mouseX, mouseY, button, dx, dy, false, 0);
    }
    
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy, boolean snapToRows, double rowSize) {
        if (button == 0 && draggingScrollBar) {
            float height = getMaxScrollHeight();
            Rectangle bounds = getBounds();
            int actualHeight = bounds.height;
            if (mouseY >= bounds.y && mouseY <= bounds.getMaxY()) {
                double maxScroll = Math.max(1, getMaxScroll());
                double int_3 = Mth.clamp(((double) (actualHeight * actualHeight) / (double) height), 32, actualHeight - 8);
                double double_6 = Math.max(1.0D, maxScroll / (actualHeight - int_3));
                float to = Mth.clamp((float) (scrollAmount() + dy * double_6), 0, getMaxScroll());
                if (snapToRows) {
                    double nearestRow = Math.round(to / rowSize) * rowSize;
                    scrollTo(nearestRow, false);
                } else
                    scrollTo(to, false);
            }
            return true;
        }
        return false;
    }
    
    public boolean updateDraggingState(double mouseX, double mouseY, int button) {
        if (!hasScrollBar())
            return false;
        double height = getMaxScroll();
        Rectangle bounds = getBounds();
        int actualHeight = bounds.height;
        if (height > actualHeight && mouseY >= bounds.y && mouseY <= bounds.getMaxY()) {
            double scrollbarPositionMinX = getScrollBarX(bounds.getMaxX());
            if (mouseX >= scrollbarPositionMinX - 1 & mouseX <= scrollbarPositionMinX + 8) {
                this.draggingScrollBar = true;
                return true;
            }
        }
        this.draggingScrollBar = false;
        return false;
    }
}
