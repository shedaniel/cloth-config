package me.shedaniel.clothconfig2.api;

import me.shedaniel.clothconfig2.impl.ModifierKeyCodeImpl;
import net.minecraft.client.util.InputUtil;

public interface ModifierKeyCode {
    InputUtil.KeyCode getKeyCode();
    
    default InputUtil.Type getType() {
        return getKeyCode().getCategory();
    }
    
    Modifier getModifier();
    
    default boolean matchesMouse(int button) {
        return getType() == InputUtil.Type.MOUSE && getKeyCode().getKeyCode() == button && getModifier().matchesCurrent();
    }
    
    default boolean matchesKey(int keyCode, int scanCode) {
        if (keyCode == InputUtil.UNKNOWN_KEYCODE.getKeyCode()) {
            return getType() == InputUtil.Type.SCANCODE && getKeyCode().getKeyCode() == scanCode && getModifier().matchesCurrent();
        } else {
            return getType() == InputUtil.Type.KEYSYM && getKeyCode().getKeyCode() == keyCode && getModifier().matchesCurrent();
        }
    }
    
    ModifierKeyCode setKeyCode(InputUtil.KeyCode keyCode);
    
    ModifierKeyCode setModifier(Modifier modifier);
    
    default ModifierKeyCode setKeyCodeAndModifier(InputUtil.KeyCode keyCode, Modifier modifier) {
        setKeyCode(keyCode);
        setModifier(modifier);
        return this;
    }
    
    default ModifierKeyCode clearModifier() {
        return setModifier(Modifier.none());
    }
    
    static ModifierKeyCode of(InputUtil.KeyCode keyCode, Modifier modifier) {
        return new ModifierKeyCodeImpl().setKeyCodeAndModifier(keyCode, modifier);
    }
    
    static ModifierKeyCode copyOf(ModifierKeyCode code) {
        return of(code.getKeyCode(), code.getModifier());
    }
    
    static ModifierKeyCode unknown() {
        return of(InputUtil.UNKNOWN_KEYCODE, Modifier.none());
    }
    
    String toString();
    
    default String getLocalizedName() {
        return toString();
    }
    
    default boolean isUnknown() {
        return getKeyCode().equals(InputUtil.UNKNOWN_KEYCODE);
    }
}
