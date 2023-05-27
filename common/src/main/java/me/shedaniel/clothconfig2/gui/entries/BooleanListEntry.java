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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class BooleanListEntry extends TooltipListEntry<Boolean> {
    
    private final AtomicBoolean bool;
    private final boolean original;
    private final Button buttonWidget;
    private final Button resetButton;
    private final Supplier<Boolean> defaultValue;
    private final List<AbstractWidget> widgets;
    
    @ApiStatus.Internal
    @Deprecated
    public BooleanListEntry(Component fieldName, boolean bool, Component resetButtonKey, Supplier<Boolean> defaultValue, Consumer<Boolean> saveConsumer) {
        this(fieldName, bool, resetButtonKey, defaultValue, saveConsumer, null);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public BooleanListEntry(Component fieldName, boolean bool, Component resetButtonKey, Supplier<Boolean> defaultValue, Consumer<Boolean> saveConsumer, Supplier<Optional<Component[]>> tooltipSupplier) {
        this(fieldName, bool, resetButtonKey, defaultValue, saveConsumer, tooltipSupplier, false);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public BooleanListEntry(Component fieldName, boolean bool, Component resetButtonKey, Supplier<Boolean> defaultValue, Consumer<Boolean> saveConsumer, Supplier<Optional<Component[]>> tooltipSupplier, boolean requiresRestart) {
        super(fieldName, tooltipSupplier, requiresRestart);
        this.defaultValue = defaultValue;
        this.original = bool;
        this.bool = new AtomicBoolean(bool);
        this.buttonWidget = Button.builder(Component.empty(), widget -> {
            BooleanListEntry.this.bool.set(!BooleanListEntry.this.bool.get());
        }).bounds(0, 0, 150, 20).build();
        this.resetButton = Button.builder(resetButtonKey, widget -> {
            BooleanListEntry.this.bool.set(defaultValue.get());
        }).bounds(0, 0, Minecraft.getInstance().font.width(resetButtonKey) + 6, 20).build();
        this.saveCallback = saveConsumer;
        this.widgets = Lists.newArrayList(buttonWidget, resetButton);
    }
    
    @Override
    public boolean isEdited() {
        return super.isEdited() || original != bool.get();
    }
    
    @Override
    public Boolean getValue() {
        return bool.get();
    }
    
    @Override
    public Optional<Boolean> getDefaultValue() {
        return defaultValue == null ? Optional.empty() : Optional.ofNullable(defaultValue.get());
    }
    
    @Override
    public void render(GuiGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.render(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
        Window window = Minecraft.getInstance().getWindow();
        this.resetButton.active = isEditable() && getDefaultValue().isPresent() && defaultValue.get() != bool.get();
        this.resetButton.setY(y);
        this.buttonWidget.active = isEditable();
        this.buttonWidget.setY(y);
        this.buttonWidget.setMessage(getYesNoText(bool.get()));
        Component displayedFieldName = getDisplayedFieldName();
        if (Minecraft.getInstance().font.isBidirectional()) {
            graphics.drawString(Minecraft.getInstance().font, displayedFieldName.getVisualOrderText(), window.getGuiScaledWidth() - x - Minecraft.getInstance().font.width(displayedFieldName), y + 6, 16777215);
            this.resetButton.setX(x);
            this.buttonWidget.setX(x + resetButton.getWidth() + 2);
        } else {
            graphics.drawString(Minecraft.getInstance().font, displayedFieldName.getVisualOrderText(), x, y + 6, getPreferredTextColor());
            this.resetButton.setX(x + entryWidth - resetButton.getWidth());
            this.buttonWidget.setX(x + entryWidth - 150);
        }
        this.buttonWidget.setWidth(150 - resetButton.getWidth() - 2);
        resetButton.render(graphics, mouseX, mouseY, delta);
        buttonWidget.render(graphics, mouseX, mouseY, delta);
    }
    
    public Component getYesNoText(boolean bool) {
        return Component.translatable("text.cloth-config.boolean.value." + bool);
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
