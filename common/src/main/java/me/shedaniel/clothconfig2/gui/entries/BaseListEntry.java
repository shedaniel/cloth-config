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
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.clothconfig2.api.Expandable;
import me.shedaniel.math.Rectangle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @param <T>    the configuration object type
 * @param <C>    the cell type
 * @param <SELF> the "curiously recurring template pattern" type parameter
 * @implNote See <a href="https://stackoverflow.com/questions/7354740/is-there-a-way-to-refer-to-the-current-type-with-a-type-variable">Is there a way to refer to the current type with a type variable?</a> on Stack Overflow.
 */
@Environment(EnvType.CLIENT)
public abstract class BaseListEntry<T, C extends BaseListCell, SELF extends BaseListEntry<T, C, SELF>> extends TooltipListEntry<List<T>> implements Expandable {
    
    protected static final ResourceLocation CONFIG_TEX = new ResourceLocation("cloth-config2", "textures/gui/cloth_config.png");
    @NotNull protected final List<C> cells;
    @NotNull protected final List<GuiEventListener> widgets;
    protected boolean expanded;
    protected boolean deleteButtonEnabled;
    protected boolean insertInFront;
    @Nullable protected Consumer<List<T>> saveConsumer;
    protected ListLabelWidget labelWidget;
    protected AbstractWidget resetWidget;
    @NotNull protected Function<SELF, C> createNewInstance;
    @NotNull protected Supplier<List<T>> defaultValue;
    @Nullable
    protected Component addTooltip = new TranslatableComponent("text.cloth-config.list.add"), removeTooltip = new TranslatableComponent("text.cloth-config.list.remove");
    
    @ApiStatus.Internal
    public BaseListEntry(@NotNull Component fieldName, @Nullable Supplier<Optional<Component[]>> tooltipSupplier, @Nullable Supplier<List<T>> defaultValue, @NotNull Function<SELF, C> createNewInstance, @Nullable Consumer<List<T>> saveConsumer, Component resetButtonKey) {
        this(fieldName, tooltipSupplier, defaultValue, createNewInstance, saveConsumer, resetButtonKey, false);
    }
    
    @ApiStatus.Internal
    public BaseListEntry(@NotNull Component fieldName, @Nullable Supplier<Optional<Component[]>> tooltipSupplier, @Nullable Supplier<List<T>> defaultValue, @NotNull Function<SELF, C> createNewInstance, @Nullable Consumer<List<T>> saveConsumer, Component resetButtonKey, boolean requiresRestart) {
        this(fieldName, tooltipSupplier, defaultValue, createNewInstance, saveConsumer, resetButtonKey, requiresRestart, true, true);
    }
    
    @ApiStatus.Internal
    public BaseListEntry(@NotNull Component fieldName, @Nullable Supplier<Optional<Component[]>> tooltipSupplier, @Nullable Supplier<List<T>> defaultValue, @NotNull Function<SELF, C> createNewInstance, @Nullable Consumer<List<T>> saveConsumer, Component resetButtonKey, boolean requiresRestart, boolean deleteButtonEnabled, boolean insertInFront) {
        super(fieldName, tooltipSupplier, requiresRestart);
        this.deleteButtonEnabled = deleteButtonEnabled;
        this.insertInFront = insertInFront;
        this.cells = Lists.newArrayList();
        this.labelWidget = new ListLabelWidget();
        this.widgets = Lists.newArrayList(labelWidget);
        this.resetWidget = new Button(0, 0, Minecraft.getInstance().font.width(resetButtonKey) + 6, 20, resetButtonKey, widget -> {
            widgets.removeAll(cells);
            for (C cell : cells) {
                cell.onDelete();
            }
            cells.clear();
            defaultValue.get().stream().map(this::getFromValue).forEach(cells::add);
            for (C cell : cells) {
                cell.onAdd();
            }
            widgets.addAll(cells);
        });
        this.widgets.add(resetWidget);
        this.saveConsumer = saveConsumer;
        this.createNewInstance = createNewInstance;
        this.defaultValue = defaultValue;
    }
    
    @Override
    public boolean isExpanded() {
        return expanded;
    }
    
    @Override
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
    
    @Override
    public boolean isEdited() {
        if (super.isEdited()) return true;
        return cells.stream().anyMatch(BaseListCell::isEdited);
    }
    
    public boolean isMatchDefault() {
        Optional<List<T>> defaultValueOptional = getDefaultValue();
        if (defaultValueOptional.isPresent()) {
            List<T> value = getValue();
            List<T> defaultValue = defaultValueOptional.get();
            if (value.size() != defaultValue.size()) return false;
            for (int i = 0; i < value.size(); i++) {
                if (!Objects.equals(value.get(i), defaultValue.get(i)))
                    return false;
            }
            return true;
        }
        return false;
    }
    
    @Override
    public boolean isRequiresRestart() {
        return cells.stream().anyMatch(BaseListCell::isRequiresRestart);
    }
    
    @Override
    public void setRequiresRestart(boolean requiresRestart) {
    }
    
    public abstract SELF self();
    
    public boolean isDeleteButtonEnabled() {
        return deleteButtonEnabled;
    }
    
    protected abstract C getFromValue(T value);
    
    @NotNull
    public Function<SELF, C> getCreateNewInstance() {
        return createNewInstance;
    }
    
    public void setCreateNewInstance(@NotNull Function<SELF, C> createNewInstance) {
        this.createNewInstance = createNewInstance;
    }
    
    @Nullable
    public Component getAddTooltip() {
        return addTooltip;
    }
    
    public void setAddTooltip(@Nullable Component addTooltip) {
        this.addTooltip = addTooltip;
    }
    
    @Nullable
    public Component getRemoveTooltip() {
        return removeTooltip;
    }
    
    public void setRemoveTooltip(@Nullable Component removeTooltip) {
        this.removeTooltip = removeTooltip;
    }
    
    @Override
    public Optional<List<T>> getDefaultValue() {
        if (defaultValue == null) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(defaultValue.get());
        }
    }
    
    @Override
    public int getItemHeight() {
        if (expanded) {
            int i = 24;
            for (BaseListCell entry : cells)
                i += entry.getCellHeight();
            return i;
        }
        return 24;
    }
    
    @Override
    public List<? extends GuiEventListener> children() {
        if (!expanded) {
            List<GuiEventListener> elements = new ArrayList<>(widgets);
            elements.removeAll(cells);
            return elements;
        }
        return widgets;
    }
    
    @Override
    public Optional<Component> getError() {
        List<Component> errors = cells.stream().map(C::getConfigError).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
        
        if (errors.size() > 1)
            return Optional.of(new TranslatableComponent("text.cloth-config.multi_error"));
        else
            return errors.stream().findFirst();
    }
    
    @Override
    public void save() {
        if (saveConsumer != null)
            saveConsumer.accept(getValue());
    }
    
    @Override
    public Rectangle getEntryArea(int x, int y, int entryWidth, int entryHeight) {
        labelWidget.rectangle.x = x - 15;
        labelWidget.rectangle.y = y;
        labelWidget.rectangle.width = entryWidth + 15;
        labelWidget.rectangle.height = 24;
        return new Rectangle(getParent().left, y, getParent().right - getParent().left, 20);
    }
    
    protected boolean isInsideCreateNew(double mouseX, double mouseY) {
        return mouseX >= labelWidget.rectangle.x + 12 && mouseY >= labelWidget.rectangle.y + 3 && mouseX <= labelWidget.rectangle.x + 12 + 11 && mouseY <= labelWidget.rectangle.y + 3 + 11;
    }
    
    protected boolean isInsideDelete(double mouseX, double mouseY) {
        return isDeleteButtonEnabled() && mouseX >= labelWidget.rectangle.x + 25 && mouseY >= labelWidget.rectangle.y + 3 && mouseX <= labelWidget.rectangle.x + 25 + 11 && mouseY <= labelWidget.rectangle.y + 3 + 11;
    }
    
    @Override
    public Optional<Component[]> getTooltip(int mouseX, int mouseY) {
        if (addTooltip != null && isInsideCreateNew(mouseX, mouseY))
            return Optional.of(new Component[]{addTooltip});
        if (removeTooltip != null && isInsideDelete(mouseX, mouseY))
            return Optional.of(new Component[]{removeTooltip});
        if (getTooltipSupplier() != null)
            return getTooltipSupplier().get();
        return Optional.empty();
    }
    
    @Override
    public void render(PoseStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
        Minecraft.getInstance().getTextureManager().bind(CONFIG_TEX);
        Lighting.turnOff();
        RenderSystem.color4f(1, 1, 1, 1);
        BaseListCell focused = !expanded || getFocused() == null || !(getFocused() instanceof BaseListCell) ? null : (BaseListCell) getFocused();
        boolean insideCreateNew = isInsideCreateNew(mouseX, mouseY);
        boolean insideDelete = isInsideDelete(mouseX, mouseY);
        blit(matrices, x - 15, y + 5, 24 + 9, (labelWidget.rectangle.contains(mouseX, mouseY) && !insideCreateNew && !insideDelete ? 18 : 0) + (expanded ? 9 : 0), 9, 9);
        blit(matrices, x - 15 + 13, y + 5, 24 + 18, insideCreateNew ? 9 : 0, 9, 9);
        if (isDeleteButtonEnabled())
            blit(matrices, x - 15 + 26, y + 5, 24 + 27, focused == null ? 0 : insideDelete ? 18 : 9, 9, 9);
        resetWidget.x = x + entryWidth - resetWidget.getWidth();
        resetWidget.y = y;
        resetWidget.active = isEditable() && getDefaultValue().isPresent() && !isMatchDefault();
        resetWidget.render(matrices, mouseX, mouseY, delta);
        Minecraft.getInstance().font.drawShadow(matrices, getDisplayedFieldName().getVisualOrderText(), isDeleteButtonEnabled() ? x + 24 : x + 24 - 9, y + 6, labelWidget.rectangle.contains(mouseX, mouseY) && !resetWidget.isMouseOver(mouseX, mouseY) && !insideDelete && !insideCreateNew ? 0xffe6fe16 : getPreferredTextColor());
        if (expanded) {
            int yy = y + 24;
            for (BaseListCell cell : cells) {
                cell.render(matrices, -1, yy, x + 14, entryWidth - 14, cell.getCellHeight(), mouseX, mouseY, getParent().getFocused() != null && getParent().getFocused().equals(this) && getFocused() != null && getFocused().equals(cell), delta);
                yy += cell.getCellHeight();
            }
        }
    }
    
    @Override
    public void updateSelected(boolean isSelected) {
        for (C cell : cells) {
            cell.updateSelected(isSelected && getFocused() == cell && expanded);
        }
    }
    
    @Override
    public int getInitialReferenceOffset() {
        return 24;
    }
    
    public boolean insertInFront() {
        return insertInFront;
    }
    
    public class ListLabelWidget implements GuiEventListener {
        protected Rectangle rectangle = new Rectangle();
        
        @Override
        public boolean mouseClicked(double double_1, double double_2, int int_1) {
            if (resetWidget.isMouseOver(double_1, double_2)) {
                return false;
            } else if (isInsideCreateNew(double_1, double_2)) {
                expanded = true;
                C cell;
                if (insertInFront()) {
                    cells.add(0, cell = createNewInstance.apply(BaseListEntry.this.self()));
                    widgets.add(0, cell);
                } else {
                    cells.add(cell = createNewInstance.apply(BaseListEntry.this.self()));
                    widgets.add(cell);
                }
                cell.onAdd();
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            } else if (isDeleteButtonEnabled() && isInsideDelete(double_1, double_2)) {
                GuiEventListener focused = getFocused();
                if (expanded && focused instanceof BaseListCell) {
                    ((BaseListCell) focused).onDelete();
                    //noinspection SuspiciousMethodCalls
                    cells.remove(focused);
                    widgets.remove(focused);
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                }
                return true;
            } else if (rectangle.contains(double_1, double_2)) {
                expanded = !expanded;
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
            return false;
        }
    }
    
}
