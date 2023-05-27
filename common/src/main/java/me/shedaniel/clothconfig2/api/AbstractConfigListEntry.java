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

package me.shedaniel.clothconfig2.api;

import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import me.shedaniel.math.Rectangle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public abstract class AbstractConfigListEntry<T> extends AbstractConfigEntry<T> {
    private final Component fieldName;
    private boolean editable = true;
    private boolean requiresRestart;
    
    public AbstractConfigListEntry(Component fieldName, boolean requiresRestart) {
        this.fieldName = fieldName;
        this.requiresRestart = requiresRestart;
    }
    
    @Override
    public boolean isRequiresRestart() {
        return requiresRestart;
    }
    
    @Override
    public void setRequiresRestart(boolean requiresRestart) {
        this.requiresRestart = requiresRestart;
    }
    
    public boolean isEditable() {
        return getConfigScreen().isEditable() && editable;
    }
    
    public void setEditable(boolean editable) {
        this.editable = editable;
    }
    
    public final int getPreferredTextColor() {
        return getConfigError().isPresent() ? 16733525 : 16777215;
    }
    
    public Rectangle getEntryArea(int x, int y, int entryWidth, int entryHeight) {
        return new Rectangle(getParent().left, y, getParent().right - getParent().left, getItemHeight() - 4);
    }
    
    public boolean isMouseInside(int mouseX, int mouseY, int x, int y, int entryWidth, int entryHeight) {
        return getParent().isMouseOver(mouseX, mouseY) && getEntryArea(x, y, entryWidth, entryHeight).contains(mouseX, mouseY);
    }
    
    @Override
    public void render(GuiGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        if (isMouseInside(mouseX, mouseY, x, y, entryWidth, entryHeight)) {
            Rectangle area = getEntryArea(x, y, entryWidth, entryHeight);
            if (getParent() instanceof ClothConfigScreen.ListWidget)
                ((ClothConfigScreen.ListWidget<AbstractConfigEntry<T>>) getParent()).thisTimeTarget = new Rectangle(area.x, area.y + getParent().getScroll(), area.width, area.height);
        }
    }
    
    @Override
    public Component getFieldName() {
        return fieldName;
    }
}
