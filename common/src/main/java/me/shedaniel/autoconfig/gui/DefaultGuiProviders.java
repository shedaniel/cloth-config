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

package me.shedaniel.autoconfig.gui;

import com.google.common.collect.Lists;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.dependencies.DependencyManager;
import me.shedaniel.autoconfig.gui.registry.GuiRegistry;
import me.shedaniel.autoconfig.gui.registry.api.GuiRegistryAccess;
import me.shedaniel.autoconfig.util.Utils;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.MultiElementListEntry;
import me.shedaniel.clothconfig2.gui.entries.NestedListListEntry;
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static me.shedaniel.autoconfig.util.Utils.getUnsafely;
import static me.shedaniel.autoconfig.util.Utils.setUnsafely;

@Environment(EnvType.CLIENT)
public class DefaultGuiProviders {
    
    private static final ConfigEntryBuilder ENTRY_BUILDER = ConfigEntryBuilder.create();
    private static final Function<Enum<?>, Component> DEFAULT_NAME_PROVIDER = t -> Component.translatable(t instanceof SelectionListEntry.Translatable ? ((SelectionListEntry.Translatable) t).getKey() : t.toString());
    
    private DefaultGuiProviders() {
    }
    
    public static GuiRegistry apply(GuiRegistry registry) {
        registry.registerAnnotationProvider(
                (i18n, field, config, defaults, guiProvider) -> Collections.emptyList(),
                ConfigEntry.Gui.Excluded.class
        );
        
        registry.registerAnnotationProvider(
                (i18n, field, config, defaults, guiProvider) -> {
                    ConfigEntry.BoundedDiscrete bounds
                            = field.getAnnotation(ConfigEntry.BoundedDiscrete.class);
                    
                    return Collections.singletonList(
                            ENTRY_BUILDER.startIntSlider(
                                            Component.translatable(i18n),
                                            getUnsafely(field, config, 0),
                                            (int) bounds.min(),
                                            (int) bounds.max()
                                    )
                                    .setDefaultValue(() -> getUnsafely(field, defaults))
                                    .setSaveConsumer(newValue -> setUnsafely(field, config, newValue))
                                    .build()
                    );
                },
                field -> field.getType() == int.class || field.getType() == Integer.class,
                ConfigEntry.BoundedDiscrete.class
        );
        
        registry.registerAnnotationProvider(
                (i18n, field, config, defaults, guiProvider) -> {
                    ConfigEntry.BoundedDiscrete bounds
                            = field.getAnnotation(ConfigEntry.BoundedDiscrete.class);
                    
                    return Collections.singletonList(
                            ENTRY_BUILDER.startLongSlider(
                                            Component.translatable(i18n),
                                            getUnsafely(field, config, 0L),
                                            bounds.min(),
                                            bounds.max()
                                    )
                                    .setDefaultValue(() -> getUnsafely(field, defaults))
                                    .setSaveConsumer(newValue -> setUnsafely(field, config, newValue))
                                    .build()
                    );
                },
                field -> field.getType() == long.class || field.getType() == Long.class,
                ConfigEntry.BoundedDiscrete.class
        );
        
        registry.registerAnnotationProvider(
                (i18n, field, config, defaults, guiProvider) -> {
                    ConfigEntry.ColorPicker colorPicker
                            = field.getAnnotation(ConfigEntry.ColorPicker.class);
                    
                    return Collections.singletonList(
                            ENTRY_BUILDER.startColorField(
                                            Component.translatable(i18n),
                                            getUnsafely(field, config, 0)
                                    )
                                    .setAlphaMode(colorPicker.allowAlpha())
                                    .setDefaultValue(() -> getUnsafely(field, defaults))
                                    .setSaveConsumer(newValue -> setUnsafely(field, config, newValue))
                                    .build()
                    );
                },
                field -> field.getType() == int.class || field.getType() == Integer.class,
                ConfigEntry.ColorPicker.class
        );
        
        registry.registerAnnotationProvider(
                (i18n, field, config, defaults, guiProvider) -> {
                    List<AbstractConfigListEntry> children = getChildren(i18n, field, config, defaults, guiProvider);
    
                    if (!children.isEmpty()) {
                        // Check if the transitive field has a dependency declared
                        Annotation dependency;
                        if (field.isAnnotationPresent(ConfigEntry.Gui.DependsOnGroup.class))
                            dependency = field.getAnnotation(ConfigEntry.Gui.DependsOnGroup.class);
                        else if (field.isAnnotationPresent(ConfigEntry.Gui.DependsOn.class))
                            dependency = field.getAnnotation(ConfigEntry.Gui.DependsOn.class);
                        else
                            dependency = null;
    
                        // Apply the field's dependency to its children
                        if (dependency != null) {
                            DependencyManager dependencies = guiProvider.getDependencyManager();
                            children.forEach(gui -> dependencies.registerAdditionalDependency(gui, dependency));
                        }
                    }
                    
                    return children;
                },
                field -> !field.getType().isPrimitive(),
                ConfigEntry.Gui.TransitiveObject.class
        );
        
        registry.registerAnnotationProvider(
                (i18n, field, config, defaults, guiProvider) -> Collections.singletonList(
                        ENTRY_BUILDER.startSubCategory(
                                        Component.translatable(i18n),
                                        getChildren(i18n, field, config, defaults, guiProvider)
                                )
                                .setExpanded(field.getAnnotation(ConfigEntry.Gui.CollapsibleObject.class).startExpanded())
                                .build()
                ),
                field -> !field.getType().isPrimitive(),
                ConfigEntry.Gui.CollapsibleObject.class
        );
        
        registry.registerPredicateProvider(
                (i18n, field, config, defaults, guiProvider) -> {
                    Object[] enumConstants = field.getType().getEnumConstants();
                    Enum[] enums = new Enum[enumConstants.length];
                    for (int i = 0; i < enumConstants.length; i++) {
                        enums[i] = (Enum) enumConstants[i];
                    }
                    return Collections.singletonList(
                            ENTRY_BUILDER.startSelector(
                                            Component.translatable(i18n),
                                            enums,
                                            getUnsafely(field, config, getUnsafely(field, defaults))
                                    )
                                    .setDefaultValue(() -> getUnsafely(field, defaults))
                                    .setSaveConsumer(newValue -> setUnsafely(field, config, newValue))
                                    .build()
                    );
                },
                field -> field.getType().isEnum() && field.isAnnotationPresent(ConfigEntry.Gui.EnumHandler.class) && field.getAnnotation(ConfigEntry.Gui.EnumHandler.class).option() == ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON
        );
        
        //noinspection unchecked
        registry.registerPredicateProvider(
                (i18n, field, config, defaults, guiProvider) -> {
                    List<Enum<?>> enums = Arrays.asList(((Class<? extends Enum<?>>) field.getType()).getEnumConstants());
                    return Collections.singletonList(
                            ENTRY_BUILDER.startDropdownMenu(
                                            Component.translatable(i18n),
                                            DropdownMenuBuilder.TopCellElementBuilder.of(
                                                    getUnsafely(field, config, getUnsafely(field, defaults)),
                                                    str -> {
                                                        String s = Component.literal(str).getString();
                                                        for (Enum<?> constant : enums) {
                                                            if (DEFAULT_NAME_PROVIDER.apply(constant).getString().equals(s)) {
                                                                return constant;
                                                            }
                                                        }
                                                        return null;
                                                    },
                                                    DEFAULT_NAME_PROVIDER
                                            ),
                                            DropdownMenuBuilder.CellCreatorBuilder.of(DEFAULT_NAME_PROVIDER)
                                    )
                                    .setSelections(enums)
                                    .setDefaultValue(() -> getUnsafely(field, defaults))
                                    .setSaveConsumer(newValue -> setUnsafely(field, config, newValue))
                                    .build()
                    );
                },
                field -> field.getType().isEnum()
        );
        
        registry.registerPredicateProvider((i18n, field, config, defaults, registry1) -> Collections.singletonList(
                ENTRY_BUILDER.startIntList(Component.translatable(i18n), getUnsafely(field, config))
                        .setDefaultValue(() -> getUnsafely(field, defaults))
                        .setSaveConsumer(newValue -> setUnsafely(field, config, newValue))
                        .build()
        ), isListOfType(Integer.class));
        
        registry.registerPredicateProvider((i18n, field, config, defaults, registry1) -> Collections.singletonList(
                ENTRY_BUILDER.startLongList(Component.translatable(i18n), getUnsafely(field, config))
                        .setDefaultValue(() -> getUnsafely(field, defaults))
                        .setSaveConsumer(newValue -> setUnsafely(field, config, newValue))
                        .build()
        ), isListOfType(Long.class));
        
        registry.registerPredicateProvider((i18n, field, config, defaults, registry1) -> Collections.singletonList(
                ENTRY_BUILDER.startFloatList(Component.translatable(i18n), getUnsafely(field, config))
                        .setDefaultValue(() -> getUnsafely(field, defaults))
                        .setSaveConsumer(newValue -> setUnsafely(field, config, newValue))
                        .build()
        ), isListOfType(Float.class));
        
        registry.registerPredicateProvider((i18n, field, config, defaults, registry1) -> Collections.singletonList(
                ENTRY_BUILDER.startDoubleList(Component.translatable(i18n), getUnsafely(field, config))
                        .setDefaultValue(() -> getUnsafely(field, defaults))
                        .setSaveConsumer(newValue -> setUnsafely(field, config, newValue))
                        .build()
        ), isListOfType(Double.class));
        
        registry.registerPredicateProvider((i18n, field, config, defaults, registry1) -> Collections.singletonList(
                ENTRY_BUILDER.startStrList(Component.translatable(i18n), getUnsafely(field, config))
                        .setDefaultValue(() -> getUnsafely(field, defaults))
                        .setSaveConsumer(newValue -> setUnsafely(field, config, newValue))
                        .build()
        ), isListOfType(String.class));
        
        registry.registerPredicateProvider((i18n, field, config, defaults, registry1) -> {
            List<Object> configValue = getUnsafely(field, config);
            
            Class<?> fieldTypeParam = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
            
            Object defaultElemValue = Utils.constructUnsafely(fieldTypeParam);
            
            String remainingI13n = i18n.substring(0, i18n.indexOf(".option") + ".option".length());
            String classI13n = String.format("%s.%s", remainingI13n, fieldTypeParam.getSimpleName());
            
            return Collections.singletonList(
                    new NestedListListEntry<Object, MultiElementListEntry<Object>>(
                            Component.translatable(i18n),
                            configValue,
                            false,
                            null,
                            newValue -> setUnsafely(field, config, newValue),
                            () -> getUnsafely(field, defaults),
                            ENTRY_BUILDER.getResetButtonKey(),
                            true,
                            false,
                            (elem, nestedListListEntry) -> {
                                if (elem == null) {
                                    Object newDefaultElemValue = Utils.constructUnsafely(fieldTypeParam);
                                    return new MultiElementListEntry<>(Component.translatable(classI13n), newDefaultElemValue, (List) getChildren(classI13n, fieldTypeParam, newDefaultElemValue, defaultElemValue, registry1), true);
                                } else
                                    return new MultiElementListEntry<>(Component.translatable(classI13n), elem, (List) getChildren(classI13n, fieldTypeParam, elem, defaultElemValue, registry1), true);
                            }
                    )
            );
        }, isNotListOfType(Integer.class, Long.class, Float.class, Double.class, String.class));
        
        registry.registerTypeProvider(
                (i18n, field, config, defaults, guiProvider) -> Collections.singletonList(
                        ENTRY_BUILDER.startBooleanToggle(
                                        Component.translatable(i18n),
                                        getUnsafely(field, config, false)
                                )
                                .setDefaultValue(() -> getUnsafely(field, defaults))
                                .setSaveConsumer(newValue -> setUnsafely(field, config, newValue))
                                .setYesNoTextSupplier(bool -> {
                                    String key = i18n + ".boolean." + bool;
                                    String translate = I18n.get(key);
                                    if (translate.equals(key))
                                        return Component.translatable("text.cloth-config.boolean.value." + bool);
                                    return Component.literal(translate);
                                })
                                .build()
                ),
                boolean.class, Boolean.class
        );
        
        registry.registerTypeProvider(
                (i18n, field, config, defaults, guiProvider) -> Collections.singletonList(
                        ENTRY_BUILDER.startIntField(
                                        Component.translatable(i18n),
                                        getUnsafely(field, config, 0)
                                )
                                .setDefaultValue(() -> getUnsafely(field, defaults))
                                .setSaveConsumer(newValue -> setUnsafely(field, config, newValue))
                                .build()
                ),
                int.class, Integer.class
        );
        
        registry.registerTypeProvider(
                (i18n, field, config, defaults, guiProvider) -> Collections.singletonList(
                        ENTRY_BUILDER.startIntList(
                                        Component.translatable(i18n),
                                        Lists.newArrayList(getUnsafely(field, config, new Integer[0]))
                                )
                                .setDefaultValue(() -> defaults == null ? null : Lists.newArrayList((Integer[]) getUnsafely(field, defaults)))
                                .setSaveConsumer(newValue -> setUnsafely(field, config, newValue.toArray(new Integer[0])))
                                .build()
                ),
                Integer[].class
        );
        
        registry.registerTypeProvider(
                (i18n, field, config, defaults, guiProvider) -> Collections.singletonList(
                        ENTRY_BUILDER.startIntList(
                                        Component.translatable(i18n),
                                        Lists.newArrayList(IntStream.of(getUnsafely(field, config, new int[0])).boxed().collect(Collectors.toList()))
                                )
                                .setDefaultValue(() -> defaults == null ? null : Lists.newArrayList(Arrays.asList(ArrayUtils.toObject((int[]) getUnsafely(field, defaults)))))
                                .setSaveConsumer(newValue -> setUnsafely(field, config, newValue.stream().mapToInt(Integer::intValue).toArray()))
                                .build()
                ),
                int[].class
        );
        
        registry.registerTypeProvider(
                (i18n, field, config, defaults, guiProvider) -> Collections.singletonList(
                        ENTRY_BUILDER.startLongField(
                                        Component.translatable(i18n),
                                        getUnsafely(field, config, 0L)
                                )
                                .setDefaultValue(() -> getUnsafely(field, defaults))
                                .setSaveConsumer(newValue -> setUnsafely(field, config, newValue))
                                .build()
                ),
                long.class, Long.class
        );
        
        registry.registerTypeProvider(
                (i18n, field, config, defaults, guiProvider) -> Collections.singletonList(
                        ENTRY_BUILDER.startLongList(
                                        Component.translatable(i18n),
                                        Lists.newArrayList(getUnsafely(field, config, new Long[0]))
                                )
                                .setDefaultValue(() -> defaults == null ? null : Lists.newArrayList((Long[]) getUnsafely(field, defaults)))
                                .setSaveConsumer(newValue -> setUnsafely(field, config, newValue.toArray(new Long[0])))
                                .build()
                ),
                Long[].class
        );
        
        registry.registerTypeProvider(
                (i18n, field, config, defaults, guiProvider) -> Collections.singletonList(
                        ENTRY_BUILDER.startLongList(
                                        Component.translatable(i18n),
                                        Lists.newArrayList(LongStream.of(getUnsafely(field, config, new long[0])).boxed().collect(Collectors.toList()))
                                )
                                .setDefaultValue(() -> defaults == null ? null : Lists.newArrayList(Arrays.asList(ArrayUtils.toObject((long[]) getUnsafely(field, defaults)))))
                                .setSaveConsumer(newValue -> setUnsafely(field, config, newValue.stream().mapToLong(Long::longValue).toArray()))
                                .build()
                ),
                long[].class
        );
        
        registry.registerTypeProvider(
                (i18n, field, config, defaults, guiProvider) -> Collections.singletonList(
                        ENTRY_BUILDER.startFloatField(
                                        Component.translatable(i18n),
                                        getUnsafely(field, config, 0f)
                                )
                                .setDefaultValue(() -> getUnsafely(field, defaults))
                                .setSaveConsumer(newValue -> setUnsafely(field, config, newValue))
                                .build()
                ),
                float.class, Float.class
        );
        
        registry.registerTypeProvider(
                (i18n, field, config, defaults, guiProvider) -> Collections.singletonList(
                        ENTRY_BUILDER.startFloatList(
                                        Component.translatable(i18n),
                                        Lists.newArrayList(getUnsafely(field, config, new Float[0]))
                                )
                                .setDefaultValue(() -> defaults == null ? null : Lists.newArrayList((Float[]) getUnsafely(field, defaults)))
                                .setSaveConsumer(newValue -> setUnsafely(field, config, newValue.toArray(new Float[0])))
                                .build()
                ),
                Float[].class
        );
        
        registry.registerTypeProvider(
                (i18n, field, config, defaults, guiProvider) -> Collections.singletonList(
                        ENTRY_BUILDER.startFloatList(
                                        Component.translatable(i18n),
                                        Lists.newArrayList(Arrays.asList(ArrayUtils.toObject(getUnsafely(field, config, new float[0]))))
                                )
                                .setDefaultValue(() -> defaults == null ? null : Lists.newArrayList(Arrays.asList(ArrayUtils.toObject((float[]) getUnsafely(field, defaults)))))
                                .setSaveConsumer(newValue -> setUnsafely(field, config, ArrayUtils.toPrimitive(newValue.toArray(new Float[0]))))
                                .build()
                ),
                float[].class
        );
        
        registry.registerTypeProvider(
                (i18n, field, config, defaults, guiProvider) -> Collections.singletonList(
                        ENTRY_BUILDER.startDoubleField(
                                        Component.translatable(i18n),
                                        getUnsafely(field, config, 0.0)
                                )
                                .setDefaultValue(() -> getUnsafely(field, defaults))
                                .setSaveConsumer(newValue -> setUnsafely(field, config, newValue))
                                .build()
                ),
                double.class, Double.class
        );
        
        registry.registerTypeProvider(
                (i18n, field, config, defaults, guiProvider) -> Collections.singletonList(
                        ENTRY_BUILDER.startDoubleList(
                                        Component.translatable(i18n),
                                        Lists.newArrayList(getUnsafely(field, config, new Double[0]))
                                )
                                .setDefaultValue(() -> defaults == null ? null : Lists.newArrayList((Double[]) getUnsafely(field, defaults)))
                                .setSaveConsumer(newValue -> setUnsafely(field, config, newValue.toArray(new Double[0])))
                                .build()
                ),
                Double[].class
        );
        
        registry.registerTypeProvider(
                (i18n, field, config, defaults, guiProvider) -> Collections.singletonList(
                        ENTRY_BUILDER.startDoubleList(
                                        Component.translatable(i18n),
                                        Lists.newArrayList(Arrays.asList(ArrayUtils.toObject(getUnsafely(field, config, new double[0]))))
                                )
                                .setDefaultValue(() -> defaults == null ? null : Lists.newArrayList(Arrays.asList(ArrayUtils.toObject((double[]) getUnsafely(field, defaults)))))
                                .setSaveConsumer(newValue -> setUnsafely(field, config, ArrayUtils.toPrimitive(newValue.toArray(new Double[0]))))
                                .build()
                ),
                double[].class
        );
        
        registry.registerTypeProvider(
                (i18n, field, config, defaults, guiProvider) -> Collections.singletonList(
                        ENTRY_BUILDER.startStrField(
                                        Component.translatable(i18n),
                                        getUnsafely(field, config, "")
                                )
                                .setDefaultValue(() -> getUnsafely(field, defaults))
                                .setSaveConsumer(newValue -> setUnsafely(field, config, newValue))
                                .build()
                ),
                String.class
        );
        
        registry.registerTypeProvider(
                (i18n, field, config, defaults, guiProvider) -> Collections.singletonList(
                        ENTRY_BUILDER.startStrList(
                                        Component.translatable(i18n),
                                        Lists.newArrayList(getUnsafely(field, config, new String[0]))
                                )
                                .setDefaultValue(() -> defaults == null ? null : Lists.newArrayList((String[]) getUnsafely(field, defaults)))
                                .setSaveConsumer(newValue -> setUnsafely(field, config, newValue.toArray(new String[0])))
                                .build()
                ),
                String[].class
        );
        
        registry.registerPredicateProvider((i18n, field, config, defaults, registry1) -> {
            Object configValue = getUnsafely(field, config);
            List<Object> configValueAsList = new ArrayList<>(Array.getLength(configValue));
            for (int i = 0; i < Array.getLength(configValue); i++) {
                configValueAsList.add(Array.get(configValue, i));
            }
            
            Class<?> fieldTypeParam = field.getType().getComponentType();
            
            Object defaultElemValue = Utils.constructUnsafely(fieldTypeParam);
            
            String remainingI13n = i18n.substring(0, i18n.indexOf(".option") + ".option".length());
            String classI13n = String.format("%s.%s", remainingI13n, fieldTypeParam.getSimpleName());
            
            return Collections.singletonList(
                    new NestedListListEntry<Object, MultiElementListEntry<Object>>(
                            Component.translatable(i18n),
                            configValueAsList,
                            false,
                            null,
                            newValue -> {
                                Object[] newArray = (Object[]) Array.newInstance(fieldTypeParam, newValue.size());
                                for (int i = 0; i < newValue.size(); i++) {
                                    Array.set(newArray, i, newValue.get(i));
                                }
                                setUnsafely(field, config, newArray);
                            },
                            () -> {
                                Object o = getUnsafely(field, defaults);
                                List<Object> asList = new ArrayList<>(Array.getLength(o));
                                for (int i = 0; i < Array.getLength(o); i++) {
                                    asList.add(Array.get(o, i));
                                }
                                return asList;
                            },
                            ENTRY_BUILDER.getResetButtonKey(),
                            true,
                            false,
                            (elem, nestedListListEntry) -> {
                                if (elem == null) {
                                    Object newDefaultElemValue = Utils.constructUnsafely(fieldTypeParam);
                                    return new MultiElementListEntry<>(Component.translatable(classI13n), newDefaultElemValue, (List) getChildren(classI13n, fieldTypeParam, newDefaultElemValue, defaultElemValue, registry1), true);
                                } else
                                    return new MultiElementListEntry<>(Component.translatable(classI13n), elem, (List) getChildren(classI13n, fieldTypeParam, elem, defaultElemValue, registry1), true);
                            }
                    )
            );
        }, field -> {
            return field.getType().isArray() && (field.getType() != String[].class
                                                 && field.getType() != int[].class && field.getType() != Integer[].class
                                                 && field.getType() != long[].class && field.getType() != Long[].class
                                                 && field.getType() != float[].class && field.getType() != Float[].class
                                                 && field.getType() != double[].class && field.getType() != Double[].class);
        });
        
        return registry;
    }
    
    private static List<AbstractConfigListEntry> getChildren(String i18n, Field field, Object config, Object defaults, GuiRegistryAccess guiProvider) {
        return getChildren(i18n, field.getType(), getUnsafely(field, config), getUnsafely(field, defaults), guiProvider);
    }
    
    private static List<AbstractConfigListEntry> getChildren(String i18n, Class<?> fieldType, Object iConfig, Object iDefaults, GuiRegistryAccess guiProvider) {
        return Arrays.stream(fieldType.getDeclaredFields())
                .map(
                        iField -> {
                            String iI13n = String.format("%s.%s", i18n, iField.getName());
                            return guiProvider.getAndTransform(iI13n, iField, iConfig, iDefaults, guiProvider);
                        }
                )
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
    
    /**
     * Returns a predicate that tests if the field is a list containing some particular {@link Type}s, i.e. {@code List<Integer>}.
     *
     * @param types the types to check for in the list's parameter
     * @return {@code true} if the field is a list containing the provided type, {@code false} otherwise
     */
    private static Predicate<Field> isListOfType(Type... types) {
        return field -> {
            if (List.class.isAssignableFrom(field.getType()) && field.getGenericType() instanceof ParameterizedType) {
                Type[] args = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
                return args.length == 1 && Stream.of(types).anyMatch(type -> Objects.equals(args[0], type));
            } else {
                return false;
            }
        };
    }
    
    /**
     * Returns a predicate that tests if the field is a list <i>not</i> containing any particular {@link Type}s, i.e. anything that isn't a {@code List<Integer>}.
     *
     * @param types the types to check for in the list's parameter
     * @return {@code true} if the field is a list <i>not</i> containing any of the provided types, {@code false} otherwise
     */
    private static Predicate<Field> isNotListOfType(Type... types) {
        return field -> {
            if (List.class.isAssignableFrom(field.getType()) && field.getGenericType() instanceof ParameterizedType) {
                Type[] args = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
                return args.length == 1 && Stream.of(types).noneMatch(type -> Objects.equals(args[0], type));
            } else {
                return false;
            }
        };
    }
}
