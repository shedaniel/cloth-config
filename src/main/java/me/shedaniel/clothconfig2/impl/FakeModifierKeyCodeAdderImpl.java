package me.shedaniel.clothconfig2.impl;

import me.shedaniel.clothconfig2.api.FakeModifierKeyCodeAdder;
import me.shedaniel.clothconfig2.api.ModifierKeyCode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.options.KeyBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class FakeModifierKeyCodeAdderImpl implements FakeModifierKeyCodeAdder {
    private final List<Entry> entryList = new ArrayList<>();
    
    @Override
    public void registerModifierKeyCode(String category, String translationKey, Supplier<ModifierKeyCode> keyCode, Supplier<ModifierKeyCode> defaultKeyCode, Consumer<ModifierKeyCode> onChanged) {
        entryList.add(new Entry(category, translationKey, keyCode, defaultKeyCode, onChanged));
    }
    
    @Override
    public List<KeyBinding> getFakeBindings() {
        List<KeyBinding> keyBindings = new ArrayList<>();
        for (Entry entry : entryList) {
            keyBindings.add(new FakeKeyBindings(entry.translationKey, entry.keyCode.get(), entry.defaultKeyCode.get(), entry.category, entry.onChanged));
        }
        return keyBindings;
    }
    
    private class Entry {
        private String category;
        private String translationKey;
        private Supplier<ModifierKeyCode> keyCode;
        private Supplier<ModifierKeyCode> defaultKeyCode;
        private Consumer<ModifierKeyCode> onChanged;
        
        private Entry(String category, String translationKey, Supplier<ModifierKeyCode> keyCode, Supplier<ModifierKeyCode> defaultKeyCode, Consumer<ModifierKeyCode> onChanged) {
            this.category = category;
            this.translationKey = translationKey;
            this.keyCode = keyCode;
            this.defaultKeyCode = defaultKeyCode;
            this.onChanged = onChanged;
        }
    }
}
