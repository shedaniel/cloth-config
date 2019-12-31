package me.shedaniel.clothconfig2.impl;

import me.shedaniel.clothconfig2.api.ModifierKeyCode;
import me.shedaniel.clothconfig2.api.FakeModifierKeyCodeAdder;
import net.minecraft.client.options.KeyBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FakeModifierKeyCodeAdderImpl implements FakeModifierKeyCodeAdder {
    private List<Entry> entryList = new ArrayList<>();
    
    @Override
    public void registerModifierKeyCode(String category, String translationKey, ModifierKeyCode keyCode, ModifierKeyCode defaultKeyCode, Consumer<ModifierKeyCode> onChanged) {
        entryList.add(new Entry(category, translationKey, keyCode, defaultKeyCode, onChanged));
    }
    
    @Override
    public List<KeyBinding> getFakeBindings() {
        List<KeyBinding> keyBindings = new ArrayList<>();
        for (Entry entry : entryList) {
            keyBindings.add(new FakeKeyBindings(entry.translationKey, entry.keyCode, entry.defaultKeyCode, entry.category, entry.onChanged));
        }
        return keyBindings;
    }
    
    private class Entry {
        private String category;
        private String translationKey;
        private ModifierKeyCode keyCode;
        private ModifierKeyCode defaultKeyCode;
        private Consumer<ModifierKeyCode> onChanged;
        
        private Entry(String category, String translationKey, ModifierKeyCode keyCode, ModifierKeyCode defaultKeyCode, Consumer<ModifierKeyCode> onChanged) {
            this.category = category;
            this.translationKey = translationKey;
            this.keyCode = keyCode;
            this.defaultKeyCode = defaultKeyCode;
            this.onChanged = onChanged;
        }
    }
}
