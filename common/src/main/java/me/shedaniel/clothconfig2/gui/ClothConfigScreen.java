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

package me.shedaniel.clothconfig2.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import me.shedaniel.clothconfig2.api.*;
import me.shedaniel.clothconfig2.api.animator.ValueAnimator;
import me.shedaniel.clothconfig2.gui.entries.EmptyEntry;
import me.shedaniel.clothconfig2.gui.widget.DynamicElementListWidget;
import me.shedaniel.clothconfig2.gui.widget.SearchFieldEntry;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@SuppressWarnings({"deprecation", "rawtypes", "DuplicatedCode"})
@Environment(EnvType.CLIENT)
public class ClothConfigScreen extends AbstractTabbedConfigScreen {
    private final ScrollingContainer tabsScroller = new ScrollingContainer() {
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
    private final LinkedHashMap<Component, List<AbstractConfigEntry<?>>> categorizedEntries = Maps.newLinkedHashMap();
    private final List<Tuple<Component, Integer>> tabs;
    private SearchFieldEntry searchFieldEntry;
    private AbstractWidget buttonLeftTab, buttonRightTab;
    private Rectangle tabsBounds, tabsLeftBounds, tabsRightBounds;
    private double tabsMaximumScrolled = -1d;
    private final List<ClothConfigTabButton> tabButtons = Lists.newArrayList();
    private final Map<Component, ConfigCategory> categoryMap;
    
    @ApiStatus.Internal
    public ClothConfigScreen(Screen parent, Component title, Map<Component, ConfigCategory> categoryMap, ResourceLocation backgroundLocation) {
        super(parent, title, backgroundLocation);
        categoryMap.forEach((categoryName, category) -> {
            List<AbstractConfigEntry<?>> entries = Lists.newArrayList();
            for (Object object : category.getEntries()) {
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
            if (category.getBackground() != null) {
                registerCategoryBackground(categoryName, category.getBackground());
            }
        });
        
        this.tabs = categorizedEntries.keySet().stream().map(s -> new Tuple<>(s, Minecraft.getInstance().font.width(s) + 8)).collect(Collectors.toList());
        this.categoryMap = categoryMap;
    }
    
    @Override
    public Component getSelectedCategory() {
        return tabs.get(selectedCategoryIndex).getA();
    }
    
    @Override
    public Map<Component, List<AbstractConfigEntry<?>>> getCategorizedEntries() {
        return categorizedEntries;
    }
    
    @Override
    protected void init() {
        super.init();
        this.tabButtons.clear();
        
        childrenL().add(listWidget = new ListWidget(this, minecraft, width, height, isShowingTabs() ? 70 : 30, height - 32, getBackgroundLocation()));
        listWidget.children().add((AbstractConfigEntry) new EmptyEntry(5));
        listWidget.children().add((AbstractConfigEntry) (searchFieldEntry = new SearchFieldEntry(this, listWidget)));
        listWidget.children().add((AbstractConfigEntry) new EmptyEntry(5));
        if (categorizedEntries.size() > selectedCategoryIndex) {
            listWidget.children().addAll((List) Lists.newArrayList(categorizedEntries.values()).get(selectedCategoryIndex));
        }
        int buttonWidths = Math.min(200, (width - 50 - 12) / 3);
        addRenderableWidget(new Button(width / 2 - buttonWidths - 3, height - 26, buttonWidths, 20, isEdited() ? new TranslatableComponent("text.cloth-config.cancel_discard") : new TranslatableComponent("gui.cancel"), widget -> quit()));
        addRenderableWidget(new Button(width / 2 + 3, height - 26, buttonWidths, 20, NarratorChatListener.NO_TITLE, button -> saveAll(true)) {
            @Override
            public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
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
                setMessage(hasErrors ? new TranslatableComponent("text.cloth-config.error_cannot_save") : new TranslatableComponent("text.cloth-config.save_and_done"));
                super.render(matrices, mouseX, mouseY, delta);
            }
        });
        if (isShowingTabs()) {
            tabsBounds = new Rectangle(0, 41, width, 24);
            tabsLeftBounds = new Rectangle(0, 41, 18, 24);
            tabsRightBounds = new Rectangle(width - 18, 41, 18, 24);
            childrenL().add(buttonLeftTab = new Button(4, 44, 12, 18, NarratorChatListener.NO_TITLE, button -> tabsScroller.scrollTo(0, true)) {
                @Override
                public void renderButton(PoseStack matrices, int mouseX, int mouseY, float delta) {
                    RenderSystem.setShader(GameRenderer::getPositionTexShader);
                    RenderSystem.setShaderTexture(0, CONFIG_TEX);
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
                    int int_3 = this.getYImage(this.isHovered);
                    RenderSystem.enableBlend();
                    RenderSystem.blendFuncSeparate(770, 771, 0, 1);
                    RenderSystem.blendFunc(770, 771);
                    this.blit(matrices, x, y, 12, 18 * int_3, width, height);
                }
            });
            int j = 0;
            for (Tuple<Component, Integer> tab : tabs) {
                tabButtons.add(new ClothConfigTabButton(this, j, -100, 43, tab.getB(), 20, tab.getA(), this.categoryMap.get(tab.getA()).getDescription()));
                j++;
            }
            childrenL().addAll(tabButtons);
            childrenL().add(buttonRightTab = new Button(width - 16, 44, 12, 18, NarratorChatListener.NO_TITLE, button -> tabsScroller.scrollTo(tabsScroller.getMaxScroll(), true)) {
                @Override
                public void renderButton(PoseStack matrices, int mouseX, int mouseY, float delta) {
                    RenderSystem.setShader(GameRenderer::getPositionTexShader);
                    RenderSystem.setShaderTexture(0, CONFIG_TEX);
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
                    int int_3 = this.getYImage(this.isHovered);
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
    public boolean matchesSearch(Iterator<String> tags) {
        return searchFieldEntry.matchesSearch(tags);
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
            for (Tuple<Component, Integer> pair : tabs) i[0] += pair.getB() + 2;
            tabsMaximumScrolled = i[0];
        }
        return tabsMaximumScrolled + 6;
    }
    
    public void resetTabsMaximumScrolled() {
        tabsMaximumScrolled = -1d;
    }
    
    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
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
            drawCenteredString(matrices, minecraft.font, title, width / 2, 18, -1);
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
            drawCenteredString(matrices, minecraft.font, title, width / 2, 12, -1);
        
        if (isEditable()) {
            List<Component> errors = Lists.newArrayList();
            for (List<AbstractConfigEntry<?>> entries : Lists.newArrayList(categorizedEntries.values()))
                for (AbstractConfigEntry<?> entry : entries)
                    if (entry.getConfigError().isPresent())
                        errors.add(entry.getConfigError().get());
            if (errors.size() > 0) {
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderTexture(0, CONFIG_TEX);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                String text = "§c" + (errors.size() == 1 ? errors.get(0).plainCopy().getString() : I18n.get("text.cloth-config.multi_error"));
                if (isTransparentBackground()) {
                    int stringWidth = minecraft.font.width(text);
                    fillGradient(matrices, 8, 9, 20 + stringWidth, 14 + minecraft.font.lineHeight, 0x68000000, 0x68000000);
                }
                blit(matrices, 10, 10, 0, 54, 3, 11);
                drawString(matrices, minecraft.font, text, 18, 12, -1);
                if (errors.size() > 1) {
                    int stringWidth = minecraft.font.width(text);
                    if (mouseX >= 10 && mouseY >= 10 && mouseX <= 18 + stringWidth && mouseY <= 14 + minecraft.font.lineHeight)
                        addTooltip(Tooltip.of(new Point(mouseX, mouseY), errors.toArray(new Component[0])));
                }
            }
        } else if (!isEditable()) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, CONFIG_TEX);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            String text = "§c" + I18n.get("text.cloth-config.not_editable");
            if (isTransparentBackground()) {
                int stringWidth = minecraft.font.width(text);
                fillGradient(matrices, 8, 9, 20 + stringWidth, 14 + minecraft.font.lineHeight, 0x68000000, 0x68000000);
            }
            blit(matrices, 10, 10, 0, 54, 3, 11);
            drawString(matrices, minecraft.font, text, 18, 12, -1);
        }
        super.render(matrices, mouseX, mouseY, delta);
    }
    
    private void drawTabsShades(PoseStack matrices, int lightColor, int darkColor) {
        drawTabsShades(matrices.last().pose(), lightColor, darkColor);
    }
    
    private void drawTabsShades(Matrix4f matrix, int lightColor, int darkColor) {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(770, 771, 0, 1);
        RenderSystem.disableTexture();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        buffer.vertex(matrix, tabsBounds.getMinX() + 20, tabsBounds.getMinY() + 4, 0.0F).uv(0, 1f).color(0, 0, 0, lightColor).endVertex();
        buffer.vertex(matrix, tabsBounds.getMaxX() - 20, tabsBounds.getMinY() + 4, 0.0F).uv(1f, 1f).color(0, 0, 0, lightColor).endVertex();
        buffer.vertex(matrix, tabsBounds.getMaxX() - 20, tabsBounds.getMinY(), 0.0F).uv(1f, 0).color(0, 0, 0, darkColor).endVertex();
        buffer.vertex(matrix, tabsBounds.getMinX() + 20, tabsBounds.getMinY(), 0.0F).uv(0, 0).color(0, 0, 0, darkColor).endVertex();
        buffer.vertex(matrix, tabsBounds.getMinX() + 20, tabsBounds.getMaxY(), 0.0F).uv(0, 1f).color(0, 0, 0, darkColor).endVertex();
        buffer.vertex(matrix, tabsBounds.getMaxX() - 20, tabsBounds.getMaxY(), 0.0F).uv(1f, 1f).color(0, 0, 0, darkColor).endVertex();
        buffer.vertex(matrix, tabsBounds.getMaxX() - 20, tabsBounds.getMaxY() - 4, 0.0F).uv(1f, 0).color(0, 0, 0, lightColor).endVertex();
        buffer.vertex(matrix, tabsBounds.getMinX() + 20, tabsBounds.getMaxY() - 4, 0.0F).uv(0, 0).color(0, 0, 0, lightColor).endVertex();
        tesselator.end();
        RenderSystem.enableTexture();
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
        private final AbstractConfigScreen screen;
        private final ValueAnimator<Rectangle> currentBounds = ValueAnimator.ofRectangle();
        public UnaryOperator<List<R>> entriesTransformer = UnaryOperator.identity();
        public Rectangle thisTimeTarget;
        public long lastTouch;
        
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
        protected void renderItem(PoseStack matrices, R item, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
            if (item instanceof AbstractConfigEntry)
                ((AbstractConfigEntry) item).updateSelected(getFocused() == item);
            super.renderItem(matrices, item, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isSelected, delta);
        }
        
        @Override
        protected void renderList(PoseStack matrices, int startX, int startY, int mouseX, int mouseY, float delta) {
            thisTimeTarget = null;
            Rectangle hoverBounds = currentBounds.value();
            if (!hoverBounds.isEmpty()) {
                long timePast = System.currentTimeMillis() - lastTouch;
                int alpha = timePast <= 200 ? 255 : Mth.ceil(255 - Math.min(timePast - 200, 500F) / 500F * 255.0);
                alpha = (alpha * 36 / 255) << 24;
                fillGradient(matrices, hoverBounds.x, hoverBounds.y - scroll, hoverBounds.getMaxX(), hoverBounds.getMaxY() - scroll, 0xFFFFFF | alpha, 0xFFFFFF | alpha);
            }
            super.renderList(matrices, startX, startY, mouseX, mouseY, delta);
            if (thisTimeTarget != null && isMouseOver(mouseX, mouseY)) {
                lastTouch = System.currentTimeMillis();
            }
            if (thisTimeTarget != null && !thisTimeTarget.equals(currentBounds.target())) {
                currentBounds.setTo(thisTimeTarget, 100);
            } else if (!currentBounds.target().isEmpty()) {
                currentBounds.update(delta);
            }
        }
        
        protected void fillGradient(PoseStack matrices, double xStart, double yStart, double xEnd, double yEnd, int colorStart, int colorEnd) {
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferBuilder = tesselator.getBuilder();
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            fillGradient(matrices.last().pose(), bufferBuilder, xStart, yStart, xEnd, yEnd, this.getBlitOffset(), colorStart, colorEnd);
            tesselator.end();
            RenderSystem.disableBlend();
            RenderSystem.enableTexture();
        }
        
        protected static void fillGradient(Matrix4f matrix4f, BufferBuilder bufferBuilder, double xStart, double yStart, double xEnd, double yEnd, int i, int j, int k) {
            float f = (float) (j >> 24 & 255) / 255.0F;
            float g = (float) (j >> 16 & 255) / 255.0F;
            float h = (float) (j >> 8 & 255) / 255.0F;
            float l = (float) (j & 255) / 255.0F;
            float m = (float) (k >> 24 & 255) / 255.0F;
            float n = (float) (k >> 16 & 255) / 255.0F;
            float o = (float) (k >> 8 & 255) / 255.0F;
            float p = (float) (k & 255) / 255.0F;
            bufferBuilder.vertex(matrix4f, (float) xEnd, (float) yStart, (float) i).color(g, h, l, f).endVertex();
            bufferBuilder.vertex(matrix4f, (float) xStart, (float) yStart, (float) i).color(g, h, l, f).endVertex();
            bufferBuilder.vertex(matrix4f, (float) xStart, (float) yEnd, (float) i).color(n, o, p, m).endVertex();
            bufferBuilder.vertex(matrix4f, (float) xEnd, (float) yEnd, (float) i).color(n, o, p, m).endVertex();
        }
        
        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            this.updateScrollingState(mouseX, mouseY, button);
            if (!this.isMouseOver(mouseX, mouseY)) {
                return false;
            } else {
                for (R entry : children()) {
                    if (entry.mouseClicked(mouseX, mouseY, button)) {
                        this.setFocused(entry);
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
        protected void renderBackBackground(PoseStack matrices, BufferBuilder buffer, Tesselator tessellator) {
            if (!screen.isTransparentBackground())
                super.renderBackBackground(matrices, buffer, tessellator);
            else {
                fillGradient(matrices, left, top, right, bottom, 0x68000000, 0x68000000);
            }
        }
        
        @Override
        protected void renderHoleBackground(PoseStack matrices, int y1, int y2, int alpha1, int alpha2) {
            if (!screen.isTransparentBackground())
                super.renderHoleBackground(matrices, y1, y2, alpha1, alpha2);
        }
        
        @Override
        public List<R> children() {
            return entriesTransformer.apply(super.children());
        }
    }
}
