package me.shedaniel.clothconfig2;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import me.shedaniel.clothconfig2.api.*;
import me.shedaniel.clothconfig2.gui.entries.MultiElementListEntry;
import me.shedaniel.clothconfig2.gui.entries.NestedListListEntry;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;

import java.util.*;
import java.util.stream.Collectors;

public class ClothConfigForgeDemo {
    
    public static void registerModsPage() {
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (client, parent) -> {
            return ClothConfigForgeDemo.getConfigBuilderWithDemo(parent).build();
        });
    }
    
    public static ConfigBuilder getConfigBuilderWithDemo(Screen parent) {
        class Pair<T, R> {
            T t;
            R r;
            
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
        
        ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle(new TranslatableComponent("title.cloth-config.config"));
        builder.setDefaultBackgroundTexture(new ResourceLocation("minecraft:textures/block/oak_planks.png"));
//        builder.setGlobalized(true);
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory testing = builder.getOrCreateCategory(new TranslatableComponent("category.cloth-config.testing"));
        testing.addEntry(entryBuilder.startKeyCodeField(new TextComponent("Cool Key"), InputConstants.UNKNOWN).setDefaultValue(InputConstants.UNKNOWN).build());
        testing.addEntry(entryBuilder.startModifierKeyCodeField(new TextComponent("Cool Modifier Key"), ModifierKeyCode.of(InputConstants.Type.KEYSYM.getOrCreate(79), Modifier.of(false, true, false))).setDefaultValue(ModifierKeyCode.of(InputConstants.Type.KEYSYM.getOrCreate(79), Modifier.of(false, true, false))).build());
        testing.addEntry(entryBuilder.startDoubleList(new TextComponent("A list of Doubles"), Arrays.asList(1d, 2d, 3d)).setDefaultValue(Arrays.asList(1d, 2d, 3d)).build());
        testing.addEntry(entryBuilder.startLongList(new TextComponent("A list of Longs"), Arrays.asList(1L, 2L, 3L)).setDefaultValue(Arrays.asList(1L, 2L, 3L)).build());
        testing.addEntry(entryBuilder.startStrList(new TextComponent("A list of Strings"), Arrays.asList("abc", "xyz")).setDefaultValue(Arrays.asList("abc", "xyz")).build());
        SubCategoryBuilder colors = entryBuilder.startSubCategory(new TextComponent("Colors")).setExpanded(true);
        colors.add(entryBuilder.startColorField(new TextComponent("A color field"), 0x00ffff).setDefaultValue(0x00ffff).build());
        colors.add(entryBuilder.startColorField(new TextComponent("An alpha color field"), 0xff00ffff).setDefaultValue(0xff00ffff).setAlphaMode(true).build());
        colors.add(entryBuilder.startColorField(new TextComponent("An alpha color field"), 0xffffffff).setDefaultValue(0xffff0000).setAlphaMode(true).build());
        colors.add(entryBuilder.startDropdownMenu(new TextComponent("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        colors.add(entryBuilder.startDropdownMenu(new TextComponent("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        colors.add(entryBuilder.startDropdownMenu(new TextComponent("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        colors.add(entryBuilder.startDropdownMenu(new TextComponent("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        colors.add(entryBuilder.startDropdownMenu(new TextComponent("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        SubCategoryBuilder innerColors = entryBuilder.startSubCategory(new TextComponent("Inner Colors")).setExpanded(true);
        innerColors.add(entryBuilder.startDropdownMenu(new TextComponent("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        innerColors.add(entryBuilder.startDropdownMenu(new TextComponent("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        innerColors.add(entryBuilder.startDropdownMenu(new TextComponent("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        SubCategoryBuilder innerInnerColors = entryBuilder.startSubCategory(new TextComponent("Inner Inner Colors")).setExpanded(true);
        innerInnerColors.add(entryBuilder.startDropdownMenu(new TextComponent("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        innerInnerColors.add(entryBuilder.startDropdownMenu(new TextComponent("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        innerInnerColors.add(entryBuilder.startDropdownMenu(new TextComponent("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        innerColors.add(innerInnerColors.build());
        colors.add(innerColors.build());
        testing.addEntry(colors.build());
        testing.addEntry(entryBuilder.startDropdownMenu(new TextComponent("Suggestion Random Int"), DropdownMenuBuilder.TopCellElementBuilder.of(10,
                s -> {
                    try {
                        return Integer.parseInt(s);
                    } catch (NumberFormatException ignored) {
                        
                    }
                    return null;
                })).setDefaultValue(10).setSelections(Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)).build());
        testing.addEntry(entryBuilder.startDropdownMenu(new TextComponent("Selection Random Int"), DropdownMenuBuilder.TopCellElementBuilder.of(10,
                s -> {
                    try {
                        return Integer.parseInt(s);
                    } catch (NumberFormatException ignored) {
                        
                    }
                    return null;
                })).setDefaultValue(5).setSuggestionMode(false).setSelections(Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)).build());
        testing.addEntry(new NestedListListEntry<Pair<Integer, Integer>, MultiElementListEntry<Pair<Integer, Integer>>>(
                new TextComponent("Nice"),
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
                        return new MultiElementListEntry<>(new TextComponent("Pair"), newDefaultElemValue,
                                Lists.newArrayList(entryBuilder.startIntField(new TextComponent("Left"), newDefaultElemValue.getLeft()).setDefaultValue(10).build(),
                                        entryBuilder.startIntField(new TextComponent("Right"), newDefaultElemValue.getRight()).setDefaultValue(10).build()),
                                true);
                    } else {
                        return new MultiElementListEntry<>(new TextComponent("Pair"), elem,
                                Lists.newArrayList(entryBuilder.startIntField(new TextComponent("Left"), elem.getLeft()).setDefaultValue(10).build(),
                                        entryBuilder.startIntField(new TextComponent("Right"), elem.getRight()).setDefaultValue(10).build()),
                                true);
                    }
                }
        ));
        builder.transparentBackground();
        return builder;
    }
}
