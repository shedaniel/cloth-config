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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This serializer wraps another serializer and produces a folder with each field of the config
 * corresponding to a single config file.
 * The top level config must inherit from GlobalData.
 * Each field of the top level config must be of a type inheriting from ConfigData.
 */
public final class PartitioningSerializer<T extends PartitioningSerializer.GlobalData, M extends ConfigData> implements ConfigSerializer<T> {
    
    private Class<T> configClass;
    private Map<Field, ConfigSerializer<M>> serializers;
    
    private PartitioningSerializer(Config definition, Class<T> configClass, ConfigSerializer.Factory<M> factory) {
        this.configClass = configClass;
        
        //noinspection unchecked
        serializers = getModuleFields(configClass).stream()
                .collect(
                        Utils.toLinkedMap(
                                Function.identity(),
                                field -> factory.create(
                                        createDefinition(
                                                String.format(
                                                        "%s/%s",
                                                        definition.name(),
                                                        field.getType().getAnnotation(Config.class).name()
                                                )
                                        ),
                                        (Class<M>) field.getType()
                                )
                        )
                );
    }
    
    public static <T extends PartitioningSerializer.GlobalData, M extends ConfigData>
    ConfigSerializer.Factory<T> wrap(ConfigSerializer.Factory<M> inner) {
        return (definition, configClass) -> new PartitioningSerializer<>(definition, configClass, inner);
    }
    
    private static Config createDefinition(String name) {
        return new Config() {
            
            @Override
            public Class<? extends Annotation> annotationType() {
                return Config.class;
            }
            
            @Override
            public String name() {
                return name;
            }
            
            @Override
            public int hashCode() {
                return ("name".hashCode() * 127) ^ name().hashCode();
            }
            
            @Override
            public boolean equals(Object obj) {
                return obj instanceof Config && ((Config) obj).name().equals(name());
            }
        };
    }
    
    private static boolean isValidModule(Field field) {
        return ConfigData.class.isAssignableFrom(field.getType())
               && field.getType().isAnnotationPresent(Config.class);
    }
    
    private static List<Field> getModuleFields(Class<?> configClass) {
        return Arrays.stream(configClass.getDeclaredFields())
                .filter(PartitioningSerializer::isValidModule)
                .collect(Collectors.toList());
    }
    
    @Override
    public void serialize(T config) throws SerializationException {
        for (Map.Entry<Field, ConfigSerializer<M>> entry : serializers.entrySet()) {
            entry.getValue().serialize(Utils.getUnsafely(entry.getKey(), config));
        }
    }
    
    @Override
    public T deserialize() throws SerializationException {
        T ret = createDefault();
        for (Map.Entry<Field, ConfigSerializer<M>> entry : serializers.entrySet()) {
            Utils.setUnsafely(entry.getKey(), ret, entry.getValue().deserialize());
        }
        return ret;
    }
    
    @Override
    public T createDefault() {
        return Utils.constructUnsafely(configClass);
    }
    
    public static abstract class GlobalData implements ConfigData {
        
        public GlobalData() {
            Arrays.stream(getClass().getDeclaredFields())
                    .filter(field -> !isValidModule(field))
                    .forEach(field -> {
                        throw new RuntimeException(String.format("Invalid module: %s", field));
                    });
        }
        
        @Override
        final public void validatePostLoad() throws ValidationException {
            for (Field moduleField : getModuleFields(getClass())) {
                ((ConfigData) Utils.getUnsafely(moduleField, this)).validatePostLoad();
            }
        }
    }
}
