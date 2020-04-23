package me.shedaniel.forge.clothconfig2;

import com.google.common.collect.ImmutableList;
import me.shedaniel.forge.clothconfig2.api.*;
import me.shedaniel.forge.clothconfig2.gui.entries.DoubleListEntry;
import me.shedaniel.forge.clothconfig2.gui.entries.DropdownBoxEntry;
import me.shedaniel.forge.clothconfig2.gui.entries.LongSliderEntry;
import me.shedaniel.forge.clothconfig2.gui.entries.TooltipListEntry;
import me.shedaniel.forge.clothconfig2.gui.widget.DynamicEntryListWidget;
import me.shedaniel.forge.clothconfig2.impl.EasingMethod;
import me.shedaniel.forge.clothconfig2.impl.EasingMethod.EasingMethodImpl;
import me.shedaniel.forge.clothconfig2.impl.EasingMethods;
import me.shedaniel.forge.clothconfig2.impl.builders.DropdownMenuBuilder;
import me.shedaniel.forge.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class ClothConfigInitializer {
    
    public static final Logger LOGGER = LogManager.getFormatterLogger("ClothConfig");
    private static EasingMethod easingMethod = EasingMethodImpl.LINEAR;
    private static long scrollDuration = 600;
    private static double scrollStep = 19;
    private static double bounceBackMultiplier = .24;
    
    public static double handleScrollingPosition(double[] target, double scroll, double maxScroll, float delta, double start, double duration) {
        if (getBounceBackMultiplier() >= 0) {
            target[0] = clamp(target[0], maxScroll);
            if (target[0] < 0) {
                target[0] -= target[0] * (1 - getBounceBackMultiplier()) * delta / 3;
            } else if (target[0] > maxScroll) {
                target[0] = (target[0] - maxScroll) * (1 - (1 - getBounceBackMultiplier()) * delta / 3) + maxScroll;
            }
        } else
            target[0] = clamp(target[0], maxScroll, 0);
        if (!Precision.almostEquals(scroll, target[0], Precision.FLOAT_EPSILON))
            return expoEase(scroll, target[0], Math.min((System.currentTimeMillis() - start) / duration * delta * 3, 1));
        else
            return target[0];
    }
    
    public static double expoEase(double start, double end, double amount) {
        return start + (end - start) * getEasingMethod().apply(amount);
    }
    
    public static double clamp(double v, double maxScroll) {
        return clamp(v, maxScroll, DynamicEntryListWidget.SmoothScrollingSettings.CLAMP_EXTENSION);
    }
    
    public static double clamp(double v, double maxScroll, double clampExtension) {
        return MathHelper.clamp(v, -clampExtension, maxScroll + clampExtension);
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
        File file = new File(FMLPaths.CONFIGDIR.get().toFile(), "cloth-config2/config.properties");
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
        File file = new File(FMLPaths.CONFIGDIR.get().toFile(), "cloth-config2/config.properties");
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
    
    public ClothConfigInitializer() {
        loadConfig();
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (client, parent) -> {
            return getConfigBuilderWithDemo().build();
        });
    }
    
    @SuppressWarnings("deprecation")
    public static ConfigBuilder getConfigBuilder() {
        ConfigBuilder builder = ConfigBuilder.create().setParentScreen(Minecraft.getInstance().currentScreen).setTitle("title.cloth-config.config");
        builder.setDefaultBackgroundTexture(new ResourceLocation("minecraft:textures/block/oak_planks.png"));
        ConfigCategory scrolling = builder.getOrCreateCategory("category.cloth-config.scrolling");
        ConfigEntryBuilder entryBuilder = ConfigEntryBuilder.create();
        DropdownBoxEntry<EasingMethod> easingMethodEntry = entryBuilder.startDropdownMenu("Easing Method", DropdownMenuBuilder.TopCellElementBuilder.of(easingMethod, str -> {
            for (EasingMethod m : EasingMethods.getMethods())
                if (m.toString().equals(str))
                    return m;
            return null;
        })).setDefaultValue(EasingMethodImpl.LINEAR).setSaveConsumer(o -> easingMethod = o).setSelections(EasingMethods.getMethods()).build();
        LongSliderEntry scrollDurationEntry = entryBuilder.startLongSlider("option.cloth-config.scrollDuration", scrollDuration, 0, 5000).setTextGetter(integer -> integer <= 0 ? "Value: Disabled" : (integer > 1500 ? String.format("Value: %.1fs", integer / 1000f) : "Value: " + integer + "ms")).setDefaultValue(600).setSaveConsumer(i -> scrollDuration = i).build();
        DoubleListEntry scrollStepEntry = entryBuilder.startDoubleField("option.cloth-config.scrollStep", scrollStep).setDefaultValue(19).setSaveConsumer(i -> scrollStep = i).build();
        LongSliderEntry bounceMultiplierEntry = entryBuilder.startLongSlider("option.cloth-config.bounceBackMultiplier", (long) (bounceBackMultiplier * 1000), -10, 750).setTextGetter(integer -> integer < 0 ? "Value: Disabled" : String.format("Value: %s", integer / 1000d)).setDefaultValue(240).setSaveConsumer(i -> bounceBackMultiplier = i / 1000d).build();
        
        scrolling.addEntry(new TooltipListEntry<Object>(I18n.format("option.cloth-config.setDefaultSmoothScroll"), null) {
            final int width = 220;
            private final Widget buttonWidget = new AbstractButton(0, 0, 0, 20, getFieldName()) {
                @Override
                public void onPress() {
                    easingMethodEntry.getSelectionElement().getTopRenderer().setValue(EasingMethodImpl.LINEAR);
                    scrollDurationEntry.setValue(600);
                    scrollStepEntry.setValue("19.0");
                    bounceMultiplierEntry.setValue(240);
                    getScreen().setEdited(true, isRequiresRestart());
                }
            };
            private final List<Widget> children = ImmutableList.of(buttonWidget);
            
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
            public List<? extends IGuiEventListener> children() {
                return children;
            }
            
            @Override
            public void render(int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
                super.render(index, y, x, entryWidth, entryHeight, mouseX, mouseY, isSelected, delta);
                MainWindow window = Minecraft.getInstance().getMainWindow();
                this.buttonWidget.active = this.isEditable();
                this.buttonWidget.y = y;
                this.buttonWidget.x = x + entryWidth / 2 - width / 2;
                this.buttonWidget.setWidth(width);
                this.buttonWidget.render(mouseX, mouseY, delta);
            }
        });
        
        scrolling.addEntry(new TooltipListEntry<Object>(I18n.format("option.cloth-config.disableSmoothScroll"), null) {
            final int width = 220;
            private final Widget buttonWidget = new AbstractButton(0, 0, 0, 20, getFieldName()) {
                @Override
                public void onPress() {
                    easingMethodEntry.getSelectionElement().getTopRenderer().setValue(EasingMethodImpl.NONE);
                    scrollDurationEntry.setValue(0);
                    scrollStepEntry.setValue("16.0");
                    bounceMultiplierEntry.setValue(-10);
                    getScreen().setEdited(true, isRequiresRestart());
                }
            };
            private final List<Widget> children = ImmutableList.of(buttonWidget);
            
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
            public List<? extends IGuiEventListener> children() {
                return children;
            }
            
            @Override
            public void render(int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
                super.render(index, y, x, entryWidth, entryHeight, mouseX, mouseY, isSelected, delta);
                MainWindow window = Minecraft.getInstance().getMainWindow();
                this.buttonWidget.active = this.isEditable();
                this.buttonWidget.y = y;
                this.buttonWidget.x = x + entryWidth / 2 - width / 2;
                this.buttonWidget.setWidth(width);
                this.buttonWidget.render(mouseX, mouseY, delta);
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
        ConfigBuilder builder = getConfigBuilder();
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory testing = builder.getOrCreateCategory("category.cloth-config.testing");
//        testing.addEntry(entryBuilder.startDropdownMenu("lol apple", DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        testing.addEntry(entryBuilder.startKeyCodeField("Cool Key", InputMappings.INPUT_INVALID).setDefaultValue(InputMappings.INPUT_INVALID).build());
        testing.addEntry(entryBuilder.startModifierKeyCodeField("Cool Modifier Key", ModifierKeyCode.of(InputMappings.Type.KEYSYM.getOrMakeInput(79), Modifier.of(false, true, false))).setDefaultValue(ModifierKeyCode.of(InputMappings.Type.KEYSYM.getOrMakeInput(79), Modifier.of(false, true, false))).build());
        testing.addEntry(entryBuilder.startDoubleList("A list of Doubles", Arrays.asList(1d, 2d, 3d)).setDefaultValue(Arrays.asList(1d, 2d, 3d)).build());
        testing.addEntry(entryBuilder.startLongList("A list of Longs", Arrays.asList(1L, 2L, 3L)).setDefaultValue(Arrays.asList(1L, 2L, 3L)).build());
        testing.addEntry(entryBuilder.startStrList("A list of Strings", Arrays.asList("abc", "xyz")).setDefaultValue(Arrays.asList("abc", "xyz")).build());
        SubCategoryBuilder colors = entryBuilder.startSubCategory("Colors").setExpanded(true);
        colors.add(entryBuilder.startColorField("A color field", 0x00ffff).setDefaultValue(0x00ffff).build());
        colors.add(entryBuilder.startColorField("An alpha color field", 0xff00ffff).setDefaultValue(0xff00ffff).setAlphaMode(true).build());
        colors.add(entryBuilder.startDropdownMenu("lol apple", DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        colors.add(entryBuilder.startDropdownMenu("lol apple", DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        colors.add(entryBuilder.startDropdownMenu("lol apple", DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        colors.add(entryBuilder.startDropdownMenu("lol apple", DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        colors.add(entryBuilder.startDropdownMenu("lol apple", DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        testing.addEntry(colors.build());
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