package me.shedaniel.clothconfig2.impl;

import me.shedaniel.clothconfig2.api.Modifier;
import me.shedaniel.clothconfig2.api.ModifierKeyCode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class ModifierKeyCodeImpl implements ModifierKeyCode {
    private InputUtil.KeyCode keyCode;
    private Modifier modifier;
    
    public ModifierKeyCodeImpl() {
    }
    
    @Override
    public InputUtil.KeyCode getKeyCode() {
        return keyCode;
    }
    
    @Override
    public Modifier getModifier() {
        return modifier;
    }
    
    @Override
    public ModifierKeyCode setKeyCode(InputUtil.KeyCode keyCode) {
        this.keyCode = keyCode.getCategory().createFromCode(keyCode.getKeyCode());
        if (keyCode.equals(InputUtil.UNKNOWN_KEYCODE))
            setModifier(Modifier.none());
        return this;
    }
    
    @Override
    public ModifierKeyCode setModifier(Modifier modifier) {
        this.modifier = Modifier.of(modifier.getValue());
        return this;
    }
    
    @Override
    public String toString() {
        String string_1 = this.keyCode.getName();
        int int_1 = this.keyCode.getKeyCode();
        String string_2 = null;
        switch (this.keyCode.getCategory()) {
            case KEYSYM:
                string_2 = InputUtil.getKeycodeName(int_1);
                break;
            case SCANCODE:
                string_2 = InputUtil.getScancodeName(int_1);
                break;
            case MOUSE:
                String string_3 = I18n.translate(string_1);
                string_2 = Objects.equals(string_3, string_1) ? I18n.translate(InputUtil.Type.MOUSE.getName(), int_1 + 1) : string_3;
        }
        String base = string_2 == null ? I18n.translate(string_1) : string_2;
        if (modifier.hasShift())
            base = I18n.translate("modifier.cloth-config.shift", base);
        if (modifier.hasControl())
            base = I18n.translate("modifier.cloth-config.ctrl", base);
        if (modifier.hasAlt())
            base = I18n.translate("modifier.cloth-config.alt", base);
        return base;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ModifierKeyCode))
            return false;
        ModifierKeyCode that = (ModifierKeyCode) o;
        return keyCode.equals(that.getKeyCode()) && modifier.equals(that.getModifier());
    }
    
    @Override
    public int hashCode() {
        int result = keyCode != null ? keyCode.hashCode() : 0;
        result = 31 * result + (modifier != null ? modifier.hashCode() : 0);
        return result;
    }
}
