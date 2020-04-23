package me.shedaniel.forge.clothconfig2.impl;

import me.shedaniel.forge.clothconfig2.api.Modifier;
import me.shedaniel.forge.clothconfig2.api.ModifierKeyCode;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class ModifierKeyCodeImpl implements ModifierKeyCode {
    private InputMappings.Input keyCode;
    private Modifier modifier;
    
    public ModifierKeyCodeImpl() {
    }
    
    @Override
    public InputMappings.Input getKeyCode() {
        return keyCode;
    }
    
    @Override
    public Modifier getModifier() {
        return modifier;
    }
    
    @Override
    public ModifierKeyCode setKeyCode(InputMappings.Input keyCode) {
        this.keyCode = keyCode.getType().getOrMakeInput(keyCode.getKeyCode());
        if (keyCode.equals(InputMappings.INPUT_INVALID))
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
        String string_1 = this.keyCode.getTranslationKey();
        int int_1 = this.keyCode.getKeyCode();
        String string_2 = null;
        switch (this.keyCode.getType()) {
            case KEYSYM:
                string_2 = InputMappings.getKeynameFromKeycode(int_1);
                break;
            case SCANCODE:
                string_2 = InputMappings.getKeyNameFromScanCode(int_1);
                break;
            case MOUSE:
                String string_3 = I18n.format(string_1);
                string_2 = Objects.equals(string_3, string_1) ? I18n.format(InputMappings.Type.MOUSE.getName(), int_1 + 1) : string_3;
        }
        String base = string_2 == null ? I18n.format(string_1) : string_2;
        if (modifier.hasShift())
            base = I18n.format("modifier.cloth-config.shift", base);
        if (modifier.hasControl())
            base = I18n.format("modifier.cloth-config.ctrl", base);
        if (modifier.hasAlt())
            base = I18n.format("modifier.cloth-config.alt", base);
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
