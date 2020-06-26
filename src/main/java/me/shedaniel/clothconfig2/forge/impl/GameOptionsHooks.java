package me.shedaniel.clothconfig2.forge.impl;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface GameOptionsHooks {
    void cloth_setKeysAll(KeyBinding[] all);
}
