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

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * A slider widget whose state is partially managed by a parent {@link Context}
 * object.
 */
public class ManagedSliderWidget extends AbstractSliderButton {
    private static final ResourceLocation WIDGETS_TEX = new ResourceLocation("textures/gui/widgets.png");

    private final Context context;

    public ManagedSliderWidget(int x, int y, int width, int height, Context context) {
        super(x, y, width, height, NarratorChatListener.NO_TITLE, 0);

        this.context = context;
    }

    public void syncValueFromContext() {
        this.value = context.value();
        updateMessage();
    }

    @Override
    public void updateMessage() {
        setMessage(context.message());
    }

    @Override
    protected void applyValue() {
        context.valueApplied(value);
    }

    @Override
    public boolean keyPressed(int keyCode, int int_2, int int_3) {
        if (!context.editable())
            return false;
        return super.keyPressed(keyCode, int_2, int_3);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY) {
        if (!context.editable())
            return false;
        return super.mouseDragged(mouseX, mouseY, mouseButton, deltaX, deltaY);
    }

    @Override
    protected final void renderBg(PoseStack matrices, Minecraft client, int mouseX, int mouseY) {
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

        // Render anything on top of the background but below the scrubber
        renderBgHighlight(matrices, client, mouseX, mouseY);

        // Render the scrubber on top of anything we've drawn
        renderScrubber(matrices, client, mouseX, mouseY);
    }

    protected void renderBgHighlight(PoseStack matrices, Minecraft client, int mouseX, int mouseY) {
    }

    protected void renderScrubber(PoseStack matrices, Minecraft client, int mouseX, int mouseY) {
        super.renderBg(matrices, client, mouseX, mouseY);
    }

    /**
     * Provides a set of operations to allow a parent object to manage a {@link ManagedSliderWidget}.
     */
    public interface Context {
        Component message();

        double value();

        void valueApplied(double value);

        boolean editable();
    }
}
