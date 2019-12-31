package me.shedaniel.clothconfig2.api;

import me.shedaniel.clothconfig2.impl.FakeModifierKeyCodeAdderImpl;
import net.minecraft.client.options.KeyBinding;

import java.util.List;
import java.util.function.Consumer;

public interface FakeModifierKeyCodeAdder {
    FakeModifierKeyCodeAdder INSTANCE = new FakeModifierKeyCodeAdderImpl();
    
    void registerModifierKeyCode(String category, String translationKey, ModifierKeyCode keyCode, ModifierKeyCode defaultKeyCode, Consumer<ModifierKeyCode> onChanged);
    
    default void registerModifierKeyCode(String category, String translationKey, ModifierKeyCode keyCode, Consumer<ModifierKeyCode> onChanged) {
        registerModifierKeyCode(category, translationKey, keyCode, keyCode, onChanged);
    }
    
    List<KeyBinding> getFakeBindings();
}
