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

package me.shedaniel.autoconfig.annotation;

import me.shedaniel.clothconfig2.impl.dependencies.BooleanDependency;
import me.shedaniel.clothconfig2.impl.dependencies.DependencyGroup;
import me.shedaniel.clothconfig2.impl.dependencies.EnumDependency;

import java.lang.annotation.*;

public class ConfigEntry {
    
    private ConfigEntry() {
    }
    
    /**
     * Sets the category name of the config entry. Categories are created in order.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Category {
        String value();
    }
    
    /**
     * Applies to int and long fields.
     * Sets the GUI to a slider.
     * In a future version it will enforce bounds at deserialization.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface BoundedDiscrete {
        long min() default 0;
        
        long max();
    }
    
    /**
     * Applies to int fields.
     * Sets the GUI to a color picker.
     * In a future version it will enforce bounds at deserialization.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface ColorPicker {
        boolean allowAlpha() default false;
    }

//    /**
//     * Applies to float and double fields.
//     * In a future version it will enforce bounds at deserialization.
//     */
//    @Retention(RetentionPolicy.RUNTIME)
//    @Target(ElementType.FIELD)
//    public @interface BoundedFloating {
//        double min() default 0;
//
//        double max();
//    }
    
    public static class Gui {
        private Gui() {
        }
        
        /**
         * Removes the field from the config GUI.
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        public @interface Excluded {
        }
        
        /**
         * Applies to objects.
         * Adds GUI entries for the field's inner fields at the same level as this field.
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        public @interface TransitiveObject {
        }
        
        /**
         * Applies to objects.
         * Adds GUI entries for the field's inner fields in a collapsible section.
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        public @interface CollapsibleObject {
            boolean startExpanded() default false;
        }
        
        /**
         * Applies a tooltip to list entries that support it, defined in your lang file.
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        public @interface Tooltip {
            int count() default 1;
        }
        
        /**
         * Applies no tooltip to list entries that support it, defined in your lang file.
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        public @interface NoTooltip {
            
        }
        
        /**
         * Applies a section of text right before this entry, defined in your lang file.
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        public @interface PrefixText {
        }
        
        /**
         * Requires restart if the field is modified.
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        public @interface RequiresRestart {
            boolean value() default true;
        }
        
        /**
         * Defines how an enum is handled
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        public @interface EnumHandler {
            EnumDisplayOption option() default EnumDisplayOption.DROPDOWN;
            
            enum EnumDisplayOption {
                DROPDOWN,
                BUTTON
            }
        }
    
        /**
         * Depends on the referenced field.
         * <br>
         * A field can be annotated multiple times.
         * All defined dependencies must be met for the config entry to be enabled.
         * <br>
         * Alternatively, {@link DependsOnGroup} can also be used to define multiple dependencies,
         * optionally using alternative group-matching conditions such as {@code any}, {@code none}, or exactly {@code one}. 
         * 
         * @see DependsOnGroup
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        @Repeatable(DependsOnGroup.class)
        public @interface DependsOn {
            /**
             * The i18n key of the field to depend on.
             * <br><br>
             * Can be either absolute or relative. Relative keys must start with a '{@code .}' character, multiple
             * '{@code .}' characters can be used to reference parent keys. Absolute keys can either include or omit
             * the part or the key referencing the root {@link Config @Config} class.
             * <br><br>
             * For example, the following keys are equivalent when referenced from a field with key
             * <em>"{@code text.autoconfig.example.option.module.childObject.someOption}"</em>:
             * <ul>
             *     <li>Absolute: <em>"{@code text.autoconfig.example.option.module.childObject.otherOption}"</em></li>
             *     <li>Absolute: <em>"{@code option.module.childObject.otherOption}"</em></li>
             *     <li>Relative: <em>"{@code .otherOption}"</em></li>
             * </ul>
             * An option in a different object <em>"{@code siblingObject}"</em> could be referenced using any of the following:
             * <ul>
             *     <li>Absolute: <em>"{@code text.autoconfig.example.option.module.siblingObject.otherOption}"</em></li>
             *     <li>Absolute: <em>"{@code option.module.siblingObject.otherOption}"</em></li>
             *     <li>Relative: <em>"{@code ..siblingObject.otherOption}"</em> (uses multiple '{@code .}' characters)</li>
             * </ul>
             */
            String value();
    
            /**
             * If set to true, the annotated field will be hidden (instead of
             * simply being disabled) when the dependency is unmet.
             */
            boolean hiddenWhenNotMet() default false;
    
            /**
             * One or more conditions to be checked against the dependency's value.
             * If any condition is matched, the annotated field is enabled.
             * <br><br>
             * 
             * <h2>Parsing
             * <p>The value must be parsable into the appropriate type for the dependency, for example:
             * <ul>
             *     <li>{@code "true"} or {@code "false"} for a boolean dependency</li>
             *     <li>The {@code toString()} value for an Enum dependency</li>
             *     <li>Etc</li>
             * </ul>
             * 
             * <h2>Quantity
             * <p>Some dependency types may be stricter about the number of conditions defined.
             * For example:
             * <ul>
             *     <li>{@link BooleanDependency} requires exactly one condition be defined</li>
             *     <li>{@link EnumDependency} requires one or more conditions be defined</li>
             * </ul>
             * 
             * <h2>Flags
             * <p>The value can optionally be prefixed with "{@link me.shedaniel.clothconfig2.api.dependencies.conditions.Condition.Flag flags}"
             * that affect how the condition is applied.
             * <p>If the value starts with '<code>{</code>' then a corresponding '<code>}</code>' must be present.
             * Any characters within the <code>{</code> and <code>}</code> will be interpreted as flags.
             * <p>Unrecognised characters (including whitespace) within the flags section will cause an {@link IllegalArgumentException}
             * to be thrown at runtime.
             * <p>If you wish for your condition to literally start with a '<code>{</code>', you can start your value
             * with '<code>{}{</code>' instead.
             * <br><br>For example, the condition "<code>{!}Hello, world</code>" will be met when the depended-on entry's
             * value <strong>does not</strong> equal "<em>Hello, world</em>".
             * 
             * <br><br><p><strong>Valid flags include:</strong>
             * <ul>
             *     <li>'{@code !}' <em>not</em>: the condition will be inverted.</li>
             *     <li>'{@code i}' <em>insensitive</em>: a text-based condition will ignore capitalization.</li>
             * </ul>
             */
            String[] conditions();
        }
    
        /**
         * Defines a group of dependencies, with a "group-matching" {@link DependencyGroup.Condition} to be met.
         * 
         * @see DependencyGroup.Condition
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        public @interface DependsOnGroup {
        
            /**
             * The dependencies to be included in the group.
             */
            DependsOn[] value();
        
            /**
             * The condition for this group to be met. By defaults, require all dependencies to be met.
             *
             * @see DependencyGroup.Condition
             */
            DependencyGroup.Condition condition() default DependencyGroup.Condition.ALL;
    
            /**
             * Whether this group should be logically inverted. For example an inverted group with an
             * {@link DependencyGroup.Condition#ALL "ALL" condition} set would be considered met if any one dependency was unmet.
             */
            boolean inverted() default false;
        }
    }
}
