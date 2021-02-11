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

package me.shedaniel.autoconfig.serializer;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.util.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This serializer serializes configs into Toml files using Toml4j.
 */
@SuppressWarnings("unused")
public class Toml4jConfigSerializer<T extends ConfigData> implements ConfigSerializer<T> {
    
    private Config definition;
    private Class<T> configClass;
    private TomlWriter tomlWriter;
    
    @SuppressWarnings("WeakerAccess")
    public Toml4jConfigSerializer(Config definition, Class<T> configClass, TomlWriter tomlWriter) {
        this.definition = definition;
        this.configClass = configClass;
        this.tomlWriter = tomlWriter;
    }
    
    public Toml4jConfigSerializer(Config definition, Class<T> configClass) {
        this(definition, configClass, new TomlWriter());
    }
    
    private Path getConfigPath() {
        return Utils.getConfigFolder().resolve(definition.name() + ".toml");
    }
    
    @Override
    public void serialize(T config) throws SerializationException {
        Path configPath = getConfigPath();
        try {
            Files.createDirectories(configPath.getParent());
            tomlWriter.write(config, configPath.toFile());
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }
    
    @Override
    public T deserialize() throws SerializationException {
        Path configPath = getConfigPath();
        if (Files.exists(configPath)) {
            try {
                return new Toml().read(configPath.toFile()).to(configClass);
            } catch (IllegalStateException e) {
                throw new SerializationException(e);
            }
        } else {
            return createDefault();
        }
    }
    
    @Override
    public T createDefault() {
        return Utils.constructUnsafely(configClass);
    }
}
