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

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import me.shedaniel.clothconfig2.ClothConfigInitializer;
import me.shedaniel.clothconfig2.api.animator.NumberAnimator;
import me.shedaniel.clothconfig2.api.animator.ValueAnimator;
import me.shedaniel.math.Rectangle;
import me.shedaniel.math.impl.PointHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import static me.shedaniel.clothconfig2.api.scroll.ScrollingContainer.clampExtension;
import static me.shedaniel.clothconfig2.api.scroll.ScrollingContainer.handleBounceBack;

@Environment(EnvType.CLIENT)
public abstract class DynamicSmoothScrollingEntryListWidget<E extends DynamicEntryListWidget.Entry<E>> extends DynamicEntryListWidget<E> {
    
    protected boolean smoothScrolling = true;
    protected final NumberAnimator<Double> scrollAnimator = ValueAnimator.ofDouble();
    
    public DynamicSmoothScrollingEntryListWidget(Minecraft client, int width, int height, int top, int bottom, ResourceLocation backgroundLocation) {
        super(client, width, height, top, bottom, backgroundLocation);
    }
    
    public boolean isSmoothScrolling() {
        return smoothScrolling;
    }
    
    public void setSmoothScrolling(boolean smoothScrolling) {
        this.smoothScrolling = smoothScrolling;
    }
    
    @Override
    public void capYPosition(double scroll) {
        if (!smoothScrolling) {
            scrollAnimator.setAs(Mth.clamp(scroll, 0.0D, this.getMaxScroll()));
        } else {
            scrollAnimator.setAs(clampExtension(scroll, getMaxScroll()));
        }
        this.scroll = scrollAnimator.value();
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!smoothScrolling)
            return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        if ((this.getFocused() != null && this.isDragging() && button == 0) && this.getFocused().mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            return true;
        } else if (button == 0 && this.scrolling) {
            if (mouseY < (double) this.top) {
                this.capYPosition(0.0D);
            } else if (mouseY > (double) this.bottom) {
                this.capYPosition(this.getMaxScroll());
            } else {
                double double_5 = Math.max(1, this.getMaxScroll());
                int int_2 = this.bottom - this.top;
                int int_3 = Mth.clamp((int) ((float) (int_2 * int_2) / (float) this.getMaxScrollPosition()), 32, int_2 - 8);
                double double_6 = Math.max(1.0D, double_5 / (double) (int_2 - int_3));
                this.capYPosition(Mth.clamp(this.getScroll() + deltaY * double_6, 0, getMaxScroll()));
            }
            return true;
        }
        return false;
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        for (E entry : children()) {
            if (entry.mouseScrolled(mouseX, mouseY, amount)) {
                return true;
            }
        }
        if (!smoothScrolling) {
            scroll += 16 * -amount;
            this.scroll = Mth.clamp(amount, 0.0D, this.getMaxScroll());
            return true;
        }
        offset(ClothConfigInitializer.getScrollStep() * -amount, true);
        return true;
    }
    
    public void offset(double value, boolean animated) {
        scrollTo(scrollAnimator.target() + value, animated);
    }
    
    public void scrollTo(double value, boolean animated) {
        scrollTo(value, animated, ClothConfigInitializer.getScrollDuration());
    }
    
    public void scrollTo(double value, boolean animated, long duration) {
        if (animated) {
            scrollAnimator.setTo(value, duration);
        } else {
            scrollAnimator.setAs(value);
        }
    }
    
    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        scrollAnimator.setTarget(handleBounceBack(this.scrollAnimator.target(), this.getMaxScroll(), delta));
        this.scrollAnimator.update(delta);
        this.scroll = scrollAnimator.value();
        super.render(matrices, mouseX, mouseY, delta);
    }
    
    @Override
    protected void renderScrollBar(PoseStack matrices, Tesselator tessellator, BufferBuilder buffer, int maxScroll, int scrollbarPositionMinX, int scrollbarPositionMaxX) {
        if (!smoothScrolling)
            super.renderScrollBar(matrices, tessellator, buffer, maxScroll, scrollbarPositionMinX, scrollbarPositionMaxX);
        else if (maxScroll > 0) {
            int height = ((this.bottom - this.top) * (this.bottom - this.top)) / this.getMaxScrollPosition();
            height = Mth.clamp(height, 32, this.bottom - this.top - 8);
            height -= Math.min((scroll < 0 ? (int) -scroll : scroll > getMaxScroll() ? (int) scroll - getMaxScroll() : 0), height * .95);
            height = Math.max(10, height);
            int minY = Math.min(Math.max((int) this.getScroll() * (this.bottom - this.top - height) / maxScroll + this.top, this.top), this.bottom - height);
            
            int bottomc = new Rectangle(scrollbarPositionMinX, minY, scrollbarPositionMaxX - scrollbarPositionMinX, height).contains(PointHelper.ofMouse()) ? 168 : 128;
            int topc = new Rectangle(scrollbarPositionMinX, minY, scrollbarPositionMaxX - scrollbarPositionMinX, height).contains(PointHelper.ofMouse()) ? 222 : 172;
            
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            RenderSystem.disableTexture();
            Matrix4f matrix = matrices.last().pose();
            // Black Bar
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            buffer.vertex(matrix, scrollbarPositionMinX, this.bottom, 0.0F).color(0, 0, 0, 255).endVertex();
            buffer.vertex(matrix, scrollbarPositionMaxX, this.bottom, 0.0F).color(0, 0, 0, 255).endVertex();
            buffer.vertex(matrix, scrollbarPositionMaxX, this.top, 0.0F).color(0, 0, 0, 255).endVertex();
            buffer.vertex(matrix, scrollbarPositionMinX, this.top, 0.0F).color(0, 0, 0, 255).endVertex();
            
            // Bottom
            buffer.vertex(matrix, scrollbarPositionMinX, minY + height, 0.0F).color(bottomc, bottomc, bottomc, 255).endVertex();
            buffer.vertex(matrix, scrollbarPositionMaxX, minY + height, 0.0F).color(bottomc, bottomc, bottomc, 255).endVertex();
            buffer.vertex(matrix, scrollbarPositionMaxX, minY, 0.0F).color(bottomc, bottomc, bottomc, 255).endVertex();
            buffer.vertex(matrix, scrollbarPositionMinX, minY, 0.0F).color(bottomc, bottomc, bottomc, 255).endVertex();
            
            // Top
            buffer.vertex(matrix, scrollbarPositionMinX, (minY + height - 1), 0.0F).color(topc, topc, topc, 255).endVertex();
            buffer.vertex(matrix, (scrollbarPositionMaxX - 1), (minY + height - 1), 0.0F).color(topc, topc, topc, 255).endVertex();
            buffer.vertex(matrix, (scrollbarPositionMaxX - 1), minY, 0.0F).color(topc, topc, topc, 255).endVertex();
            buffer.vertex(matrix, scrollbarPositionMinX, minY, 0.0F).color(topc, topc, topc, 255).endVertex();
            tessellator.end();
            RenderSystem.disableBlend();
            RenderSystem.enableTexture();
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
