package me.shedaniel.clothconfig2.gui;

import com.google.common.collect.Maps;
import me.shedaniel.clothconfig2.api.TabbedConfigScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import java.util.Map;

public abstract class AbstractTabbedConfigScreen extends AbstractConfigScreen implements TabbedConfigScreen {
    private final Map<Component, ResourceLocation> categoryBackgroundLocation = Maps.newHashMap();
    
    protected AbstractTabbedConfigScreen(Screen parent, Component title, ResourceLocation backgroundLocation) {
        super(parent, title, backgroundLocation);
    }
    
    @Override
    public final void registerCategoryBackground(Component text, ResourceLocation identifier) {
        this.categoryBackgroundLocation.put(text, identifier);
    }
    
    @Override
    public ResourceLocation getBackgroundLocation() {
        Component selectedCategory = getSelectedCategory();
        if (categoryBackgroundLocation.containsKey(selectedCategory))
            return categoryBackgroundLocation.get(selectedCategory);
        return super.getBackgroundLocation();
    }
}
