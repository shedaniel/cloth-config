package me.shedaniel.clothconfig2.mixin;

import me.shedaniel.clothconfig2.impl.KeyBindingHooks;
import net.minecraft.client.options.KeyBinding;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(KeyBinding.class)
public class MixinKeyBinding implements KeyBindingHooks {
    @Shadow @Mutable @Final private String id;
    
    @Override
    public void cloth_setId(String id) {
        this.id = id;
    }
}
