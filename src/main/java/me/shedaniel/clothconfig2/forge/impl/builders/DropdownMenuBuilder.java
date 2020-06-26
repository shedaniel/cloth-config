package me.shedaniel.clothconfig2.forge.impl.builders;

import me.shedaniel.clothconfig2.forge.gui.entries.DropdownBoxEntry;
import me.shedaniel.clothconfig2.forge.gui.entries.DropdownBoxEntry.DefaultSelectionCellCreator;
import me.shedaniel.clothconfig2.forge.gui.entries.DropdownBoxEntry.DefaultSelectionTopCellElement;
import me.shedaniel.clothconfig2.forge.gui.entries.DropdownBoxEntry.SelectionCellCreator;
import me.shedaniel.clothconfig2.forge.gui.entries.DropdownBoxEntry.SelectionTopCellElement;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class DropdownMenuBuilder<T> extends FieldBuilder<T, DropdownBoxEntry<T>> {
    protected SelectionTopCellElement<T> topCellElement;
    protected SelectionCellCreator<T> cellCreator;
    protected Function<T, Optional<ITextComponent[]>> tooltipSupplier = str -> Optional.empty();
    protected Consumer<T> saveConsumer = null;
    protected Iterable<T> selections = Collections.emptyList();
    protected boolean suggestionMode = true;
    
    public DropdownMenuBuilder(ITextComponent resetButtonKey, ITextComponent fieldNameKey, SelectionTopCellElement<T> topCellElement, SelectionCellCreator<T> cellCreator) {
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
    
    public DropdownMenuBuilder<T> setTooltipSupplier(Supplier<Optional<ITextComponent[]>> tooltipSupplier) {
        this.tooltipSupplier = str -> tooltipSupplier.get();
        return this;
    }
    
    public DropdownMenuBuilder<T> setTooltipSupplier(Function<T, Optional<ITextComponent[]>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
        return this;
    }
    
    public DropdownMenuBuilder<T> setTooltip(Optional<ITextComponent[]> tooltip) {
        this.tooltipSupplier = str -> tooltip;
        return this;
    }
    
    public DropdownMenuBuilder<T> setTooltip(ITextComponent... tooltip) {
        this.tooltipSupplier = str -> Optional.ofNullable(tooltip);
        return this;
    }
    
    public DropdownMenuBuilder<T> requireRestart() {
        requireRestart(true);
        return this;
    }
    
    public DropdownMenuBuilder<T> setErrorSupplier(Function<T, Optional<ITextComponent>> errorSupplier) {
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
                if (Registry.ITEM.getValue(identifier).isPresent())
                    return identifier;
            } catch (Exception ignored) {
            }
            return null;
        };
        public static final Function<String, ResourceLocation> BLOCK_IDENTIFIER_FUNCTION = str -> {
            try {
                ResourceLocation identifier = new ResourceLocation(str);
                if (Registry.BLOCK.getValue(identifier).isPresent())
                    return identifier;
            } catch (Exception ignored) {
            }
            return null;
        };
        public static final Function<String, Item> ITEM_FUNCTION = str -> {
            try {
                return Registry.ITEM.getValue(new ResourceLocation(str)).orElse(null);
            } catch (Exception ignored) {
            }
            return null;
        };
        public static final Function<String, Block> BLOCK_FUNCTION = str -> {
            try {
                return Registry.BLOCK.getValue(new ResourceLocation(str)).orElse(null);
            } catch (Exception ignored) {
            }
            return null;
        };
        private static final ItemStack BARRIER = new ItemStack(Items.BARRIER);
        
        public static <T> SelectionTopCellElement<T> of(T value, Function<String, T> toObjectFunction) {
            return of(value, toObjectFunction, t -> new StringTextComponent(t.toString()));
        }
        
        public static <T> SelectionTopCellElement<T> of(T value, Function<String, T> toObjectFunction, Function<T, ITextComponent> toTextFunction) {
            return new DefaultSelectionTopCellElement<>(value, toObjectFunction, toTextFunction);
        }
        
        public static SelectionTopCellElement<ResourceLocation> ofItemIdentifier(Item item) {
            return new DefaultSelectionTopCellElement<ResourceLocation>(Registry.ITEM.getKey(item), ITEM_IDENTIFIER_FUNCTION, identifier -> new StringTextComponent(identifier.toString())) {
                @Override
                public void render(MatrixStack matrices, int mouseX, int mouseY, int x, int y, int width, int height, float delta) {
                    textFieldWidget.field_230690_l_ = x + 4;
                    textFieldWidget.field_230691_m_ = y + 6;
                    textFieldWidget.func_230991_b_(width - 4 - 20);
                    textFieldWidget.setEnabled(getParent().isEditable());
                    textFieldWidget.setTextColor(getPreferredTextColor());
                    textFieldWidget.func_230430_a_(matrices, mouseX, mouseY, delta);
                    ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
                    ItemStack stack = hasConfigError() ? BARRIER : new ItemStack(Registry.ITEM.getOrDefault(getValue()));
                    itemRenderer.renderItemIntoGUI(stack, x + width - 18, y + 2);
                }
            };
        }
        
        public static SelectionTopCellElement<ResourceLocation> ofBlockIdentifier(Block block) {
            return new DefaultSelectionTopCellElement<ResourceLocation>(Registry.BLOCK.getKey(block), BLOCK_IDENTIFIER_FUNCTION, identifier -> new StringTextComponent(identifier.toString())) {
                @Override
                public void render(MatrixStack matrices, int mouseX, int mouseY, int x, int y, int width, int height, float delta) {
                    textFieldWidget.field_230690_l_ = x + 4;
                    textFieldWidget.field_230691_m_ = y + 6;
                    textFieldWidget.func_230991_b_(width - 4 - 20);
                    textFieldWidget.setEnabled(getParent().isEditable());
                    textFieldWidget.setTextColor(getPreferredTextColor());
                    textFieldWidget.func_230430_a_(matrices, mouseX, mouseY, delta);
                    ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
                    ItemStack stack = hasConfigError() ? BARRIER : new ItemStack(Registry.BLOCK.getOrDefault(getValue()));
                    itemRenderer.renderItemIntoGUI(stack, x + width - 18, y + 2);
                }
            };
        }
        
        public static SelectionTopCellElement<Item> ofItemObject(Item item) {
            return new DefaultSelectionTopCellElement<Item>(item, ITEM_FUNCTION, i -> new StringTextComponent(Registry.ITEM.getKey(i).toString())) {
                @Override
                public void render(MatrixStack matrices, int mouseX, int mouseY, int x, int y, int width, int height, float delta) {
                    textFieldWidget.field_230690_l_ = x + 4;
                    textFieldWidget.field_230691_m_ = y + 6;
                    textFieldWidget.func_230991_b_(width - 4 - 20);
                    textFieldWidget.setEnabled(getParent().isEditable());
                    textFieldWidget.setTextColor(getPreferredTextColor());
                    textFieldWidget.func_230430_a_(matrices, mouseX, mouseY, delta);
                    ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
                    ItemStack stack = hasConfigError() ? BARRIER : new ItemStack(getValue());
                    itemRenderer.renderItemIntoGUI(stack, x + width - 18, y + 2);
                }
            };
        }
        
        public static SelectionTopCellElement<Block> ofBlockObject(Block block) {
            return new DefaultSelectionTopCellElement<Block>(block, BLOCK_FUNCTION, i -> new StringTextComponent(Registry.BLOCK.getKey(i).toString())) {
                @Override
                public void render(MatrixStack matrices, int mouseX, int mouseY, int x, int y, int width, int height, float delta) {
                    textFieldWidget.field_230690_l_ = x + 4;
                    textFieldWidget.field_230691_m_ = y + 6;
                    textFieldWidget.func_230991_b_(width - 4 - 20);
                    textFieldWidget.setEnabled(getParent().isEditable());
                    textFieldWidget.setTextColor(getPreferredTextColor());
                    textFieldWidget.func_230430_a_(matrices, mouseX, mouseY, delta);
                    ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
                    ItemStack stack = hasConfigError() ? BARRIER : new ItemStack(getValue());
                    itemRenderer.renderItemIntoGUI(stack, x + width - 18, y + 2);
                }
            };
        }
    }
    
    public static class CellCreatorBuilder {
        public static <T> SelectionCellCreator<T> of() {
            return new DefaultSelectionCellCreator<>();
        }
        
        public static <T> SelectionCellCreator<T> of(Function<T, ITextComponent> toTextFunction) {
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
        
        public static <T> SelectionCellCreator<T> ofWidth(int cellWidth, Function<T, ITextComponent> toTextFunction) {
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
        
        public static <T> SelectionCellCreator<T> ofCellCount(int maxItems, Function<T, ITextComponent> toTextFunction) {
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
        
        public static <T> SelectionCellCreator<T> of(int cellWidth, int maxItems, Function<T, ITextComponent> toTextFunction) {
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
        
        public static <T> SelectionCellCreator<T> of(int cellHeight, int cellWidth, int maxItems, Function<T, ITextComponent> toTextFunction) {
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
                    ItemStack s = new ItemStack(Registry.ITEM.getOrDefault(selection));
                    return new DropdownBoxEntry.DefaultSelectionCellElement<ResourceLocation>(selection, toTextFunction) {
                        @Override
                        public void render(MatrixStack matrices, int mouseX, int mouseY, int x, int y, int width, int height, float delta) {
                            rendering = true;
                            this.x = x;
                            this.y = y;
                            this.width = width;
                            this.height = height;
                            boolean b = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
                            if (b)
                                func_238467_a_(matrices, x + 1, y + 1, x + width - 1, y + height - 1, -15132391);
                            Minecraft.getInstance().fontRenderer.func_238407_a_(matrices, toTextFunction.apply(r), x + 6 + 18, y + 6, b ? 16777215 : 8947848);
                            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
                            itemRenderer.renderItemIntoGUI(s, x + 4, y + 2);
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
                    ItemStack s = new ItemStack(Registry.BLOCK.getOrDefault(selection));
                    return new DropdownBoxEntry.DefaultSelectionCellElement<ResourceLocation>(selection, toTextFunction) {
                        @Override
                        public void render(MatrixStack matrices, int mouseX, int mouseY, int x, int y, int width, int height, float delta) {
                            rendering = true;
                            this.x = x;
                            this.y = y;
                            this.width = width;
                            this.height = height;
                            boolean b = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
                            if (b)
                                func_238467_a_(matrices, x + 1, y + 1, x + width - 1, y + height - 1, -15132391);
                            Minecraft.getInstance().fontRenderer.func_238407_a_(matrices, toTextFunction.apply(r), x + 6 + 18, y + 6, b ? 16777215 : 8947848);
                            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
                            itemRenderer.renderItemIntoGUI(s, x + 4, y + 2);
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
            return new DefaultSelectionCellCreator<Item>(i -> new StringTextComponent(Registry.ITEM.getKey(i).toString())) {
                @Override
                public DropdownBoxEntry.SelectionCellElement<Item> create(Item selection) {
                    ItemStack s = new ItemStack(selection);
                    return new DropdownBoxEntry.DefaultSelectionCellElement<Item>(selection, toTextFunction) {
                        @Override
                        public void render(MatrixStack matrices, int mouseX, int mouseY, int x, int y, int width, int height, float delta) {
                            rendering = true;
                            this.x = x;
                            this.y = y;
                            this.width = width;
                            this.height = height;
                            boolean b = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
                            if (b)
                                func_238467_a_(matrices, x + 1, y + 1, x + width - 1, y + height - 1, -15132391);
                            Minecraft.getInstance().fontRenderer.func_238407_a_(matrices, toTextFunction.apply(r), x + 6 + 18, y + 6, b ? 16777215 : 8947848);
                            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
                            itemRenderer.renderItemIntoGUI(s, x + 4, y + 2);
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
            return new DefaultSelectionCellCreator<Block>(i -> new StringTextComponent(Registry.BLOCK.getKey(i).toString())) {
                @Override
                public DropdownBoxEntry.SelectionCellElement<Block> create(Block selection) {
                    ItemStack s = new ItemStack(selection);
                    return new DropdownBoxEntry.DefaultSelectionCellElement<Block>(selection, toTextFunction) {
                        @Override
                        public void render(MatrixStack matrices, int mouseX, int mouseY, int x, int y, int width, int height, float delta) {
                            rendering = true;
                            this.x = x;
                            this.y = y;
                            this.width = width;
                            this.height = height;
                            boolean b = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
                            if (b)
                                func_238467_a_(matrices, x + 1, y + 1, x + width - 1, y + height - 1, -15132391);
                            Minecraft.getInstance().fontRenderer.func_238407_a_(matrices, toTextFunction.apply(r), x + 6 + 18, y + 6, b ? 16777215 : 8947848);
                            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
                            itemRenderer.renderItemIntoGUI(s, x + 4, y + 2);
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
