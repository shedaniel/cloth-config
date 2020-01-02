package me.shedaniel.clothconfig2.impl;

import me.shedaniel.clothconfig2.api.Modifier;
import me.shedaniel.clothconfig2.api.ModifierKeyCode;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.UUID;
import java.util.function.Consumer;

public class FakeKeyBindings extends KeyBinding {
    private UUID uuid;
    private ModifierKeyCode keyCode;
    private ModifierKeyCode defaultKeyCode;
    private Consumer<ModifierKeyCode> onChanged;
    
    public FakeKeyBindings(String key, ModifierKeyCode keyCode, ModifierKeyCode defaultKeyCode, String category, Consumer<ModifierKeyCode> onChanged) {
        super(UUID.randomUUID().toString(), InputUtil.Type.KEYSYM, -1, category);
        uuid = UUID.fromString(getId());
        ((KeyBindingHooks) this).cloth_setId("ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ" + key);
        this.keyCode = keyCode;
        this.defaultKeyCode = ModifierKeyCode.copyOf(defaultKeyCode);
        this.onChanged = onChanged;
    }
    
    @Override
    public InputUtil.KeyCode getDefaultKeyCode() {
        return defaultKeyCode.getKeyCode();
    }
    
    @Override
    public void setKeyCode(InputUtil.KeyCode inputUtil$KeyCode_1) {
        keyCode.setKeyCode(inputUtil$KeyCode_1);
        keyCode.setModifier(Modifier.none());
        onChanged.accept(keyCode);
    }
    
    @Override
    public boolean equals(KeyBinding keyBinding_1) {
        return false;
    }
    
    @Override
    public boolean isNotBound() {
        return keyCode.isUnknown();
    }
    
    @Override
    public boolean matchesKey(int int_1, int int_2) {
        return keyCode.matchesKey(int_1, int_2);
    }
    
    @Override
    public boolean matchesMouse(int int_1) {
        return keyCode.matchesMouse(int_1);
    }
    
    @Override
    public String getLocalizedName() {
        return keyCode.getLocalizedName();
    }
    
    @Override
    public boolean isDefault() {
        return keyCode.equals(defaultKeyCode);
    }
    
    @Override
    public String getName() {
        return keyCode.getLocalizedName();
    }
    
    @Override
    public String getId() {
        if (uuid == null)
            return super.getId();
        return super.getId().substring(77);
    }
}
