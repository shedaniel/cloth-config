package me.shedaniel.clothconfig2.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.clothconfig2.api.*;
import me.shedaniel.clothconfig2.gui.widget.DynamicElementListWidget;
import me.shedaniel.math.Rectangle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Matrix4f;
import org.jetbrains.annotations.ApiStatus;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings({"deprecation", "rawtypes", "DuplicatedCode"})
@Environment(EnvType.CLIENT)
public abstract class ClothConfigScreen extends AbstractTabbedConfigScreen {
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
    private final LinkedHashMap<Text, List<AbstractConfigEntry<?>>> categorizedEntries = Maps.newLinkedHashMap();
    private final List<Pair<Text, Integer>> tabs;
    private AbstractButtonWidget quitButton, saveButton, buttonLeftTab, buttonRightTab;
    private Rectangle tabsBounds, tabsLeftBounds, tabsRightBounds;
    private double tabsMaximumScrolled = -1d;
    private final List<ClothConfigTabButton> tabButtons = Lists.newArrayList();
    
    @Deprecated
    public ClothConfigScreen(Screen parent, Text title, Map<Text, List<Object>> entriesMap, Identifier backgroundLocation) {
        super(parent, title, backgroundLocation);
        entriesMap.forEach((categoryName, list) -> {
            List<AbstractConfigEntry<?>> entries = Lists.newArrayList();
            for (Object object : list) {
                AbstractConfigListEntry<?> entry;
                if (object instanceof Pair<?, ?>) {
                    entry = (AbstractConfigListEntry<?>) ((Pair<?, ?>) object).getRight();
                } else {
                    entry = (AbstractConfigListEntry<?>) object;
                }
                entry.setScreen(this);
                entries.add(entry);
            }
            categorizedEntries.put(categoryName, entries);
        });
        this.tabs = categorizedEntries.keySet().stream().map(s -> new Pair<>(s, MinecraftClient.getInstance().textRenderer.getWidth(s) + 8)).collect(Collectors.toList());
    }
    
    @Override
    public Text getSelectedCategory() {
        return tabs.get(selectedCategoryIndex).getLeft();
    }
    
    @Override
    public Map<Text, List<AbstractConfigEntry<?>>> getCategorizedEntries() {
        return categorizedEntries;
    }
    
    @Override
    public void tick() {
        super.tick();
        boolean edited = isEdited();
        quitButton.setMessage(edited ? new TranslatableText("text.cloth-config.cancel_discard") : new TranslatableText("gui.cancel"));
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
        
        children.add(listWidget = new ListWidget(this, client, width, height, isShowingTabs() ? 70 : 30, height - 32, getBackgroundLocation()));
        if (categorizedEntries.size() > selectedCategoryIndex) {
            listWidget.children().addAll((List) Lists.newArrayList(categorizedEntries.values()).get(selectedCategoryIndex));
        }
        int buttonWidths = Math.min(200, (width - 50 - 12) / 3);
        addButton(quitButton = new ButtonWidget(width / 2 - buttonWidths - 3, height - 26, buttonWidths, 20, isEdited() ? new TranslatableText("text.cloth-config.cancel_discard") : new TranslatableText("gui.cancel"), widget -> {
            quit();
        }));
        addButton(saveButton = new AbstractPressableButtonWidget(width / 2 + 3, height - 26, buttonWidths, 20, NarratorManager.EMPTY) {
            @Override
            public void onPress() {
                saveAll(true);
            }
            
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
                setMessage(hasErrors ? new TranslatableText("text.cloth-config.error_cannot_save") : new TranslatableText("text.cloth-config.save_and_done"));
                super.render(matrices, mouseX, mouseY, delta);
            }
        });
        saveButton.active = isEdited();
        if (isShowingTabs()) {
            tabsBounds = new Rectangle(0, 41, width, 24);
            tabsLeftBounds = new Rectangle(0, 41, 18, 24);
            tabsRightBounds = new Rectangle(width - 18, 41, 18, 24);
            children.add(buttonLeftTab = new AbstractPressableButtonWidget(4, 44, 12, 18, NarratorManager.EMPTY) {
                @Override
                public void onPress() {
                    tabsScroller.scrollTo(0, false);
                }
                
                @Override
                public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                    client.getTextureManager().bindTexture(CONFIG_TEX);
                    RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
                    int int_3 = this.getYImage(this.isHovered());
                    RenderSystem.enableBlend();
                    RenderSystem.blendFuncSeparate(770, 771, 0, 1);
                    RenderSystem.blendFunc(770, 771);
                    this.drawTexture(matrices, x, y, 12, 18 * int_3, width, height);
                }
            });
            int j = 0;
            for (Pair<Text, Integer> tab : tabs) {
                tabButtons.add(new ClothConfigTabButton(this, j, -100, 43, tab.getRight(), 20, tab.getLeft()));
                j++;
            }
            children.addAll(tabButtons);
            children.add(buttonRightTab = new AbstractPressableButtonWidget(width - 16, 44, 12, 18, NarratorManager.EMPTY) {
                @Override
                public void onPress() {
                    tabsScroller.scrollTo(tabsScroller.getMaxScroll(), false);
                }
                
                @Override
                public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                    client.getTextureManager().bindTexture(CONFIG_TEX);
                    RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
                    int int_3 = this.getYImage(this.isHovered());
                    RenderSystem.enableBlend();
                    RenderSystem.blendFuncSeparate(770, 771, 0, 1);
                    RenderSystem.blendFunc(770, 771);
                    this.drawTexture(matrices, x, y, 0, 18 * int_3, width, height);
                }
            });
        } else {
            tabsBounds = tabsLeftBounds = tabsRightBounds = new Rectangle();
        }
    }
    
    @Override
    public boolean mouseScrolled(double double_1, double double_2, double double_3) {
        if (tabsBounds.contains(double_1, double_2) && !tabsLeftBounds.contains(double_1, double_2) && !tabsRightBounds.contains(double_1, double_2) && double_3 != 0d) {
            tabsScroller.offset(-double_3 * 16, true);
            return true;
        }
        return super.mouseScrolled(double_1, double_2, double_3);
    }
    
    public double getTabsMaximumScrolled() {
        if (tabsMaximumScrolled == -1d) {
            int[] i = {0};
            for (Pair<Text, Integer> pair : tabs) i[0] += pair.getRight() + 2;
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
        for (AbstractConfigEntry child : listWidget.children())
            child.lateRender(matrices, mouseX, mouseY, delta);
        ScissorsHandler.INSTANCE.removeLastScissor();
        if (isShowingTabs()) {
            method_27534(matrices, client.textRenderer, title, width / 2, 18, -1);
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
            method_27534(matrices, client.textRenderer, title, width / 2, 12, -1);
        
        if (isEditable()) {
            List<Text> errors = Lists.newArrayList();
            for (List<AbstractConfigEntry<?>> entries : Lists.newArrayList(categorizedEntries.values()))
                for (AbstractConfigEntry<?> entry : entries)
                    if (entry.getConfigError().isPresent())
                        errors.add(entry.getConfigError().get());
            if (errors.size() > 0) {
                client.getTextureManager().bindTexture(CONFIG_TEX);
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                String text = "§c" + (errors.size() == 1 ? errors.get(0).copy().getString() : I18n.translate("text.cloth-config.multi_error"));
                if (isTransparentBackground()) {
                    int stringWidth = client.textRenderer.getWidth(text);
                    fillGradient(matrices, 8, 9, 20 + stringWidth, 14 + client.textRenderer.fontHeight, 0x68000000, 0x68000000);
                }
                drawTexture(matrices, 10, 10, 0, 54, 3, 11);
                drawString(matrices, client.textRenderer, text, 18, 12, -1);
            }
        } else if (!isEditable()) {
            client.getTextureManager().bindTexture(CONFIG_TEX);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            String text = "§c" + I18n.translate("text.cloth-config.not_editable");
            if (isTransparentBackground()) {
                int stringWidth = client.textRenderer.getWidth(text);
                fillGradient(matrices, 8, 9, 20 + stringWidth, 14 + client.textRenderer.fontHeight, 0x68000000, 0x68000000);
            }
            drawTexture(matrices, 10, 10, 0, 54, 3, 11);
            drawString(matrices, client.textRenderer, text, 18, 12, -1);
        }
        super.render(matrices, mouseX, mouseY, delta);
    }
    
    @ApiStatus.ScheduledForRemoval
    @Deprecated
    public void queueTooltip(QueuedTooltip queuedTooltip) {
        super.addTooltip(queuedTooltip);
    }
    
    private void drawTabsShades(MatrixStack matrices, int lightColor, int darkColor) {
        drawTabsShades(matrices.peek().getModel(), lightColor, darkColor);
    }
    
    private void drawTabsShades(Matrix4f matrix, int lightColor, int darkColor) {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(770, 771, 0, 1);
        RenderSystem.disableAlphaTest();
        RenderSystem.shadeModel(7425);
        RenderSystem.disableTexture();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        buffer.vertex(matrix, tabsBounds.getMinX() + 20, tabsBounds.getMinY() + 4, 0.0F).texture(0, 1f).color(0, 0, 0, lightColor).next();
        buffer.vertex(matrix, tabsBounds.getMaxX() - 20, tabsBounds.getMinY() + 4, 0.0F).texture(1f, 1f).color(0, 0, 0, lightColor).next();
        buffer.vertex(matrix, tabsBounds.getMaxX() - 20, tabsBounds.getMinY(), 0.0F).texture(1f, 0).color(0, 0, 0, darkColor).next();
        buffer.vertex(matrix, tabsBounds.getMinX() + 20, tabsBounds.getMinY(), 0.0F).texture(0, 0).color(0, 0, 0, darkColor).next();
        tessellator.draw();
        buffer.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        buffer.vertex(matrix, tabsBounds.getMinX() + 20, tabsBounds.getMaxY(), 0.0F).texture(0, 1f).color(0, 0, 0, darkColor).next();
        buffer.vertex(matrix, tabsBounds.getMaxX() - 20, tabsBounds.getMaxY(), 0.0F).texture(1f, 1f).color(0, 0, 0, darkColor).next();
        buffer.vertex(matrix, tabsBounds.getMaxX() - 20, tabsBounds.getMaxY() - 4, 0.0F).texture(1f, 0).color(0, 0, 0, lightColor).next();
        buffer.vertex(matrix, tabsBounds.getMinX() + 20, tabsBounds.getMaxY() - 4, 0.0F).texture(0, 0).color(0, 0, 0, lightColor).next();
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
        
        public ListWidget(AbstractConfigScreen screen, MinecraftClient client, int width, int height, int top, int bottom, Identifier backgroundLocation) {
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
                ((AbstractConfigEntry) item).updateSelected(getFocused() == item);
            super.renderItem(matrices, item, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isSelected, delta);
        }
        
        @Override
        public boolean mouseClicked(double double_1, double double_2, int int_1) {
            this.updateScrollingState(double_1, double_2, int_1);
            if (!this.isMouseOver(double_1, double_2)) {
                return false;
            } else {
                for (R entry : children()) {
                    if (entry.mouseClicked(double_1, double_2, int_1)) {
                        this.setFocused(entry);
                        this.setDragging(true);
                        return true;
                    }
                }
                if (int_1 == 0) {
                    this.clickedHeader((int) (double_1 - (double) (this.left + this.width / 2 - this.getItemWidth() / 2)), (int) (double_2 - (double) this.top) + (int) this.getScroll() - 4);
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
        protected void renderHoleBackground(MatrixStack matrices, int int_1, int int_2, int int_3, int int_4) {
            if (!screen.isTransparentBackground())
                super.renderHoleBackground(matrices, int_1, int_2, int_3, int_4);
        }
    }
}
