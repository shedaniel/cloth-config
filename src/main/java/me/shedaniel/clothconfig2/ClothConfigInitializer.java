package me.shedaniel.clothconfig2;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.EasingMethod;
import me.shedaniel.clothconfig2.impl.EasingMethod.EasingMethodImpl;
import me.shedaniel.clothconfig2.impl.EasingMethods;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.util.Properties;

public class ClothConfigInitializer implements ClientModInitializer {
    
    public static final Logger LOGGER = LogManager.getFormatterLogger("ClothConfig");
    private static EasingMethod easingMethod = EasingMethodImpl.QUART;
    private static long scrollDuration = 1000;
    private static double scrollStep = 16;
    private static double bounceBackMultiplier = .93;
    
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
            easingMethod = EasingMethodImpl.QUART;
            scrollDuration = 1000;
            scrollStep = 16;
            bounceBackMultiplier = .93;
            if (!file.exists()) {
                saveConfig();
            }
            Properties properties = new Properties();
            properties.load(new FileInputStream(file));
            String easing = properties.getProperty("easingMethod", "QUART");
            for(EasingMethod value : EasingMethods.getMethods()) {
                if (value.toString().equalsIgnoreCase(easing)) {
                    easingMethod = value;
                    break;
                }
            }
            scrollDuration = Long.parseLong(properties.getProperty("scrollDuration", "1000"));
            scrollStep = Double.parseDouble(properties.getProperty("scrollStep", "16"));
            bounceBackMultiplier = Double.parseDouble(properties.getProperty("bounceBackMultiplier", "0.93"));
        } catch (Exception e) {
            e.printStackTrace();
            easingMethod = EasingMethodImpl.QUART;
            scrollDuration = 1000;
            scrollStep = 16;
            bounceBackMultiplier = .93;
            try {
                if (file.exists())
                    file.delete();
            } catch (Exception ignored) {
            }
            saveConfig();
        }
    }
    
    private static void saveConfig() {
        File file = new File(FabricLoader.getInstance().getConfigDirectory(), "cloth-config2/config.properties");
        try {
            FileWriter writer = new FileWriter(file, false);
            Properties properties = new Properties();
            properties.setProperty("easingMethod", easingMethod.toString());
            properties.setProperty("scrollDuration", scrollDuration + "");
            properties.setProperty("scrollStep", scrollDuration + "");
            properties.setProperty("bounceBackMultiplier", scrollDuration + "");
            properties.store(writer, null);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            easingMethod = EasingMethodImpl.QUART;
            scrollDuration = 1000;
            scrollStep = 16;
            bounceBackMultiplier = .93;
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
                        ConfigBuilder builder = ConfigBuilder.create().setParentScreen(MinecraftClient.getInstance().currentScreen).setTitle("Cloth Mod Config Config");
                        builder.setDefaultBackgroundTexture(new Identifier("minecraft:textures/block/oak_planks.png"));
                        ConfigCategory scrolling = builder.getOrCreateCategory("Scrolling");
                        ConfigEntryBuilder entryBuilder = ConfigEntryBuilder.create();
                        scrolling.addEntry(entryBuilder.startEnumSelector("Easing Method", EasingMethodImpl.class, easingMethod instanceof EasingMethodImpl ? (EasingMethodImpl) easingMethod : EasingMethodImpl.QUART).setDefaultValue(EasingMethodImpl.QUART).setSaveConsumer(o -> easingMethod = (EasingMethod) o).build());
                        scrolling.addEntry(entryBuilder.startLongSlider("Scroll Duration", scrollDuration, 0, 5000).setTextGetter(integer -> {
                            return integer <= 0 ? "Value: Disabled" : (integer > 1500 ? String.format("Value: %.1fs", integer / 1000f) : "Value: " + integer + "ms");
                        }).setDefaultValue(1000).setSaveConsumer(i -> scrollDuration = i).build());
                        scrolling.addEntry(entryBuilder.startDoubleField("Scroll Step", scrollStep).setDefaultValue(16).setSaveConsumer(i -> scrollStep = i).build());
                        scrolling.addEntry(entryBuilder.startDoubleField("Bounce Multiplier", bounceBackMultiplier).setDefaultValue(0.93).setSaveConsumer(i -> bounceBackMultiplier = i).build());
                        builder.setSavingRunnable(() -> {
                            saveConfig();
                        });
                        MinecraftClient.getInstance().openScreen(builder.build());
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
            } catch (Exception e) {
                ClothConfigInitializer.LOGGER.error("[ClothConfig] Failed to add test config override for ModMenu!", e);
            }
        }
    }
    
}
