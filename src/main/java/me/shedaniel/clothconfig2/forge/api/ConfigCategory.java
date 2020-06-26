package me.shedaniel.clothconfig2.forge.api;

import java.util.List;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ConfigCategory {
    
    ITextComponent getCategoryKey();
    
    @Deprecated
    List<Object> getEntries();
    
    ConfigCategory addEntry(AbstractConfigListEntry entry);
    
    ConfigCategory setCategoryBackground(ResourceLocation identifier);
    
    void removeCategory();
    
}
