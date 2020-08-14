package me.shedaniel.clothconfig2.forge.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.clothconfig2.forge.api.*;
import me.shedaniel.clothconfig2.forge.gui.widget.DynamicElementListWidget;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"deprecation", "rawtypes", "DuplicatedCode"})
@OnlyIn(Dist.CLIENT)
public class ClothConfigScreen extends AbstractTabbedConfigScreen {
    private ScrollingContainer tabsScroller = new ScrollingContainer() {
        @Override
        public Rectangle getBounds() {
            return new Rectangle(0, 0, 1, ClothConfigScreen.this.width - 40); // We don't need to handle dragging
        }
        
        @Override
        public int getMaxScrollHeight() {
            return (int) ClothConfigScreen.this.getTabsMaximumScrolled();
        }
        
        @Override
        public void updatePosition(float delta) {
            super.updatePosition(delta);
            scrollAmount = clamp(scrollAmount, 0);
        }
    };
    public ListWidget<AbstractConfigEntry<AbstractConfigEntry<?>>> listWidget;
    private final LinkedHashMap<ITextComponent, List<AbstractConfigEntry<?>>> categorizedEntries = Maps.newLinkedHashMap();
    private final List<Tuple<ITextComponent, Integer>> tabs;
    private Widget quitButton, saveButton, buttonLeftTab, buttonRightTab;
    private Rectangle tabsBounds, tabsLeftBounds, tabsRightBounds;
    private double tabsMaximumScrolled = -1d;
    private final List<ClothConfigTabButton> tabButtons = Lists.newArrayList();
    
    @ApiStatus.Internal
    public ClothConfigScreen(Screen parent, ITextComponent title, Map<ITextComponent, List<Object>> entriesMap, ResourceLocation backgroundLocation) {
        super(parent, title, backgroundLocation);
        entriesMap.forEach((categoryName, list) -> {
            List<AbstractConfigEntry<?>> entries = Lists.newArrayList();
            for (Object object : list) {
                AbstractConfigListEntry<?> entry;
                if (object instanceof Tuple<?, ?>) {
                    entry = (AbstractConfigListEntry<?>) ((Tuple<?, ?>) object).getB();
                } else {
                    entry = (AbstractConfigListEntry<?>) object;
                }
                entry.setScreen(this);
                entries.add(entry);
            }
            categorizedEntries.put(categoryName, entries);
        });
        this.tabs = categorizedEntries.keySet().stream().map(s -> new Tuple<>(s, Minecraft.getInstance().fontRenderer.func_238414_a_(s) + 8)).collect(Collectors.toList());
    }
    
    @Override
    public ITextComponent getSelectedCategory() {
        return tabs.get(selectedCategoryIndex).getA();
    }
    
    @Override
    public Map<ITextComponent, List<AbstractConfigEntry<?>>> getCategorizedEntries() {
        return categorizedEntries;
    }
    
    @Override
    public void tick() {
        super.tick();
        boolean edited = isEdited();
        quitButton.setMessage(edited ? new TranslationTextComponent("text.cloth-config.cancel_discard") : new TranslationTextComponent("gui.cancel"));
        saveButton.active = edited;
    }
    
    @Override
    public boolean isEdited() {
        return super.isEdited();
    }
    
    /**
     * Override #isEdited please
     */
    @Deprecated
    public void setEdited(boolean edited) {
        super.setEdited(edited);
    }
    
    /**
     * Override #isEdited please
     */
    @Override
    @Deprecated
    public void setEdited(boolean edited, boolean requiresRestart) {
        super.setEdited(edited, requiresRestart);
    }
    
    @Override
    public void saveAll(boolean openOtherScreens) {
        super.saveAll(openOtherScreens);
    }
    
    @Override
    protected void init() {
        super.init();
        this.tabButtons.clear();
        
        children.add(listWidget = new ListWidget(this, minecraft, width, height, isShowingTabs() ? 70 : 30, height - 32, getBackgroundLocation()));
        if (categorizedEntries.size() > selectedCategoryIndex) {
            listWidget.getEventListeners().addAll((List) Lists.newArrayList(categorizedEntries.values()).get(selectedCategoryIndex));
        }
        int buttonWidths = Math.min(200, (width - 50 - 12) / 3);
        addButton(quitButton = new Button(width / 2 - buttonWidths - 3, height - 26, buttonWidths, 20, isEdited() ? new TranslationTextComponent("text.cloth-config.cancel_discard") : new TranslationTextComponent("gui.cancel"), widget -> quit()));
        addButton(saveButton = new Button(width / 2 + 3, height - 26, buttonWidths, 20, NarratorChatListener.EMPTY, button -> saveAll(true)) {
            @Override
            public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                boolean hasErrors = false;
                for (List<AbstractConfigEntry<?>> entries : Lists.newArrayList(categorizedEntries.values())) {
                    for (AbstractConfigEntry<?> entry : entries)
                        if (entry.getConfigError().isPresent()) {
                            hasErrors = true;
                            break;
                        }
                    if (hasErrors)
                        break;
                }
                active = isEdited() && !hasErrors;
                setMessage(hasErrors ? new TranslationTextComponent("text.cloth-config.error_cannot_save") : new TranslationTextComponent("text.cloth-config.save_and_done"));
                super.render(matrices, mouseX, mouseY, delta);
            }
        });
        saveButton.active = isEdited();
        if (isShowingTabs()) {
            tabsBounds = new Rectangle(0, 41, width, 24);
            tabsLeftBounds = new Rectangle(0, 41, 18, 24);
            tabsRightBounds = new Rectangle(width - 18, 41, 18, 24);
            children.add(buttonLeftTab = new Button(4, 44, 12, 18, NarratorChatListener.EMPTY, button -> tabsScroller.scrollTo(0, true)) {
                @Override
                public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                    minecraft.getTextureManager().bindTexture(CONFIG_TEX);
                    RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
                    int int_3 = this.getYImage(this.isHovered());
                    RenderSystem.enableBlend();
                    RenderSystem.blendFuncSeparate(770, 771, 0, 1);
                    RenderSystem.blendFunc(770, 771);
                    this.blit(matrices, x, y, 12, 18 * int_3, width, height);
                }
            });
            int j = 0;
            for (Tuple<ITextComponent, Integer> tab : tabs) {
                tabButtons.add(new ClothConfigTabButton(this, j, -100, 43, tab.getB(), 20, tab.getA()));
                j++;
            }
            children.addAll(tabButtons);
            children.add(buttonRightTab = new Button(width - 16, 44, 12, 18, NarratorChatListener.EMPTY, button -> tabsScroller.scrollTo(tabsScroller.getMaxScroll(), true)) {
                @Override
                public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                    minecraft.getTextureManager().bindTexture(CONFIG_TEX);
                    RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
                    int int_3 = this.getYImage(this.isHovered());
                    RenderSystem.enableBlend();
                    RenderSystem.blendFuncSeparate(770, 771, 0, 1);
                    RenderSystem.blendFunc(770, 771);
                    this.blit(matrices, x, y, 0, 18 * int_3, width, height);
                }
            });
        } else {
            tabsBounds = tabsLeftBounds = tabsRightBounds = new Rectangle();
        }
        Optional.ofNullable(this.afterInitConsumer).ifPresent(consumer -> consumer.accept(this));
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (tabsBounds.contains(mouseX, mouseY) && !tabsLeftBounds.contains(mouseX, mouseY) && !tabsRightBounds.contains(mouseX, mouseY) && amount != 0d) {
            tabsScroller.offset(-amount * 16, true);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }
    
    public double getTabsMaximumScrolled() {
        if (tabsMaximumScrolled == -1d) {
            int[] i = {0};
            for (Tuple<ITextComponent, Integer> pair : tabs) i[0] += pair.getB() + 2;
            tabsMaximumScrolled = i[0];
        }
        return tabsMaximumScrolled + 6;
    }
    
    public void resetTabsMaximumScrolled() {
        tabsMaximumScrolled = -1d;
    }
    
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (isShowingTabs()) {
            tabsScroller.updatePosition(delta * 3);
            int xx = 24 - (int) tabsScroller.scrollAmount;
            for (ClothConfigTabButton tabButton : tabButtons) {
                tabButton.x = xx;
                xx += tabButton.getWidth() + 2;
            }
            buttonLeftTab.active = tabsScroller.scrollAmount > 0d;
            buttonRightTab.active = tabsScroller.scrollAmount < getTabsMaximumScrolled() - width + 40;
        }
        if (isTransparentBackground()) {
            fillGradient(matrices, 0, 0, this.width, this.height, -1072689136, -804253680);
        } else {
            renderDirtBackground(0);
        }
        listWidget.render(matrices, mouseX, mouseY, delta);
        ScissorsHandler.INSTANCE.scissor(new Rectangle(listWidget.left, listWidget.top, listWidget.width, listWidget.bottom - listWidget.top));
        for (AbstractConfigEntry child : listWidget.getEventListeners())
            child.lateRender(matrices, mouseX, mouseY, delta);
        ScissorsHandler.INSTANCE.removeLastScissor();
        if (isShowingTabs()) {
            drawCenteredString(matrices, minecraft.fontRenderer, title, width / 2, 18, -1);
            Rectangle onlyInnerTabBounds = new Rectangle(tabsBounds.x + 20, tabsBounds.y, tabsBounds.width - 40, tabsBounds.height);
            ScissorsHandler.INSTANCE.scissor(onlyInnerTabBounds);
            if (isTransparentBackground())
                fillGradient(matrices, onlyInnerTabBounds.x, onlyInnerTabBounds.y, onlyInnerTabBounds.getMaxX(), onlyInnerTabBounds.getMaxY(), 0x68000000, 0x68000000);
            else
                overlayBackground(matrices, onlyInnerTabBounds, 32, 32, 32, 255, 255);
            tabButtons.forEach(widget -> widget.render(matrices, mouseX, mouseY, delta));
            drawTabsShades(matrices, 0, isTransparentBackground() ? 120 : 255);
            ScissorsHandler.INSTANCE.removeLastScissor();
            buttonLeftTab.render(matrices, mouseX, mouseY, delta);
            buttonRightTab.render(matrices, mouseX, mouseY, delta);
        } else
            drawCenteredString(matrices, minecraft.fontRenderer, title, width / 2, 12, -1);
        
        if (isEditable()) {
            List<ITextComponent> errors = Lists.newArrayList();
            for (List<AbstractConfigEntry<?>> entries : Lists.newArrayList(categorizedEntries.values()))
                for (AbstractConfigEntry<?> entry : entries)
                    if (entry.getConfigError().isPresent())
                        errors.add(entry.getConfigError().get());
            if (errors.size() > 0) {
                minecraft.getTextureManager().bindTexture(CONFIG_TEX);
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                String text = "§c" + (errors.size() == 1 ? errors.get(0).deepCopy().getString() : I18n.format("text.cloth-config.multi_error"));
                if (isTransparentBackground()) {
                    int stringWidth = minecraft.fontRenderer.getStringWidth(text);
                    fillGradient(matrices, 8, 9, 20 + stringWidth, 14 + minecraft.fontRenderer.FONT_HEIGHT, 0x68000000, 0x68000000);
                }
                blit(matrices, 10, 10, 0, 54, 3, 11);
                drawString(matrices, minecraft.fontRenderer, text, 18, 12, -1);
                if (errors.size() > 1) {
                    int stringWidth = minecraft.fontRenderer.getStringWidth(text);
                    if (mouseX >= 10 && mouseY >= 10 && mouseX <= 18 + stringWidth && mouseY <= 14 + minecraft.fontRenderer.FONT_HEIGHT)
                        addTooltip(Tooltip.of(new Point(mouseX, mouseY), errors.toArray(new ITextComponent[0])));
                }
            }
        } else if (!isEditable()) {
            minecraft.getTextureManager().bindTexture(CONFIG_TEX);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            String text = "§c" + I18n.format("text.cloth-config.not_editable");
            if (isTransparentBackground()) {
                int stringWidth = minecraft.fontRenderer.getStringWidth(text);
                fillGradient(matrices, 8, 9, 20 + stringWidth, 14 + minecraft.fontRenderer.FONT_HEIGHT, 0x68000000, 0x68000000);
            }
            blit(matrices, 10, 10, 0, 54, 3, 11);
            drawString(matrices, minecraft.fontRenderer, text, 18, 12, -1);
        }
        super.render(matrices, mouseX, mouseY, delta);
    }
    
    @ApiStatus.ScheduledForRemoval
    @Deprecated
    public void queueTooltip(QueuedTooltip queuedTooltip) {
        super.addTooltip(queuedTooltip);
    }
    
    private void drawTabsShades(MatrixStack matrices, int lightColor, int darkColor) {
        drawTabsShades(matrices.getLast().getMatrix(), lightColor, darkColor);
    }
    
    private void drawTabsShades(Matrix4f matrix, int lightColor, int darkColor) {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(770, 771, 0, 1);
        RenderSystem.disableAlphaTest();
        RenderSystem.shadeModel(7425);
        RenderSystem.disableTexture();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        buffer.pos(matrix, tabsBounds.getMinX() + 20, tabsBounds.getMinY() + 4, 0.0F).tex(0, 1f).color(0, 0, 0, lightColor).endVertex();
        buffer.pos(matrix, tabsBounds.getMaxX() - 20, tabsBounds.getMinY() + 4, 0.0F).tex(1f, 1f).color(0, 0, 0, lightColor).endVertex();
        buffer.pos(matrix, tabsBounds.getMaxX() - 20, tabsBounds.getMinY(), 0.0F).tex(1f, 0).color(0, 0, 0, darkColor).endVertex();
        buffer.pos(matrix, tabsBounds.getMinX() + 20, tabsBounds.getMinY(), 0.0F).tex(0, 0).color(0, 0, 0, darkColor).endVertex();
        tessellator.draw();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        buffer.pos(matrix, tabsBounds.getMinX() + 20, tabsBounds.getMaxY(), 0.0F).tex(0, 1f).color(0, 0, 0, darkColor).endVertex();
        buffer.pos(matrix, tabsBounds.getMaxX() - 20, tabsBounds.getMaxY(), 0.0F).tex(1f, 1f).color(0, 0, 0, darkColor).endVertex();
        buffer.pos(matrix, tabsBounds.getMaxX() - 20, tabsBounds.getMaxY() - 4, 0.0F).tex(1f, 0).color(0, 0, 0, lightColor).endVertex();
        buffer.pos(matrix, tabsBounds.getMinX() + 20, tabsBounds.getMaxY() - 4, 0.0F).tex(0, 0).color(0, 0, 0, lightColor).endVertex();
        tessellator.draw();
        RenderSystem.enableTexture();
        RenderSystem.shadeModel(7424);
        RenderSystem.enableAlphaTest();
        RenderSystem.disableBlend();
    }
    
    @Override
    public void save() {
        super.save();
    }
    
    @Override
    public boolean isEditable() {
        return super.isEditable();
    }
    
    public static class ListWidget<R extends DynamicElementListWidget.ElementEntry<R>> extends DynamicElementListWidget<R> {
        private AbstractConfigScreen screen;
        
        public ListWidget(AbstractConfigScreen screen, Minecraft client, int width, int height, int top, int bottom, ResourceLocation backgroundLocation) {
            super(client, width, height, top, bottom, backgroundLocation);
            setRenderSelection(false);
            this.screen = screen;
        }
        
        @Override
        public int getItemWidth() {
            return width - 80;
        }
        
        @Override
        protected int getScrollbarPosition() {
            return left + width - 36;
        }
        
        @Override
        protected void renderItem(MatrixStack matrices, R item, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
            if (item instanceof AbstractConfigEntry)
                ((AbstractConfigEntry) item).updateSelected(getListener() == item);
            super.renderItem(matrices, item, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isSelected, delta);
        }
        
        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            this.updateScrollingState(mouseX, mouseY, button);
            if (!this.isMouseOver(mouseX, mouseY)) {
                return false;
            } else {
                for (R entry : getEventListeners()) {
                    if (entry.mouseClicked(mouseX, mouseY, button)) {
                        this.setListener(entry);
                        this.setDragging(true);
                        return true;
                    }
                }
                if (button == 0) {
                    this.clickedHeader((int) (mouseX - (double) (this.left + this.width / 2 - this.getItemWidth() / 2)), (int) (mouseY - (double) this.top) + (int) this.getScroll() - 4);
                    return true;
                }
                
                return this.scrolling;
            }
        }
        
        @Override
        protected void renderBackBackground(MatrixStack matrices, BufferBuilder buffer, Tessellator tessellator) {
            if (!screen.isTransparentBackground())
                super.renderBackBackground(matrices, buffer, tessellator);
            else {
                fillGradient(matrices, left, top, right, bottom, 0x68000000, 0x68000000);
            }
        }
        
        @Override
        protected void renderHoleBackground(MatrixStack matrices, int y1, int y2, int alpha1, int alpha2) {
            if (!screen.isTransparentBackground())
                super.renderHoleBackground(matrices, y1, y2, alpha1, alpha2);
        }
    }
}
