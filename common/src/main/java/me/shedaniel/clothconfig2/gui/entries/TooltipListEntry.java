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

import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.Tooltip;
import me.shedaniel.math.Point;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public abstract class TooltipListEntry<T> extends AbstractConfigListEntry<T> {
    @Nullable private Supplier<Optional<Component[]>> tooltipSupplier;
    
    @ApiStatus.Internal
    @Deprecated
    public TooltipListEntry(Component fieldName, @Nullable Supplier<Optional<Component[]>> tooltipSupplier) {
        this(fieldName, tooltipSupplier, false);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public TooltipListEntry(Component fieldName, @Nullable Supplier<Optional<Component[]>> tooltipSupplier, boolean requiresRestart) {
        super(fieldName, requiresRestart);
        this.tooltipSupplier = tooltipSupplier;
    }
    
    @Override
    public void render(PoseStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
        if (isMouseInside(mouseX, mouseY, x, y, entryWidth, entryHeight)) {
            Optional<Component[]> tooltip = getTooltip(mouseX, mouseY);
            if (tooltip.isPresent() && tooltip.get().length > 0)
                addTooltip(Tooltip.of(new Point(mouseX, mouseY), postProcessTooltip(tooltip.get())));
        }
    }
    
    private FormattedCharSequence[] postProcessTooltip(Component[] tooltip) {
        return Arrays.stream(tooltip).flatMap(component -> Minecraft.getInstance().font.split(component, getConfigScreen().width).stream())
                .toArray(FormattedCharSequence[]::new);
    }
    
    public Optional<Component[]> getTooltip() {
        Optional<Component[]> tooltip = tooltipSupplier == null ? Optional.empty() : tooltipSupplier.get();
        Optional<Component> dependencyTooltip = getDependencyTooltip();
        
        if (tooltip.isEmpty() && dependencyTooltip.isEmpty())
            return Optional.empty();
    
        // Concatenate the two tooltips
        int len = tooltip.map(components -> components.length).orElse(0);
        if (dependencyTooltip.isPresent()) len++;
        int finalLen = len;
        
        Component[] newArray = new Component[len];
        tooltip.ifPresent(components -> System.arraycopy(components, 0, newArray, 0, components.length));
        dependencyTooltip.ifPresent(component -> newArray[finalLen-1] = component);
        
        return Optional.of(newArray);
        
        // FIXME: streams would be cleaner if I could figure out how to replace the above
//        Object[] array = Stream.concat(
//                tooltip.stream().map(Arrays::stream),
//                dependsOnTooltip.stream()
//        ).toArray();
//        
//        return array.length == 0 ? Optional.empty() : Optional.of((Component[]) array);
    }
    
    public Optional<Component[]> getTooltip(int mouseX, int mouseY) {
        return getTooltip();
    }
    
    public Optional<Component> getDependencyTooltip() {
        // TODO consider showing the tooltip when hasDependency() instead?
        if (dependencySatisfied())
            return Optional.empty();
    
        Component value = getDependentValue()
                ? Component.translatable("text.cloth-config.dependencies.enabled")
                : Component.translatable("text.cloth-config.dependencies.disabled");
        
        return Optional.of(Component.translatable("text.cloth-config.dependencies.warning",
                            getDependency().getDisplayedFieldName(), value));
    }
    
    @Nullable
    public Supplier<Optional<Component[]>> getTooltipSupplier() {
        return tooltipSupplier;
    }
    
    public void setTooltipSupplier(@Nullable Supplier<Optional<Component[]>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
    }
    
}
