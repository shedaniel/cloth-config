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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;

import java.util.Optional;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public abstract class BaseListCell extends AbstractContainerEventHandler implements NarratableEntry {
    private Supplier<Optional<Component>> errorSupplier;
    
    public final int getPreferredTextColor() {
        return getConfigError().isPresent() ? 16733525 : 14737632;
    }
    
    public final Optional<Component> getConfigError() {
        if (errorSupplier != null && errorSupplier.get().isPresent())
            return errorSupplier.get();
        return getError();
    }
    
    public void setErrorSupplier(Supplier<Optional<Component>> errorSupplier) {
        this.errorSupplier = errorSupplier;
    }
    
    public abstract Optional<Component> getError();
    
    public abstract int getCellHeight();
    
    public abstract void render(GuiGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta);
    
    public void lateRender(GuiGraphics graphics, int mouseX, int mouseY, float delta) {}
    
    public int getMorePossibleHeight() {
    	return 0;
    }
    
    public void updateSelected(boolean isSelected) {}
    
    public boolean isRequiresRestart() {
        return false;
    }
    
    public boolean isEdited() {
        return getConfigError().isPresent();
    }
    
    public void onAdd() {}
    
    public void onDelete() {}
}
