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

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import me.shedaniel.clothconfig2.CCTextures;
import me.shedaniel.clothconfig2.api.AbstractConfigEntry;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.Expandable;
import me.shedaniel.clothconfig2.gui.widget.DynamicEntryListWidget;
import me.shedaniel.math.Rectangle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Environment(EnvType.CLIENT)
public class SubCategoryListEntry extends TooltipListEntry<List<AbstractConfigListEntry>> implements Expandable {
    private final List<AbstractConfigListEntry> entries;
    private final CategoryLabelWidget widget;
    private final List<Object> children; // GuiEventListener & NarratableEntry
    private boolean expanded;
    
    @Deprecated
    public SubCategoryListEntry(Component categoryName, List<AbstractConfigListEntry> entries, boolean defaultExpanded) {
        super(categoryName, null);
        this.entries = entries;
        this.expanded = defaultExpanded;
        this.widget = new CategoryLabelWidget();
        this.children = Lists.newArrayList(widget);
        this.children.addAll(entries);
        this.setReferenceProviderEntries((List) entries);
    }
    
    @Override
    public Iterator<String> getSearchTags() {
        return Iterators.concat(super.getSearchTags(), Iterators.concat(entries.stream().<Iterator<String>>map(AbstractConfigEntry::getSearchTags).iterator()));
    }
    
    @Override
    public boolean isExpanded() {
        return expanded && isEnabled();
    }
    
    @Override
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
    
    @Override
    public boolean isRequiresRestart() {
        for (AbstractConfigListEntry entry : entries)
            if (entry.isRequiresRestart())
                return true;
        return false;
    }
    
    @Override
    public void setRequiresRestart(boolean requiresRestart) {
        
    }
    
    public Component getCategoryName() {
        return getFieldName();
    }
    
    @Override
    public List<AbstractConfigListEntry> getValue() {
        return entries;
    }
    
    public List<AbstractConfigListEntry> filteredEntries() {
        return new AbstractList<AbstractConfigListEntry>() {
            @Override
            public Iterator<AbstractConfigListEntry> iterator() {
                return Iterators.filter(entries.iterator(), entry -> {
                    return entry.isDisplayed() && getConfigScreen() != null && getConfigScreen().matchesSearch(entry.getSearchTags());
                });
            }
            
            @Override
            public AbstractConfigListEntry get(int index) {
                return Iterators.get(iterator(), index);
            }
            
            @Override
            public int size() {
                return Iterators.size(iterator());
            }
        };
    }
    
    @Override
    public Optional<List<AbstractConfigListEntry>> getDefaultValue() {
        return Optional.empty();
    }
    
    @Override
    public void render(GuiGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.render(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
        boolean insideWidget = widget.rectangle.contains(mouseX, mouseY);
        graphics.blit(RenderType::guiTextured, CCTextures.CONFIG, x - 15, y + 5, 24, (isEnabled() ? (insideWidget ? 18 : 0) : 36) + (isExpanded() ? 9 : 0), 9, 9, 256, 256);
        graphics.drawString(Minecraft.getInstance().font, getDisplayedFieldName().getVisualOrderText(), x, y + 6, insideWidget ? 0xffe6fe16 : 0xffffffff);
        for (AbstractConfigListEntry<?> entry : entries) {
            entry.setParent((DynamicEntryListWidget) getParent());
            entry.setScreen(getConfigScreen());
        }
        if (isExpanded()) {
            int yy = y + 24;
            for (AbstractConfigListEntry<?> entry : filteredEntries()) {
                entry.render(graphics, -1, yy, x + 14, entryWidth - 14, entry.getItemHeight(), mouseX, mouseY, isHovered && getFocused() == entry, delta);
                yy += entry.getItemHeight();
            }
        }
    }
    
    @Override
    public void tick() {
        super.tick();
        for (AbstractConfigListEntry<?> entry : entries) {
           entry.tick(); 
        }
    }
    
    @Override
    public void updateSelected(boolean isSelected) {
        for (AbstractConfigListEntry<?> entry : entries) {
            entry.updateSelected(isExpanded() && isSelected && getFocused() == entry && entry.isDisplayed() && getConfigScreen().matchesSearch(entry.getSearchTags()));
        }
    }
    
    @Override
    public void setFocused(@Nullable GuiEventListener guiEventListener) {
        super.setFocused(guiEventListener);
//        if (guiEventListener != null && children().contains(guiEventListener) && guiEventListener instanceof AbstractConfigListEntry) {
//            // traverse up to find the row start
//            Stack<Pair<AbstractConfigEntry<?>, Iterator<? extends GuiEventListener>>> stack = new Stack<>();
//            Set<GuiEventListener> visited = new HashSet<>();
//            for (AbstractConfigEntry<List<AbstractConfigListEntry>> entry : getParent().children()) {
//                stack.push(Pair.of(entry, entry.children().iterator()));
//            }
//            List<AbstractConfigEntry<?>> parents = null;
//            
//            while (!stack.isEmpty()) {
//                Iterator<? extends GuiEventListener> iterator = stack.peek().getSecond();
//                if (iterator.hasNext()) {
//                    GuiEventListener child = iterator.next();
//                    if (visited.add(child)) {
//                        if (child == this) {
//                            parents = Lists.newArrayList(stack.stream().map(Pair::getFirst).iterator());
//                            break;
//                        } else if (child instanceof AbstractConfigEntry<?> childEntry) {
//                            stack.push(Pair.of(childEntry, childEntry.children().iterator()));
//                        }
//                    }
//                } else {
//                    stack.pop();
//                }
//            }
//            
//            if (parents != null) {
//                int rowStart = getParent().getRowTop(getParent().children().indexOf(parents.get(0))) + (int) getParent().getScroll() - getParent().top - 4;
//                if (Minecraft.getInstance().getLastInputType().isKeyboard()) {
//                    getParent().ensureVisible(rowStart, ((AbstractConfigListEntry<?>) guiEventListener).getItemHeight());
//                }
//            }
//        }
    }
    
    @Override
    public boolean isEdited() {
        for (AbstractConfigListEntry<?> entry : entries) {
            if (entry.isEdited()) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void lateRender(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        if (isExpanded()) {
            for (AbstractConfigListEntry<?> entry : filteredEntries()) {
                entry.lateRender(graphics, mouseX, mouseY, delta);
            }
        }
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public int getMorePossibleHeight() {
        if (!isExpanded()) return -1;
        List<Integer> list = new ArrayList<>();
        int i = 24;
        for (AbstractConfigListEntry<?> entry : filteredEntries()) {
            i += entry.getItemHeight();
            if (entry.getMorePossibleHeight() >= 0) {
                list.add(i + entry.getMorePossibleHeight());
            }
        }
        list.add(i);
        return list.stream().max(Integer::compare).orElse(0) - getItemHeight();
    }
    
    @Override
    public Rectangle getEntryArea(int x, int y, int entryWidth, int entryHeight) {
        widget.rectangle.x = x - 15;
        widget.rectangle.y = y;
        widget.rectangle.width = entryWidth + 15;
        widget.rectangle.height = 24;
        return new Rectangle(getParent().left, y, getParent().right - getParent().left, 20);
    }
    
    @Override
    public int getItemHeight() {
        if (isExpanded()) {
            int i = 24;
            for (AbstractConfigListEntry<?> entry : filteredEntries())
                i += entry.getItemHeight();
            return i;
        }
        return 24;
    }
    
    @Override
    public int getInitialReferenceOffset() {
        return 24;
    }
    
    @Override
    public List<? extends GuiEventListener> children() {
        return isExpanded() ? (List) children : Collections.singletonList(widget);
    }
    
    @Override
    public List<? extends NarratableEntry> narratables() {
        return isExpanded() ? (List) children : Collections.singletonList(widget);
    }
    
    @Override
    public void save() {
        entries.forEach(AbstractConfigListEntry::save);
    }
    
    @Override
    public Optional<Component> getError() {
        Component error = null;
        for (AbstractConfigListEntry<?> entry : entries) {
            Optional<Component> configError = entry.getConfigError();
            if (configError.isPresent()) {
                if (error != null)
                    return Optional.ofNullable(Component.translatable("text.cloth-config.multi_error"));
                return configError;
            }
        }
        return Optional.ofNullable(error);
    }
    
    public class CategoryLabelWidget implements GuiEventListener, NarratableEntry {
        private final Rectangle rectangle = new Rectangle();
        private boolean isHovered;
        
        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int int_1) {
            if (isEnabled() && rectangle.contains(mouseX, mouseY)) {
                setExpanded(!expanded);
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return isHovered = true;
            }
            return isHovered = false;
        }
        
        @Override
        public void setFocused(boolean bl) {
        }
        
        @Override
        public boolean isFocused() {
            return false;
        }
        
        @Override
        public NarrationPriority narrationPriority() {
            return isHovered ? NarrationPriority.HOVERED : NarrationPriority.NONE;
        }
        
        @Override
        public void updateNarration(NarrationElementOutput narrationElementOutput) {
            narrationElementOutput.add(NarratedElementType.TITLE, getFieldName());
        }
    }
    
}
