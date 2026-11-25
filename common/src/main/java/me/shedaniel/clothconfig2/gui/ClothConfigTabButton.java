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

package me.shedaniel.clothconfig2.gui;

import me.shedaniel.clothconfig2.api.Tooltip;
import me.shedaniel.math.Point;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public class ClothConfigTabButton extends AbstractButton {
    
    private final int index;
    private final ClothConfigScreen screen;
    @Nullable
    private final Supplier<Optional<FormattedText[]>> descriptionSupplier;
    
    public ClothConfigTabButton(ClothConfigScreen screen, int index, int int_1, int int_2, int int_3, int int_4, Component string_1, Supplier<Optional<FormattedText[]>> descriptionSupplier) {
        super(int_1, int_2, int_3, int_4, string_1);
        this.index = index;
        this.screen = screen;
        this.descriptionSupplier = descriptionSupplier;
    }
    
    public ClothConfigTabButton(ClothConfigScreen screen, int index, int int_1, int int_2, int int_3, int int_4, Component string_1) {
        this(screen, index, int_1, int_2, int_3, int_4, string_1, null);
    }
    
    @Override
    public void onPress(InputWithModifiers input) {
        if (index != -1)
            screen.selectedCategoryIndex = index;
        screen.init(screen.width, screen.height);
    }
    
    @Override
    public void extractContents(GuiGraphicsExtractor graphics) {
        active = index != screen.selectedCategoryIndex;
        this.extractDefaultSprite(graphics);
        this.extractDefaultLabel(graphics.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE));
        
        int mouseX = graphics.mouseX();
        int mouseY = graphics.mouseY();
        if (isMouseOver(mouseX, mouseY)) {
            Optional<FormattedText[]> tooltip = getDescription();
            if (tooltip.isPresent() && tooltip.get().length > 0)
                screen.addTooltip(Tooltip.of(new Point(mouseX, mouseY), tooltip.get()));
        }
    }
    
    @Override
    protected boolean isValidClickButton(MouseButtonInfo mouseButtonInfo) {
        return visible && active && super.isValidClickButton(mouseButtonInfo);
    }
    
    @Override
    public boolean isMouseOver(double double_1, double double_2) {
        return this.visible && double_1 >= this.getX() && double_2 >= this.getY() && double_1 < this.getX() + this.width && double_2 < this.getY() + this.height && double_1 >= 20 && double_1 < screen.width - 20;
    }
    
    public Optional<FormattedText[]> getDescription() {
        if (descriptionSupplier != null)
            return descriptionSupplier.get();
        return Optional.empty();
    }
    
    @Override
    public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        
    }
}
