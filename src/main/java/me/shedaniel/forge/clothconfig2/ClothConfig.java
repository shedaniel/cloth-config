package me.shedaniel.forge.clothconfig2;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod("cloth-config2")
public class ClothConfig {
    public ClothConfig() {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> new ClothConfigInitializer());
    }
}
