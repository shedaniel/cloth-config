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

import me.shedaniel.clothconfig2.gui.AbstractConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FormattedCharSink;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class TextListEntry extends TooltipListEntry<Object> {
    public static final int LINE_HEIGHT = 12;
    public static final int DISABLED_COLOR = ARGB.opaque(TextColor.DARK_GRAY.getValue());
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
    public void extractRenderState(GuiGraphicsExtractor graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.extractRenderState(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
        if (this.savedWidth != entryWidth || this.savedX != x || this.savedY != y) {
            this.wrappedLines = this.textRenderer.split(this.text, entryWidth);
            this.savedWidth = entryWidth;
            this.savedX = x;
            this.savedY = y;
        }
        int yy = y + 7;
        int textColor = isEnabled() ? color : DISABLED_COLOR;
        for (FormattedCharSequence string : wrappedLines) {
            graphics.text(Minecraft.getInstance().font, string, x, yy, textColor);
            yy += Minecraft.getInstance().font.lineHeight + 3;
        }
        
        Style style = this.getTextAt(mouseX, mouseY);
        AbstractConfigScreen configScreen = this.getConfigScreen();
        
        if (style != null && configScreen != null) {
            if (Minecraft.getInstance().level == null && style.getHoverEvent() != null && style.getHoverEvent().action() == HoverEvent.Action.SHOW_ITEM) {
                return;
            }
            
//            graphics.extractComponentHoverEffect(Minecraft.getInstance().font, style, mouseX, mouseY);
        }
    }
    
    @Override
    public int getItemHeight() {
        if (savedWidth == -1) return LINE_HEIGHT;
        int lineCount = this.wrappedLines.size();
        return lineCount == 0 ? 0 : 14 + lineCount * LINE_HEIGHT;
    }
    
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0) {
            Style style = this.getTextAt(event.x(), event.y());
            AbstractConfigScreen configScreen = this.getConfigScreen();
            if (configScreen != null && style != null && style.getClickEvent() != null) {
                AbstractConfigScreen.handleClickEvent(style.getClickEvent(), Minecraft.getInstance(), configScreen);
                return true;
            }
        }
        
        return super.mouseClicked(event, doubleClick);
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
                    return this.componentStyleAtWidth(orderedText, textX);
                }
            }
        }
        return null;
    }
    
    private Style componentStyleAtWidth(FormattedCharSequence text, int width) {
        class WidthLimitedCharSink implements FormattedCharSink {
            private float maxWidth;
            private int position;
            
            public WidthLimitedCharSink(final float f) {
                this.maxWidth = f;
            }
            
            public boolean accept(int i, Style style, int j) {
                this.maxWidth -= Minecraft.getInstance().font.getSplitter().stringWidth(FormattedCharSequence.codepoint(j, style));
                if (this.maxWidth >= 0.0F) {
                    this.position = i + Character.charCount(j);
                    return true;
                } else {
                    return false;
                }
            }
            
            public int getPosition() {
                return this.position;
            }
            
            public void resetPosition() {
                this.position = 0;
            }
        }
        
        WidthLimitedCharSink widthLimitedCharSink = new WidthLimitedCharSink((float) width);
        MutableObject<Style> mutableObject = new MutableObject<>();
        text.accept((i, style, j) -> {
            if (!widthLimitedCharSink.accept(i, style, j)) {
                mutableObject.setValue(style);
                return false;
            } else {
                return true;
            }
        });
        return mutableObject.get();
    }
    
    @Override
    public Object getValue() {
        return null;
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
