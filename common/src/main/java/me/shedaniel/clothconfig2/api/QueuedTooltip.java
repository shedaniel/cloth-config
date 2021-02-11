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

import me.shedaniel.math.Point;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class QueuedTooltip implements Tooltip {
    private final Point location;
    private final List<FormattedCharSequence> text;
    
    private QueuedTooltip(Point location, List<FormattedCharSequence> text) {
        this.location = location;
        this.text = Collections.unmodifiableList(text);
    }
    
    public static QueuedTooltip create(Point location, List<Component> text) {
        return new QueuedTooltip(location, Language.getInstance().getVisualOrder((List) text));
    }
    
    public static QueuedTooltip create(Point location, Component... text) {
        return QueuedTooltip.create(location, Arrays.asList(text));
    }
    
    public static QueuedTooltip create(Point location, FormattedCharSequence... text) {
        return new QueuedTooltip(location, Arrays.asList(text));
    }
    
    public static QueuedTooltip create(Point location, FormattedText... text) {
        return new QueuedTooltip(location, Language.getInstance().getVisualOrder(Arrays.asList(text)));
    }
    
    @Override
    public Point getPoint() {
        return location;
    }
    
    @ApiStatus.Internal
    @Override
    public List<FormattedCharSequence> getText() {
        return text;
    }
}
