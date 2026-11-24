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

package me.shedaniel.clothconfig2.impl.builders;

import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry.DefaultSelectionCellCreator;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry.DefaultSelectionTopCellElement;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry.SelectionCellCreator;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry.SelectionTopCellElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class DropdownMenuBuilder<T> extends FieldBuilder<T, DropdownBoxEntry<T>, DropdownMenuBuilder<T>> {
    protected SelectionTopCellElement<T> topCellElement;
    protected SelectionCellCreator<T> cellCreator;
    protected Function<T, Optional<Component[]>> tooltipSupplier = str -> Optional.empty();
    protected Consumer<T> saveConsumer = null;
    protected Iterable<T> selections = Collections.emptyList();
    protected boolean suggestionMode = true;
    
    public DropdownMenuBuilder(Component resetButtonKey, Component fieldNameKey, SelectionTopCellElement<T> topCellElement, SelectionCellCreator<T> cellCreator) {
        super(resetButtonKey, fieldNameKey);
        this.topCellElement = Objects.requireNonNull(topCellElement);
        this.cellCreator = Objects.requireNonNull(cellCreator);
    }
    
    public DropdownMenuBuilder<T> setSelections(Iterable<T> selections) {
        this.selections = selections;
        return this;
    }
    
    public DropdownMenuBuilder<T> setDefaultValue(Supplier<T> defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }
    
    public DropdownMenuBuilder<T> setDefaultValue(T defaultValue) {
        this.defaultValue = () -> Objects.requireNonNull(defaultValue);
        return this;
    }
    
    public DropdownMenuBuilder<T> setSaveConsumer(Consumer<T> saveConsumer) {
        this.saveConsumer = saveConsumer;
        return this;
    }
    
    public DropdownMenuBuilder<T> setTooltipSupplier(Supplier<Optional<Component[]>> tooltipSupplier) {
        this.tooltipSupplier = str -> tooltipSupplier.get();
        return this;
    }
    
    public DropdownMenuBuilder<T> setTooltipSupplier(Function<T, Optional<Component[]>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
        return this;
    }
    
    public DropdownMenuBuilder<T> setTooltip(Optional<Component[]> tooltip) {
        this.tooltipSupplier = str -> tooltip;
        return this;
    }
    
    public DropdownMenuBuilder<T> setTooltip(Component... tooltip) {
        this.tooltipSupplier = str -> Optional.ofNullable(tooltip);
        return this;
    }
    
    public DropdownMenuBuilder<T> requireRestart() {
        requireRestart(true);
        return this;
    }
    
    public DropdownMenuBuilder<T> setErrorSupplier(Function<T, Optional<Component>> errorSupplier) {
        this.errorSupplier = errorSupplier;
        return this;
    }
    
    public DropdownMenuBuilder<T> setSuggestionMode(boolean suggestionMode) {
        this.suggestionMode = suggestionMode;
        return this;
    }
    
    public boolean isSuggestionMode() {
        return suggestionMode;
    }
    
    @NotNull
    @Override
    public DropdownBoxEntry<T> build() {
        DropdownBoxEntry<T> entry = new DropdownBoxEntry<>(getFieldNameKey(), getResetButtonKey(), null, isRequireRestart(), defaultValue, saveConsumer, selections, topCellElement, cellCreator);
        entry.setTooltipSupplier(() -> tooltipSupplier.apply(entry.getValue()));
        if (errorSupplier != null)
            entry.setErrorSupplier(() -> errorSupplier.apply(entry.getValue()));
        entry.setSuggestionMode(suggestionMode);
        return finishBuilding(entry);
    }
    
    public static class TopCellElementBuilder {
        public static final Function<String, Identifier> IDENTIFIER_FUNCTION = str -> {
            try {
                return Identifier.parse(str);
            } catch (NumberFormatException e) {
                return null;
            }
        };
        public static final Function<String, Identifier> ITEM_IDENTIFIER_FUNCTION = str -> {
            try {
                Identifier identifier = Identifier.parse(str);
                if (BuiltInRegistries.ITEM.getOptional(identifier).isPresent())
                    return identifier;
            } catch (Exception ignored) {
            }
            return null;
        };
        public static final Function<String, Identifier> BLOCK_IDENTIFIER_FUNCTION = str -> {
            try {
                Identifier identifier = Identifier.parse(str);
                if (BuiltInRegistries.BLOCK.getOptional(identifier).isPresent())
                    return identifier;
            } catch (Exception ignored) {
            }
            return null;
        };
        public static final Function<String, Item> ITEM_FUNCTION = str -> {
            try {
                return BuiltInRegistries.ITEM.getOptional(Identifier.parse(str)).orElse(null);
            } catch (Exception ignored) {
            }
            return null;
        };
        public static final Function<String, Block> BLOCK_FUNCTION = str -> {
            try {
                return BuiltInRegistries.BLOCK.getOptional(Identifier.parse(str)).orElse(null);
            } catch (Exception ignored) {
            }
            return null;
        };
        
        public static <T> SelectionTopCellElement<T> of(T value, Function<String, T> toObjectFunction) {
            return of(value, toObjectFunction, t -> Component.literal(t.toString()));
        }
        
        public static <T> SelectionTopCellElement<T> of(T value, Function<String, T> toObjectFunction, Function<T, Component> toTextFunction) {
            return new DefaultSelectionTopCellElement<>(value, toObjectFunction, toTextFunction);
        }
        
        public static SelectionTopCellElement<Identifier> ofItemIdentifier(Item item) {
            return new DefaultSelectionTopCellElement<>(BuiltInRegistries.ITEM.getKey(item), ITEM_IDENTIFIER_FUNCTION, identifier -> Component.literal(identifier.toString())) {
                @Override
                public void extractRenderState(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
                    textFieldWidget.setX(x + 4);
                    textFieldWidget.setY(y + 6);
                    textFieldWidget.setWidth(width - 4 - 20);
                    textFieldWidget.setEditable(getParent().isEditable());
                    textFieldWidget.setTextColor(getPreferredTextColor());
                    textFieldWidget.extractRenderState(graphics);
                    ItemStack stack = hasConfigError() ? new ItemStack(Items.BARRIER) : new ItemStack(BuiltInRegistries.ITEM.getValue(getValue()));
                    graphics.item(stack, x + width - 18, y + 2);
                }
            };
        }
        
        public static SelectionTopCellElement<Identifier> ofBlockIdentifier(Block block) {
            return new DefaultSelectionTopCellElement<>(BuiltInRegistries.BLOCK.getKey(block), BLOCK_IDENTIFIER_FUNCTION, identifier -> Component.literal(identifier.toString())) {
                @Override
                public void extractRenderState(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
                    textFieldWidget.setX(x + 4);
                    textFieldWidget.setY(y + 6);
                    textFieldWidget.setWidth(width - 4 - 20);
                    textFieldWidget.setEditable(getParent().isEditable());
                    textFieldWidget.setTextColor(getPreferredTextColor());
                    textFieldWidget.extractRenderState(graphics);
                    ItemStack stack = hasConfigError() ? new ItemStack(Items.BARRIER) : new ItemStack(BuiltInRegistries.BLOCK.getValue(getValue()));
                    graphics.item(stack, x + width - 18, y + 2);
                }
            };
        }
        
        public static SelectionTopCellElement<Item> ofItemObject(Item item) {
            return new DefaultSelectionTopCellElement<>(item, ITEM_FUNCTION, i -> Component.literal(BuiltInRegistries.ITEM.getKey(i).toString())) {
                @Override
                public void extractRenderState(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
                    textFieldWidget.setX(x + 4);
                    textFieldWidget.setY(y + 6);
                    textFieldWidget.setWidth(width - 4 - 20);
                    textFieldWidget.setEditable(getParent().isEditable());
                    textFieldWidget.setTextColor(getPreferredTextColor());
                    textFieldWidget.extractRenderState(graphics);
                    ItemStack stack = hasConfigError() ? new ItemStack(Items.BARRIER) : new ItemStack(getValue());
                    graphics.item(stack, x + width - 18, y + 2);
                }
            };
        }
        
        public static SelectionTopCellElement<Block> ofBlockObject(Block block) {
            return new DefaultSelectionTopCellElement<>(block, BLOCK_FUNCTION, i -> Component.literal(BuiltInRegistries.BLOCK.getKey(i).toString())) {
                @Override
                public void extractRenderState(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
                    textFieldWidget.setX(x + 4);
                    textFieldWidget.setY(y + 6);
                    textFieldWidget.setWidth(width - 4 - 20);
                    textFieldWidget.setEditable(getParent().isEditable());
                    textFieldWidget.setTextColor(getPreferredTextColor());
                    textFieldWidget.extractRenderState(graphics);
                    ItemStack stack = hasConfigError() ? new ItemStack(Items.BARRIER) : new ItemStack(getValue());
                    graphics.item(stack, x + width - 18, y + 2);
                }
            };
        }
    }
    
    public static class CellCreatorBuilder {
        public static <T> SelectionCellCreator<T> of() {
            return new DefaultSelectionCellCreator<>();
        }
        
        public static <T> SelectionCellCreator<T> of(Function<T, Component> toTextFunction) {
            return new DefaultSelectionCellCreator<>(toTextFunction);
        }
        
        public static <T> SelectionCellCreator<T> ofWidth(int cellWidth) {
            return new DefaultSelectionCellCreator<T>() {
                @Override
                public int getCellWidth() {
                    return cellWidth;
                }
            };
        }
        
        public static <T> SelectionCellCreator<T> ofWidth(int cellWidth, Function<T, Component> toTextFunction) {
            return new DefaultSelectionCellCreator<T>(toTextFunction) {
                @Override
                public int getCellWidth() {
                    return cellWidth;
                }
            };
        }
        
        public static <T> SelectionCellCreator<T> ofCellCount(int maxItems) {
            return new DefaultSelectionCellCreator<T>() {
                @Override
                public int getDropBoxMaxHeight() {
                    return getCellHeight() * maxItems;
                }
            };
        }
        
        public static <T> SelectionCellCreator<T> ofCellCount(int maxItems, Function<T, Component> toTextFunction) {
            return new DefaultSelectionCellCreator<T>(toTextFunction) {
                @Override
                public int getDropBoxMaxHeight() {
                    return getCellHeight() * maxItems;
                }
            };
        }
        
        public static <T> SelectionCellCreator<T> of(int cellWidth, int maxItems) {
            return new DefaultSelectionCellCreator<T>() {
                @Override
                public int getCellWidth() {
                    return cellWidth;
                }
                
                @Override
                public int getDropBoxMaxHeight() {
                    return getCellHeight() * maxItems;
                }
            };
        }
        
        public static <T> SelectionCellCreator<T> of(int cellWidth, int maxItems, Function<T, Component> toTextFunction) {
            return new DefaultSelectionCellCreator<T>(toTextFunction) {
                @Override
                public int getCellWidth() {
                    return cellWidth;
                }
                
                @Override
                public int getDropBoxMaxHeight() {
                    return getCellHeight() * maxItems;
                }
            };
        }
        
        public static <T> SelectionCellCreator<T> of(int cellHeight, int cellWidth, int maxItems) {
            return new DefaultSelectionCellCreator<T>() {
                @Override
                public int getCellHeight() {
                    return cellHeight;
                }
                
                @Override
                public int getCellWidth() {
                    return cellWidth;
                }
                
                @Override
                public int getDropBoxMaxHeight() {
                    return getCellHeight() * maxItems;
                }
            };
        }
        
        public static <T> SelectionCellCreator<T> of(int cellHeight, int cellWidth, int maxItems, Function<T, Component> toTextFunction) {
            return new DefaultSelectionCellCreator<T>(toTextFunction) {
                @Override
                public int getCellHeight() {
                    return cellHeight;
                }
                
                @Override
                public int getCellWidth() {
                    return cellWidth;
                }
                
                @Override
                public int getDropBoxMaxHeight() {
                    return getCellHeight() * maxItems;
                }
            };
        }
        
        public static SelectionCellCreator<Identifier> ofItemIdentifier() {
            return ofItemIdentifier(20, 146, 7);
        }
        
        public static SelectionCellCreator<Identifier> ofItemIdentifier(int maxItems) {
            return ofItemIdentifier(20, 146, maxItems);
        }
        
        public static SelectionCellCreator<Identifier> ofItemIdentifier(int cellHeight, int cellWidth, int maxItems) {
            return new DefaultSelectionCellCreator<Identifier>() {
                @Override
                public DropdownBoxEntry.SelectionCellElement<Identifier> create(Identifier selection) {
                    ItemStack s = new ItemStack(BuiltInRegistries.ITEM.getValue(selection));
                    return new DropdownBoxEntry.DefaultSelectionCellElement<Identifier>(selection, toTextFunction) {
                        @Override
                        public void extractRenderState(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
                            rendering = true;
                            this.x = x;
                            this.y = y;
                            this.width = width;
                            this.height = height;
                            int mouseX = graphics.mouseX();
                            int mouseY = graphics.mouseY();
                            boolean b = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
                            if (b)
                                graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, -15132391);
                            graphics.text(Minecraft.getInstance().font, toTextFunction.apply(r).getVisualOrderText(), x + 6 + 18, y + 6, b ? 0xffffffff : 0xff888888);
                            graphics.item(s, x + 4, y + 2);
                        }
                    };
                }
                
                @Override
                public int getCellHeight() {
                    return cellHeight;
                }
                
                @Override
                public int getCellWidth() {
                    return cellWidth;
                }
                
                @Override
                public int getDropBoxMaxHeight() {
                    return getCellHeight() * maxItems;
                }
            };
        }
        
        
        public static SelectionCellCreator<Identifier> ofBlockIdentifier() {
            return ofBlockIdentifier(20, 146, 7);
        }
        
        public static SelectionCellCreator<Identifier> ofBlockIdentifier(int maxItems) {
            return ofBlockIdentifier(20, 146, maxItems);
        }
        
        public static SelectionCellCreator<Identifier> ofBlockIdentifier(int cellHeight, int cellWidth, int maxItems) {
            return new DefaultSelectionCellCreator<Identifier>() {
                @Override
                public DropdownBoxEntry.SelectionCellElement<Identifier> create(Identifier selection) {
                    ItemStack s = new ItemStack(BuiltInRegistries.BLOCK.getValue(selection));
                    return new DropdownBoxEntry.DefaultSelectionCellElement<Identifier>(selection, toTextFunction) {
                        @Override
                        public void extractRenderState(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
                            rendering = true;
                            this.x = x;
                            this.y = y;
                            this.width = width;
                            this.height = height;
                            int mouseX = graphics.mouseX();
                            int mouseY = graphics.mouseY();
                            boolean b = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
                            if (b)
                                graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, -15132391);
                            graphics.text(Minecraft.getInstance().font, toTextFunction.apply(r).getVisualOrderText(), x + 6 + 18, y + 6, b ? 0xffffffff : 0xff888888);
                            graphics.item(s, x + 4, y + 2);
                        }
                    };
                }
                
                @Override
                public int getCellHeight() {
                    return cellHeight;
                }
                
                @Override
                public int getCellWidth() {
                    return cellWidth;
                }
                
                @Override
                public int getDropBoxMaxHeight() {
                    return getCellHeight() * maxItems;
                }
            };
        }
        
        public static SelectionCellCreator<Item> ofItemObject() {
            return ofItemObject(20, 146, 7);
        }
        
        public static SelectionCellCreator<Item> ofItemObject(int maxItems) {
            return ofItemObject(20, 146, maxItems);
        }
        
        public static SelectionCellCreator<Item> ofItemObject(int cellHeight, int cellWidth, int maxItems) {
            return new DefaultSelectionCellCreator<Item>(i -> Component.literal(BuiltInRegistries.ITEM.getKey(i).toString())) {
                @Override
                public DropdownBoxEntry.SelectionCellElement<Item> create(Item selection) {
                    ItemStack s = new ItemStack(selection);
                    return new DropdownBoxEntry.DefaultSelectionCellElement<Item>(selection, toTextFunction) {
                        @Override
                        public void extractRenderState(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
                            rendering = true;
                            this.x = x;
                            this.y = y;
                            this.width = width;
                            this.height = height;
                            int mouseX = graphics.mouseX();
                            int mouseY = graphics.mouseY();
                            boolean b = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
                            if (b)
                                graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, -15132391);
                            graphics.text(Minecraft.getInstance().font, toTextFunction.apply(r).getVisualOrderText(), x + 6 + 18, y + 6, b ? 0xffffffff : 0xff888888);
                            graphics.item(s, x + 4, y + 2);
                        }
                    };
                }
                
                @Override
                public int getCellHeight() {
                    return cellHeight;
                }
                
                @Override
                public int getCellWidth() {
                    return cellWidth;
                }
                
                @Override
                public int getDropBoxMaxHeight() {
                    return getCellHeight() * maxItems;
                }
            };
        }
        
        public static SelectionCellCreator<Block> ofBlockObject() {
            return ofBlockObject(20, 146, 7);
        }
        
        public static SelectionCellCreator<Block> ofBlockObject(int maxItems) {
            return ofBlockObject(20, 146, maxItems);
        }
        
        public static SelectionCellCreator<Block> ofBlockObject(int cellHeight, int cellWidth, int maxItems) {
            return new DefaultSelectionCellCreator<Block>(i -> Component.literal(BuiltInRegistries.BLOCK.getKey(i).toString())) {
                @Override
                public DropdownBoxEntry.SelectionCellElement<Block> create(Block selection) {
                    ItemStack s = new ItemStack(selection);
                    return new DropdownBoxEntry.DefaultSelectionCellElement<Block>(selection, toTextFunction) {
                        @Override
                        public void extractRenderState(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
                            rendering = true;
                            this.x = x;
                            this.y = y;
                            this.width = width;
                            this.height = height;
                            int mouseX = graphics.mouseX();
                            int mouseY = graphics.mouseY();
                            boolean b = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
                            if (b)
                                graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, -15132391);
                            graphics.text(Minecraft.getInstance().font, toTextFunction.apply(r).getVisualOrderText(), x + 6 + 18, y + 6, b ? 0xffffffff : 0xff888888);
                            graphics.item(s, x + 4, y + 2);
                        }
                    };
                }
                
                @Override
                public int getCellHeight() {
                    return cellHeight;
                }
                
                @Override
                public int getCellWidth() {
                    return cellWidth;
                }
                
                @Override
                public int getDropBoxMaxHeight() {
                    return getCellHeight() * maxItems;
                }
            };
        }
    }
}
