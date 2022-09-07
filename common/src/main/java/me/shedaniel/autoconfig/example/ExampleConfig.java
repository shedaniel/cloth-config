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

package me.shedaniel.autoconfig.example;

import blue.endless.jankson.Comment;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"unused", "FieldMayBeFinal"})
@ApiStatus.Internal
@Config(name = "autoconfig1u_example")
@Config.Gui.Background("minecraft:textures/block/oak_planks.png")
@Config.Gui.CategoryBackground(category = "b", background = "minecraft:textures/block/stone.png")
public class ExampleConfig extends PartitioningSerializer.GlobalData {
    @ConfigEntry.Category("a")
    @ConfigEntry.Gui.TransitiveObject
    public ModuleA moduleA = new ModuleA();
    
    @ConfigEntry.Category("a")
    @ConfigEntry.Gui.TransitiveObject
    public Empty empty = new Empty();
    
    @ConfigEntry.Category("b")
    @ConfigEntry.Gui.TransitiveObject
    public ModuleB moduleB = new ModuleB();
    
    enum ExampleEnum {
        FOO,
        BAR,
        BAZ
    }
    
    @Config(name = "module_a")
    public static class ModuleA implements ConfigData {
        @ConfigEntry.Gui.PrefixText
        public boolean aBoolean = true;
        
        @ConfigEntry.Gui.Tooltip(count = 2)
        public ExampleEnum anEnum = ExampleEnum.FOO;
        
        @ConfigEntry.Gui.Tooltip(count = 2)
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public ExampleEnum anEnumWithButton = ExampleEnum.FOO;
        
        @Comment("This tooltip was automatically applied from a Jankson @Comment")
        public String aString = "hello";
        
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
        public PairOfIntPairs anObject = new PairOfIntPairs(new PairOfInts(), new PairOfInts(3, 4));
        
        public List<Integer> list = Arrays.asList(1, 2, 3);
        
        public int[] array = new int[]{1, 2, 3};
        
        public List<PairOfInts> complexList = Arrays.asList(new PairOfInts(0, 1), new PairOfInts(3, 7));
        
        public PairOfInts[] complexArray = new PairOfInts[]{new PairOfInts(0, 1), new PairOfInts(3, 7)};
    }
    
    @Config(name = "module_b")
    public static class ModuleB implements ConfigData {
        @ConfigEntry.BoundedDiscrete(min = -1000, max = 2000)
        public int intSlider = 500;
        
        @ConfigEntry.BoundedDiscrete(min = -1000, max = 2000)
        public Long longSlider = 500L;
        
        @ConfigEntry.Gui.TransitiveObject
        public PairOfIntPairs anObject = new PairOfIntPairs(new PairOfInts(), new PairOfInts(3, 4));
        
        @ConfigEntry.Gui.Excluded
        public List<PairOfInts> aList = Arrays.asList(new PairOfInts(), new PairOfInts(3, 4));
        
        @ConfigEntry.ColorPicker
        public int color = 0xFFFFFF;
    }
    
    @Config(name = "empty")
    public static class Empty implements ConfigData {
        
    }
    
    public static class PairOfInts {
        public int foo;
        public int bar;
        
        public PairOfInts() {
            this(1, 2);
        }
        
        public PairOfInts(int foo, int bar) {
            this.foo = foo;
            this.bar = bar;
        }
    }
    
    public static class PairOfIntPairs {
        @ConfigEntry.Gui.CollapsibleObject()
        public PairOfInts first;
        
        @ConfigEntry.Gui.CollapsibleObject()
        public PairOfInts second;
        
        public PairOfIntPairs() {
            this(new PairOfInts(), new PairOfInts());
        }
        
        public PairOfIntPairs(PairOfInts first, PairOfInts second) {
            this.first = first;
            this.second = second;
        }
    }
}
