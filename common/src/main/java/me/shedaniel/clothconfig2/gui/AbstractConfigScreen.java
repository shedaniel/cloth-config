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

package me.shedaniel.clothconfig2.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import me.shedaniel.clothconfig2.api.*;
import me.shedaniel.clothconfig2.gui.entries.KeyCodeEntry;
import me.shedaniel.math.Color;
import me.shedaniel.math.Rectangle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class AbstractConfigScreen extends Screen implements ConfigScreen {
    protected static final Identifier CONFIG_TEX = Identifier.fromNamespaceAndPath("cloth-config2", "textures/gui/cloth_config.png");
    private final Identifier backgroundLocation;
    protected boolean confirmSave;
    protected final Screen parent;
    private boolean alwaysShowTabs = false;
    private boolean transparentBackground = false;
    @Nullable
    private Component defaultFallbackCategory = null;
    public int selectedCategoryIndex = 0;
    private boolean editable = true;
    private KeyCodeEntry focusedBinding;
    private ModifierKeyCode startedKeyCode = null;
    private final List<Tooltip> tooltips = Lists.newArrayList();
    @Nullable
    private Runnable savingRunnable = null;
    @Nullable
    protected Consumer<Screen> afterInitConsumer = null;
    
    protected AbstractConfigScreen(Screen parent, Component title, Identifier backgroundLocation) {
        super(title);
        this.parent = parent;
        this.backgroundLocation = backgroundLocation;
    }
    
    public List<GuiEventListener> childrenL() {
        return (List<GuiEventListener>) super.children();
    }
    
    @Override
    public void setSavingRunnable(@Nullable Runnable savingRunnable) {
        this.savingRunnable = savingRunnable;
    }
    
    @Override
    public void setAfterInitConsumer(@Nullable Consumer<Screen> afterInitConsumer) {
        this.afterInitConsumer = afterInitConsumer;
    }
    
    @Override
    public Identifier getBackgroundLocation() {
        return backgroundLocation;
    }
    
    @Override
    public boolean isRequiresRestart() {
        for (List<AbstractConfigEntry<?>> entries : getCategorizedEntries().values()) {
            for (AbstractConfigEntry<?> entry : entries) {
                if (entry.getConfigError().isEmpty() && entry.isEdited() && entry.isRequiresRestart()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public abstract Map<Component, List<AbstractConfigEntry<?>>> getCategorizedEntries();
    
    @Override
    public boolean isEdited() {
        for (List<AbstractConfigEntry<?>> entries : getCategorizedEntries().values()) {
            for (AbstractConfigEntry<?> entry : entries) {
                if (entry.isEdited()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean isShowingTabs() {
        return isAlwaysShowTabs() || getCategorizedEntries().size() > 1;
    }
    
    public boolean isAlwaysShowTabs() {
        return alwaysShowTabs;
    }
    
    @ApiStatus.Internal
    public void setAlwaysShowTabs(boolean alwaysShowTabs) {
        this.alwaysShowTabs = alwaysShowTabs;
    }
    
    public boolean isTransparentBackground() {
        return transparentBackground;
    }
    
    @ApiStatus.Internal
    public void setTransparentBackground(boolean transparentBackground) {
        this.transparentBackground = transparentBackground;
    }
    
    public Component getFallbackCategory() {
        if (defaultFallbackCategory != null)
            return defaultFallbackCategory;
        return getCategorizedEntries().keySet().iterator().next();
    }
    
    @ApiStatus.Internal
    public void setFallbackCategory(@Nullable Component defaultFallbackCategory) {
        this.defaultFallbackCategory = defaultFallbackCategory;
        List<Component> categories = Lists.newArrayList(getCategorizedEntries().keySet());
        for (int i = 0; i < categories.size(); i++) {
            Component category = categories.get(i);
            if (category.equals(getFallbackCategory())) {
                this.selectedCategoryIndex = i;
                break;
            }
        }
    }
    
    @Override
    public void saveAll(boolean openOtherScreens) {
        for (List<AbstractConfigEntry<?>> entries : Lists.newArrayList(getCategorizedEntries().values()))
            for (AbstractConfigEntry<?> entry : entries)
                entry.save();
        save();
        if (openOtherScreens) {
            if (isRequiresRestart())
                AbstractConfigScreen.this.minecraft.gui.setScreen(new ClothRequiresRestartScreen(parent));
            else
                AbstractConfigScreen.this.minecraft.gui.setScreen(parent);
        }
    }
    
    public void save() {
        Optional.ofNullable(this.savingRunnable).ifPresent(Runnable::run);
    }
    
    public boolean isEditable() {
        return editable;
    }
    
    @ApiStatus.Internal
    public void setEditable(boolean editable) {
        this.editable = editable;
    }
    
    @ApiStatus.Internal
    public void setConfirmSave(boolean confirmSave) {
        this.confirmSave = confirmSave;
    }
    
    public KeyCodeEntry getFocusedBinding() {
        return focusedBinding;
    }
    
    @ApiStatus.Internal
    public void setFocusedBinding(KeyCodeEntry focusedBinding) {
        this.focusedBinding = focusedBinding;
        if (focusedBinding != null) {
            startedKeyCode = this.focusedBinding.getValue();
            startedKeyCode.setKeyCodeAndModifier(InputConstants.UNKNOWN, Modifier.none());
        } else
            startedKeyCode = null;
    }
    
    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (this.focusedBinding != null && this.startedKeyCode != null && !this.startedKeyCode.isUnknown() && focusedBinding.isAllowMouse()) {
            focusedBinding.setValue(startedKeyCode);
            setFocusedBinding(null);
            return true;
        }
        return super.mouseReleased(event);
    }
    
    @Override
    public boolean keyReleased(KeyEvent event) {
        if (this.focusedBinding != null && this.startedKeyCode != null && focusedBinding.isAllowKey()) {
            focusedBinding.setValue(startedKeyCode);
            setFocusedBinding(null);
            return true;
        }
        return super.keyReleased(event);
    }
    
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (this.focusedBinding != null && this.startedKeyCode != null && focusedBinding.isAllowMouse()) {
            if (startedKeyCode.isUnknown())
                startedKeyCode.setKeyCode(InputConstants.Type.MOUSE.getOrCreate(event.button()));
            else if (focusedBinding.isAllowModifiers()) {
                if (startedKeyCode.getType() == InputConstants.Type.KEYSYM) {
                    int code = startedKeyCode.getKeyCode().getValue();
                    if (event.hasControlDown()) {
                        Modifier modifier = startedKeyCode.getModifier();
                        startedKeyCode.setModifier(Modifier.of(modifier.hasAlt(), true, modifier.hasShift()));
                        startedKeyCode.setKeyCode(InputConstants.Type.MOUSE.getOrCreate(event.button()));
                        return true;
                    } else if (event.hasShiftDown()) {
                        Modifier modifier = startedKeyCode.getModifier();
                        startedKeyCode.setModifier(Modifier.of(modifier.hasAlt(), modifier.hasControl(), true));
                        startedKeyCode.setKeyCode(InputConstants.Type.MOUSE.getOrCreate(event.button()));
                        return true;
                    } else if (event.hasAltDown()) {
                        Modifier modifier = startedKeyCode.getModifier();
                        startedKeyCode.setModifier(Modifier.of(true, modifier.hasControl(), modifier.hasShift()));
                        startedKeyCode.setKeyCode(InputConstants.Type.MOUSE.getOrCreate(event.button()));
                        return true;
                    }
                }
            }
            return true;
        } else {
            if (this.focusedBinding != null)
                return true;
            return super.mouseClicked(event, doubleClick);
        }
    }
    
    @Override
    public boolean keyPressed(KeyEvent event) {
        if (this.focusedBinding != null && (focusedBinding.isAllowKey() || event.key() == 256)) {
            if (event.key() != 256) {
                if (startedKeyCode.isUnknown())
                    startedKeyCode.setKeyCode(InputConstants.getKey(event));
                else if (focusedBinding.isAllowModifiers()) {
                    if (startedKeyCode.getType() == InputConstants.Type.KEYSYM) {
                        int code = startedKeyCode.getKeyCode().getValue();
                        if (event.hasControlDown()) {
                            Modifier modifier = startedKeyCode.getModifier();
                            startedKeyCode.setModifier(Modifier.of(modifier.hasAlt(), true, modifier.hasShift()));
                            startedKeyCode.setKeyCode(InputConstants.getKey(event));
                            return true;
                        } else if (event.hasShiftDown()) {
                            Modifier modifier = startedKeyCode.getModifier();
                            startedKeyCode.setModifier(Modifier.of(modifier.hasAlt(), modifier.hasControl(), true));
                            startedKeyCode.setKeyCode(InputConstants.getKey(event));
                            return true;
                        } else if (event.hasAltDown()) {
                            Modifier modifier = startedKeyCode.getModifier();
                            startedKeyCode.setModifier(Modifier.of(true, modifier.hasControl(), modifier.hasShift()));
                            startedKeyCode.setKeyCode(InputConstants.getKey(event));
                            return true;
                        }
                    }
                    if (event.hasControlDown()) {
                        Modifier modifier = startedKeyCode.getModifier();
                        startedKeyCode.setModifier(Modifier.of(modifier.hasAlt(), true, modifier.hasShift()));
                        return true;
                    } else if (event.hasShiftDown()) {
                        Modifier modifier = startedKeyCode.getModifier();
                        startedKeyCode.setModifier(Modifier.of(modifier.hasAlt(), modifier.hasControl(), true));
                        return true;
                    } else if (event.hasAltDown()) {
                        Modifier modifier = startedKeyCode.getModifier();
                        startedKeyCode.setModifier(Modifier.of(true, modifier.hasControl(), modifier.hasShift()));
                        return true;
                    }
                }
            } else {
                focusedBinding.setValue(ModifierKeyCode.unknown());
                setFocusedBinding(null);
            }
            return true;
        }
        if (this.focusedBinding != null && !event.isEscape())
            return true;
        if (event.isEscape() && this.shouldCloseOnEsc()) {
            return quit();
        }
        return super.keyPressed(event);
    }
    
    protected final boolean quit() {
        if (confirmSave && isEdited())
            minecraft.gui.setScreen(new ConfirmScreen(new QuitSaveConsumer(), Component.translatable("text.cloth-config.quit_config"), Component.translatable("text.cloth-config.quit_config_sure"), Component.translatable("text.cloth-config.quit_discard"), Component.translatable("gui.cancel")));
        else
            minecraft.gui.setScreen(parent);
        return true;
    }
    
    private class QuitSaveConsumer implements BooleanConsumer {
        @Override
        public void accept(boolean t) {
            if (!t)
                minecraft.gui.setScreen(AbstractConfigScreen.this);
            else
                minecraft.gui.setScreen(parent);
        }
    }
    
    @Override
    public void tick() {
        super.tick();
        boolean edited = isEdited();
        Optional.ofNullable(getQuitButton()).ifPresent(button -> button.setMessage(edited ? Component.translatable("text.cloth-config.cancel_discard") : Component.translatable("gui.cancel")));
        for (GuiEventListener child : children()) {
            if (child instanceof TickableWidget widget)
                widget.tick();
        }
    }
    
    @Nullable
    protected AbstractWidget getQuitButton() {
        return null;
    }
    
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        for (Tooltip tooltip : tooltips) {
            graphics.setTooltipForNextFrame(Minecraft.getInstance().font, tooltip.getText(), tooltip.getX(), tooltip.getY());
        }
        this.tooltips.clear();
    }
    
    @Override
    public void addTooltip(Tooltip tooltip) {
        this.tooltips.add(tooltip);
    }
    
    @Deprecated
    protected void overlayBackground(GuiGraphicsExtractor graphics, Rectangle rect, int red, int green, int blue, int alpha) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, getBackgroundLocation(), rect.getMinX(), rect.getMinY(), rect.getMaxX(), rect.getMaxY(), rect.getWidth(), rect.getHeight(), rect.getWidth(), rect.getHeight(), 32, 32, Color.ofRGBA(red, green, blue, alpha).getColor());
    }
    
    public static void handleClickEvent(ClickEvent clickEvent, Minecraft minecraft, @Nullable Screen screen) {
        Screen.defaultHandleClickEvent(clickEvent, minecraft, screen);
    }
}
