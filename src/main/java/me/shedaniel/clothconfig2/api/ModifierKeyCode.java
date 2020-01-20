package me.shedaniel.clothconfig2.api;

import me.shedaniel.clothconfig2.impl.ModifierKeyCodeImpl;
import me.shedaniel.clothconfig2.mixin.MouseHooks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;

public interface ModifierKeyCode {
    InputUtil.KeyCode getKeyCode();
    
    default InputUtil.Type getType() {
        return getKeyCode().getCategory();
    }
    
    Modifier getModifier();
    
    default boolean matchesMouse(int button) {
        return !isUnknown() && getType() == InputUtil.Type.MOUSE && getKeyCode().getKeyCode() == button && getModifier().matchesCurrent();
    }
    
    default boolean matchesKey(int keyCode, int scanCode) {
        if (isUnknown())
            return false;
        if (keyCode == InputUtil.UNKNOWN_KEYCODE.getKeyCode()) {
            return getType() == InputUtil.Type.SCANCODE && getKeyCode().getKeyCode() == scanCode && getModifier().matchesCurrent();
        } else {
            return getType() == InputUtil.Type.KEYSYM && getKeyCode().getKeyCode() == keyCode && getModifier().matchesCurrent();
        }
    }
    
    default boolean matchesCurrentMouse() {
        if (!isUnknown() && getType() == InputUtil.Type.MOUSE && getModifier().matchesCurrent()) {
            switch (getKeyCode().getKeyCode()) {
                case 0:
                    return MinecraftClient.getInstance().mouse.wasLeftButtonClicked();
                case 1:
                    return MinecraftClient.getInstance().mouse.wasRightButtonClicked();
                case 2:
                    return ((MouseHooks) MinecraftClient.getInstance().mouse).middleButtonClicked();
            }
        }
        return false;
    }
    
    default boolean matchesCurrentKey() {
        return !isUnknown() && getType() == InputUtil.Type.KEYSYM && getModifier().matchesCurrent() && InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), getKeyCode().getKeyCode());
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
