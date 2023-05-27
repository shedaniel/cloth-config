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

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class EmptyEntry extends AbstractConfigListEntry<Object> {
    private final int height;
    
    public EmptyEntry(int height) {
        super(Component.literal(UUID.randomUUID().toString()), false);
        this.height = height;
    }
    
    @Override
    public int getItemHeight() {
        return height;
    }
    
    @Nullable
    public ComponentPath nextFocusPath(FocusNavigationEvent focusNavigationEvent) {
        return null;
    }
    
    @Override
    public List<? extends NarratableEntry> narratables() {
        return Collections.emptyList();
    }
    
    @Override
    public Iterator<String> getSearchTags() {
        return Collections.emptyIterator();
    }
    
    @Override
    public Object getValue() {
        return null;
    }
    
    @Override
    public Optional<Object> getDefaultValue() {
        return Optional.empty();
    }
    
    @Override
    public boolean isMouseInside(int mouseX, int mouseY, int x, int y, int entryWidth, int entryHeight) {
        return false;
    }
    
    @Override
    public void render(GuiGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {}
    
    @Override
    public List<? extends GuiEventListener> children() {
        return Collections.emptyList();
    }
}
