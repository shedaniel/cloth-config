package me.shedaniel.clothconfig;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import me.shedaniel.cloth.api.ConfigScreenBuilder;
import me.shedaniel.cloth.gui.ClothConfigScreen;
import me.shedaniel.cloth.gui.entries.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

public class ClothConfigInitializer implements ClientModInitializer {
    
    public static final Logger LOGGER = LogManager.getFormatterLogger("ClothConfig");
    
    @Override
    public void onInitializeClient() {
        if (FabricLoader.getInstance().isModLoaded("modmenu")) {
            try {
                Class<?> clazz = Class.forName("io.github.prospector.modmenu.api.ModMenuApi");
                Method method = clazz.getMethod("addConfigOverride", String.class, Runnable.class);
                method.invoke(null, "cloth-config", (Runnable) () -> {
                    ConfigScreenBuilder builder = ConfigScreenBuilder.create(MinecraftClient.getInstance().currentScreen, "Cloth Mod Config Demo", null);
                    ConfigScreenBuilder.CategoryBuilder playZone = builder.addCategory("Play Zone");
                    playZone.addOption(new BooleanListEntry("Simple Boolean", false, "text.cloth-config.reset_value", null, null));
                    playZone.addOption(new StringListEntry("String Field", "ab", "text.cloth-config.reset_value", () -> "ab", null));
                    playZone.addOption(new LongSliderEntry("Long Slider", -10, 10, 0, null, "text.cloth-config.reset_value", () -> 0l));
                    playZone.addOption(new IntegerListEntry("Integer Field", 2, "text.cloth-config.reset_value", () -> 2, null).setMaximum(99).setMinimum(2));
                    List<ClothConfigScreen.AbstractListEntry> randomCategory = Lists.newArrayList();
                    randomCategory.add(new TextListEntry("x", "§7This is a promotional message brought to you by Danielshe. Shop your favorite Lil Tater at store.liltater.com!", -1, () -> Optional.of(new String[]{"This is an example tooltip."})));
                    randomCategory.add(new SubCategoryListEntry("Sub-Sub-Category", ImmutableList.of(new EnumListEntry<DemoEnum>("Enum Field No. 1", DemoEnum.class, DemoEnum.CONSTANT_2, "text.cloth-config.reset_value", () -> DemoEnum.CONSTANT_1, null), new EnumListEntry<DemoEnum>("Enum Field No. 2", DemoEnum.class, DemoEnum.CONSTANT_2, "text.cloth-config.reset_value", () -> DemoEnum.CONSTANT_1, null)), false));
                    for(int i = 0; i < 10; i++)
                        randomCategory.add(new IntegerSliderEntry("Integer Slider No. " + (i + 1), -99, 99, 0, "text.cloth-config.reset_value", () -> 0, null));
                    playZone.addOption(new SubCategoryListEntry("Random Sub-Category", randomCategory, false));
                    ConfigScreenBuilder.CategoryBuilder enumZone = builder.addCategory("Enum Zone");
                    enumZone.setBackgroundTexture(new Identifier("minecraft:textures/block/stone.png"));
                    enumZone.addOption(new EnumListEntry<DemoEnum>("Enum Field", DemoEnum.class, DemoEnum.CONSTANT_2, "text.cloth-config.reset_value", () -> DemoEnum.CONSTANT_1, null));
                    MinecraftClient.getInstance().openScreen(builder.build());
                });
            } catch (Exception e) {
                ClothConfigInitializer.LOGGER.error("[ClothConfig] Failed to add test config override for ModMenu!", e);
            }
        }
    }
    
    private static enum DemoEnum {
        CONSTANT_1("Constant 1"),
        CONSTANT_2("Constant 2"),
        CONSTANT_3("Constant 3");
        
        private final String key;
        
        private DemoEnum(String key) {
            this.key = key;
        }
        
        @Override
        public String toString() {
            return this.key;
        }
    }
    
}
