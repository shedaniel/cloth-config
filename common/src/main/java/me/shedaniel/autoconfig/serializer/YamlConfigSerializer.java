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

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.util.Utils;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class YamlConfigSerializer<T extends ConfigData> implements ConfigSerializer<T> {
    
    private Config definition;
    private Class<T> configClass;
    private Yaml yaml;
    
    @SuppressWarnings("WeakerAccess")
    public YamlConfigSerializer(Config definition, Class<T> configClass, Yaml yaml) {
        this.definition = definition;
        this.configClass = configClass;
        this.yaml = yaml;
    }
    
    public YamlConfigSerializer(Config definition, Class<T> configClass) {
        this(definition, configClass, new Yaml());
    }
    
    private Path getConfigPath() {
        return Utils.getConfigFolder().resolve(definition.name() + ".yaml");
    }
    
    @Override
    public void serialize(T config) throws SerializationException {
        Path configPath = getConfigPath();
        try {
            Files.createDirectories(configPath.getParent());
            Files.write(configPath, yaml.dump(config).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }
    
    @Override
    public T deserialize() throws SerializationException {
        Path configPath = getConfigPath();
        if (Files.exists(configPath)) {
            try (InputStream stream = Files.newInputStream(configPath)) {
                return yaml.load(stream);
            } catch (IOException e) {
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

