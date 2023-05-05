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
import me.shedaniel.clothconfig2.gui.AbstractConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class TextListEntry extends TooltipListEntry<Object> {
    public static final int LINE_HEIGHT = 12;
    private final Font textRenderer = Minecraft.getInstance().font;
    private final int color;
    private final Component text;
    private int savedWidth = -1;
    private int savedX = -1;
    private int savedY = -1;
    private List<FormattedCharSequence> wrappedLines;
    
    @ApiStatus.Internal
    @Deprecated
    public TextListEntry(Component fieldName, Component text) {
        this(fieldName, text, -1);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public TextListEntry(Component fieldName, Component text, int color) {
        this(fieldName, text, color, null);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public TextListEntry(Component fieldName, Component text, int color, Supplier<Optional<Component[]>> tooltipSupplier) {
        super(fieldName, tooltipSupplier);
        this.text = text;
        this.color = color;
        this.wrappedLines = Collections.emptyList();
    }
    
    @Override
    public void render(PoseStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
        if (this.savedWidth != entryWidth || this.savedX != x || this.savedY != y) {
            this.wrappedLines = this.textRenderer.split(this.text, entryWidth);
            this.savedWidth = entryWidth;
            this.savedX = x;
            this.savedY = y;
        }
        int yy = y + 7;
        int textColor = isEnabled() ? color : 0x555555;
        for (FormattedCharSequence string : wrappedLines) {
            Minecraft.getInstance().font.drawShadow(matrices, string, x, yy, textColor);
            yy += Minecraft.getInstance().font.lineHeight + 3;
        }
        
        Style style = this.getTextAt(mouseX, mouseY);
        AbstractConfigScreen configScreen = this.getConfigScreen();
        
        if (style != null && configScreen != null) {
            configScreen.renderComponentHoverEffect(matrices, style, mouseX, mouseY);
        }
    }
    
    @Override
    public int getItemHeight() {
        if (savedWidth == -1) return LINE_HEIGHT;
        int lineCount = this.wrappedLines.size();
        return lineCount == 0 ? 0 : 14 + lineCount * LINE_HEIGHT;
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            Style style = this.getTextAt(mouseX, mouseY);
            AbstractConfigScreen configScreen = this.getConfigScreen();
            if (configScreen != null && configScreen.handleComponentClicked(style)) {
                return true;
            }
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Nullable
    private Style getTextAt(double x, double y) {
        int lineCount = this.wrappedLines.size();
        
        if (lineCount > 0) {
            int textX = Mth.floor(x - this.savedX);
            int textY = Mth.floor(y - 7 - this.savedY);
            if (textX >= 0 && textY >= 0 && textX <= this.savedWidth && textY < LINE_HEIGHT * lineCount + lineCount) {
                int line = textY / LINE_HEIGHT;
                if (line < this.wrappedLines.size()) {
                    FormattedCharSequence orderedText = this.wrappedLines.get(line);
                    return this.textRenderer.getSplitter().componentStyleAtWidth(orderedText, textX);
                }
            }
        }
        return null;
    }
    
    @Override
    public Object getValue() {
        return null;
    }
    
    @Override
    public String getI18nKey() {
        return this.text.getContents() instanceof TranslatableContents translatable ?
                translatable.getKey() : this.text.getString();
    }
    
    @Override
    public Optional<Object> getDefaultValue() {
        return Optional.empty();
    }
    
    @Override
    public List<? extends GuiEventListener> children() {
        return Collections.emptyList();
    }
    
    @Override
    public List<? extends NarratableEntry> narratables() {
        return Collections.emptyList();
    }
}
