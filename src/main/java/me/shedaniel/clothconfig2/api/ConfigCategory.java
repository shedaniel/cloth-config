package me.shedaniel.clothconfig2.api;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public interface ConfigCategory {
    
    Text getCategoryKey();
    
    @Deprecated
    List<Object> getEntries();
    
    ConfigCategory addEntry(AbstractConfigListEntry entry);
    
    ConfigCategory setCategoryBackground(Identifier identifier);
    
    void setBackground(@Nullable Identifier background);
    
    @Nullable Identifier getBackground();
    
    @Nullable
    Supplier<Optional<StringRenderable[]>> getDescription();
    
    void setDescription(@Nullable Supplier<Optional<StringRenderable[]>> description);
    
    default void setDescription(@Nullable StringRenderable[] description) {
        setDescription(() -> Optional.ofNullable(description));
    }
    
    void removeCategory();
    
}
