package me.shedaniel.clothconfig2.forge;

import com.google.common.collect.Lists;
import me.shedaniel.clothconfig2.forge.api.*;
import me.shedaniel.clothconfig2.forge.gui.entries.MultiElementListEntry;
import me.shedaniel.clothconfig2.forge.gui.entries.NestedListListEntry;
import me.shedaniel.clothconfig2.forge.impl.EasingMethod;
import me.shedaniel.clothconfig2.forge.impl.EasingMethod.EasingMethodImpl;
import me.shedaniel.clothconfig2.forge.impl.builders.DropdownMenuBuilder;
import me.shedaniel.clothconfig2.forge.impl.builders.SubCategoryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class ClothConfigInitializer {
    public static final Logger LOGGER = LogManager.getFormatterLogger("ClothConfig");
    
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public static double handleScrollingPosition(double[] target, double scroll, double maxScroll, float delta, double start, double duration) {
        return ScrollingContainer.handleScrollingPosition(target, scroll, maxScroll, delta, start, duration);
    }
    
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public static double expoEase(double start, double end, double amount) {
        return ScrollingContainer.ease(start, end, amount, getEasingMethod());
    }
    
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public static double clamp(double v, double maxScroll) {
        return ScrollingContainer.clampExtension(v, maxScroll);
    }
    
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public static double clamp(double v, double maxScroll, double clampExtension) {
        return ScrollingContainer.clampExtension(v, -clampExtension, maxScroll + clampExtension);
    }
    
    public static EasingMethod getEasingMethod() {
        return EasingMethodImpl.NONE;
    }
    
    public static long getScrollDuration() {
        return 0;
    }
    
    public static double getScrollStep() {
        return 16.0;
    }
    
    public static double getBounceBackMultiplier() {
        return -10;
    }
    
    public static void registerModsPage() {
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (client, parent) -> {
            return getConfigBuilderWithDemo(parent).build();
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
        
        ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle(new TranslationTextComponent("title.cloth-config.config"));
        builder.setDefaultBackgroundTexture(new ResourceLocation("minecraft:textures/block/oak_planks.png"));
//        builder.setGlobalized(true);
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory testing = builder.getOrCreateCategory(new TranslationTextComponent("category.cloth-config.testing"));
        testing.addEntry(entryBuilder.startKeyCodeField(new StringTextComponent("Cool Key"), InputMappings.INPUT_INVALID).setDefaultValue(InputMappings.INPUT_INVALID).build());
        testing.addEntry(entryBuilder.startModifierKeyCodeField(new StringTextComponent("Cool Modifier Key"), ModifierKeyCode.of(InputMappings.Type.KEYSYM.getOrMakeInput(79), Modifier.of(false, true, false))).setDefaultValue(ModifierKeyCode.of(InputMappings.Type.KEYSYM.getOrMakeInput(79), Modifier.of(false, true, false))).build());
        testing.addEntry(entryBuilder.startDoubleList(new StringTextComponent("A list of Doubles"), Arrays.asList(1d, 2d, 3d)).setDefaultValue(Arrays.asList(1d, 2d, 3d)).build());
        testing.addEntry(entryBuilder.startLongList(new StringTextComponent("A list of Longs"), Arrays.asList(1L, 2L, 3L)).setDefaultValue(Arrays.asList(1L, 2L, 3L)).build());
        testing.addEntry(entryBuilder.startStrList(new StringTextComponent("A list of Strings"), Arrays.asList("abc", "xyz")).setDefaultValue(Arrays.asList("abc", "xyz")).build());
        SubCategoryBuilder colors = entryBuilder.startSubCategory(new StringTextComponent("Colors")).setExpanded(true);
        colors.add(entryBuilder.startColorField(new StringTextComponent("A color field"), 0x00ffff).setDefaultValue(0x00ffff).build());
        colors.add(entryBuilder.startColorField(new StringTextComponent("An alpha color field"), 0xff00ffff).setDefaultValue(0xff00ffff).setAlphaMode(true).build());
        colors.add(entryBuilder.startColorField(new StringTextComponent("An alpha color field"), 0xffffffff).setDefaultValue(0xffff0000).setAlphaMode(true).build());
        colors.add(entryBuilder.startDropdownMenu(new StringTextComponent("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        colors.add(entryBuilder.startDropdownMenu(new StringTextComponent("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        colors.add(entryBuilder.startDropdownMenu(new StringTextComponent("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        colors.add(entryBuilder.startDropdownMenu(new StringTextComponent("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        colors.add(entryBuilder.startDropdownMenu(new StringTextComponent("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        SubCategoryBuilder innerColors = entryBuilder.startSubCategory(new StringTextComponent("Inner Colors")).setExpanded(true);
        innerColors.add(entryBuilder.startDropdownMenu(new StringTextComponent("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        innerColors.add(entryBuilder.startDropdownMenu(new StringTextComponent("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        innerColors.add(entryBuilder.startDropdownMenu(new StringTextComponent("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        SubCategoryBuilder innerInnerColors = entryBuilder.startSubCategory(new StringTextComponent("Inner Inner Colors")).setExpanded(true);
        innerInnerColors.add(entryBuilder.startDropdownMenu(new StringTextComponent("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        innerInnerColors.add(entryBuilder.startDropdownMenu(new StringTextComponent("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        innerInnerColors.add(entryBuilder.startDropdownMenu(new StringTextComponent("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        innerColors.add(innerInnerColors.build());
        colors.add(innerColors.build());
        testing.addEntry(colors.build());
        testing.addEntry(entryBuilder.startDropdownMenu(new StringTextComponent("Suggestion Random Int"), DropdownMenuBuilder.TopCellElementBuilder.of(10,
                s -> {
                    try {
                        return Integer.parseInt(s);
                    } catch (NumberFormatException ignored) {
                        
                    }
                    return null;
                })).setDefaultValue(10).setSelections(Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)).build());
        testing.addEntry(entryBuilder.startDropdownMenu(new StringTextComponent("Selection Random Int"), DropdownMenuBuilder.TopCellElementBuilder.of(10,
                s -> {
                    try {
                        return Integer.parseInt(s);
                    } catch (NumberFormatException ignored) {
                        
                    }
                    return null;
                })).setDefaultValue(5).setSuggestionMode(false).setSelections(Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)).build());
        testing.addEntry(new NestedListListEntry<Pair<Integer, Integer>, MultiElementListEntry<Pair<Integer, Integer>>>(
                new StringTextComponent("Nice"),
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
                        return new MultiElementListEntry<>(new StringTextComponent("Pair"), newDefaultElemValue,
                                Lists.newArrayList(entryBuilder.startIntField(new StringTextComponent("Left"), newDefaultElemValue.getLeft()).setDefaultValue(10).build(),
                                        entryBuilder.startIntField(new StringTextComponent("Right"), newDefaultElemValue.getRight()).setDefaultValue(10).build()),
                                true);
                    } else {
                        return new MultiElementListEntry<>(new StringTextComponent("Pair"), elem,
                                Lists.newArrayList(entryBuilder.startIntField(new StringTextComponent("Left"), elem.getLeft()).setDefaultValue(10).build(),
                                        entryBuilder.startIntField(new StringTextComponent("Right"), elem.getRight()).setDefaultValue(10).build()),
                                true);
                    }
                }
        ));
        builder.transparentBackground();
        return builder;
    }
}
