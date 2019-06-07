package me.shedaniel.clothconfig2.api;

import net.minecraft.util.Identifier;

import java.util.List;

public interface ConfigCategory {
    
    @Deprecated
    List<Object> getEntries();
    
    ConfigCategory addEntry(AbstractConfigListEntry entry);
    
    ConfigCategory setCategoryBackground(Identifier identifier);
    
}
