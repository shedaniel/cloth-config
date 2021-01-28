package me.shedaniel.clothconfig2.api;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public interface TabbedConfigScreen extends ConfigScreen {
    void registerCategoryBackground(Component text, ResourceLocation identifier);
    
    Component getSelectedCategory();
}
