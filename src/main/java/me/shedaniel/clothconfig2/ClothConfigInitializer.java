package me.shedaniel.clothconfig2;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import me.shedaniel.clothconfig2.api.*;
import me.shedaniel.clothconfig2.gui.entries.*;
import me.shedaniel.clothconfig2.impl.EasingMethod;
import me.shedaniel.clothconfig2.impl.EasingMethod.EasingMethodImpl;
import me.shedaniel.clothconfig2.impl.EasingMethods;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class ClothConfigInitializer implements ClientModInitializer {
    
    public static final Logger LOGGER = LogManager.getFormatterLogger("ClothConfig");
    private static EasingMethod easingMethod = EasingMethodImpl.LINEAR;
    private static long scrollDuration = 600;
    private static double scrollStep = 19;
    private static double bounceBackMultiplier = .24;
    
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
        return easingMethod;
    }
    
    public static long getScrollDuration() {
        return scrollDuration;
    }
    
    public static double getScrollStep() {
        return scrollStep;
    }
    
    public static double getBounceBackMultiplier() {
        return bounceBackMultiplier;
    }
    
    private static void loadConfig() {
        File file = new File(FabricLoader.getInstance().getConfigDirectory(), "cloth-config2/config.properties");
        try {
            file.getParentFile().mkdirs();
            easingMethod = EasingMethodImpl.LINEAR;
            scrollDuration = 600;
            scrollStep = 19;
            bounceBackMultiplier = .24;
            if (!file.exists()) {
                saveConfig();
            }
            Properties properties = new Properties();
            properties.load(new FileInputStream(file));
            String easing = properties.getProperty("easingMethod1", "LINEAR");
            for (EasingMethod value : EasingMethods.getMethods()) {
                if (value.toString().equalsIgnoreCase(easing)) {
                    easingMethod = value;
                    break;
                }
            }
            scrollDuration = Long.parseLong(properties.getProperty("scrollDuration1", "600"));
            scrollStep = Double.parseDouble(properties.getProperty("scrollStep1", "19"));
            bounceBackMultiplier = Double.parseDouble(properties.getProperty("bounceBackMultiplier2", "0.24"));
        } catch (Exception e) {
            e.printStackTrace();
            easingMethod = EasingMethodImpl.LINEAR;
            scrollDuration = 600;
            scrollStep = 19;
            bounceBackMultiplier = .24;
            try {
                Files.deleteIfExists(file.toPath());
            } catch (Exception ignored) {
            }
        }
        saveConfig();
    }
    
    private static void saveConfig() {
        File file = new File(FabricLoader.getInstance().getConfigDirectory(), "cloth-config2/config.properties");
        try {
            FileWriter writer = new FileWriter(file, false);
            Properties properties = new Properties();
            properties.setProperty("easingMethod1", easingMethod.toString());
            properties.setProperty("scrollDuration1", scrollDuration + "");
            properties.setProperty("scrollStep1", scrollStep + "");
            properties.setProperty("bounceBackMultiplier2", bounceBackMultiplier + "");
            properties.store(writer, null);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            easingMethod = EasingMethodImpl.LINEAR;
            scrollDuration = 600;
            scrollStep = 19;
            bounceBackMultiplier = .24;
        }
    }
    
    @Override
    public void onInitializeClient() {
        loadConfig();
        if (FabricLoader.getInstance().isModLoaded("modmenu")) {
            try {
                Class<?> clazz = Class.forName("io.github.prospector.modmenu.api.ModMenuApi");
                Method method = clazz.getMethod("addConfigOverride", String.class, Runnable.class);
                method.invoke(null, "cloth-config2", (Runnable) () -> {
                    try {
                        MinecraftClient.getInstance().openScreen(getConfigBuilderWithDemo().build());
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
            } catch (Exception e) {
                ClothConfigInitializer.LOGGER.error("[ClothConfig] Failed to add test config override for ModMenu!", e);
            }
        }
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            try {
                KeyBindingRegistryImpl.INSTANCE.addCategory("Cloth Test Keybinds");
                FakeModifierKeyCodeAdder.INSTANCE.registerModifierKeyCode("Cloth Test Keybinds", "Keybind 1",
                        ModifierKeyCode::unknown, ModifierKeyCode::unknown, System.out::println);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }
    
    @SuppressWarnings("deprecation")
    public static ConfigBuilder getConfigBuilder() {
        ConfigBuilder builder = ConfigBuilder.create().setParentScreen(MinecraftClient.getInstance().currentScreen).setTitle(new TranslatableText("title.cloth-config.config"));
        builder.setDefaultBackgroundTexture(new Identifier("minecraft:textures/block/oak_planks.png"));
        ConfigCategory scrolling = builder.getOrCreateCategory(new TranslatableText("category.cloth-config.scrolling"));
        ConfigEntryBuilder entryBuilder = ConfigEntryBuilder.create();
        DropdownBoxEntry<EasingMethod> easingMethodEntry = entryBuilder.startDropdownMenu(new LiteralText("Easing Method"), DropdownMenuBuilder.TopCellElementBuilder.of(easingMethod, str -> {
            for (EasingMethod m : EasingMethods.getMethods())
                if (m.toString().equals(str))
                    return m;
            return null;
        })).setDefaultValue(EasingMethodImpl.LINEAR).setSaveConsumer(o -> easingMethod = o).setSelections(EasingMethods.getMethods()).build();
        LongSliderEntry scrollDurationEntry = entryBuilder.startLongSlider(new TranslatableText("option.cloth-config.scrollDuration"), scrollDuration, 0, 5000).setTextGetter(integer -> new LiteralText(integer <= 0 ? "Value: Disabled" : (integer > 1500 ? String.format("Value: %.1fs", integer / 1000f) : "Value: " + integer + "ms"))).setDefaultValue(600).setSaveConsumer(i -> scrollDuration = i).build();
        DoubleListEntry scrollStepEntry = entryBuilder.startDoubleField(new TranslatableText("option.cloth-config.scrollStep"), scrollStep).setDefaultValue(19).setSaveConsumer(i -> scrollStep = i).build();
        LongSliderEntry bounceMultiplierEntry = entryBuilder.startLongSlider(new TranslatableText("option.cloth-config.bounceBackMultiplier"), (long) (bounceBackMultiplier * 1000), -10, 750).setTextGetter(integer -> new LiteralText(integer < 0 ? "Value: Disabled" : String.format("Value: %s", integer / 1000d))).setDefaultValue(240).setSaveConsumer(i -> bounceBackMultiplier = i / 1000d).build();
        scrolling.addEntry(new TooltipListEntry<Object>(new TranslatableText("option.cloth-config.setDefaultSmoothScroll"), null) {
            final int width = 220;
            private final AbstractButtonWidget buttonWidget = new AbstractPressableButtonWidget(0, 0, 0, 20, getFieldName()) {
                @Override
                public void onPress() {
                    easingMethodEntry.getSelectionElement().getTopRenderer().setValue(EasingMethodImpl.LINEAR);
                    scrollDurationEntry.setValue(600);
                    scrollStepEntry.setValue("19.0");
                    bounceMultiplierEntry.setValue(240);
                }
            };
            private final List<AbstractButtonWidget> children = ImmutableList.of(buttonWidget);
            
            @Override
            public Object getValue() {
                return null;
            }
            
            @Override
            public Optional<Object> getDefaultValue() {
                return Optional.empty();
            }
            
            @Override
            public void save() {
            }
            
            @Override
            public List<? extends Element> children() {
                return children;
            }
            
            @Override
            public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
                super.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isSelected, delta);
                Window window = MinecraftClient.getInstance().getWindow();
                this.buttonWidget.active = this.isEditable();
                this.buttonWidget.y = y;
                this.buttonWidget.x = x + entryWidth / 2 - width / 2;
                this.buttonWidget.setWidth(width);
                this.buttonWidget.render(matrices, mouseX, mouseY, delta);
            }
        });
        
        scrolling.addEntry(new TooltipListEntry<Object>(new TranslatableText("option.cloth-config.disableSmoothScroll"), null) {
            final int width = 220;
            private final AbstractButtonWidget buttonWidget = new AbstractPressableButtonWidget(0, 0, 0, 20, getFieldName()) {
                @Override
                public void onPress() {
                    easingMethodEntry.getSelectionElement().getTopRenderer().setValue(EasingMethodImpl.NONE);
                    scrollDurationEntry.setValue(0);
                    scrollStepEntry.setValue("16.0");
                    bounceMultiplierEntry.setValue(-10);
                }
            };
            private final List<AbstractButtonWidget> children = ImmutableList.of(buttonWidget);
            
            @Override
            public Object getValue() {
                return null;
            }
            
            @Override
            public Optional<Object> getDefaultValue() {
                return Optional.empty();
            }
            
            @Override
            public void save() {
            }
            
            @Override
            public List<? extends Element> children() {
                return children;
            }
            
            @Override
            public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
                super.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isSelected, delta);
                Window window = MinecraftClient.getInstance().getWindow();
                this.buttonWidget.active = this.isEditable();
                this.buttonWidget.y = y;
                this.buttonWidget.x = x + entryWidth / 2 - width / 2;
                this.buttonWidget.setWidth(width);
                this.buttonWidget.render(matrices, mouseX, mouseY, delta);
            }
        });
        scrolling.addEntry(easingMethodEntry);
        scrolling.addEntry(scrollDurationEntry);
        scrolling.addEntry(scrollStepEntry);
        scrolling.addEntry(bounceMultiplierEntry);
        builder.setSavingRunnable(ClothConfigInitializer::saveConfig);
        builder.transparentBackground();
        return builder;
    }
    
    public static ConfigBuilder getConfigBuilderWithDemo() {
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
        
        ConfigBuilder builder = getConfigBuilder();
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory testing = builder.getOrCreateCategory(new TranslatableText("category.cloth-config.testing"));
        testing.addEntry(entryBuilder.startKeyCodeField(new LiteralText("Cool Key"), InputUtil.UNKNOWN_KEYCODE).setDefaultValue(InputUtil.UNKNOWN_KEYCODE).build());
        testing.addEntry(entryBuilder.startModifierKeyCodeField(new LiteralText("Cool Modifier Key"), ModifierKeyCode.of(InputUtil.Type.KEYSYM.createFromCode(79), Modifier.of(false, true, false))).setDefaultValue(ModifierKeyCode.of(InputUtil.Type.KEYSYM.createFromCode(79), Modifier.of(false, true, false))).build());
        testing.addEntry(entryBuilder.startDoubleList(new LiteralText("A list of Doubles"), Arrays.asList(1d, 2d, 3d)).setDefaultValue(Arrays.asList(1d, 2d, 3d)).build());
        testing.addEntry(entryBuilder.startLongList(new LiteralText("A list of Longs"), Arrays.asList(1L, 2L, 3L)).setDefaultValue(Arrays.asList(1L, 2L, 3L)).build());
        testing.addEntry(entryBuilder.startStrList(new LiteralText("A list of Strings"), Arrays.asList("abc", "xyz")).setDefaultValue(Arrays.asList("abc", "xyz")).build());
        SubCategoryBuilder colors = entryBuilder.startSubCategory(new LiteralText("Colors")).setExpanded(true);
        colors.add(entryBuilder.startColorField(new LiteralText("A color field"), 0x00ffff).setDefaultValue(0x00ffff).build());
        colors.add(entryBuilder.startColorField(new LiteralText("An alpha color field"), 0xff00ffff).setDefaultValue(0xff00ffff).setAlphaMode(true).build());
        colors.add(entryBuilder.startColorField(new LiteralText("An alpha color field"), 0xffffffff).setDefaultValue(0xffff0000).setAlphaMode(true).build());
        colors.add(entryBuilder.startDropdownMenu(new LiteralText("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        colors.add(entryBuilder.startDropdownMenu(new LiteralText("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        colors.add(entryBuilder.startDropdownMenu(new LiteralText("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        colors.add(entryBuilder.startDropdownMenu(new LiteralText("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        colors.add(entryBuilder.startDropdownMenu(new LiteralText("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        testing.addEntry(colors.build());
        testing.addEntry(entryBuilder.startDropdownMenu(new LiteralText("Suggestion Random Int"), DropdownMenuBuilder.TopCellElementBuilder.of(10,
                s -> {
                    try {
                        return Integer.parseInt(s);
                    } catch (NumberFormatException ignored) {
                        
                    }
                    return null;
                })).setDefaultValue(10).setSelections(Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)).build());
        testing.addEntry(entryBuilder.startDropdownMenu(new LiteralText("Selection Random Int"), DropdownMenuBuilder.TopCellElementBuilder.of(10,
                s -> {
                    try {
                        return Integer.parseInt(s);
                    } catch (NumberFormatException ignored) {
                        
                    }
                    return null;
                })).setDefaultValue(5).setSuggestionMode(false).setSelections(Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)).build());
        testing.addEntry(new NestedListListEntry<Pair<Integer, Integer>, MultiElementListEntry<Pair<Integer, Integer>>>(
                new LiteralText("Nice"),
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
                        return new MultiElementListEntry<>(new LiteralText("Pair"), newDefaultElemValue,
                                Lists.newArrayList(entryBuilder.startIntField(new LiteralText("Left"), newDefaultElemValue.getLeft()).setDefaultValue(10).build(),
                                        entryBuilder.startIntField(new LiteralText("Right"), newDefaultElemValue.getRight()).setDefaultValue(10).build()),
                                true);
                    } else {
                        return new MultiElementListEntry<>(new LiteralText("Pair"), elem,
                                Lists.newArrayList(entryBuilder.startIntField(new LiteralText("Left"), elem.getLeft()).setDefaultValue(10).build(),
                                        entryBuilder.startIntField(new LiteralText("Right"), elem.getRight()).setDefaultValue(10).build()),
                                true);
                    }
                }
        ));
        return builder;
    }
    
    public static class Precision {
        public static final float FLOAT_EPSILON = 1e-3f;
        public static final double DOUBLE_EPSILON = 1e-7;
        
        public static boolean almostEquals(float value1, float value2, float acceptableDifference) {
            return Math.abs(value1 - value2) <= acceptableDifference;
        }
        
        public static boolean almostEquals(double value1, double value2, double acceptableDifference) {
            return Math.abs(value1 - value2) <= acceptableDifference;
        }
    }
    
}
