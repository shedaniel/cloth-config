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

package me.shedaniel.clothconfig2.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Environment(EnvType.CLIENT)
public abstract class DynamicElementListWidget<E extends DynamicElementListWidget.ElementEntry<E>> extends DynamicSmoothScrollingEntryListWidget<E> {
    private static final Component USAGE_NARRATION = Component.translatable("narration.selection.usage");
    
    public DynamicElementListWidget(Minecraft client, int width, int height, int top, int bottom, ResourceLocation backgroundLocation) {
        super(client, width, height, top, bottom, backgroundLocation);
    }
    
    @Nullable
    public ComponentPath nextFocusPath(FocusNavigationEvent focusNavigationEvent) {
        if (this.getItemCount() == 0) {
            return null;
        } else if (!(focusNavigationEvent instanceof FocusNavigationEvent.ArrowNavigation arrowNavigation)) {
            return super.nextFocusPath(focusNavigationEvent);
        } else {
            E entry = this.getFocused();
            if (arrowNavigation.direction().getAxis() == ScreenAxis.HORIZONTAL && entry != null) {
                return ComponentPath.path(this, entry.nextFocusPath(focusNavigationEvent));
            } else {
                int i = -1;
                ScreenDirection screenDirection = arrowNavigation.direction();
                if (entry != null) {
                    i = entry.children().indexOf(entry.getFocused());
                }
                
                if (i == -1) {
                    switch (screenDirection) {
                        case LEFT:
                            i = Integer.MAX_VALUE;
                            screenDirection = ScreenDirection.DOWN;
                            break;
                        case RIGHT:
                            i = 0;
                            screenDirection = ScreenDirection.DOWN;
                            break;
                        default:
                            i = 0;
                    }
                }
                
                E entry2 = entry;
                
                ComponentPath componentPath;
                do {
                    entry2 = this.nextEntry(screenDirection, (entryx) -> {
                        return !entryx.children().isEmpty();
                    }, entry2);
                    if (entry2 == null) {
                        return null;
                    }
                    
                    componentPath = entry2.focusPathAtIndex(arrowNavigation, i);
                } while (componentPath == null);
                
                return ComponentPath.path(this, componentPath);
            }
        }
    }
    
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
        E entry = this.hoveredItem;
        if (entry != null) {
            entry.updateNarration(narrationElementOutput.nest());
            this.narrateListElementPosition(narrationElementOutput, entry);
        } else {
            E entry2 = this.getFocused();
            if (entry2 != null) {
                entry2.updateNarration(narrationElementOutput.nest());
                this.narrateListElementPosition(narrationElementOutput, entry2);
            }
        }
        
        narrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("narration.component_list.usage"));
    }
    
    public void setFocused(@Nullable GuiEventListener guiEventListener) {
        super.setFocused(guiEventListener);
        if (guiEventListener == null) {
            this.selectItem(null);
        }
        
    }
    
    public NarratableEntry.NarrationPriority narrationPriority() {
        return this.isFocused() ? NarrationPriority.FOCUSED : super.narrationPriority();
    }
    
    protected boolean isSelected(int i) {
        return false;
    }
    
    @Environment(EnvType.CLIENT)
    public abstract static class ElementEntry<E extends ElementEntry<E>> extends Entry<E> implements ContainerEventHandler, NarratableEntry {
        @Nullable
        private GuiEventListener focused;
        @Nullable
        private NarratableEntry lastNarratable;
        private boolean dragging;
        
        public ElementEntry() {
        }
        
        public boolean isDragging() {
            return this.dragging;
        }
        
        public void setDragging(boolean bl) {
            this.dragging = bl;
        }
        
        @Nullable
        public GuiEventListener getFocused() {
            return this.focused;
        }
        
        public void setFocused(@Nullable GuiEventListener guiEventListener) {
            if (this.focused != null) {
                this.focused.setFocused(false);
            }
            
            if (guiEventListener != null) {
                guiEventListener.setFocused(true);
            }
            
            this.focused = guiEventListener;
        }
        
        @Nullable
        public ComponentPath focusPathAtIndex(FocusNavigationEvent focusNavigationEvent, int i) {
            if (this.children().isEmpty()) {
                return null;
            } else {
                ComponentPath componentPath = this.children().get(Math.min(i, this.children().size() - 1)).nextFocusPath(focusNavigationEvent);
                return ComponentPath.path(this, componentPath);
            }
        }
        
        @Nullable
        public ComponentPath nextFocusPath(FocusNavigationEvent focusNavigationEvent) {
            if (focusNavigationEvent instanceof FocusNavigationEvent.ArrowNavigation arrowNavigation) {
                int var10000 = switch (arrowNavigation.direction()) {
                    case LEFT -> -1;
                    case RIGHT -> 1;
                    case UP, DOWN -> 0;
                };
    
                if (var10000 == 0) {
                    return null;
                }
                
                int j = Mth.clamp(var10000 + this.children().indexOf(this.getFocused()), 0, this.children().size() - 1);
                
                for (int k = j; k >= 0 && k < this.children().size(); k += var10000) {
                    GuiEventListener guiEventListener = this.children().get(k);
                    ComponentPath componentPath = guiEventListener.nextFocusPath(focusNavigationEvent);
                    if (componentPath != null) {
                        return ComponentPath.path(this, componentPath);
                    }
                }
            }
            
            return ContainerEventHandler.super.nextFocusPath(focusNavigationEvent);
        }
        
        public abstract List<? extends NarratableEntry> narratables();
        
        public void updateNarration(NarrationElementOutput narrationElementOutput) {
            List<? extends NarratableEntry> list = this.narratables();
            Screen.NarratableSearchResult narratableSearchResult = Screen.findNarratableWidget(list, this.lastNarratable);
            if (narratableSearchResult != null) {
                if (narratableSearchResult.priority.isTerminal()) {
                    this.lastNarratable = narratableSearchResult.entry;
                }
                
                if (list.size() > 1) {
                    narrationElementOutput.add(NarratedElementType.POSITION, Component.translatable("narrator.position.object_list", narratableSearchResult.index + 1, list.size()));
                    if (narratableSearchResult.priority == NarrationPriority.FOCUSED) {
                        narrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("narration.component_list.usage"));
                    }
                }
                
                narratableSearchResult.entry.updateNarration(narrationElementOutput.nest());
            }
            
        }
        
        @Override
        public boolean isActive() {
            return false;
        }
        
        @Override
        public NarrationPriority narrationPriority() {
            if (this.isFocused()) {
                return NarrationPriority.FOCUSED;
            } else {
                return NarrationPriority.NONE;
            }
        }
        
        @Override
        public boolean mouseClicked(double d, double e, int i) {
            if (!isEnabled()) {
                return false;
            }
            return ContainerEventHandler.super.mouseClicked(d, e, i);
        }
        
        @Override
        public boolean mouseReleased(double d, double e, int i) {
            if (!isEnabled()) {
                return false;
            }
            return ContainerEventHandler.super.mouseReleased(d, e, i);
        }
        
        @Override
        public boolean mouseDragged(double d, double e, int i, double f, double g) {
            if (!isEnabled()) {
                return false;
            }
            return ContainerEventHandler.super.mouseDragged(d, e, i, f, g);
        }
        
        @Override
        public boolean mouseScrolled(double d, double e, double amountX, double amountY) {
            if (!isEnabled()) {
                return false;
            }
            return ContainerEventHandler.super.mouseScrolled(d, e, amountX, amountY);
        }
        
        @Override
        public boolean keyPressed(int i, int j, int k) {
            if (!isEnabled()) {
                return false;
            }
            return ContainerEventHandler.super.keyPressed(i, j, k);
        }
        
        @Override
        public boolean keyReleased(int i, int j, int k) {
            if (!isEnabled()) {
                return false;
            }
            return ContainerEventHandler.super.keyReleased(i, j, k);
        }
        
        @Override
        public boolean charTyped(char c, int i) {
            if (!isEnabled()) {
                return false;
            }
            return ContainerEventHandler.super.charTyped(c, i);
        }
    }
}

