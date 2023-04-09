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

package me.shedaniel.clothconfig2;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import me.shedaniel.autoconfig.util.Utils;
import me.shedaniel.clothconfig2.api.*;
import me.shedaniel.clothconfig2.api.dependencies.Dependency;
import me.shedaniel.clothconfig2.api.dependencies.requirements.ComparisonOperator;
import me.shedaniel.clothconfig2.gui.entries.*;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import me.shedaniel.clothconfig2.impl.dependencies.conditions.GenericMatcherCondition;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.*;
import java.util.stream.Collectors;

public class ClothConfigDemo {
    public static ConfigBuilder getConfigBuilderWithDemo() {
        class Pair<T, R> {
            final T t;
            final R r;
            
            public Pair(T t, R r) {
                this.t = t;
                this.r = r;
            }
            
            public T getLeft() {
                return t;
            }
            
            public R getRight() {
                return r;
            }
            
            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                
                Pair<?, ?> pair = (Pair<?, ?>) o;
                
                if (!Objects.equals(t, pair.t)) return false;
                return Objects.equals(r, pair.r);
            }
            
            @Override
            public int hashCode() {
                int result = t != null ? t.hashCode() : 0;
                result = 31 * result + (r != null ? r.hashCode() : 0);
                return result;
            }
        }
    
        enum DependencyDemoEnum {
            EXCELLENT, GOOD, OKAY, BAD, HORRIBLE
        }
        
        ConfigBuilder builder = ConfigBuilder.create().setTitle(Component.translatable("title.cloth-config.config"));
        builder.setDefaultBackgroundTexture(new ResourceLocation("minecraft:textures/block/oak_planks.png"));
        builder.setGlobalized(true);
        builder.setGlobalizedExpanded(false);
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory testing = builder.getOrCreateCategory(Component.translatable("category.cloth-config.testing"));
        testing.addEntry(entryBuilder.startKeyCodeField(Component.literal("Cool Key"), InputConstants.UNKNOWN).setDefaultValue(InputConstants.UNKNOWN).build());
        testing.addEntry(entryBuilder.startModifierKeyCodeField(Component.literal("Cool Modifier Key"), ModifierKeyCode.of(InputConstants.Type.KEYSYM.getOrCreate(79), Modifier.of(false, true, false))).setDefaultValue(ModifierKeyCode.of(InputConstants.Type.KEYSYM.getOrCreate(79), Modifier.of(false, true, false))).build());
        testing.addEntry(entryBuilder.startDoubleList(Component.literal("A list of Doubles"), Arrays.asList(1d, 2d, 3d)).setDefaultValue(Arrays.asList(1d, 2d, 3d)).build());
        testing.addEntry(entryBuilder.startLongList(Component.literal("A list of Longs"), Arrays.asList(1L, 2L, 3L)).setDefaultValue(Arrays.asList(1L, 2L, 3L)).setInsertButtonEnabled(false).build());
        testing.addEntry(entryBuilder.startStrList(Component.literal("A list of Strings"), Arrays.asList("abc", "xyz")).setTooltip(Component.literal("Yes this is some beautiful tooltip\nOh and this is the second line!")).setDefaultValue(Arrays.asList("abc", "xyz")).build());
        SubCategoryBuilder colors = entryBuilder.startSubCategory(Component.literal("Colors")).setExpanded(true);
        colors.add(entryBuilder.startColorField(Component.literal("A color field"), 0x00ffff).setDefaultValue(0x00ffff).build());
        colors.add(entryBuilder.startColorField(Component.literal("An alpha color field"), 0xff00ffff).setDefaultValue(0xff00ffff).setAlphaMode(true).build());
        colors.add(entryBuilder.startColorField(Component.literal("An alpha color field"), 0xffffffff).setDefaultValue(0xffff0000).setAlphaMode(true).build());
        colors.add(entryBuilder.startDropdownMenu(Component.literal("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        colors.add(entryBuilder.startDropdownMenu(Component.literal("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        colors.add(entryBuilder.startDropdownMenu(Component.literal("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        colors.add(entryBuilder.startDropdownMenu(Component.literal("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        colors.add(entryBuilder.startDropdownMenu(Component.literal("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        SubCategoryBuilder innerColors = entryBuilder.startSubCategory(Component.literal("Inner Colors")).setExpanded(true);
        innerColors.add(entryBuilder.startDropdownMenu(Component.literal("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        innerColors.add(entryBuilder.startDropdownMenu(Component.literal("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        innerColors.add(entryBuilder.startDropdownMenu(Component.literal("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        SubCategoryBuilder innerInnerColors = entryBuilder.startSubCategory(Component.literal("Inner Inner Colors")).setExpanded(true);
        innerInnerColors.add(entryBuilder.startDropdownMenu(Component.literal("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        innerInnerColors.add(entryBuilder.startDropdownMenu(Component.literal("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        innerInnerColors.add(entryBuilder.startDropdownMenu(Component.literal("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        innerColors.add(innerInnerColors.build());
        colors.add(innerColors.build());
        testing.addEntry(colors.build());
        testing.addEntry(entryBuilder.startDropdownMenu(Component.literal("Suggestion Random Int"), DropdownMenuBuilder.TopCellElementBuilder.of(10,
                s -> {
                    try {
                        return Integer.parseInt(s);
                    } catch (NumberFormatException ignored) {
                        
                    }
                    return null;
                })).setDefaultValue(10).setSelections(Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)).build());
        testing.addEntry(entryBuilder.startDropdownMenu(Component.literal("Selection Random Int"), DropdownMenuBuilder.TopCellElementBuilder.of(10,
                s -> {
                    try {
                        return Integer.parseInt(s);
                    } catch (NumberFormatException ignored) {
                        
                    }
                    return null;
                })).setDefaultValue(5).setSuggestionMode(false).setSelections(Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)).build());
        testing.addEntry(new NestedListListEntry<Pair<Integer, Integer>, MultiElementListEntry<Pair<Integer, Integer>>>(
                Component.literal("Nice"),
                Lists.newArrayList(new Pair<>(10, 10), new Pair<>(20, 40)),
                false,
                Optional::empty,
                list -> {},
                () -> Lists.newArrayList(new Pair<>(10, 10), new Pair<>(20, 40)),
                entryBuilder.getResetButtonKey(),
                true,
                true,
                (elem, nestedListListEntry) -> {
                    if (elem == null) {
                        Pair<Integer, Integer> newDefaultElemValue = new Pair<>(10, 10);
                        return new MultiElementListEntry<>(Component.literal("Pair"), newDefaultElemValue,
                                Lists.newArrayList(entryBuilder.startIntField(Component.literal("Left"), newDefaultElemValue.getLeft()).setDefaultValue(10).build(),
                                        entryBuilder.startIntField(Component.literal("Right"), newDefaultElemValue.getRight()).setDefaultValue(10).build()),
                                true);
                    } else {
                        return new MultiElementListEntry<>(Component.literal("Pair"), elem,
                                Lists.newArrayList(entryBuilder.startIntField(Component.literal("Left"), elem.getLeft()).setDefaultValue(10).build(),
                                        entryBuilder.startIntField(Component.literal("Right"), elem.getRight()).setDefaultValue(10).build()),
                                true);
                    }
                }
        ));
        
        SubCategoryBuilder depends = entryBuilder.startSubCategory(Component.literal("Dependencies")).setExpanded(true);
        LinkedList<BooleanListEntry> toggles = new LinkedList<>();
        BooleanListEntry dependency = entryBuilder.startBooleanToggle(Component.literal("A cool toggle"), false).setTooltip(Component.literal("Toggle me...")).build();
        toggles.add(dependency);
        toggles.add(entryBuilder.startBooleanToggle(Component.literal("I only work when cool is toggled..."), true)
                .setEnabledIf(Dependency.isTrue(dependency)).build());
        toggles.add(entryBuilder.startBooleanToggle(Component.literal("I only appear when cool is toggled..."), true)
                .setShownIf(Dependency.isTrue(dependency)).build());
        depends.addAll(toggles);
        depends.add(entryBuilder.startBooleanToggle(Component.literal("I only work when cool matches one of these toggles ^^"), true)
                        .setEnabledIf(Dependency.builder()
                                .dependingOn(toggles.removeFirst())
                                .matching(toggles.stream().map(GenericMatcherCondition::new).toList())
                                .build())
                .build());
        SubCategoryBuilder dependantSub = entryBuilder.startSubCategory(Component.literal("How do deps work with sub-categories?"))
                .setEnabledIf(Dependency.isTrue(dependency));
        dependantSub.add(entryBuilder.startTextDescription(Component.literal("This sub category depends on Cool being toggled")).build());
        dependantSub.add(entryBuilder.startBooleanToggle(Component.literal("Example entry"), true).build());
        dependantSub.add(entryBuilder.startBooleanToggle(Component.literal("Another example..."), true).build());
        depends.add(dependantSub.build());
        depends.add(entryBuilder.startLongList(Component.literal("A list of Longs"), Arrays.asList(1L, 2L, 3L)).setDefaultValue(Arrays.asList(1L, 2L, 3L))
                .setEnabledIf(Dependency.isTrue(dependency)).build());
        EnumListEntry<DependencyDemoEnum> enumDependency = entryBuilder.startEnumSelector(Component.literal("Select a good or bad option"), DependencyDemoEnum.class, DependencyDemoEnum.OKAY).build();
        depends.add(enumDependency);
        IntegerSliderEntry intDependency = entryBuilder.startIntSlider(Component.literal("Select something big or small"), 50, -100, 100).build();
        depends.add(intDependency);
        depends.add(entryBuilder.startBooleanToggle(Component.literal("I only work when a good option is chosen..."), true).setTooltip(Component.literal("Select good or better above"))
                .setEnabledIf(Dependency.isValue(enumDependency, DependencyDemoEnum.EXCELLENT, DependencyDemoEnum.GOOD))
                .build());
        depends.add(entryBuilder.startBooleanToggle(Component.literal("I need a good option AND a cool toggle!"), true).setTooltip(Component.literal("Select good or better and also toggle cool"))
                .setEnabledIf(Dependency.all(
                        Dependency.isTrue(dependency),
                        Dependency.isValue(enumDependency, DependencyDemoEnum.EXCELLENT, DependencyDemoEnum.GOOD)))
                .build());
        depends.add(entryBuilder.startBooleanToggle(Component.literal("I only work when numbers are awesome!"), true)
                .setTooltip(Component.literal("Move the slider above..."))
                .setEnabledIf(Dependency.builder()
                                .dependingOn(intDependency)
                                .matching(ComparisonOperator.LESS, -70)
                                .matching(ComparisonOperator.GREATER, 70)
                                .build())
                .build());
    
        testing.addEntry(depends.build());
        testing.addEntry(entryBuilder.startBooleanToggle(Component.literal("I appear when bad option is chosen..."), true)
                .setShownIf(Dependency.builder()
                        .dependingOn(enumDependency)
                        .matching(DependencyDemoEnum.HORRIBLE)
                        .matching(DependencyDemoEnum.BAD)
                        .build())
                .setTooltip(Component.literal("Hopefully I keep my index"))
                .build());
        
        testing.addEntry(entryBuilder.startTextDescription(
                Component.translatable("text.cloth-config.testing.1",
                        Component.literal("ClothConfig").withStyle(s -> s.withBold(true).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackInfo(Util.make(new ItemStack(Items.PINK_WOOL), stack -> stack.setHoverName(Component.literal("(\u30FB\u2200\u30FB)")).enchant(Enchantments.BLOCK_EFFICIENCY, 10)))))),
                        Component.translatable("text.cloth-config.testing.2").withStyle(s -> s.withColor(ChatFormatting.BLUE).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("https://shedaniel.gitbook.io/cloth-config/"))).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://shedaniel.gitbook.io/cloth-config/"))),
                        Component.translatable("text.cloth-config.testing.3").withStyle(s -> s.withColor(ChatFormatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, Utils.getConfigFolder().getParent().resolve("options.txt").toString())))
                )
        ).build());
        builder.transparentBackground();
        return builder;
    }
}
