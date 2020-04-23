package me.shedaniel.clothconfig2.impl;

import me.shedaniel.clothconfig2.api.Modifier;
import me.shedaniel.clothconfig2.api.ModifierKeyCode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

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
        return getLocalizedName().getString();
    }
    
    @Override
    public Text getLocalizedName() {
        Text base = this.keyCode.method_27445();
        if (modifier.hasShift())
            base = new TranslatableText("modifier.cloth-config.shift", base);
        if (modifier.hasControl())
            base = new TranslatableText("modifier.cloth-config.ctrl", base);
        if (modifier.hasAlt())
            base = new TranslatableText("modifier.cloth-config.alt", base);
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
