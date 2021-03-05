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

import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry.DefaultSelectionCellCreator;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry.DefaultSelectionTopCellElement;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry.SelectionCellCreator;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry.SelectionTopCellElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class DropdownMenuBuilder<T> extends FieldBuilder<T, DropdownBoxEntry<T>, DropdownMenuBuilder<T>> {
    protected SelectionTopCellElement<T> topCellElement;
    protected SelectionCellCreator<T> cellCreator;
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
        return super.setDefaultValue(defaultValue);
    }
    
    public DropdownMenuBuilder<T> setDefaultValue(T defaultValue) {
        return super.setDefaultValue(defaultValue);
    }
    
    public DropdownMenuBuilder<T> setSaveConsumer(Consumer<T> saveConsumer) {
        return super.setSaveConsumer(saveConsumer);
    }
    
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public DropdownMenuBuilder<T> setTooltipSupplier(Supplier<Optional<Component[]>> tooltipSupplier) {
        return super.setTooltip(tooltipSupplier);
    }
    
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public DropdownMenuBuilder<T> setTooltipSupplier(Function<T, Optional<Component[]>> tooltipSupplier) {
        return super.setTooltip(tooltipSupplier);
    }
    
    public DropdownMenuBuilder<T> setTooltip(Optional<Component[]> tooltip) {
        return super.setTooltip(tooltip);
    }
    
    public DropdownMenuBuilder<T> setTooltip(Component... tooltip) {
        return super.setTooltip(tooltip);
    }
    
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public DropdownMenuBuilder<T> requireRestart() {
        return super.requiresRestart();
    }
    
    public DropdownMenuBuilder<T> setErrorSupplier(Function<T, Optional<Component>> errorSupplier) {
        return super.setErrorSupplier(errorSupplier);
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
        return entry;
    }
    
    public static class TopCellElementBuilder {
        public static final Function<String, ResourceLocation> IDENTIFIER_FUNCTION = str -> {
            try {
                return new ResourceLocation(str);
            } catch (NumberFormatException e) {
                return null;
            }
        };
        public static final Function<String, ResourceLocation> ITEM_IDENTIFIER_FUNCTION = str -> {
            try {
                ResourceLocation identifier = new ResourceLocation(str);
                if (Registry.ITEM.getOptional(identifier).isPresent())
                    return identifier;
            } catch (Exception ignored) {
            }
            return null;
        };
        public static final Function<String, ResourceLocation> BLOCK_IDENTIFIER_FUNCTION = str -> {
            try {
                ResourceLocation identifier = new ResourceLocation(str);
                if (Registry.BLOCK.getOptional(identifier).isPresent())
                    return identifier;
            } catch (Exception ignored) {
            }
            return null;
        };
        public static final Function<String, Item> ITEM_FUNCTION = str -> {
            try {
                return Registry.ITEM.getOptional(new ResourceLocation(str)).orElse(null);
            } catch (Exception ignored) {
            }
            return null;
        };
        public static final Function<String, Block> BLOCK_FUNCTION = str -> {
            try {
                return Registry.BLOCK.getOptional(new ResourceLocation(str)).orElse(null);
            } catch (Exception ignored) {
            }
            return null;
        };
        private static final ItemStack BARRIER = new ItemStack(Items.BARRIER);
        
        public static <T> SelectionTopCellElement<T> of(T value, Function<String, T> toObjectFunction) {
            return of(value, toObjectFunction, t -> new TextComponent(t.toString()));
        }
        
        public static <T> SelectionTopCellElement<T> of(T value, Function<String, T> toObjectFunction, Function<T, Component> toTextFunction) {
            return new DefaultSelectionTopCellElement<>(value, toObjectFunction, toTextFunction);
        }
        
        public static SelectionTopCellElement<ResourceLocation> ofItemIdentifier(Item item) {
            return new DefaultSelectionTopCellElement<ResourceLocation>(Registry.ITEM.getKey(item), ITEM_IDENTIFIER_FUNCTION, identifier -> new TextComponent(identifier.toString())) {
                @Override
                public void render(PoseStack matrices, int mouseX, int mouseY, int x, int y, int width, int height, float delta) {
                    textFieldWidget.x = x + 4;
                    textFieldWidget.y = y + 6;
                    textFieldWidget.setWidth(width - 4 - 20);
                    textFieldWidget.setEditable(getParent().isEditable());
                    textFieldWidget.setTextColor(getPreferredTextColor());
                    textFieldWidget.render(matrices, mouseX, mouseY, delta);
                    ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
                    ItemStack stack = hasConfigError() ? BARRIER : new ItemStack(Registry.ITEM.get(getValue()));
                    itemRenderer.renderGuiItem(stack, x + width - 18, y + 2);
                }
            };
        }
        
        public static SelectionTopCellElement<ResourceLocation> ofBlockIdentifier(Block block) {
            return new DefaultSelectionTopCellElement<ResourceLocation>(Registry.BLOCK.getKey(block), BLOCK_IDENTIFIER_FUNCTION, identifier -> new TextComponent(identifier.toString())) {
                @Override
                public void render(PoseStack matrices, int mouseX, int mouseY, int x, int y, int width, int height, float delta) {
                    textFieldWidget.x = x + 4;
                    textFieldWidget.y = y + 6;
                    textFieldWidget.setWidth(width - 4 - 20);
                    textFieldWidget.setEditable(getParent().isEditable());
                    textFieldWidget.setTextColor(getPreferredTextColor());
                    textFieldWidget.render(matrices, mouseX, mouseY, delta);
                    ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
                    ItemStack stack = hasConfigError() ? BARRIER : new ItemStack(Registry.BLOCK.get(getValue()));
                    itemRenderer.renderGuiItem(stack, x + width - 18, y + 2);
                }
            };
        }
        
        public static SelectionTopCellElement<Item> ofItemObject(Item item) {
            return new DefaultSelectionTopCellElement<Item>(item, ITEM_FUNCTION, i -> new TextComponent(Registry.ITEM.getKey(i).toString())) {
                @Override
                public void render(PoseStack matrices, int mouseX, int mouseY, int x, int y, int width, int height, float delta) {
                    textFieldWidget.x = x + 4;
                    textFieldWidget.y = y + 6;
                    textFieldWidget.setWidth(width - 4 - 20);
                    textFieldWidget.setEditable(getParent().isEditable());
                    textFieldWidget.setTextColor(getPreferredTextColor());
                    textFieldWidget.render(matrices, mouseX, mouseY, delta);
                    ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
                    ItemStack stack = hasConfigError() ? BARRIER : new ItemStack(getValue());
                    itemRenderer.renderGuiItem(stack, x + width - 18, y + 2);
                }
            };
        }
        
        public static SelectionTopCellElement<Block> ofBlockObject(Block block) {
            return new DefaultSelectionTopCellElement<Block>(block, BLOCK_FUNCTION, i -> new TextComponent(Registry.BLOCK.getKey(i).toString())) {
                @Override
                public void render(PoseStack matrices, int mouseX, int mouseY, int x, int y, int width, int height, float delta) {
                    textFieldWidget.x = x + 4;
                    textFieldWidget.y = y + 6;
                    textFieldWidget.setWidth(width - 4 - 20);
                    textFieldWidget.setEditable(getParent().isEditable());
                    textFieldWidget.setTextColor(getPreferredTextColor());
                    textFieldWidget.render(matrices, mouseX, mouseY, delta);
                    ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
                    ItemStack stack = hasConfigError() ? BARRIER : new ItemStack(getValue());
                    itemRenderer.renderGuiItem(stack, x + width - 18, y + 2);
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
        
        public static SelectionCellCreator<ResourceLocation> ofItemIdentifier() {
            return ofItemIdentifier(20, 146, 7);
        }
        
        public static SelectionCellCreator<ResourceLocation> ofItemIdentifier(int maxItems) {
            return ofItemIdentifier(20, 146, maxItems);
        }
        
        public static SelectionCellCreator<ResourceLocation> ofItemIdentifier(int cellHeight, int cellWidth, int maxItems) {
            return new DefaultSelectionCellCreator<ResourceLocation>() {
                @Override
                public DropdownBoxEntry.SelectionCellElement<ResourceLocation> create(ResourceLocation selection) {
                    ItemStack s = new ItemStack(Registry.ITEM.get(selection));
                    return new DropdownBoxEntry.DefaultSelectionCellElement<ResourceLocation>(selection, toTextFunction) {
                        @Override
                        public void render(PoseStack matrices, int mouseX, int mouseY, int x, int y, int width, int height, float delta) {
                            rendering = true;
                            this.x = x;
                            this.y = y;
                            this.width = width;
                            this.height = height;
                            boolean b = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
                            if (b)
                                fill(matrices, x + 1, y + 1, x + width - 1, y + height - 1, -15132391);
                            Minecraft.getInstance().font.drawShadow(matrices, toTextFunction.apply(r).getVisualOrderText(), x + 6 + 18, y + 6, b ? 16777215 : 8947848);
                            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
                            itemRenderer.renderGuiItem(s, x + 4, y + 2);
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
        
        
        public static SelectionCellCreator<ResourceLocation> ofBlockIdentifier() {
            return ofBlockIdentifier(20, 146, 7);
        }
        
        public static SelectionCellCreator<ResourceLocation> ofBlockIdentifier(int maxItems) {
            return ofBlockIdentifier(20, 146, maxItems);
        }
        
        public static SelectionCellCreator<ResourceLocation> ofBlockIdentifier(int cellHeight, int cellWidth, int maxItems) {
            return new DefaultSelectionCellCreator<ResourceLocation>() {
                @Override
                public DropdownBoxEntry.SelectionCellElement<ResourceLocation> create(ResourceLocation selection) {
                    ItemStack s = new ItemStack(Registry.BLOCK.get(selection));
                    return new DropdownBoxEntry.DefaultSelectionCellElement<ResourceLocation>(selection, toTextFunction) {
                        @Override
                        public void render(PoseStack matrices, int mouseX, int mouseY, int x, int y, int width, int height, float delta) {
                            rendering = true;
                            this.x = x;
                            this.y = y;
                            this.width = width;
                            this.height = height;
                            boolean b = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
                            if (b)
                                fill(matrices, x + 1, y + 1, x + width - 1, y + height - 1, -15132391);
                            Minecraft.getInstance().font.drawShadow(matrices, toTextFunction.apply(r).getVisualOrderText(), x + 6 + 18, y + 6, b ? 16777215 : 8947848);
                            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
                            itemRenderer.renderGuiItem(s, x + 4, y + 2);
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
            return new DefaultSelectionCellCreator<Item>(i -> new TextComponent(Registry.ITEM.getKey(i).toString())) {
                @Override
                public DropdownBoxEntry.SelectionCellElement<Item> create(Item selection) {
                    ItemStack s = new ItemStack(selection);
                    return new DropdownBoxEntry.DefaultSelectionCellElement<Item>(selection, toTextFunction) {
                        @Override
                        public void render(PoseStack matrices, int mouseX, int mouseY, int x, int y, int width, int height, float delta) {
                            rendering = true;
                            this.x = x;
                            this.y = y;
                            this.width = width;
                            this.height = height;
                            boolean b = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
                            if (b)
                                fill(matrices, x + 1, y + 1, x + width - 1, y + height - 1, -15132391);
                            Minecraft.getInstance().font.drawShadow(matrices, toTextFunction.apply(r).getVisualOrderText(), x + 6 + 18, y + 6, b ? 16777215 : 8947848);
                            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
                            itemRenderer.renderGuiItem(s, x + 4, y + 2);
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
            return new DefaultSelectionCellCreator<Block>(i -> new TextComponent(Registry.BLOCK.getKey(i).toString())) {
                @Override
                public DropdownBoxEntry.SelectionCellElement<Block> create(Block selection) {
                    ItemStack s = new ItemStack(selection);
                    return new DropdownBoxEntry.DefaultSelectionCellElement<Block>(selection, toTextFunction) {
                        @Override
                        public void render(PoseStack matrices, int mouseX, int mouseY, int x, int y, int width, int height, float delta) {
                            rendering = true;
                            this.x = x;
                            this.y = y;
                            this.width = width;
                            this.height = height;
                            boolean b = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
                            if (b)
                                fill(matrices, x + 1, y + 1, x + width - 1, y + height - 1, -15132391);
                            Minecraft.getInstance().font.drawShadow(matrices, toTextFunction.apply(r).getVisualOrderText(), x + 6 + 18, y + 6, b ? 16777215 : 8947848);
                            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
                            itemRenderer.renderGuiItem(s, x + 4, y + 2);
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
