package me.shedaniel.clothconfig2.mixin;

import me.shedaniel.clothconfig2.api.FakeModifierKeyCodeAdder;
import me.shedaniel.clothconfig2.impl.FakeKeyBindings;
import me.shedaniel.clothconfig2.impl.GameOptionsHooks;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import net.minecraft.client.gui.screen.options.GameOptionsScreen;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ControlsOptionsScreen.class)
public class MixinControlsOptionsScreen extends GameOptionsScreen {
    public MixinControlsOptionsScreen(Screen screen_1, GameOptions gameOptions_1, Text text_1) {
        super(screen_1, gameOptions_1, text_1);
    }
    
    @Inject(method = "init()V", at = @At("HEAD"))
    private void initHead(CallbackInfo info) {
        List<KeyBinding> newKeysAll = new ArrayList<>();
        KeyBinding[] var3 = client.options.keysAll;
        
        for (KeyBinding binding : var3) {
            if (!(binding instanceof FakeKeyBindings)) {
                newKeysAll.add(binding);
            }
        }
        
        newKeysAll.addAll(FakeModifierKeyCodeAdder.INSTANCE.getFakeBindings());
        ((GameOptionsHooks) client.options).cloth_setKeysAll(newKeysAll.toArray(new KeyBinding[0]));
    }
    
    @Inject(method = "init()V", at = @At("RETURN"))
    private void initReturn(CallbackInfo info) {
        List<KeyBinding> newKeysAll = new ArrayList<>();
        KeyBinding[] var3 = client.options.keysAll;
        for (KeyBinding binding : var3) {
            if (!(binding instanceof FakeKeyBindings)) {
                newKeysAll.add(binding);
            }
        }
        ((GameOptionsHooks) client.options).cloth_setKeysAll(newKeysAll.toArray(new KeyBinding[0]));
    }
}
