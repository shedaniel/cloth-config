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

import me.shedaniel.autoconfig.requirements.DefaultRequirements;
import me.shedaniel.clothconfig2.api.DisableableWidget;
import me.shedaniel.clothconfig2.api.HideableWidget;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
    }
    
    public static class Requirements {
        private Requirements() {}
        
        /**
         * Defines zero or more {@link Requirement} definitions that will control whether this Config Entry GUI is
         * enabled or disabled.
         * 
         * @see Requirement
         * @see DisableableWidget
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        public @interface EnableIf {
            Requirement[] value();
            Quantifier quantifier() default Quantifier.ALL;
        }
        
        /**
         * Defines zero or more {@link Requirement} definitions that will control whether this Config Entry GUI is
         * displayed on the screen.
         *
         * @see Requirement
         * @see HideableWidget
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        public @interface DisplayIf {
            Requirement[] value();
            Quantifier quantifier() default Quantifier.ALL;
        }
        
        /**
         * Defines a Requirement, which is a reference to a method handler
         * along with a set of arguments to be passed to the Handler.
         */
        @Retention(RetentionPolicy.RUNTIME)
        public @interface Requirement {
            /**
             * The name of the Handler method to be used.
             */
            String value();
            
            /**
             * The class in which to look for the Handler method. Defaults to {@link DefaultRequirements}.
             * 
             * @see DefaultRequirements
             */
            Class<?> cls() default DefaultRequirements.class;
            
            /**
             * Zero or more {@link ConfigEntry.Ref references} to Config Entries, whose value should be passed to the handler method.
             */
            ConfigEntry.Ref[] refArgs() default {};
            
            /**
             * Zero or more static values to be passed to the handler method.
             * 
             * <p>The following parameter types are supported:
             * <ul>
             *     <li>{@link String}: The arg will be used as-is.
             *     <li>{@link Character}: The first char of the arg will be used. The arg must be exactly 1 characters long.
             *     <li>{@link Boolean}: The arg will be checked against the following values (case-insensitive):
             *     <ul>
             *         <li>{@code true} for: {@code "true"}, {@code "t"}, or {@code "1"}.
             *         <li>{@code false} for: {@code "false"}, {@code "f"}, or {@code "0"}.
             *     </ul>
             *     <li>{@link Enum}: The arg will be compared against the {@link Enum#name() name values} of the expected Enum.
             *         The value must be an exact match (case-sensitive).
             *     <li>{@code short int long float double}: The arg will be parsed using the appropriate method,
             *         such as {@link Integer#valueOf(String)}.
             * </ul>
             * <p>An exception will be thrown if a match is not found, parsing fails, or the expected type is not listed above.
             */
            String[] staticArgs() default {};
        }
        
        /**
         * Can be applied to a handler method to declare a list of {@link Ref refs} that should be passed to the handler
         * as its initial arguments.
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.METHOD)
        public @interface ConstParams {
            Ref[] value();
        }
    }
    
    /**
     * Defines a reference to a {@link ConfigEntry}.
     *
     * <p>{@code value} should be the name of the {@code ConfigEntry}'s defining field.
     * <p>{@code cls} defines the class in which to look for the field.
     * If {@code cls} is set to the default value ({@link None None.class}),
     * then the annotated class is searched instead.
     */
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Ref {
        String value();
        Class<?> cls() default None.class;
    }
    
    /**
     * A quantifier representing how many things are required.
     */
    public enum Quantifier {
        ALL, ANY, ONE, NONE
    }
    
    /**
     * Used by ConfigEntry annotations to indicate no class is set.
     */
    private static class None {
        private None() {}
    }
}
