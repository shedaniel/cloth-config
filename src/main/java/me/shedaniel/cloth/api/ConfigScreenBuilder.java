package me.shedaniel.cloth.api;

import me.shedaniel.cloth.gui.ClothConfigScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public interface ConfigScreenBuilder {
    
    public static ConfigScreenBuilder create(Screen parentScreen, String title, Consumer<ConfigScreenBuilder.SavedConfig> onSave) {
        return new ClothConfigScreen.Builder(parentScreen, title, onSave);
    }
    
    public static ConfigScreenBuilder create() {
        return create(null, "text.cloth-config.config", null);
    }
    
    String getTitle();
    
    void setTitle(String title);
    
    Screen getParentScreen();
    
    void setParentScreen(Screen parent);
    
    Consumer<SavedConfig> getOnSave();
    
    void setOnSave(Consumer<SavedConfig> onSave);
    
    ClothConfigScreen build();
    
    Screen build(Consumer<ClothConfigScreen> afterInitConsumer);
    
    List<String> getCategories();
    
    CategoryBuilder addCategory(String category);
    
    CategoryBuilder getCategory(String category);
    
    boolean isEditable();
    
    void setEditable(boolean editable);
    
    default void addCategories(String... categories) {
        for(String category : categories)
            addCategory(category);
    }
    
    void removeCategory(String category);
    
    default void removeCategories(String... categories) {
        for(String category : categories)
            removeCategory(category);
    }
    
    boolean hasCategory(String category);
    
    void addOption(String category, String key, Object object);
    
    void addOption(String category, ClothConfigScreen.AbstractListEntry entry);
    
    @Deprecated
    List<Pair<String, Object>> getOptions(String category);
    
    void setDoesConfirmSave(boolean confirmSave);
    
    boolean doesConfirmSave();
    
    void setShouldProcessErrors(boolean processErrors);
    
    boolean shouldProcessErrors();
    
    @Deprecated
    public Map<String, List<Pair<String, Object>>> getDataMap();
    
    boolean isSmoothScrollingTabs();
    
    void setSmoothScrollingTabs(boolean smoothScrolling);
    
    boolean isSmoothScrollingList();
    
    void setSmoothScrollingList(boolean smoothScrolling);
    
    Identifier getBackgroundTexture();
    
    void setBackgroundTexture(Identifier backgroundTexture);
    
    Identifier getCategoryBackgroundTexture(String category);
    
    Identifier getNullableCategoryBackgroundTexture(String category);
    
    @Deprecated
    Map<String, Identifier> getCategoryBackgroundMap();
    
    public static interface CategoryBuilder {
        Identifier getBackgroundTexture();
        
        void setBackgroundTexture(Identifier backgroundTexture);
        
        Identifier getNullableBackgroundTexture();
        
        @Deprecated
        List<Pair<String, Object>> getOptions();
        
        CategoryBuilder addOption(ClothConfigScreen.AbstractListEntry entry);
        
        @Deprecated
        CategoryBuilder addOption(String key, Object object);
        
        ConfigScreenBuilder removeFromParent();
        
        ConfigScreenBuilder parent();
        
        String getName();
        
        boolean exists();
    }
    
    public static interface SavedConfig {
        boolean containsCategory(String category);
        
        SavedCategory getCategory(String category);
        
        List<SavedCategory> getCategories();
    }
    
    public static interface SavedCategory {
        boolean exists();
        
        String getName();
        
        @Deprecated
        List<Pair<String, Object>> getOptionPairs();
        
        List<SavedOption> getOptions();
        
        Optional<SavedOption> getOption(String fieldKey);
    }
    
    public static interface SavedOption {
        String getFieldKey();
        
        Object getValue();
    }
    
}
