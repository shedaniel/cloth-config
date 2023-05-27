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

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class ColorDisplayWidget extends AbstractWidget {
    
    protected EditBox textFieldWidget;
    protected int color;
    protected int size;
    
    public ColorDisplayWidget(EditBox textFieldWidget, int x, int y, int size, int color) {
        super(x, y, size, size, Component.empty());
        this.textFieldWidget = textFieldWidget;
        this.color = color;
        this.size = size;
    }
    
    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        graphics.fillGradient(this.getX(), this.getY(), this.getX() + size, this.getY() + size, textFieldWidget.isFocused() ? -1 : -6250336, textFieldWidget.isFocused() ? -1 : -6250336);
        graphics.fillGradient(this.getX() + 1, this.getY() + 1, this.getX() + size - 1, this.getY() + size - 1, 0xffffffff, 0xffffffff);
        graphics.fillGradient(this.getX() + 1, this.getY() + 1, this.getX() + size - 1, this.getY() + size - 1, color, color);
    }
    
    @Override
    public void onClick(double mouseX, double mouseY) {
    }
    
    @Override
    public void onRelease(double mouseX, double mouseY) {
    }
    
    @Override
    public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        
    }
    
    public void setColor(int color) {
        this.color = color;
    }
}
