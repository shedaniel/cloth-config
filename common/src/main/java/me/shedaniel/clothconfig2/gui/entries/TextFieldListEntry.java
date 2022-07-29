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

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public abstract class TextFieldListEntry<T> extends TooltipListEntry<T> {
    
    protected EditBox textFieldWidget;
    protected Button resetButton;
    protected Supplier<T> defaultValue;
    protected T original;
    protected List<AbstractWidget> widgets;
    private boolean isSelected = false;
    
    @ApiStatus.Internal
    @Deprecated
    protected TextFieldListEntry(Component fieldName, T original, Component resetButtonKey, Supplier<T> defaultValue) {
        this(fieldName, original, resetButtonKey, defaultValue, null);
    }
    
    @ApiStatus.Internal
    @Deprecated
    protected TextFieldListEntry(Component fieldName, T original, Component resetButtonKey, Supplier<T> defaultValue, Supplier<Optional<Component[]>> tooltipSupplier) {
        this(fieldName, original, resetButtonKey, defaultValue, tooltipSupplier, false);
    }
    
    @ApiStatus.Internal
    @Deprecated
    protected TextFieldListEntry(Component fieldName, T original, Component resetButtonKey, Supplier<T> defaultValue, Supplier<Optional<Component[]>> tooltipSupplier, boolean requiresRestart) {
        super(fieldName, tooltipSupplier, requiresRestart);
        this.defaultValue = defaultValue;
        this.original = original;
        this.textFieldWidget = new EditBox(Minecraft.getInstance().font, 0, 0, 148, 18, Component.empty()) {
            @Override
            public void render(PoseStack matrices, int int_1, int int_2, float float_1) {
                setFocused(isSelected && TextFieldListEntry.this.getFocused() == this);
                textFieldPreRender(this);
                super.render(matrices, int_1, int_2, float_1);
            }
            
            @Override
            public void insertText(String string_1) {
                super.insertText(stripAddText(string_1));
            }
        };
        textFieldWidget.setMaxLength(999999);
        textFieldWidget.setValue(String.valueOf(original));
        this.resetButton = new Button(0, 0, Minecraft.getInstance().font.width(resetButtonKey) + 6, 20, resetButtonKey, widget -> {
            TextFieldListEntry.this.textFieldWidget.setValue(String.valueOf(defaultValue.get()));
        });
        this.widgets = Lists.newArrayList(textFieldWidget, resetButton);
    }
    
    @Override
    public boolean isEdited() {
        return isChanged(original, textFieldWidget.getValue());
    }
    
    protected boolean isChanged(T original, String s) {
        return !String.valueOf(original).equals(s);
    }
    
    protected static void setTextFieldWidth(EditBox widget, int width) {
        widget.setWidth(width);
    }
    
    @Deprecated
    public void setValue(String s) {
        textFieldWidget.setValue(String.valueOf(s));
    }
    
    protected String stripAddText(String s) {
        return s;
    }
    
    protected void textFieldPreRender(EditBox widget) {
        
    }
    
    @Override
    public void updateSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
    
    @Override
    public void render(PoseStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
        Window window = Minecraft.getInstance().getWindow();
        this.resetButton.active = isEditable() && getDefaultValue().isPresent() && !isMatchDefault(textFieldWidget.getValue());
        this.resetButton.y = y;
        this.textFieldWidget.setEditable(isEditable());
        this.textFieldWidget.y = y + 1;
        Component displayedFieldName = getDisplayedFieldName();
        if (Minecraft.getInstance().font.isBidirectional()) {
            Minecraft.getInstance().font.drawShadow(matrices, displayedFieldName.getVisualOrderText(), window.getGuiScaledWidth() - x - Minecraft.getInstance().font.width(displayedFieldName), y + 6, getPreferredTextColor());
            this.resetButton.x = x;
            this.textFieldWidget.x = x + resetButton.getWidth();
        } else {
            Minecraft.getInstance().font.drawShadow(matrices, displayedFieldName.getVisualOrderText(), x, y + 6, getPreferredTextColor());
            this.resetButton.x = x + entryWidth - resetButton.getWidth();
            this.textFieldWidget.x = x + entryWidth - 148;
        }
        setTextFieldWidth(textFieldWidget, 148 - resetButton.getWidth() - 4);
        resetButton.render(matrices, mouseX, mouseY, delta);
        textFieldWidget.render(matrices, mouseX, mouseY, delta);
    }
    
    protected abstract boolean isMatchDefault(String text);
    
    @Override
    public Optional<T> getDefaultValue() {
        return defaultValue == null ? Optional.empty() : Optional.ofNullable(defaultValue.get());
    }
    
    @Override
    public List<? extends GuiEventListener> children() {
        return widgets;
    }
    
    @Override
    public List<? extends NarratableEntry> narratables() {
        return widgets;
    }
}
