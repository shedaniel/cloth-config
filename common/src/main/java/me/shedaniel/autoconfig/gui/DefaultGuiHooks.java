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

package me.shedaniel.autoconfig.gui;

import me.shedaniel.autoconfig.gui.registry.GuiRegistry;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public class DefaultGuiHooks {
    private DefaultGuiHooks() {}
    
    public static GuiRegistry apply(GuiRegistry registry) {
        
        // FIXME Remove debugging code
        registry.registerPreHook((guis, i18n, field, config, defaults, guiProvider) ->
                System.out.println(String.join("\n",
                        "Pre hook for `%s#%s`:".formatted(field.getDeclaringClass().getSimpleName(), field.getName()),
                        " - \"%s\"".formatted(i18n)
                )));
        
        registry.registerPostHook((guis, i18n, field, config, defaults, guiProvider) ->
                System.out.println(Stream.concat(
                        Stream.of(
                                "Post hook for `%s#%s`:".formatted(field.getDeclaringClass().getSimpleName(), field.getName()),
                                " - \"%s\"".formatted(i18n)
                        ),
                        guis.stream().map(Object::getClass).map(Class::getSimpleName).map(name -> " - " + name)
                ).collect(Collectors.joining("\n"))));
        
        return registry;
    }
}
