package me.shedaniel.clothconfig2.impl.builders;

import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry.DefaultSelectionCellCreator;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry.DefaultSelectionTopCellElement;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry.SelectionCellCreator;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry.SelectionTopCellElement;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class DropdownMenuBuilder<T> extends FieldBuilder<T, DropdownBoxEntry<T>> {
    protected SelectionTopCellElement<T> topCellElement;
    protected SelectionCellCreator<T> cellCreator;
    protected Function<T, Optional<String[]>> tooltipSupplier = str -> Optional.empty();
    protected Consumer<T> saveConsumer = null;
    protected Iterable<T> selections = Collections.emptyList();
    
    public DropdownMenuBuilder(String resetButtonKey, String fieldNameKey, SelectionTopCellElement<T> topCellElement, SelectionCellCreator<T> cellCreator) {
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
    
    public DropdownMenuBuilder<T> setTooltipSupplier(Supplier<Optional<String[]>> tooltipSupplier) {
        this.tooltipSupplier = str -> tooltipSupplier.get();
        return this;
    }
    
    public DropdownMenuBuilder<T> setTooltipSupplier(Function<T, Optional<String[]>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
        return this;
    }
    
    public DropdownMenuBuilder<T> setTooltip(Optional<String[]> tooltip) {
        this.tooltipSupplier = str -> tooltip;
        return this;
    }
    
    public DropdownMenuBuilder<T> setTooltip(String... tooltip) {
        this.tooltipSupplier = str -> Optional.ofNullable(tooltip);
        return this;
    }
    
    public DropdownMenuBuilder<T> requireRestart() {
        requireRestart(true);
        return this;
    }
    
    public DropdownMenuBuilder<T> setErrorSupplier(Function<T, Optional<String>> errorSupplier) {
        this.errorSupplier = errorSupplier;
        return this;
    }
    
    @Nonnull
    @Override
    public DropdownBoxEntry<T> build() {
        DropdownBoxEntry<T> entry = new DropdownBoxEntry<T>(getFieldNameKey(), getResetButtonKey(), null, isRequireRestart(), defaultValue, saveConsumer, selections, topCellElement, cellCreator);
        entry.setTooltipSupplier(() -> tooltipSupplier.apply(entry.getValue()));
        if (errorSupplier != null)
            entry.setErrorSupplier(() -> errorSupplier.apply(entry.getValue()));
        return entry;
    }
    
    public static class TopCellElementBuilder {
        public static final Function<String, Identifier> IDENTIFIER_FUNCTION = str -> {
            try {
                return new Identifier(str);
            } catch (NumberFormatException e) {
                return null;
            }
        };
        public static final Function<String, Identifier> ITEM_IDENTIFIER_FUNCTION = str -> {
            try {
                Identifier identifier = new Identifier(str);
                if (Registry.ITEM.getOrEmpty(identifier).isPresent())
                    return identifier;
            } catch (Exception ignored) {
            }
            return null;
        };
        public static final Function<String, Identifier> BLOCK_IDENTIFIER_FUNCTION = str -> {
            try {
                Identifier identifier = new Identifier(str);
                if (Registry.BLOCK.getOrEmpty(identifier).isPresent())
                    return identifier;
            } catch (Exception ignored) {
            }
            return null;
        };
        public static final Function<String, Item> ITEM_FUNCTION = str -> {
            try {
                return Registry.ITEM.getOrEmpty(new Identifier(str)).get();
            } catch (Exception ignored) {
            }
            return null;
        };
        public static final Function<String, Block> BLOCK_FUNCTION = str -> {
            try {
                return Registry.BLOCK.getOrEmpty(new Identifier(str)).get();
            } catch (Exception ignored) {
            }
            return null;
        };
        private static final ItemStack BARRIER = new ItemStack(Items.BARRIER);
    
        public static <T> SelectionTopCellElement<T> of(T value, Function<String, T> toObjectFunction) {
            return of(value, toObjectFunction, Object::toString);
        }
        
        public static <T> SelectionTopCellElement<T> of(T value, Function<String, T> toObjectFunction, Function<T, String> toStringFunction) {
            return new DefaultSelectionTopCellElement<>(value, toObjectFunction, toStringFunction);
        }
        
        public static SelectionTopCellElement<Identifier> ofItemIdentifier(Item item) {
            return new DefaultSelectionTopCellElement<Identifier>(Registry.ITEM.getId(item), ITEM_IDENTIFIER_FUNCTION, Identifier::toString) {
                @Override
                public void render(int mouseX, int mouseY, int x, int y, int width, int height, float delta) {
                    textFieldWidget.x = x + 4;
                    textFieldWidget.y = y + 6;
                    textFieldWidget.setWidth(width - 4 - 20);
                    textFieldWidget.setEditable(getParent().isEditable());
                    textFieldWidget.setEditableColor(getPreferredTextColor());
                    textFieldWidget.render(mouseX, mouseY, delta);
                    ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
                    ItemStack stack = hasConfigError() ? BARRIER : new ItemStack(Registry.ITEM.get(getValue()));
                    itemRenderer.renderGuiItemIcon(stack, x + width - 18, y + 2);
                }
            };
        }
        
        public static SelectionTopCellElement<Identifier> ofBlockIdentifier(Block block) {
            return new DefaultSelectionTopCellElement<Identifier>(Registry.BLOCK.getId(block), BLOCK_IDENTIFIER_FUNCTION, Identifier::toString) {
                @Override
                public void render(int mouseX, int mouseY, int x, int y, int width, int height, float delta) {
                    textFieldWidget.x = x + 4;
                    textFieldWidget.y = y + 6;
                    textFieldWidget.setWidth(width - 4 - 20);
                    textFieldWidget.setEditable(getParent().isEditable());
                    textFieldWidget.setEditableColor(getPreferredTextColor());
                    textFieldWidget.render(mouseX, mouseY, delta);
                    ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
                    ItemStack stack = hasConfigError() ? BARRIER : new ItemStack(Registry.BLOCK.get(getValue()));
                    itemRenderer.renderGuiItemIcon(stack, x + width - 18, y + 2);
                }
            };
        }
        
        public static SelectionTopCellElement<Item> ofItemObject(Item item) {
            return new DefaultSelectionTopCellElement<Item>(item, ITEM_FUNCTION, i -> Registry.ITEM.getId(i).toString()) {
                @Override
                public void render(int mouseX, int mouseY, int x, int y, int width, int height, float delta) {
                    textFieldWidget.x = x + 4;
                    textFieldWidget.y = y + 6;
                    textFieldWidget.setWidth(width - 4 - 20);
                    textFieldWidget.setEditable(getParent().isEditable());
                    textFieldWidget.setEditableColor(getPreferredTextColor());
                    textFieldWidget.render(mouseX, mouseY, delta);
                    ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
                    ItemStack stack = hasConfigError() ? BARRIER : new ItemStack(getValue());
                    itemRenderer.renderGuiItemIcon(stack, x + width - 18, y + 2);
                }
            };
        }
        
        public static SelectionTopCellElement<Block> ofBlockObject(Block block) {
            return new DefaultSelectionTopCellElement<Block>(block, BLOCK_FUNCTION, i -> Registry.BLOCK.getId(i).toString()) {
                @Override
                public void render(int mouseX, int mouseY, int x, int y, int width, int height, float delta) {
                    textFieldWidget.x = x + 4;
                    textFieldWidget.y = y + 6;
                    textFieldWidget.setWidth(width - 4 - 20);
                    textFieldWidget.setEditable(getParent().isEditable());
                    textFieldWidget.setEditableColor(getPreferredTextColor());
                    textFieldWidget.render(mouseX, mouseY, delta);
                    ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
                    ItemStack stack = hasConfigError() ? BARRIER : new ItemStack(getValue());
                    itemRenderer.renderGuiItemIcon(stack, x + width - 18, y + 2);
                }
            };
        }
    }
    
    public static class CellCreatorBuilder {
        public static <T> SelectionCellCreator<T> of() {
            return new DefaultSelectionCellCreator<>();
        }
        
        public static <T> SelectionCellCreator<T> of(Function<T, String> toStringFunction) {
            return new DefaultSelectionCellCreator<>(toStringFunction);
        }
        
        public static <T> SelectionCellCreator<T> ofWidth(int cellWidth) {
            return new DefaultSelectionCellCreator<T>() {
                @Override
                public int getCellWidth() {
                    return cellWidth;
                }
            };
        }
        
        public static <T> SelectionCellCreator<T> ofWidth(int cellWidth, Function<T, String> toStringFunction) {
            return new DefaultSelectionCellCreator<T>(toStringFunction) {
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
        
        public static <T> SelectionCellCreator<T> ofCellCount(int maxItems, Function<T, String> toStringFunction) {
            return new DefaultSelectionCellCreator<T>(toStringFunction) {
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
        
        public static <T> SelectionCellCreator<T> of(int cellWidth, int maxItems, Function<T, String> toStringFunction) {
            return new DefaultSelectionCellCreator<T>(toStringFunction) {
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
        
        public static <T> SelectionCellCreator<T> of(int cellHeight, int cellWidth, int maxItems, Function<T, String> toStringFunction) {
            return new DefaultSelectionCellCreator<T>(toStringFunction) {
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
                    ItemStack s = new ItemStack(Registry.ITEM.get(selection));
                    return new DropdownBoxEntry.DefaultSelectionCellElement<Identifier>(selection, toStringFunction) {
                        @Override
                        public void render(int mouseX, int mouseY, int x, int y, int width, int height, float delta) {
                            rendering = true;
                            this.x = x;
                            this.y = y;
                            this.width = width;
                            this.height = height;
                            boolean b = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
                            if (b)
                                fill(x + 1, y + 1, x + width - 1, y + height - 1, -15132391);
                            MinecraftClient.getInstance().textRenderer.drawWithShadow(toStringFunction.apply(r), x + 6 + 18, y + 6, b ? 16777215 : 8947848);
                            ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
                            itemRenderer.renderGuiItemIcon(s, x + 4, y + 2);
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
                    ItemStack s = new ItemStack(Registry.BLOCK.get(selection));
                    return new DropdownBoxEntry.DefaultSelectionCellElement<Identifier>(selection, toStringFunction) {
                        @Override
                        public void render(int mouseX, int mouseY, int x, int y, int width, int height, float delta) {
                            rendering = true;
                            this.x = x;
                            this.y = y;
                            this.width = width;
                            this.height = height;
                            boolean b = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
                            if (b)
                                fill(x + 1, y + 1, x + width - 1, y + height - 1, -15132391);
                            MinecraftClient.getInstance().textRenderer.drawWithShadow(toStringFunction.apply(r), x + 6 + 18, y + 6, b ? 16777215 : 8947848);
                            ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
                            itemRenderer.renderGuiItemIcon(s, x + 4, y + 2);
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
            return new DefaultSelectionCellCreator<Item>(i -> Registry.ITEM.getId(i).toString()) {
                @Override
                public DropdownBoxEntry.SelectionCellElement<Item> create(Item selection) {
                    ItemStack s = new ItemStack(selection);
                    return new DropdownBoxEntry.DefaultSelectionCellElement<Item>(selection, toStringFunction) {
                        @Override
                        public void render(int mouseX, int mouseY, int x, int y, int width, int height, float delta) {
                            rendering = true;
                            this.x = x;
                            this.y = y;
                            this.width = width;
                            this.height = height;
                            boolean b = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
                            if (b)
                                fill(x + 1, y + 1, x + width - 1, y + height - 1, -15132391);
                            MinecraftClient.getInstance().textRenderer.drawWithShadow(toStringFunction.apply(r), x + 6 + 18, y + 6, b ? 16777215 : 8947848);
                            ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
                            itemRenderer.renderGuiItemIcon(s, x + 4, y + 2);
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
            return new DefaultSelectionCellCreator<Block>(i -> Registry.BLOCK.getId(i).toString()) {
                @Override
                public DropdownBoxEntry.SelectionCellElement<Block> create(Block selection) {
                    ItemStack s = new ItemStack(selection);
                    return new DropdownBoxEntry.DefaultSelectionCellElement<Block>(selection, toStringFunction) {
                        @Override
                        public void render(int mouseX, int mouseY, int x, int y, int width, int height, float delta) {
                            rendering = true;
                            this.x = x;
                            this.y = y;
                            this.width = width;
                            this.height = height;
                            boolean b = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
                            if (b)
                                fill(x + 1, y + 1, x + width - 1, y + height - 1, -15132391);
                            MinecraftClient.getInstance().textRenderer.drawWithShadow(toStringFunction.apply(r), x + 6 + 18, y + 6, b ? 16777215 : 8947848);
                            ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
                            itemRenderer.renderGuiItemIcon(s, x + 4, y + 2);
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
