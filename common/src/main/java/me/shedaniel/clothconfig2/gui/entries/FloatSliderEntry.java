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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class FloatSliderEntry extends TooltipListEntry<Float> {
    
    protected Slider sliderWidget;
    protected Button resetButton;
    protected Float value;
    protected final Float orginial;
    private float minimum, maximum;
    private int precision;
    private final Supplier<Float> defaultValue;
    private Function<Float, Component> textGetter = value -> Component.literal(String.format("Value: %." + precision + "f", value));
    private final List<AbstractWidget> widgets;
    
    @ApiStatus.Internal
    @Deprecated
    public FloatSliderEntry(Component fieldName, float minimum, float maximum, float value, int precision, Component resetButtonKey, Supplier<Float> defaultValue, Consumer<Float> saveConsumer) {
        this(fieldName, minimum, maximum, value, precision, resetButtonKey, defaultValue, saveConsumer, null);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public FloatSliderEntry(Component fieldName, float minimum, float maximum, float value, int precision, Component resetButtonKey, Supplier<Float> defaultValue, Consumer<Float> saveConsumer, Supplier<Optional<Component[]>> tooltipSupplier) {
        this(fieldName, minimum, maximum, value, precision, resetButtonKey, defaultValue, saveConsumer, tooltipSupplier, false);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public FloatSliderEntry(Component fieldName, float minimum, float maximum, float value, int precision, Component resetButtonKey, Supplier<Float> defaultValue, Consumer<Float> saveConsumer, Supplier<Optional<Component[]>> tooltipSupplier, boolean requiresRestart) {
        super(fieldName, tooltipSupplier, requiresRestart);
        this.orginial = value;
        this.defaultValue = defaultValue;
        this.value = value;
        this.saveCallback = saveConsumer;
        this.maximum = maximum;
        this.minimum = minimum;
        this.precision = precision;
        this.sliderWidget = new Slider(0, 0, 152, 20, ((double) this.value - minimum) / Math.abs(maximum - minimum));
        this.resetButton = new Button(0, 0, Minecraft.getInstance().font.width(resetButtonKey) + 6, 20, resetButtonKey, widget -> {
            setValue(defaultValue.get());
        });
        this.sliderWidget.setMessage(textGetter.apply(FloatSliderEntry.this.value));
        this.widgets = Lists.newArrayList(sliderWidget, resetButton);
    }
    
    public Function<Float, Component> getTextGetter() {
        return textGetter;
    }
    
    public FloatSliderEntry setTextGetter(Function<Float, Component> textGetter) {
        this.textGetter = textGetter;
        this.sliderWidget.setMessage(textGetter.apply(FloatSliderEntry.this.value));
        return this;
    }
    
    @Override
    public Float getValue() {
        return value;
    }
    
    @Deprecated
    public void setValue(float value) {
        sliderWidget.setValue((Mth.clamp(value, minimum, maximum) - minimum) / (double) Math.abs(maximum - minimum));
        this.value = roundNumber(Math.min(Math.max(value, minimum), maximum));
        sliderWidget.updateMessage();
    }
    
    private float roundNumber(double value) {
        return (float) (Math.round(value * Math.pow(10, this.precision)) / Math.pow(10, this.precision));
    }
    
    @Override
    public boolean isEdited() {
        return super.isEdited() || getValue() != orginial;
    }
    
    @Override
    public Optional<Float> getDefaultValue() {
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
    
    public FloatSliderEntry setMaximum(float maximum) {
        this.maximum = maximum;
        return this;
    }
    
    public FloatSliderEntry setMinimum(float minimum) {
        this.minimum = minimum;
        return this;
    }
    
    public FloatSliderEntry setPrecision(int precision) {
        this.precision = precision;
        return this;
    }
    
    @Override
    public void render(PoseStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
        Window window = Minecraft.getInstance().getWindow();
        this.resetButton.active = isEditable() && getDefaultValue().isPresent() && defaultValue.get() != value;
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
            setMessage(textGetter.apply(FloatSliderEntry.this.value));
        }
        
        @Override
        protected void applyValue() {
            FloatSliderEntry.this.value = roundNumber((float) (minimum + Math.abs(maximum - minimum) * value));
        }
        
        @Override
        public boolean keyPressed(int int_1, int int_2, int int_3) {
            if (!isEditable())
                return false;
            return super.keyPressed(int_1, int_2, int_3);
        }
        
        @Override
        public boolean mouseDragged(double double_1, double double_2, int int_1, double double_3, double double_4) {
            if (!isEditable())
                return false;
            return super.mouseDragged(double_1, double_2, int_1, double_3, double_4);
        }
        
        public double getProgress() {
            return value;
        }
        
        public void setProgress(double progress) {
            this.value = progress;
        }
        
        public void setValue(double value) {
            this.value = value;
        }
    }
    
}
