package me.shedaniel.clothconfig2.gui;

import com.google.common.collect.Maps;
import me.shedaniel.clothconfig2.api.TabbedConfigScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Map;

public abstract class AbstractTabbedConfigScreen extends AbstractConfigScreen implements TabbedConfigScreen {
    private final Map<Text, Identifier> categoryBackgroundLocation = Maps.newHashMap();
    
    protected AbstractTabbedConfigScreen(Screen parent, Text title, Identifier backgroundLocation) {
        super(parent, title, backgroundLocation);
    }
    
    @Override
    public final void registerCategoryBackground(Text text, Identifier identifier) {
        this.categoryBackgroundLocation.put(text, identifier);
    }
    
    @Override
    public Identifier getBackgroundLocation() {
        Text selectedCategory = getSelectedCategory();
        if (categoryBackgroundLocation.containsKey(selectedCategory))
            return categoryBackgroundLocation.get(selectedCategory);
        return super.getBackgroundLocation();
    }
}
