package me.shedaniel.clothconfig2.forge.gui;

import com.google.common.collect.Maps;
import me.shedaniel.clothconfig2.forge.api.TabbedConfigScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import java.util.Map;

public abstract class AbstractTabbedConfigScreen extends AbstractConfigScreen implements TabbedConfigScreen {
    private final Map<ITextComponent, ResourceLocation> categoryBackgroundLocation = Maps.newHashMap();
    
    protected AbstractTabbedConfigScreen(Screen parent, ITextComponent title, ResourceLocation backgroundLocation) {
        super(parent, title, backgroundLocation);
    }
    
    @Override
    public final void registerCategoryBackground(ITextComponent text, ResourceLocation identifier) {
        this.categoryBackgroundLocation.put(text, identifier);
    }
    
    @Override
    public ResourceLocation getBackgroundLocation() {
        ITextComponent selectedCategory = getSelectedCategory();
        if (categoryBackgroundLocation.containsKey(selectedCategory))
            return categoryBackgroundLocation.get(selectedCategory);
        return super.getBackgroundLocation();
    }
}
