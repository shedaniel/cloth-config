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
package me.shedaniel.clothconfig2.gui.widget;

import me.shedaniel.clothconfig2.ClothConfigInitializer;
import me.shedaniel.math.Rectangle;
import me.shedaniel.math.impl.PointHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;

import static me.shedaniel.clothconfig2.api.ScrollingContainer.clampExtension;
import static me.shedaniel.clothconfig2.api.ScrollingContainer.handleScrollingPosition;

@Environment(EnvType.CLIENT)
public abstract class DynamicNewSmoothScrollingEntryListWidget<E extends DynamicEntryListWidget.Entry<E>> extends DynamicEntryListWidget<E> {
    
    protected double target;
    protected boolean smoothScrolling = true;
    protected long start;
    protected long duration;
    
    public DynamicNewSmoothScrollingEntryListWidget(MinecraftClient client, int width, int height, int top, int bottom, Identifier backgroundLocation) {
        super(client, width, height, top, bottom, backgroundLocation);
    }
    
    public boolean isSmoothScrolling() {
        return smoothScrolling;
    }
    
    public void setSmoothScrolling(boolean smoothScrolling) {
        this.smoothScrolling = smoothScrolling;
    }
    
    @Override
    public void capYPosition(double double_1) {
        if (!smoothScrolling)
            this.scroll = MathHelper.clamp(double_1, 0.0D, this.getMaxScroll());
        else {
            scroll = clampExtension(double_1, getMaxScroll());
            target = clampExtension(double_1, getMaxScroll());
        }
    }
    
    @Override
    public boolean mouseDragged(double double_1, double double_2, int int_1, double double_3, double double_4) {
        if (!smoothScrolling)
            return super.mouseDragged(double_1, double_2, int_1, double_3, double_4);
        if ((this.getFocused() != null && this.isDragging() && int_1 == 0) && this.getFocused().mouseDragged(double_1, double_2, int_1, double_3, double_4)) {
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
                this.capYPosition(MathHelper.clamp(this.getScroll() + double_4 * double_6, 0, getMaxScroll()));
            }
            return true;
        }
        return false;
    }
    
    @Override
    public boolean mouseScrolled(double double_1, double double_2, double double_3) {
        for (E entry : children()) {
            if (entry.mouseScrolled(double_1, double_2, double_3)) {
                return true;
            }
        }
        if (!smoothScrolling) {
            scroll += 16 * -double_3;
            this.scroll = MathHelper.clamp(double_3, 0.0D, this.getMaxScroll());
            return true;
        }
        offset(ClothConfigInitializer.getScrollStep() * -double_3, true);
        return true;
    }
    
    public void offset(double value, boolean animated) {
        scrollTo(target + value, animated);
    }
    
    public void scrollTo(double value, boolean animated) {
        scrollTo(value, animated, ClothConfigInitializer.getScrollDuration());
    }
    
    public void scrollTo(double value, boolean animated, long duration) {
        target = clampExtension(value, getMaxScroll());
        
        if (animated) {
            start = System.currentTimeMillis();
            this.duration = duration;
        } else
            scroll = target;
    }
    
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        double[] target = {this.target};
        scroll = handleScrollingPosition(target, scroll, getMaxScroll(), delta, start, duration);
        this.target = target[0];
        super.render(matrices, mouseX, mouseY, delta);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    protected void renderScrollBar(MatrixStack matrices, Tessellator tessellator, BufferBuilder buffer, int maxScroll, int scrollbarPositionMinX, int scrollbarPositionMaxX) {
        if (!smoothScrolling)
            super.renderScrollBar(matrices, tessellator, buffer, maxScroll, scrollbarPositionMinX, scrollbarPositionMaxX);
        else if (maxScroll > 0) {
            int height = ((this.bottom - this.top) * (this.bottom - this.top)) / this.getMaxScrollPosition();
            height = MathHelper.clamp(height, 32, this.bottom - this.top - 8);
            height -= Math.min((scroll < 0 ? (int) -scroll : scroll > getMaxScroll() ? (int) scroll - getMaxScroll() : 0), height * .95);
            height = Math.max(10, height);
            int minY = Math.min(Math.max((int) this.getScroll() * (this.bottom - this.top - height) / maxScroll + this.top, this.top), this.bottom - height);
            
            int bottomc = new Rectangle(scrollbarPositionMinX, minY, scrollbarPositionMaxX - scrollbarPositionMinX, height).contains(PointHelper.ofMouse()) ? 168 : 128;
            int topc = new Rectangle(scrollbarPositionMinX, minY, scrollbarPositionMaxX - scrollbarPositionMinX, height).contains(PointHelper.ofMouse()) ? 222 : 172;
            
            Matrix4f matrix = matrices.peek().getModel();
            // Black Bar
            buffer.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            buffer.vertex(matrix, scrollbarPositionMinX, this.bottom, 0.0F).texture(0, 1).color(0, 0, 0, 255).next();
            buffer.vertex(matrix, scrollbarPositionMaxX, this.bottom, 0.0F).texture(1, 1).color(0, 0, 0, 255).next();
            buffer.vertex(matrix, scrollbarPositionMaxX, this.top, 0.0F).texture(1, 0).color(0, 0, 0, 255).next();
            buffer.vertex(matrix, scrollbarPositionMinX, this.top, 0.0F).texture(0, 0).color(0, 0, 0, 255).next();
            tessellator.draw();
            
            // Bottom
            buffer.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            buffer.vertex(matrix, scrollbarPositionMinX, minY + height, 0.0F).texture(0, 1).color(bottomc, bottomc, bottomc, 255).next();
            buffer.vertex(matrix, scrollbarPositionMaxX, minY + height, 0.0F).texture(1, 1).color(bottomc, bottomc, bottomc, 255).next();
            buffer.vertex(matrix, scrollbarPositionMaxX, minY, 0.0F).texture(1, 0).color(bottomc, bottomc, bottomc, 255).next();
            buffer.vertex(matrix, scrollbarPositionMinX, minY, 0.0F).texture(0, 0).color(bottomc, bottomc, bottomc, 255).next();
            tessellator.draw();
            
            // Top
            buffer.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            buffer.vertex(matrix, scrollbarPositionMinX, (minY + height - 1), 0.0F).texture(0, 1).color(topc, topc, topc, 255).next();
            buffer.vertex(matrix, (scrollbarPositionMaxX - 1), (minY + height - 1), 0.0F).texture(1, 1).color(topc, topc, topc, 255).next();
            buffer.vertex(matrix, (scrollbarPositionMaxX - 1), minY, 0.0F).texture(1, 0).color(topc, topc, topc, 255).next();
            buffer.vertex(matrix, scrollbarPositionMinX, minY, 0.0F).texture(0, 0).color(topc, topc, topc, 255).next();
            tessellator.draw();
        }
    }
    
    public static class Interpolation {
        public static double expoEase(double start, double end, double amount) {
            return start + (end - start) * ClothConfigInitializer.getEasingMethod().apply(amount);
        }
    }
    
    public static class Precision {
        public static final float FLOAT_EPSILON = 1e-3f;
        public static final double DOUBLE_EPSILON = 1e-7;
        
        public static boolean almostEquals(float value1, float value2, float acceptableDifference) {
            return Math.abs(value1 - value2) <= acceptableDifference;
        }
        
        public static boolean almostEquals(double value1, double value2, double acceptableDifference) {
            return Math.abs(value1 - value2) <= acceptableDifference;
        }
    }
    
}
