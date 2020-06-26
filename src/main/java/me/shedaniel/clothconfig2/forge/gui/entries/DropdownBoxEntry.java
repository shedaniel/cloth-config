package me.shedaniel.clothconfig2.forge.gui.entries;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.clothconfig2.forge.ClothConfigInitializer;
import me.shedaniel.clothconfig2.forge.api.ScissorsHandler;
import me.shedaniel.clothconfig2.forge.api.ScrollingContainer;
import me.shedaniel.math.Rectangle;
import me.shedaniel.clothconfig2.forge.api.PointHelper;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static me.shedaniel.clothconfig2.forge.api.ScrollingContainer.handleScrollingPosition;

@SuppressWarnings("deprecation")
@OnlyIn(Dist.CLIENT)
public class DropdownBoxEntry<T> extends TooltipListEntry<T> {
    
    protected Button resetButton;
    protected SelectionElement<T> selectionElement;
    @NotNull private Supplier<T> defaultValue;
    @Nullable private Consumer<T> saveConsumer;
    private boolean suggestionMode = true;
    
    @ApiStatus.Internal
    @Deprecated
    public DropdownBoxEntry(ITextComponent fieldName, @NotNull ITextComponent resetButtonKey, @Nullable Supplier<Optional<ITextComponent[]>> tooltipSupplier, boolean requiresRestart, @Nullable Supplier<T> defaultValue, @Nullable Consumer<T> saveConsumer, @Nullable Iterable<T> selections, @NotNull SelectionTopCellElement<T> topRenderer, @NotNull SelectionCellCreator<T> cellCreator) {
        super(fieldName, tooltipSupplier, requiresRestart);
        this.defaultValue = defaultValue;
        this.saveConsumer = saveConsumer;
        this.resetButton = new Button(0, 0, Minecraft.getInstance().fontRenderer.func_238414_a_(resetButtonKey) + 6, 20, resetButtonKey, widget -> {
            selectionElement.topRenderer.setValue(defaultValue.get());
        });
        this.selectionElement = new SelectionElement<>(this, new Rectangle(0, 0, 150, 20), new DefaultDropdownMenuElement<>(selections == null ? ImmutableList.of() : ImmutableList.copyOf(selections)), topRenderer, cellCreator);
    }
    
    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
        super.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isSelected, delta);
        MainWindow window = Minecraft.getInstance().getMainWindow();
        this.resetButton.field_230693_o_ = isEditable() && getDefaultValue().isPresent() && (!defaultValue.get().equals(getValue()) || getConfigError().isPresent());
        this.resetButton.field_230691_m_ = y;
        this.selectionElement.active = isEditable();
        this.selectionElement.bounds.y = y;
        ITextComponent displayedFieldName = getDisplayedFieldName();
        if (Minecraft.getInstance().fontRenderer.getBidiFlag()) {
            Minecraft.getInstance().fontRenderer.func_238407_a_(matrices, displayedFieldName, window.getScaledWidth() - x - Minecraft.getInstance().fontRenderer.func_238414_a_(displayedFieldName), y + 5, getPreferredTextColor());
            this.resetButton.field_230690_l_ = x;
            this.selectionElement.bounds.x = x + resetButton.func_230998_h_() + 1;
        } else {
            Minecraft.getInstance().fontRenderer.func_238407_a_(matrices, displayedFieldName, x, y + 5, getPreferredTextColor());
            this.resetButton.field_230690_l_ = x + entryWidth - resetButton.func_230998_h_();
            this.selectionElement.bounds.x = x + entryWidth - 150 + 1;
        }
        this.selectionElement.bounds.width = 150 - resetButton.func_230998_h_() - 4;
        resetButton.func_230430_a_(matrices, mouseX, mouseY, delta);
        selectionElement.func_230430_a_(matrices, mouseX, mouseY, delta);
    }
    
    @Override
    public boolean isEdited() {
        return this.selectionElement.topRenderer.isEdited();
    }
    
    public boolean isSuggestionMode() {
        return suggestionMode;
    }
    
    public void setSuggestionMode(boolean suggestionMode) {
        this.suggestionMode = suggestionMode;
    }
    
    @Override
    public void updateSelected(boolean isSelected) {
        selectionElement.topRenderer.isSelected = isSelected;
        selectionElement.menu.isSelected = isSelected;
    }
    
    @NotNull
    public ImmutableList<T> getSelections() {
        return selectionElement.menu.getSelections();
    }
    
    @Override
    public T getValue() {
        return selectionElement.getValue();
    }
    
    @Deprecated
    public SelectionElement<T> getSelectionElement() {
        return selectionElement;
    }
    
    @Override
    public Optional<T> getDefaultValue() {
        return defaultValue == null ? Optional.empty() : Optional.ofNullable(defaultValue.get());
    }
    
    @Override
    public void save() {
        if (saveConsumer != null)
            saveConsumer.accept(getValue());
    }
    
    @Override
    public List<? extends IGuiEventListener> func_231039_at__() {
        return Lists.newArrayList(selectionElement, resetButton);
    }
    
    @Override
    public Optional<ITextComponent> getError() {
        return selectionElement.topRenderer.getError();
    }
    
    @Override
    public void lateRender(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        selectionElement.lateRender(matrices, mouseX, mouseY, delta);
    }
    
    @Override
    public int getMorePossibleHeight() {
        return selectionElement.getMorePossibleHeight();
    }
    
    @Override
    public boolean func_231043_a_(double double_1, double double_2, double double_3) {
        return selectionElement.func_231043_a_(double_1, double_2, double_3);
    }
    
    public static class SelectionElement<R> extends FocusableGui implements IRenderable {
        protected Rectangle bounds;
        protected boolean active;
        protected SelectionTopCellElement<R> topRenderer;
        protected DropdownBoxEntry<R> entry;
        protected DropdownMenuElement<R> menu;
        protected boolean dontReFocus = false;
        
        public SelectionElement(DropdownBoxEntry<R> entry, Rectangle bounds, DropdownMenuElement<R> menu, SelectionTopCellElement<R> topRenderer, SelectionCellCreator<R> cellCreator) {
            this.bounds = bounds;
            this.entry = entry;
            this.menu = Objects.requireNonNull(menu);
            this.menu.entry = entry;
            this.menu.cellCreator = Objects.requireNonNull(cellCreator);
            this.menu.initCells();
            this.topRenderer = Objects.requireNonNull(topRenderer);
            this.topRenderer.entry = entry;
        }
        
        @Override
        public void func_230430_a_(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            func_238467_a_(matrices, bounds.x, bounds.y, bounds.x + bounds.width, bounds.y + bounds.height, topRenderer.isSelected ? -1 : -6250336);
            func_238467_a_(matrices, bounds.x + 1, bounds.y + 1, bounds.x + bounds.width - 1, bounds.y + bounds.height - 1, -16777216);
            topRenderer.render(matrices, mouseX, mouseY, bounds.x, bounds.y, bounds.width, bounds.height, delta);
            if (menu.isExpanded())
                menu.render(matrices, mouseX, mouseY, bounds, delta);
        }
        
        @Deprecated
        public SelectionTopCellElement<R> getTopRenderer() {
            return topRenderer;
        }
        
        @Override
        public boolean func_231043_a_(double double_1, double double_2, double double_3) {
            if (menu.isExpanded())
                return menu.func_231043_a_(double_1, double_2, double_3);
            return false;
        }
        
        public void lateRender(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            if (menu.isExpanded())
                menu.lateRender(matrices, mouseX, mouseY, delta);
        }
        
        public int getMorePossibleHeight() {
            if (menu.isExpanded())
                return menu.getHeight();
            return -1;
        }
        
        public R getValue() {
            return topRenderer.getValue();
        }
        
        @Override
        public List<? extends IGuiEventListener> func_231039_at__() {
            return Lists.newArrayList(topRenderer, menu);
        }
        
        @Override
        public boolean func_231044_a_(double double_1, double double_2, int int_1) {
            dontReFocus = false;
            boolean b = super.func_231044_a_(double_1, double_2, int_1);
            if (dontReFocus) {
                func_231035_a_(null);
                dontReFocus = false;
            }
            return b;
        }
    }
    
    public static abstract class DropdownMenuElement<R> extends FocusableGui {
        @Deprecated @NotNull private SelectionCellCreator<R> cellCreator;
        @Deprecated @NotNull private DropdownBoxEntry<R> entry;
        private boolean isSelected;
        
        @NotNull
        public SelectionCellCreator<R> getCellCreator() {
            return cellCreator;
        }
        
        @NotNull
        public final DropdownBoxEntry<R> getEntry() {
            return entry;
        }
        
        @NotNull
        public abstract ImmutableList<R> getSelections();
        
        public abstract void initCells();
        
        public abstract void render(MatrixStack matrices, int mouseX, int mouseY, Rectangle rectangle, float delta);
        
        public abstract void lateRender(MatrixStack matrices, int mouseX, int mouseY, float delta);
        
        public abstract int getHeight();
        
        public final boolean isExpanded() {
            return isSelected && this.getEntry().func_241217_q_() == this.getEntry().selectionElement;
        }
        
        public final boolean isSuggestionMode() {
            return entry.isSuggestionMode();
        }
        
        @Override
        public abstract List<SelectionCellElement<R>> func_231039_at__();
    }
    
    public static class DefaultDropdownMenuElement<R> extends DropdownMenuElement<R> {
        @NotNull protected ImmutableList<R> selections;
        @NotNull protected List<SelectionCellElement<R>> cells;
        @NotNull protected List<SelectionCellElement<R>> currentElements;
        protected ITextComponent lastSearchKeyword = NarratorChatListener.EMPTY;
        protected Rectangle lastRectangle;
        protected boolean scrolling;
        protected double scroll, target;
        protected long start;
        protected long duration;
        
        public DefaultDropdownMenuElement(@NotNull ImmutableList<R> selections) {
            this.selections = selections;
            this.cells = Lists.newArrayList();
            this.currentElements = Lists.newArrayList();
        }
        
        public double getMaxScroll() {
            return getCellCreator().getCellHeight() * currentElements.size();
        }
        
        protected double getMaxScrollPosition() {
            return Math.max(0, this.getMaxScroll() - (getHeight()));
        }
        
        @Override
        @NotNull
        public ImmutableList<R> getSelections() {
            return selections;
        }
        
        @Override
        public void initCells() {
            for (R selection : getSelections()) {
                cells.add(getCellCreator().create(selection));
            }
            for (SelectionCellElement<R> cell : cells) {
                cell.entry = getEntry();
            }
            search();
        }
        
        public void search() {
            if (isSuggestionMode()) {
                currentElements.clear();
                String keyword = this.lastSearchKeyword.getString().toLowerCase();
                for (SelectionCellElement<R> cell : cells) {
                    ITextComponent key = cell.getSearchKey();
                    if (key == null || key.getString().toLowerCase().contains(keyword))
                        currentElements.add(cell);
                }
                if (!keyword.isEmpty()) {
                    Comparator<SelectionCellElement<?>> c = Comparator.comparingDouble(i -> i.getSearchKey() == null ? Double.MAX_VALUE : similarity(i.getSearchKey().getString(), keyword));
                    currentElements.sort(c.reversed());
                }
                scrollTo(0, false);
            } else {
                currentElements.clear();
                currentElements.addAll(cells);
            }
        }
        
        protected int editDistance(String s1, String s2) {
            s1 = s1.toLowerCase();
            s2 = s2.toLowerCase();
            
            int[] costs = new int[s2.length() + 1];
            for (int i = 0; i <= s1.length(); i++) {
                int lastValue = i;
                for (int j = 0; j <= s2.length(); j++) {
                    if (i == 0)
                        costs[j] = j;
                    else {
                        if (j > 0) {
                            int newValue = costs[j - 1];
                            if (s1.charAt(i - 1) != s2.charAt(j - 1))
                                newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                            costs[j - 1] = lastValue;
                            lastValue = newValue;
                        }
                    }
                }
                if (i > 0)
                    costs[s2.length()] = lastValue;
            }
            return costs[s2.length()];
        }
        
        protected double similarity(String s1, String s2) {
            String longer = s1, shorter = s2;
            if (s1.length() < s2.length()) { // longer should always have greater length
                longer = s2;
                shorter = s1;
            }
            int longerLength = longer.length();
            if (longerLength == 0) {
                return 1.0; /* both strings are zero length */
            }
            return (longerLength - editDistance(longer, shorter)) / (double) longerLength;
        }
        
        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, Rectangle rectangle, float delta) {
            if (!getEntry().selectionElement.topRenderer.getSearchTerm().equals(lastSearchKeyword)) {
                lastSearchKeyword = getEntry().selectionElement.topRenderer.getSearchTerm();
                search();
            }
            updatePosition(delta);
            lastRectangle = rectangle.clone();
            lastRectangle.translate(0, -1);
        }
        
        private void updatePosition(float delta) {
            double[] target = {this.target};
            scroll = handleScrollingPosition(target, scroll, getMaxScrollPosition(), delta, start, duration);
            this.target = target[0];
        }
        
        @Override
        public void lateRender(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            int last10Height = getHeight();
            int cWidth = getCellCreator().getCellWidth();
            func_238467_a_(matrices, lastRectangle.x, lastRectangle.y + lastRectangle.height, lastRectangle.x + cWidth, lastRectangle.y + lastRectangle.height + last10Height + 1, isExpanded() ? -1 : -6250336);
            func_238467_a_(matrices, lastRectangle.x + 1, lastRectangle.y + lastRectangle.height + 1, lastRectangle.x + cWidth - 1, lastRectangle.y + lastRectangle.height + last10Height, -16777216);
            RenderSystem.pushMatrix();
            RenderSystem.translatef(0, 0, 300f);
            
            ScissorsHandler.INSTANCE.scissor(new Rectangle(lastRectangle.x, lastRectangle.y + lastRectangle.height + 1, cWidth - 6, last10Height - 1));
            double yy = lastRectangle.y + lastRectangle.height - scroll;
            for (SelectionCellElement<R> cell : currentElements) {
                if (yy + getCellCreator().getCellHeight() >= lastRectangle.y + lastRectangle.height && yy <= lastRectangle.y + lastRectangle.height + last10Height + 1)
                    cell.render(matrices, mouseX, mouseY, lastRectangle.x, (int) yy, getMaxScrollPosition() > 6 ? getCellCreator().getCellWidth() - 6 : getCellCreator().getCellWidth(), getCellCreator().getCellHeight(), delta);
                else
                    cell.dontRender(matrices, delta);
                yy += getCellCreator().getCellHeight();
            }
            ScissorsHandler.INSTANCE.removeLastScissor();
            
            if (currentElements.isEmpty()) {
                FontRenderer textRenderer = Minecraft.getInstance().fontRenderer;
                ITextComponent text = new TranslationTextComponent("text.cloth-config.dropdown.value.unknown");
                textRenderer.func_238407_a_(matrices, text, lastRectangle.x + getCellCreator().getCellWidth() / 2f - textRenderer.func_238414_a_(text) / 2f, lastRectangle.y + lastRectangle.height + 3, -1);
            }
            
            if (getMaxScrollPosition() > 6) {
                RenderSystem.disableTexture();
                int scrollbarPositionMinX = lastRectangle.x + getCellCreator().getCellWidth() - 6;
                int scrollbarPositionMaxX = scrollbarPositionMinX + 6;
                int height = (int) (((last10Height) * (last10Height)) / this.getMaxScrollPosition());
                height = MathHelper.clamp(height, 32, last10Height - 8);
                height -= Math.min((scroll < 0 ? (int) -scroll : scroll > getMaxScrollPosition() ? (int) scroll - getMaxScrollPosition() : 0), height * .95);
                height = Math.max(10, height);
                int minY = (int) Math.min(Math.max((int) scroll * (last10Height - height) / getMaxScrollPosition() + (lastRectangle.y + lastRectangle.height + 1), (lastRectangle.y + lastRectangle.height + 1)), (lastRectangle.y + lastRectangle.height + 1 + last10Height) - height);
                
                int bottomc = new Rectangle(scrollbarPositionMinX, minY, scrollbarPositionMaxX - scrollbarPositionMinX, height).contains(PointHelper.ofMouse()) ? 168 : 128;
                int topc = new Rectangle(scrollbarPositionMinX, minY, scrollbarPositionMaxX - scrollbarPositionMinX, height).contains(PointHelper.ofMouse()) ? 222 : 172;
                
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder buffer = tessellator.getBuffer();
                
                // Bottom
                buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                buffer.pos(scrollbarPositionMinX, minY + height, 0.0D).tex(0, 1).color(bottomc, bottomc, bottomc, 255).endVertex();
                buffer.pos(scrollbarPositionMaxX, minY + height, 0.0D).tex(1, 1).color(bottomc, bottomc, bottomc, 255).endVertex();
                buffer.pos(scrollbarPositionMaxX, minY, 0.0D).tex(1, 0).color(bottomc, bottomc, bottomc, 255).endVertex();
                buffer.pos(scrollbarPositionMinX, minY, 0.0D).tex(0, 0).color(bottomc, bottomc, bottomc, 255).endVertex();
                tessellator.draw();
                
                // Top
                buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                buffer.pos(scrollbarPositionMinX, (minY + height - 1), 0.0D).tex(0, 1).color(topc, topc, topc, 255).endVertex();
                buffer.pos((scrollbarPositionMaxX - 1), (minY + height - 1), 0.0D).tex(1, 1).color(topc, topc, topc, 255).endVertex();
                buffer.pos((scrollbarPositionMaxX - 1), minY, 0.0D).tex(1, 0).color(topc, topc, topc, 255).endVertex();
                buffer.pos(scrollbarPositionMinX, minY, 0.0D).tex(0, 0).color(topc, topc, topc, 255).endVertex();
                tessellator.draw();
                RenderSystem.enableTexture();
            }
            RenderSystem.translatef(0, 0, -300f);
            RenderSystem.popMatrix();
        }
        
        @Override
        public int getHeight() {
            return Math.max(Math.min(getCellCreator().getDropBoxMaxHeight(), (int) getMaxScroll()), 14);
        }
        
        @Override
        public boolean func_231047_b_(double mouseX, double mouseY) {
            return isExpanded() && mouseX >= lastRectangle.x && mouseX <= lastRectangle.x + getCellCreator().getCellWidth() && mouseY >= lastRectangle.y + lastRectangle.height && mouseY <= lastRectangle.y + lastRectangle.height + getHeight() + 1;
        }
        
        @Override
        public boolean func_231045_a_(double double_1, double double_2, int int_1, double double_3, double double_4) {
            if (!isExpanded())
                return false;
            if (int_1 == 0 && this.scrolling) {
                if (double_2 < (double) lastRectangle.y + lastRectangle.height) {
                    scrollTo(0, false);
                } else if (double_2 > (double) lastRectangle.y + lastRectangle.height + getHeight()) {
                    scrollTo(getMaxScrollPosition(), false);
                } else {
                    double double_5 = Math.max(1, this.getMaxScrollPosition());
                    int int_2 = getHeight();
                    int int_3 = MathHelper.clamp((int) ((float) (int_2 * int_2) / (float) this.getMaxScrollPosition()), 32, int_2 - 8);
                    double double_6 = Math.max(1.0D, double_5 / (double) (int_2 - int_3));
                    this.offset(double_4 * double_6, false);
                }
                target = MathHelper.clamp(target, 0, getMaxScrollPosition());
                return true;
            }
            return false;
        }
        
        @Override
        public boolean func_231043_a_(double mouseX, double mouseY, double double_3) {
            if (func_231047_b_(mouseX, mouseY)) {
                offset(ClothConfigInitializer.getScrollStep() * -double_3, true);
                return true;
            }
            return false;
        }
        
        protected void updateScrollingState(double double_1, double double_2, int int_1) {
            this.scrolling = isExpanded() && lastRectangle != null && int_1 == 0 && double_1 >= (double) lastRectangle.x + getCellCreator().getCellWidth() - 6 && double_1 < (double) (lastRectangle.x + getCellCreator().getCellWidth());
        }
        
        @Override
        public boolean func_231044_a_(double double_1, double double_2, int int_1) {
            if (!isExpanded())
                return false;
            updateScrollingState(double_1, double_2, int_1);
            return super.func_231044_a_(double_1, double_2, int_1) || scrolling;
        }
        
        public void offset(double value, boolean animated) {
            scrollTo(target + value, animated);
        }
        
        public void scrollTo(double value, boolean animated) {
            scrollTo(value, animated, ClothConfigInitializer.getScrollDuration());
        }
        
        public void scrollTo(double value, boolean animated, long duration) {
            target = ScrollingContainer.clampExtension(value, getMaxScrollPosition());
            
            if (animated) {
                start = System.currentTimeMillis();
                this.duration = duration;
            } else
                scroll = target;
        }
        
        @Override
        public List<SelectionCellElement<R>> func_231039_at__() {
            return currentElements;
        }
    }
    
    public static abstract class SelectionCellCreator<R> {
        public abstract SelectionCellElement<R> create(R selection);
        
        public abstract int getCellHeight();
        
        public abstract int getDropBoxMaxHeight();
        
        public int getCellWidth() {
            return 132;
        }
    }
    
    public static class DefaultSelectionCellCreator<R> extends SelectionCellCreator<R> {
        protected Function<R, ITextComponent> toTextFunction;
        
        public DefaultSelectionCellCreator(Function<R, ITextComponent> toTextFunction) {
            this.toTextFunction = toTextFunction;
        }
        
        public DefaultSelectionCellCreator() {
            this(r -> new StringTextComponent(r.toString()));
        }
        
        @Override
        public SelectionCellElement<R> create(R selection) {
            return new DefaultSelectionCellElement<>(selection, toTextFunction);
        }
        
        @Override
        public int getCellHeight() {
            return 14;
        }
        
        @Override
        public int getDropBoxMaxHeight() {
            return getCellHeight() * 7;
        }
    }
    
    public static abstract class SelectionCellElement<R> extends FocusableGui {
        @SuppressWarnings("NotNullFieldNotInitialized") @Deprecated @NotNull private DropdownBoxEntry<R> entry;
        
        @NotNull
        public final DropdownBoxEntry<R> getEntry() {
            return entry;
        }
        
        public abstract void render(MatrixStack matrices, int mouseX, int mouseY, int x, int y, int width, int height, float delta);
        
        public abstract void dontRender(MatrixStack matrices, float delta);
        
        @Nullable
        public abstract ITextComponent getSearchKey();
        
        @Nullable
        public abstract R getSelection();
    }
    
    public static class DefaultSelectionCellElement<R> extends SelectionCellElement<R> {
        protected R r;
        protected int x;
        protected int y;
        protected int width;
        protected int height;
        protected boolean rendering;
        protected Function<R, ITextComponent> toTextFunction;
        
        public DefaultSelectionCellElement(R r, Function<R, ITextComponent> toTextFunction) {
            this.r = r;
            this.toTextFunction = toTextFunction;
        }
        
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
            Minecraft.getInstance().fontRenderer.func_238407_a_(matrices, toTextFunction.apply(r), x + 6, y + 3, b ? 16777215 : 8947848);
        }
        
        @Override
        public void dontRender(MatrixStack matrices, float delta) {
            rendering = false;
        }
        
        @Nullable
        @Override
        public ITextComponent getSearchKey() {
            return toTextFunction.apply(r);
        }
        
        @Nullable
        @Override
        public R getSelection() {
            return r;
        }
        
        @Override
        public List<? extends IGuiEventListener> func_231039_at__() {
            return Collections.emptyList();
        }
        
        @Override
        public boolean func_231044_a_(double mouseX, double mouseY, int int_1) {
            boolean b = rendering && mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
            if (b) {
                getEntry().selectionElement.topRenderer.setValue(r);
                getEntry().selectionElement.func_231035_a_(null);
                getEntry().selectionElement.dontReFocus = true;
                return true;
            }
            return false;
        }
    }
    
    public static abstract class SelectionTopCellElement<R> extends FocusableGui {
        @Deprecated private DropdownBoxEntry<R> entry;
        protected boolean isSelected = false;
        
        public abstract R getValue();
        
        public abstract void setValue(R value);
        
        public abstract ITextComponent getSearchTerm();
        
        public boolean isEdited() {
            return getConfigError().isPresent();
        }
        
        public abstract Optional<ITextComponent> getError();
        
        public final Optional<ITextComponent> getConfigError() {
            return entry.getConfigError();
        }
        
        public DropdownBoxEntry<R> getParent() {
            return entry;
        }
        
        public final boolean hasConfigError() {
            return getConfigError().isPresent();
        }
        
        public final boolean hasError() {
            return getError().isPresent();
        }
        
        public final int getPreferredTextColor() {
            return getConfigError().isPresent() ? 16733525 : 16777215;
        }
        
        public final boolean isSuggestionMode() {
            return getParent().isSuggestionMode();
        }
        
        public void selectFirstRecommendation() {
            List<SelectionCellElement<R>> children = getParent().selectionElement.menu.func_231039_at__();
            for (SelectionCellElement<R> child : children) {
                if (child.getSelection() != null) {
                    setValue(child.getSelection());
                    getParent().selectionElement.func_231035_a_(null);
                    break;
                }
            }
        }
        
        public abstract void render(MatrixStack matrices, int mouseX, int mouseY, int x, int y, int width, int height, float delta);
    }
    
    public static class DefaultSelectionTopCellElement<R> extends SelectionTopCellElement<R> {
        protected TextFieldWidget textFieldWidget;
        protected Function<String, R> toObjectFunction;
        protected Function<R, ITextComponent> toTextFunction;
        protected final R original;
        protected R value;
        
        public DefaultSelectionTopCellElement(R value, Function<String, R> toObjectFunction, Function<R, ITextComponent> toTextFunction) {
            this.original = Objects.requireNonNull(value);
            this.value = Objects.requireNonNull(value);
            this.toObjectFunction = Objects.requireNonNull(toObjectFunction);
            this.toTextFunction = Objects.requireNonNull(toTextFunction);
            textFieldWidget = new TextFieldWidget(Minecraft.getInstance().fontRenderer, 0, 0, 148, 18, NarratorChatListener.EMPTY) {
                @Override
                public void func_230430_a_(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                    setFocused2(isSuggestionMode() && isSelected && DefaultSelectionTopCellElement.this.getParent().func_241217_q_() == DefaultSelectionTopCellElement.this.getParent().selectionElement && DefaultSelectionTopCellElement.this.getParent().selectionElement.func_241217_q_() == DefaultSelectionTopCellElement.this && DefaultSelectionTopCellElement.this.func_241217_q_() == this);
                    super.func_230430_a_(matrices, mouseX, mouseY, delta);
                }
                
                @Override
                public boolean func_231046_a_(int int_1, int int_2, int int_3) {
                    if (int_1 == 257 || int_1 == 335) {
                        DefaultSelectionTopCellElement.this.selectFirstRecommendation();
                        return true;
                    }
                    return isSuggestionMode() && super.func_231046_a_(int_1, int_2, int_3);
                }
                
                @Override
                public boolean func_231042_a_(char chr, int keyCode) {
                    return isSuggestionMode() && super.func_231042_a_(chr, keyCode);
                }
            };
            textFieldWidget.setEnableBackgroundDrawing(false);
            textFieldWidget.setMaxStringLength(999999);
            textFieldWidget.setText(toTextFunction.apply(value).getString());
        }
        
        @Override
        public boolean isEdited() {
            return super.isEdited() || !getValue().equals(original);
        }
        
        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, int x, int y, int width, int height, float delta) {
            textFieldWidget.field_230690_l_ = x + 4;
            textFieldWidget.field_230691_m_ = y + 6;
            textFieldWidget.func_230991_b_(width - 8);
            textFieldWidget.setEnabled(getParent().isEditable());
            textFieldWidget.setTextColor(getPreferredTextColor());
            textFieldWidget.func_230430_a_(matrices, mouseX, mouseY, delta);
        }
        
        @Override
        public R getValue() {
            if (hasError())
                return value;
            return toObjectFunction.apply(textFieldWidget.getText());
        }
        
        @Override
        public void setValue(R value) {
            textFieldWidget.setText(toTextFunction.apply(value).getString());
            textFieldWidget.setCursorPosition(0);
        }
        
        @Override
        public ITextComponent getSearchTerm() {
            return new StringTextComponent(textFieldWidget.getText());
        }
        
        @Override
        public Optional<ITextComponent> getError() {
            if (toObjectFunction.apply(textFieldWidget.getText()) != null)
                return Optional.empty();
            return Optional.of(new StringTextComponent("Invalid Value!"));
        }
        
        @Override
        public List<? extends IGuiEventListener> func_231039_at__() {
            return Collections.singletonList(textFieldWidget);
        }
    }
}
