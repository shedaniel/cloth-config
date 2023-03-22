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
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class IntegerSliderEntry extends TooltipListEntry<Integer> {
    
    protected Slider sliderWidget;
    protected Button resetButton;
    protected AtomicInteger value;
    protected final long orginial;
    private int minimum, maximum;
    private final Supplier<Integer> defaultValue;
    private Function<Integer, Component> textGetter = integer -> Component.literal(String.format("Value: %d", integer));
    private final List<AbstractWidget> widgets;
    
    @ApiStatus.Internal
    @Deprecated
    public IntegerSliderEntry(Component fieldName, int minimum, int maximum, int value, Component resetButtonKey, Supplier<Integer> defaultValue, Consumer<Integer> saveConsumer) {
        this(fieldName, minimum, maximum, value, resetButtonKey, defaultValue, saveConsumer, null);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public IntegerSliderEntry(Component fieldName, int minimum, int maximum, int value, Component resetButtonKey, Supplier<Integer> defaultValue, Consumer<Integer> saveConsumer, Supplier<Optional<Component[]>> tooltipSupplier) {
        this(fieldName, minimum, maximum, value, resetButtonKey, defaultValue, saveConsumer, tooltipSupplier, false);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public IntegerSliderEntry(Component fieldName, int minimum, int maximum, int value, Component resetButtonKey, Supplier<Integer> defaultValue, Consumer<Integer> saveConsumer, Supplier<Optional<Component[]>> tooltipSupplier, boolean requiresRestart) {
        super(fieldName, tooltipSupplier, requiresRestart);
        this.orginial = value;
        this.defaultValue = defaultValue;
        this.value = new AtomicInteger(value);
        this.saveCallback = saveConsumer;
        this.maximum = maximum;
        this.minimum = minimum;
        this.sliderWidget = new Slider(0, 0, 152, 20, ((double) this.value.get() - minimum) / Math.abs(maximum - minimum));
        this.resetButton = new Button(0, 0, Minecraft.getInstance().font.width(resetButtonKey) + 6, 20, resetButtonKey, widget -> {
            setValue(defaultValue.get());
        });
        this.sliderWidget.setMessage(textGetter.apply(IntegerSliderEntry.this.value.get()));
        this.widgets = Lists.newArrayList(sliderWidget, resetButton);
    }
    
    public Function<Integer, Component> getTextGetter() {
        return textGetter;
    }
    
    public IntegerSliderEntry setTextGetter(Function<Integer, Component> textGetter) {
        this.textGetter = textGetter;
        this.sliderWidget.setMessage(textGetter.apply(IntegerSliderEntry.this.value.get()));
        return this;
    }
    
    @Override
    public Integer getValue() {
        return value.get();
    }
    
    @Deprecated
    public void setValue(int value) {
        sliderWidget.setValue((Mth.clamp(value, minimum, maximum) - minimum) / (double) Math.abs(maximum - minimum));
        this.value.set(Math.min(Math.max(value, minimum), maximum));
        sliderWidget.updateMessage();
    }
    
    @Override
    public boolean isEdited() {
        return super.isEdited() || getValue() != orginial;
    }
    
    @Override
    public Optional<Integer> getDefaultValue() {
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
    
    public IntegerSliderEntry setMaximum(int maximum) {
        this.maximum = maximum;
        return this;
    }
    
    public IntegerSliderEntry setMinimum(int minimum) {
        this.minimum = minimum;
        return this;
    }
    
    @Override
    public void render(PoseStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
        Window window = Minecraft.getInstance().getWindow();
        this.resetButton.active = isEditable() && getDefaultValue().isPresent() && defaultValue.get() != value.get();
        this.resetButton.y = y;
        this.sliderWidget.active = isEditable();
        this.sliderWidget.y = y;
        Component displayedFieldName = getDisplayedFieldName();
        if (Minecraft.getInstance().font.isBidirectional()) {
            Minecraft.getInstance().font.drawShadow(matrices, displayedFieldName.getVisualOrderText(), window.getGuiScaledWidth() - x - Minecraft.getInstance().font.width(displayedFieldName), y + 6, getPreferredTextColor());
            this.resetButton.x = x;
            this.sliderWidget.x = x + resetButton.getWidth() + 1;
        } else {
            Minecraft.getInstance().font.drawShadow(matrices, displayedFieldName.getVisualOrderText(), x, y + 6, getPreferredTextColor());
            this.resetButton.x = x + entryWidth - resetButton.getWidth();
            this.sliderWidget.x = x + entryWidth - 150;
        }
        this.sliderWidget.setWidth(150 - resetButton.getWidth() - 2);
        resetButton.render(matrices, mouseX, mouseY, delta);
        sliderWidget.render(matrices, mouseX, mouseY, delta);
    }
    
    private class Slider extends AbstractSliderButton {
        protected Slider(int int_1, int int_2, int int_3, int int_4, double double_1) {
            super(int_1, int_2, int_3, int_4, Component.empty(), double_1);
        }
        
        @Override
        public void updateMessage() {
            setMessage(textGetter.apply(IntegerSliderEntry.this.value.get()));
        }
        
        @Override
        protected void applyValue() {
            IntegerSliderEntry.this.value.set((int) (minimum + Math.abs(maximum - minimum) * value));
        }
        
        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (!isEditable())
                return false;
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
        
        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            if (!isEditable())
                return false;
            return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
        
        public double getProgress() {
            return value;
        }
        
        public void setProgress(double integer) {
            this.value = integer;
        }
        
        public void setValue(double integer) {
            this.value = integer;
        }
    }
    
}
