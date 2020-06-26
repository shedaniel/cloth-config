package me.shedaniel.clothconfig2.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod("cloth-config")
public class ClothConfig {
    public ClothConfig() {
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClothConfigInitializer::registerModsPage);
    }
}
