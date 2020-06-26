package me.shedaniel.clothconfig2.forge.impl;

import me.shedaniel.clothconfig2.forge.api.Modifier;
import me.shedaniel.clothconfig2.forge.api.ModifierKeyCode;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
        return getLocalizedName().getString();
    }
    
    @Override
    public ITextComponent getLocalizedName() {
        ITextComponent base = this.keyCode.func_237520_d_();
        if (modifier.hasShift())
            base = new TranslationTextComponent("modifier.cloth-config.shift", base);
        if (modifier.hasControl())
            base = new TranslationTextComponent("modifier.cloth-config.ctrl", base);
        if (modifier.hasAlt())
            base = new TranslationTextComponent("modifier.cloth-config.alt", base);
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
