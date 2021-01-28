package me.shedaniel.clothconfig2.api;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public interface ConfigCategory {
    
    Component getCategoryKey();
    
    @Deprecated
    List<Object> getEntries();
    
    ConfigCategory addEntry(AbstractConfigListEntry entry);
    
    ConfigCategory setCategoryBackground(ResourceLocation identifier);
    
    void setBackground(@Nullable ResourceLocation background);
    
    @Nullable ResourceLocation getBackground();
    
    @Nullable
    Supplier<Optional<FormattedText[]>> getDescription();
    
    void setDescription(@Nullable Supplier<Optional<FormattedText[]>> description);
    
    default void setDescription(@Nullable FormattedText[] description) {
        setDescription(() -> Optional.ofNullable(description));
    }
    
    void removeCategory();
    
}
