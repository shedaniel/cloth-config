package me.shedaniel.clothconfig2.api;

import me.shedaniel.clothconfig2.impl.FakeModifierKeyCodeAdderImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.options.KeyBinding;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public interface FakeModifierKeyCodeAdder {
    FakeModifierKeyCodeAdder INSTANCE = new FakeModifierKeyCodeAdderImpl();
    
    void registerModifierKeyCode(String category, String translationKey, Supplier<ModifierKeyCode> keyCode, Supplier<ModifierKeyCode> defaultKeyCode, Consumer<ModifierKeyCode> onChanged);
    
    default void registerModifierKeyCode(String category, String translationKey, Supplier<ModifierKeyCode> keyCode, Consumer<ModifierKeyCode> onChanged) {
        registerModifierKeyCode(category, translationKey, keyCode, keyCode, onChanged);
    }
    
    List<KeyBinding> getFakeBindings();
}
