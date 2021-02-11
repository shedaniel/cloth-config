/*
 * This file is part of Cloth Config.
 * Copyright (C) 2020 - 2021 shedaniel
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package me.shedaniel.autoconfig;

import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.event.ConfigSerializeEvent;
import me.shedaniel.autoconfig.serializer.ConfigSerializer;
import net.minecraft.world.InteractionResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@ApiStatus.Internal
public class ConfigManager<T extends ConfigData> implements ConfigHolder<T> {
    private final Logger logger;
    private final Config definition;
    private final Class<T> configClass;
    private final ConfigSerializer<T> serializer;
    
    private final List<ConfigSerializeEvent.Save<T>> saveEvent = new ArrayList<>();
    private final List<ConfigSerializeEvent.Load<T>> loadEvent = new ArrayList<>();
    
    private T config;
    
    ConfigManager(Config definition, Class<T> configClass, ConfigSerializer<T> serializer) {
        logger = LogManager.getLogger();
        
        this.definition = definition;
        this.configClass = configClass;
        this.serializer = serializer;
        
        if (load()) {
            save();
        }
    }
    
    public Config getDefinition() {
        return definition;
    }
    
    @Override
    @NotNull
    public Class<T> getConfigClass() {
        return configClass;
    }
    
    public ConfigSerializer<T> getSerializer() {
        return serializer;
    }
    
    @Override
    public void save() {
        for (ConfigSerializeEvent.Save<T> save : saveEvent) {
            InteractionResult result = save.onSave(this, config);
            if (result == InteractionResult.FAIL) {
                return;
            } else if (result != InteractionResult.PASS) {
                break;
            }
        }
        try {
            serializer.serialize(config);
        } catch (ConfigSerializer.SerializationException e) {
            logger.error("Failed to save config '{}'", configClass, e);
        }
    }
    
    @Override
    public boolean load() {
        try {
            T deserialized = serializer.deserialize();
            
            for (ConfigSerializeEvent.Load<T> load : loadEvent) {
                InteractionResult result = load.onLoad(this, deserialized);
                if (result == InteractionResult.FAIL) {
                    config = serializer.createDefault();
                    config.validatePostLoad();
                    return false;
                } else if (result != InteractionResult.PASS) {
                    break;
                }
            }
            
            config = deserialized;
            config.validatePostLoad();
            return true;
        } catch (ConfigSerializer.SerializationException | ConfigData.ValidationException e) {
            logger.error("Failed to load config '{}', using default!", configClass, e);
            config = serializer.createDefault();
            try {
                config.validatePostLoad();
            } catch (ConfigData.ValidationException v) {
                throw new RuntimeException("result of createDefault() was invalid!", v);
            }
            return false;
        }
    }
    
    @Override
    public T getConfig() {
        return config;
    }
    
    @Override
    public void registerLoadListener(ConfigSerializeEvent.Load<T> load) {
        this.loadEvent.add(load);
    }
    
    @Override
    public void registerSaveListener(ConfigSerializeEvent.Save<T> save) {
        this.saveEvent.add(save);
    }
}
