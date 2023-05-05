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

import com.google.common.base.MoreObjects;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.clothconfig2.api.dependencies.Dependency;
import me.shedaniel.clothconfig2.gui.AbstractConfigScreen;
import me.shedaniel.clothconfig2.gui.widget.DynamicElementListWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public abstract class AbstractConfigEntry<T> extends DynamicElementListWidget.ElementEntry<AbstractConfigEntry<T>> implements ConfigEntry<T>, ReferenceProvider<T> {
    private AbstractConfigScreen screen;
    private Supplier<Optional<Component>> errorSupplier;
    @Nullable
    private List<ReferenceProvider<?>> referencableEntries = null;
    @Nullable
    protected Consumer<T> saveCallback;
    private int cacheFieldNameHash = -1;
    private List<String> cachedTags = null;
    private Iterable<String> additionalSearchTags = null;
    
    @Nullable
    private Dependency enableIfDependency = null;
    private Dependency showIfDependency = null;
    
    public final void setReferenceProviderEntries(@Nullable List<ReferenceProvider<?>> referencableEntries) {
        this.referencableEntries = referencableEntries;
    }
    
    public void requestReferenceRebuilding() {
        AbstractConfigScreen configScreen = getConfigScreen();
        if (configScreen instanceof ReferenceBuildingConfigScreen) {
            ((ReferenceBuildingConfigScreen) configScreen).requestReferenceRebuilding();
        }
    }
    
    @Override
    public @NotNull AbstractConfigEntry<T> provideReferenceEntry() {
        return this;
    }
    
    @Nullable
    @ApiStatus.Internal
    public final List<ReferenceProvider<?>> getReferenceProviderEntries() {
        return referencableEntries;
    }
    
    public abstract boolean isRequiresRestart();
    
    public abstract void setRequiresRestart(boolean requiresRestart);
    
    public Component getDisplayedFieldName() {
        MutableComponent text = getFieldName().copy();
        boolean hasError = getConfigError().isPresent();
        boolean isEdited = isEdited();
        if (hasError)
            text = text.withStyle(ChatFormatting.RED);
        if (isEdited)
            text = text.withStyle(ChatFormatting.ITALIC);
        if (!hasError && !isEdited)
            text = text.withStyle(ChatFormatting.GRAY);
        if (!isEnabled())
            text = text.withStyle(ChatFormatting.DARK_GRAY);
        return text;
    }
    
    /**
     * True if no "enable if" dependency is set, otherwise true if the dependency conditions are currently met.
     * <p>
     * Never true if {@link #isShown()} is false.
     * 
     * @return whether the config entry is enabled
     * @see #isShown() 
     */
    public boolean isEnabled() {
        return isShown() && (enableIfDependency == null || enableIfDependency.check());
    }
    
    /**
     * True if no "show if" dependency is set, otherwise true if the dependency conditions are currently met.
     *
     * @return whether the config entry is shown in menus
     * @see #isEnabled()
     */
    public boolean isShown() {
        return showIfDependency == null || showIfDependency.check();
    }
    
    @Override
    public void setEnableIfDependency(@Nullable Dependency dependency) {
        this.enableIfDependency = dependency;
    }
    
    public @Nullable Dependency getEnableIfDependency() {
        return enableIfDependency;
    }
    
    @Override
    public void setShowIfDependency(@Nullable Dependency dependency) {
        this.showIfDependency = dependency;
    }
    
    public @Nullable Dependency getShowIfDependency() {
        return showIfDependency;
    }
    
    public Iterator<String> getSearchTags() {
        String s = getFieldName().getString();
        if (s.isEmpty()) {
            cacheFieldNameHash = -1;
            cachedTags = null;
            return MoreObjects.firstNonNull(additionalSearchTags, Collections.<String>emptyList()).iterator();
        }
        if (s.hashCode() != cacheFieldNameHash) {
            cacheFieldNameHash = s.hashCode();
            cachedTags = Lists.newArrayList(s.split(" "));
        }
        return Iterators.concat(cachedTags.iterator(), MoreObjects.firstNonNull(additionalSearchTags, Collections.<String>emptyList()).iterator());
    }
    
    public void appendSearchTags(Iterable<String> tags) {
        if (this.additionalSearchTags == null) {
            this.additionalSearchTags = tags;
        } else {
            this.additionalSearchTags = Iterables.concat(this.additionalSearchTags, tags);
        }
    }
    
    public abstract T getValue();
    
    public final Optional<Component> getConfigError() {
        if (errorSupplier != null && errorSupplier.get().isPresent())
            return errorSupplier.get();
        return getError();
    }
    
    public void lateRender(PoseStack matrices, int mouseX, int mouseY, float delta) {}
    
    public void setErrorSupplier(Supplier<Optional<Component>> errorSupplier) {
        this.errorSupplier = errorSupplier;
    }
    
    public Optional<Component> getError() {
        return Optional.empty();
    }
    
    public abstract Optional<T> getDefaultValue();
    
    @Nullable
    public final AbstractConfigScreen getConfigScreen() {
        return screen;
    }
    
    public final void addTooltip(@NotNull Tooltip tooltip) {
        screen.addTooltip(tooltip);
    }
    
    public void updateSelected(boolean isSelected) {}
    
    @ApiStatus.Internal
    public final void setScreen(AbstractConfigScreen screen) {
        this.screen = screen;
    }
    
    public void save() {
        if (this.saveCallback != null) {
            this.saveCallback.accept(getValue());
        }
    }
    
    public boolean isEdited() {
        return getConfigError().isPresent();
    }
    
    @Override
    public int getItemHeight() {
        return 24;
    }
    
    public int getInitialReferenceOffset() {
        return 0;
    }
}
