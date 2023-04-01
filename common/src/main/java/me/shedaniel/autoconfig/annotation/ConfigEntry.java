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

import me.shedaniel.clothconfig2.api.dependencies.GroupRequirement;
import me.shedaniel.clothconfig2.api.dependencies.conditions.ComparisonOperator;
import me.shedaniel.clothconfig2.api.dependencies.conditions.ConditionFlag;
import me.shedaniel.clothconfig2.impl.dependencies.BooleanDependency;

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
    
    public static class Dependency {
        private Dependency() {}
    
        /**
         * Depends on the referenced field.
         * If the dependency is not met, the annotated field's config entry will be disabled.
         * <br><br>
         * A field can be annotated multiple times.
         * All defined dependencies must be met for the config entry to be enabled.
         * <br><br>
         * Alternatively, {@link EnableIfGroup @EnableIfGroup} can also be used to define multiple dependencies,
         * optionally using alternative group-matching conditions such as {@code any}, {@code none}, or exactly {@code one}. 
         * 
         * @see EnableIfGroup
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        @Repeatable(EnableIfGroup.class)
        public @interface EnableIf {
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
             * <ul>
             *     <li>
             *         {@link BooleanDependency Boolean} dependencies require zero or one conditions.
             *         If no conditions are defined, <em>{@code "true"}</em> is assumed.
             *         If multiple conditions are defined, an {@link IllegalArgumentException} will be thrown.
             *     </li>
             *     <li>
             *         All other dependency types require one or more conditions be defined.
             *         If no conditions are defined, an {@link IllegalArgumentException} will be thrown.
             *     </li>
             * </ul>
             * 
             * <h2>Flags
             * <p>The value can optionally be prefixed with "{@link ConditionFlag flags}"
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
            String[] conditions() default {};
    
            /**
             * One or more i18n keys referencing config entries. The dependency is met if the {@link #value() depended-on}
             * config entry's value matches the value of at least one config entry listed here.
             * 
             * <p>A single dependency should not define both static conditions and reference matching conditions. I.e. if
             * {@link #conditions()} is used, this parameter should not be (and vice-versa). If both are present an
             * {@link IllegalArgumentException} will be thrown at runtime.
             * 
             * <p>
             * All config entries must use a type compatible with the depended-on config entry referenced in {@link #value()}.
             * For example, if the depended-on config entry is a {@link me.shedaniel.clothconfig2.gui.entries.BooleanListEntry boolean config entry}
             * then only other boolean config entries should be listed.
             * 
             * <p>As with static conditions, flags can be prefixed to the reference string. Refer to the static condition
             * {@link #conditions() documentation} for more detail.
             * 
             * <p>If the dependency is numeric, a {@link ComparisonOperator comparison operator}
             * can be included before the i18n key (optionally separated by whitespace, for readability).
             * If present the appropriate comparison will be used instead of checking equality.
             * For example, if <em>'{@code >}'</em> is included before the i18n key, the condition is true when the
             * depended-on config entry's value is greater than the other config entry's value.
             */
            String[] matching() default {};
    
            /**
             * Whether a tooltip describing the dependency should be generated.
             * Setting <em>'{@code tooltip}'</em> to false will also hide this dependency from tooltips
             * generated by any groups it is a member of.
             * <p>
             * If you disable auto-generated tooltips, it is strongly recommended that you implement an alternative
             * way of informing users that the annotated config entry has particular dependencies.
             */
            boolean tooltip() default true;
        }
    
        /**
         * Defines a group of dependencies, with a "group-matching" {@link GroupRequirement} to be met.
         * 
         * @see GroupRequirement
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        public @interface EnableIfGroup {
        
            /**
             * The dependencies to be included in the group.
             */
            EnableIf[] value();
        
            /**
             * The condition for this group to be met. By defaults, require all dependencies to be met.
             *
             * @see GroupRequirement
             */
            GroupRequirement condition() default GroupRequirement.ALL;
    
            /**
             * Whether this group should be logically inverted. For example an inverted group with an
             * {@link GroupRequirement#ALL "ALL" condition} set would be considered met if any one dependency was unmet.
             */
            boolean inverted() default false;
    
            /**
             * @see EnableIf#tooltip() 
             */
            boolean tooltip() default true;
        }
    
        /**
         * Depends on the referenced field.
         * If the dependency is not met, the annotated field's config entry will be completely hidden from menus.
         * <br>
         * A field can be annotated multiple times.
         * All defined dependencies must be met for the config entry to be enabled.
         * <br>
         * Alternatively, {@link ShowIfGroup @ShowIfGroup} can also be used to define multiple dependencies,
         * optionally using alternative group-matching conditions such as {@code any}, {@code none}, or exactly {@code one}. 
         *
         * @see ShowIfGroup
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        @Repeatable(ShowIfGroup.class)
        public @interface ShowIf {
    
            /**
             * @see EnableIf#value() 
             */
            String value();
    
            /**
             * @see EnableIf#conditions()
             */
            String[] conditions() default {};
    
            /**
             * @see EnableIf#matching()
             */
            String[] matching() default {};
    
            /**
              @see EnableIf#tooltip() 
             */
            boolean tooltip() default true;
        }
    
        /**
         * Defines a group of dependencies, with a "group-matching" {@link GroupRequirement} to be met.
         *
         * @see GroupRequirement
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        public @interface ShowIfGroup {
        
            /**
             * The dependencies to be included in the group.
             */
            ShowIf[] value();
        
            /**
             * The condition for this group to be met. By defaults, require all dependencies to be met.
             *
             * @see GroupRequirement
             */
            GroupRequirement condition() default GroupRequirement.ALL;
        
            /**
             * Whether this group should be logically inverted. For example an inverted group with an
             * {@link GroupRequirement#ALL "ALL" condition} set would be considered met if any one dependency was unmet.
             */
            boolean inverted() default false;
    
            /**
              @see EnableIf#tooltip() 
             */
            boolean tooltip() default true;
        }
    }
    
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
}
