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

import me.shedaniel.clothconfig2.gui.widget.ManagedSliderWidget;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Optional;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A config entry containing a single bounded value with an
 * {@link ManagedSliderWidget} for user display and input.
 *
 * Any bounded value that can be respresented as a {@code double} can be
 * managed by subclassing this class and implementing the
 * {@link #getValueForSlider()} and {@link #setValueFromSlider(double)}
 * methods.
 *
 * @param <T>    the configuration object type
 * @param <SELF> the "curiously recurring template pattern" type parameter
 * @see TooltipListEntry
 */
@Environment(EnvType.CLIENT)
public abstract class AbstractSliderEntry<T, SELF extends AbstractSliderEntry<T, SELF>> extends TooltipListEntry<T> {
    protected ManagedSliderWidget sliderWidget;
    protected Button resetButton;
    protected final T original;
    protected T minimum, maximum;
    private final Consumer<T> saveConsumer;
    private final Supplier<T> defaultValue;
    private Function<T, Component> textGetter = integer -> new TextComponent(String.format("Value: %d", integer));
    private final List<GuiEventListener> widgets;

    @ApiStatus.Internal
    public AbstractSliderEntry(Component fieldName, T minimum, T maximum, T value, Component resetButtonKey, Supplier<T> defaultValue, Consumer<T> saveConsumer, Supplier<Optional<Component[]>> tooltipSupplier, boolean requiresRestart) {
        super(fieldName, tooltipSupplier, requiresRestart);
        this.original = value;
        this.defaultValue = defaultValue;
        this.saveConsumer = saveConsumer;
        this.maximum = maximum;
        this.minimum = minimum;
        this.sliderWidget = new ManagedSliderWidget(0, 0, 152, 20, AbstractSliderEntry.this.sliderContext());
        this.resetButton = new Button(0, 0, Minecraft.getInstance().font.width(resetButtonKey) + 6, 20, resetButtonKey, widget -> {
            setValue(defaultValue.get());
            syncValueToSlider();
        });
        this.widgets = Lists.newArrayList(sliderWidget, resetButton);
    }

    abstract protected SELF self();

    protected abstract void setValue(T value);

    protected abstract double getValueForSlider();

    protected abstract void setValueFromSlider(double value);

    protected void syncValueToSlider() {
        sliderWidget.syncValueFromContext();
    }

    protected Component getValueForMessage() {
        if (textGetter == null) {
            return null;
        } else {
            return textGetter.apply(getValue());
        }
    }

    @Override
    public void save() {
        if (saveConsumer != null)
            saveConsumer.accept(getValue());
    }

    public Function<T, Component> getTextGetter() {
        return textGetter;
    }

    public SELF setTextGetter(Function<T, Component> textGetter) {
        this.textGetter = textGetter;
        this.sliderWidget.updateMessage();
        return self();
    }

    @Override
    public boolean isEdited() {
        return super.isEdited() || !Objects.equals(getValue(), original);
    }

    @Override
    public Optional<T> getDefaultValue() {
        return defaultValue == null ? Optional.empty() : Optional.ofNullable(defaultValue.get());
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return widgets;
    }

    @Override
    public void render(PoseStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
        Window window = Minecraft.getInstance().getWindow();
        this.resetButton.active = isEditable() && getDefaultValue().isPresent() && !Objects.equals(defaultValue.get(), getValue());
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

    private ManagedSliderWidget.Context sliderContext() {
        return new ManagedSliderWidget.Context() {
            public Component message() {
                return getValueForMessage();
            }

            public double value() {
                return getValueForSlider();
            }

            public void valueApplied(double value) {
                setValueFromSlider(value);
            }

            public boolean editable() {
                return isEditable();
            }
        };
    }
}
