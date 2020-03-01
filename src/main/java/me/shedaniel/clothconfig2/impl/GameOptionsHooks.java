package me.shedaniel.clothconfig2.impl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.options.KeyBinding;

@Environment(EnvType.CLIENT)
public interface GameOptionsHooks {
    void cloth_setKeysAll(KeyBinding[] all);
}
