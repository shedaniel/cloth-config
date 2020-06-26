package me.shedaniel.clothconfig2.forge.api;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public interface TabbedConfigScreen extends ConfigScreen {
    void registerCategoryBackground(ITextComponent text, ResourceLocation identifier);
    
    ITextComponent getSelectedCategory();
}
