package me.shedaniel.clothconfig2.api;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public interface TabbedConfigScreen extends ConfigScreen {
    void registerCategoryBackground(Text text, Identifier identifier);
    
    Text getSelectedCategory();
}
