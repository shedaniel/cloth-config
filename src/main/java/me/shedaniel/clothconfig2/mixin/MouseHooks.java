package me.shedaniel.clothconfig2.mixin;

import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Mouse.class)
public interface MouseHooks {
    @Accessor("middleButtonClicked")
    boolean middleButtonClicked();
}
