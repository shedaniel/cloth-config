package me.shedaniel.cloth.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AtomicDouble;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import me.shedaniel.cloth.api.ConfigScreenBuilder;
import me.shedaniel.cloth.gui.entries.*;
import me.shedaniel.clothconfig.gui.DynamicElementListWidget;
import me.shedaniel.clothconfig.gui.QueuedTooltip;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class ClothConfigScreen extends Screen {
    
    private static final Identifier CONFIG_TEX = new Identifier("cloth-config", "textures/gui/cloth_config.png");
    private final List<QueuedTooltip> queuedTooltips = Lists.newArrayList();
    public int nextTabIndex;
    public int selectedTabIndex;
    public double tabsScrollVelocity = 0d;
    public double tabsScrollProgress = 0d;
    private Screen parent;
    private ListWidget listWidget;
    private LinkedHashMap<String, List<AbstractListEntry>> tabbedEntries;
    private List<Pair<String, Integer>> tabs;
    private boolean edited;
    private boolean confirmSave;
    private AbstractButtonWidget buttonQuit;
    private AbstractButtonWidget buttonSave;
    private AbstractButtonWidget buttonLeftTab;
    private AbstractButtonWidget buttonRightTab;
    private Rectangle tabsBounds, tabsLeftBounds, tabsRightBounds;
    private String title;
    private double tabsMaximumScrolled = -1d;
    private boolean displayErrors;
    private List<ClothConfigTabButton> tabButtons;
    private boolean smoothScrollingTabs = true;
    private boolean smoothScrollingList = true;
    private Identifier defaultBackgroundLocation;
    private Map<String, Identifier> categoryBackgroundLocation;
    
    public ClothConfigScreen(Screen parent, String title, Map<String, List<Pair<String, Object>>> o) {
        this(parent, title, o, true, true);
    }
    
    public ClothConfigScreen(Screen parent, String title, Map<String, List<Pair<String, Object>>> o, boolean confirmSave, boolean displayErrors) {
        this(parent, title, o, confirmSave, displayErrors, true, DrawableHelper.BACKGROUND_LOCATION);
    }
    
    public ClothConfigScreen(Screen parent, String title, Map<String, List<Pair<String, Object>>> o, boolean confirmSave, boolean displayErrors, boolean smoothScrollingList, Identifier defaultBackgroundLocation) {
        this(parent, title, o, confirmSave, displayErrors, smoothScrollingList, defaultBackgroundLocation, Maps.newHashMap());
    }
    
    public ClothConfigScreen(Screen parent, String title, Map<String, List<Pair<String, Object>>> o, boolean confirmSave, boolean displayErrors, boolean smoothScrollingList, Identifier defaultBackgroundLocation, Map<String, Identifier> categoryBackgroundLocation) {
        super(new TextComponent(""));
        this.parent = parent;
        this.title = title;
        this.tabbedEntries = Maps.newLinkedHashMap();
        this.smoothScrollingList = smoothScrollingList;
        this.defaultBackgroundLocation = defaultBackgroundLocation;
        o.forEach((tab, pairs) -> {
            List<AbstractListEntry> list = Lists.newArrayList();
            for(Pair<String, Object> pair : pairs) {
                if (pair.getRight() instanceof ListEntry) {
                    list.add((ListEntry) pair.getRight());
                } else if (pair.getRight() instanceof AbstractListEntry) {
                    throw new IllegalArgumentException("Unsupported Type (" + pair.getLeft() + "): AbstractListEntry");
                } else if (boolean.class.isAssignableFrom(pair.getRight().getClass()) || Boolean.class.isAssignableFrom(pair.getRight().getClass())) {
                    list.add(new BooleanListEntry(pair.getLeft(), (boolean) pair.getRight(), null));
                } else if (String.class.isAssignableFrom(pair.getRight().getClass())) {
                    list.add(new StringListEntry(pair.getLeft(), (String) pair.getRight(), null));
                } else if (int.class.isAssignableFrom(pair.getRight().getClass()) || Integer.class.isAssignableFrom(pair.getRight().getClass())) {
                    list.add(new IntegerListEntry(pair.getLeft(), (int) pair.getRight(), null));
                } else if (long.class.isAssignableFrom(pair.getRight().getClass()) || Long.class.isAssignableFrom(pair.getRight().getClass())) {
                    list.add(new LongListEntry(pair.getLeft(), (long) pair.getRight(), null));
                } else if (float.class.isAssignableFrom(pair.getRight().getClass()) || Float.class.isAssignableFrom(pair.getRight().getClass())) {
                    list.add(new FloatListEntry(pair.getLeft(), (float) pair.getRight(), null));
                } else if (double.class.isAssignableFrom(pair.getRight().getClass()) || Double.class.isAssignableFrom(pair.getRight().getClass())) {
                    list.add(new DoubleListEntry(pair.getLeft(), (double) pair.getRight(), null));
                } else {
                    throw new IllegalArgumentException("Unsupported Type (" + pair.getLeft() + "): " + pair.getRight().getClass().getSimpleName());
                }
            }
            list.forEach(entry -> entry.screen = this);
            tabbedEntries.put(tab, list);
        });
        this.nextTabIndex = 0;
        this.selectedTabIndex = 0;
        this.confirmSave = confirmSave;
        this.edited = false;
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        this.tabs = tabbedEntries.keySet().stream().map(s -> new Pair<>(s, textRenderer.getStringWidth(I18n.translate(s)) + 8)).collect(Collectors.toList());
        this.tabsScrollProgress = 0d;
        this.tabButtons = Lists.newArrayList();
        this.displayErrors = displayErrors;
        this.categoryBackgroundLocation = categoryBackgroundLocation;
    }
    
    @Override
    public void tick() {
        super.tick();
        for(Element child : children())
            if (child instanceof Tickable)
                ((Tickable) child).tick();
    }
    
    public Identifier getBackgroundLocation() {
        if (categoryBackgroundLocation.containsKey(Lists.newArrayList(tabbedEntries.keySet()).get(selectedTabIndex)))
            return categoryBackgroundLocation.get(Lists.newArrayList(tabbedEntries.keySet()).get(selectedTabIndex));
        return defaultBackgroundLocation;
    }
    
    public boolean isSmoothScrollingList() {
        return smoothScrollingList;
    }
    
    public void setSmoothScrollingList(boolean smoothScrollingList) {
        this.smoothScrollingList = smoothScrollingList;
    }
    
    public boolean isSmoothScrollingTabs() {
        return smoothScrollingTabs;
    }
    
    public void setSmoothScrollingTabs(boolean smoothScrolling) {
        this.smoothScrollingTabs = smoothScrolling;
    }
    
    public boolean isEdited() {
        return edited;
    }
    
    public void setEdited(boolean edited) {
        this.edited = edited;
        buttonQuit.setMessage(edited ? I18n.translate("text.cloth-config.cancel_discard") : I18n.translate("gui.cancel"));
        buttonSave.active = edited;
    }
    
    @Override
    protected void init() {
        super.init();
        this.children.clear();
        this.tabButtons.clear();
        if (listWidget != null)
            tabbedEntries.put(tabs.get(selectedTabIndex).getLeft(), listWidget.children());
        selectedTabIndex = nextTabIndex;
        children.add(listWidget = new ListWidget(minecraft, width, height, 70, height - 32, getBackgroundLocation()));
        listWidget.setSmoothScrolling(this.smoothScrollingList);
        if (tabbedEntries.size() > selectedTabIndex)
            Lists.newArrayList(tabbedEntries.values()).get(selectedTabIndex).forEach(entry -> listWidget.children().add(entry));
        addButton(buttonQuit = new ButtonWidget(width / 2 - 154, height - 26, 150, 20, edited ? I18n.translate("text.cloth-config.cancel_discard") : I18n.translate("gui.cancel"), widget -> {
            if (confirmSave && edited)
                minecraft.openScreen(new ConfirmScreen(new QuitSaveConsumer(), new TranslatableComponent("text.cloth-config.quit_config"), new TranslatableComponent("text.cloth-config.quit_config_sure"), I18n.translate("text.cloth-config.quit_discard"), I18n.translate("gui.cancel")));
            else
                minecraft.openScreen(parent);
        }));
        addButton(buttonSave = new AbstractPressableButtonWidget(width / 2 + 4, height - 26, 150, 20, "") {
            @Override
            public void onPress() {
                Map<String, List<Pair<String, Object>>> map = Maps.newLinkedHashMap();
                tabbedEntries.forEach((s, abstractListEntries) -> {
                    List list = abstractListEntries.stream().map(entry -> new Pair(entry.getFieldName(), entry.getObject())).collect(Collectors.toList());
                    map.put(s, list);
                });
                for(List<AbstractListEntry> entries : Lists.newArrayList(tabbedEntries.values()))
                    for(AbstractListEntry entry : entries)
                        entry.save();
                onSave(map);
                ClothConfigScreen.this.minecraft.openScreen(parent);
            }
            
            @Override
            public void render(int int_1, int int_2, float float_1) {
                boolean hasErrors = false;
                if (displayErrors)
                    for(List<AbstractListEntry> entries : Lists.newArrayList(tabbedEntries.values())) {
                        for(AbstractListEntry entry : entries)
                            if (entry.getError().isPresent()) {
                                hasErrors = true;
                                break;
                            }
                        if (hasErrors)
                            break;
                    }
                active = edited && !hasErrors;
                setMessage(displayErrors && hasErrors ? I18n.translate("text.cloth-config.error_cannot_save") : I18n.translate("text.cloth-config.save_and_done"));
                super.render(int_1, int_2, float_1);
            }
        });
        buttonSave.active = edited;
        tabsBounds = new Rectangle(0, 41, width, 24);
        tabsLeftBounds = new Rectangle(0, 41, 18, 24);
        tabsRightBounds = new Rectangle(width - 18, 41, 18, 24);
        children.add(buttonLeftTab = new AbstractPressableButtonWidget(4, 44, 12, 18, "") {
            @Override
            public void onPress() {
                tabsScrollProgress = Integer.MIN_VALUE;
                tabsScrollVelocity = 0d;
                clampTabsScrolled();
            }
            
            @Override
            public void renderButton(int int_1, int int_2, float float_1) {
                minecraft.getTextureManager().bindTexture(CONFIG_TEX);
                GlStateManager.color4f(1.0F, 1.0F, 1.0F, this.alpha);
                int int_3 = this.getYImage(this.isHovered());
                GlStateManager.enableBlend();
                GlStateManager.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
                GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
                this.blit(x, y, 12, 18 * int_3, width, height);
            }
        });
        int j = 0;
        for(Pair<String, Integer> tab : tabs) {
            tabButtons.add(new ClothConfigTabButton(this, j, -100, 43, tab.getRight(), 20, I18n.translate(tab.getLeft())));
            j++;
        }
        tabButtons.forEach(children::add);
        children.add(buttonRightTab = new AbstractPressableButtonWidget(width - 16, 44, 12, 18, "") {
            @Override
            public void onPress() {
                tabsScrollProgress = Integer.MAX_VALUE;
                tabsScrollVelocity = 0d;
                clampTabsScrolled();
            }
            
            @Override
            public void renderButton(int int_1, int int_2, float float_1) {
                minecraft.getTextureManager().bindTexture(CONFIG_TEX);
                GlStateManager.color4f(1.0F, 1.0F, 1.0F, this.alpha);
                int int_3 = this.getYImage(this.isHovered());
                GlStateManager.enableBlend();
                GlStateManager.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
                GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
                this.blit(x, y, 0, 18 * int_3, width, height);
            }
        });
    }
    
    @Override
    public boolean mouseScrolled(double double_1, double double_2, double double_3) {
        if (tabsBounds.contains(double_1, double_2) && !tabsLeftBounds.contains(double_1, double_2) && !tabsRightBounds.contains(double_1, double_2) && double_3 != 0d) {
            if (double_3 < 0)
                tabsScrollVelocity += 16;
            if (double_3 > 0)
                tabsScrollVelocity -= 16;
            return true;
        }
        return super.mouseScrolled(double_1, double_2, double_3);
    }
    
    public double getTabsMaximumScrolled() {
        if (tabsMaximumScrolled == -1d) {
            AtomicDouble d = new AtomicDouble();
            tabs.forEach(pair -> d.addAndGet(pair.getRight() + 2));
            tabsMaximumScrolled = d.get();
        }
        return tabsMaximumScrolled;
    }
    
    public void resetTabsMaximumScrolled() {
        tabsMaximumScrolled = -1d;
        tabsScrollVelocity = 0f;
    }
    
    public void clampTabsScrolled() {
        int xx = 0;
        for(ClothConfigTabButton tabButton : tabButtons)
            xx += tabButton.getWidth() + 2;
        if (xx > width - 40)
            tabsScrollProgress = MathHelper.clamp(tabsScrollProgress, 0, getTabsMaximumScrolled() - width + 40);
        else
            tabsScrollProgress = 0d;
    }
    
    @Override
    public void render(int int_1, int int_2, float float_1) {
        if (smoothScrollingTabs) {
            double change = tabsScrollVelocity * 0.2f;
            if (change != 0) {
                if (change > 0 && change < .2)
                    change = .2;
                else if (change < 0 && change > -.2)
                    change = -.2;
                tabsScrollProgress += change;
                tabsScrollVelocity -= change;
                if (change > 0 == tabsScrollVelocity < 0)
                    tabsScrollVelocity = 0f;
                clampTabsScrolled();
            }
        } else {
            tabsScrollProgress += tabsScrollVelocity;
            tabsScrollVelocity = 0d;
            clampTabsScrolled();
        }
        int xx = 20 - (int) tabsScrollProgress;
        for(ClothConfigTabButton tabButton : tabButtons) {
            tabButton.x = xx;
            xx += tabButton.getWidth() + 2;
        }
        buttonLeftTab.active = tabsScrollProgress > 0d;
        buttonRightTab.active = tabsScrollProgress < getTabsMaximumScrolled() - width + 40;
        renderDirtBackground(0);
        listWidget.render(int_1, int_2, float_1);
        overlayBackground(tabsBounds, 32, 32, 32, 255, 255);
        
        drawCenteredString(minecraft.textRenderer, title, width / 2, 18, -1);
        tabButtons.forEach(widget -> widget.render(int_1, int_2, float_1));
        overlayBackground(tabsLeftBounds, 64, 64, 64, 255, 255);
        overlayBackground(tabsRightBounds, 64, 64, 64, 255, 255);
        drawShades();
        buttonLeftTab.render(int_1, int_2, float_1);
        buttonRightTab.render(int_1, int_2, float_1);
        
        if (displayErrors && isEditable()) {
            List<String> errors = Lists.newArrayList();
            for(List<AbstractListEntry> entries : Lists.newArrayList(tabbedEntries.values()))
                for(AbstractListEntry entry : entries)
                    if (entry.getError().isPresent())
                        errors.add(entry.getError().get());
            if (errors.size() > 0) {
                minecraft.getTextureManager().bindTexture(CONFIG_TEX);
                GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                blit(10, 10, 0, 54, 3, 11);
                if (errors.size() == 1)
                    drawString(minecraft.textRenderer, "§c" + errors.get(0), 18, 12, -1);
                else
                    drawString(minecraft.textRenderer, "§c" + I18n.translate("text.cloth-config.multi_error"), 18, 12, -1);
            }
        } else if (!isEditable()) {
            minecraft.getTextureManager().bindTexture(CONFIG_TEX);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            blit(10, 10, 0, 54, 3, 11);
            drawString(minecraft.textRenderer, "§c" + I18n.translate("text.cloth-config.not_editable"), 18, 12, -1);
        }
        super.render(int_1, int_2, float_1);
        queuedTooltips.forEach(queuedTooltip -> renderTooltip(queuedTooltip.getText(), queuedTooltip.getX(), queuedTooltip.getY()));
        queuedTooltips.clear();
    }
    
    public void queueTooltip(QueuedTooltip queuedTooltip) {
        queuedTooltips.add(queuedTooltip);
    }
    
    private void drawShades() {
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ZERO, DestFactor.ONE);
        GlStateManager.disableAlphaTest();
        GlStateManager.shadeModel(7425);
        GlStateManager.disableTexture();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBufferBuilder();
        buffer.begin(7, VertexFormats.POSITION_UV_COLOR);
        buffer.vertex(tabsBounds.getMinX() + 20, tabsBounds.getMinY() + 4, 0.0D).texture(0.0D, 1.0D).color(0, 0, 0, 0).next();
        buffer.vertex(tabsBounds.getMaxX() - 20, tabsBounds.getMinY() + 4, 0.0D).texture(1.0D, 1.0D).color(0, 0, 0, 0).next();
        buffer.vertex(tabsBounds.getMaxX() - 20, tabsBounds.getMinY(), 0.0D).texture(1.0D, 0.0D).color(0, 0, 0, 255).next();
        buffer.vertex(tabsBounds.getMinX() + 20, tabsBounds.getMinY(), 0.0D).texture(0.0D, 0.0D).color(0, 0, 0, 255).next();
        tessellator.draw();
        buffer.begin(7, VertexFormats.POSITION_UV_COLOR);
        buffer.vertex(tabsBounds.getMinX() + 20, tabsBounds.getMaxY(), 0.0D).texture(0.0D, 1.0D).color(0, 0, 0, 255).next();
        buffer.vertex(tabsBounds.getMaxX() - 20, tabsBounds.getMaxY(), 0.0D).texture(1.0D, 1.0D).color(0, 0, 0, 255).next();
        buffer.vertex(tabsBounds.getMaxX() - 20, tabsBounds.getMaxY() - 4, 0.0D).texture(1.0D, 0.0D).color(0, 0, 0, 0).next();
        buffer.vertex(tabsBounds.getMinX() + 20, tabsBounds.getMaxY() - 4, 0.0D).texture(0.0D, 0.0D).color(0, 0, 0, 0).next();
        tessellator.draw();
        GlStateManager.enableTexture();
        GlStateManager.shadeModel(7424);
        GlStateManager.enableAlphaTest();
        GlStateManager.disableBlend();
    }
    
    protected void overlayBackground(Rectangle rect, int red, int green, int blue, int startAlpha, int endAlpha) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBufferBuilder();
        minecraft.getTextureManager().bindTexture(getBackgroundLocation());
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        float f = 32.0F;
        buffer.begin(7, VertexFormats.POSITION_UV_COLOR);
        buffer.vertex(rect.getMinX(), rect.getMaxY(), 0.0D).texture(rect.getMinX() / 32.0D, rect.getMaxY() / 32.0D).color(red, green, blue, endAlpha).next();
        buffer.vertex(rect.getMaxX(), rect.getMaxY(), 0.0D).texture(rect.getMaxX() / 32.0D, rect.getMaxY() / 32.0D).color(red, green, blue, endAlpha).next();
        buffer.vertex(rect.getMaxX(), rect.getMinY(), 0.0D).texture(rect.getMaxX() / 32.0D, rect.getMinY() / 32.0D).color(red, green, blue, startAlpha).next();
        buffer.vertex(rect.getMinX(), rect.getMinY(), 0.0D).texture(rect.getMinX() / 32.0D, rect.getMinY() / 32.0D).color(red, green, blue, startAlpha).next();
        tessellator.draw();
    }
    
    @Override
    public boolean keyPressed(int int_1, int int_2, int int_3) {
        if (int_1 == 256 && this.shouldCloseOnEsc()) {
            if (confirmSave && edited)
                minecraft.openScreen(new ConfirmScreen(new QuitSaveConsumer(), new TranslatableComponent("text.cloth-config.quit_config"), new TranslatableComponent("text.cloth-config.quit_config_sure"), I18n.translate("text.cloth-config.quit_discard"), I18n.translate("gui.cancel")));
            else
                minecraft.openScreen(parent);
            return true;
        }
        return super.keyPressed(int_1, int_2, int_3);
    }
    
    public abstract void onSave(Map<String, List<Pair<String, Object>>> o);
    
    public boolean isEditable() {
        return true;
    }
    
    public static class Builder implements ConfigScreenBuilder {
        private Screen parentScreen;
        private Map<String, List<Pair<String, Object>>> dataMap;
        private String title;
        private Consumer<ConfigScreenBuilder.SavedConfig> onSave;
        private boolean confirmSave;
        private boolean editable;
        private boolean displayErrors;
        private boolean smoothScrollingTabs, smoothScrollingList;
        private Identifier backgroundTexture;
        private Map<String, Identifier> categoryBackgroundMap;
        
        public Builder(Screen parentScreen, String title, Consumer<ConfigScreenBuilder.SavedConfig> onSave) {
            this.parentScreen = parentScreen;
            this.title = I18n.translate(title);
            this.dataMap = Maps.newLinkedHashMap();
            this.onSave = onSave;
            this.editable = true;
            this.confirmSave = true;
            this.displayErrors = true;
            this.smoothScrollingTabs = true;
            this.smoothScrollingList = true;
            this.backgroundTexture = DrawableHelper.BACKGROUND_LOCATION;
            this.categoryBackgroundMap = Maps.newHashMap();
        }
        
        @Override
        public Identifier getBackgroundTexture() {
            return backgroundTexture;
        }
        
        @Override
        public void setBackgroundTexture(Identifier backgroundTexture) {
            this.backgroundTexture = backgroundTexture;
        }
        
        @Override
        public Screen getParentScreen() {
            return parentScreen;
        }
        
        @Override
        public void setParentScreen(Screen parent) {
            parentScreen = parent;
        }
        
        @Override
        public String getTitle() {
            return title;
        }
        
        @Override
        public void setTitle(String title) {
            this.title = title == null ? "" : title;
        }
        
        @Override
        public Consumer<ConfigScreenBuilder.SavedConfig> getOnSave() {
            return onSave;
        }
        
        @Override
        public void setOnSave(Consumer<ConfigScreenBuilder.SavedConfig> onSave) {
            this.onSave = onSave;
        }
        
        @Override
        public List<String> getCategories() {
            return Collections.unmodifiableList(Lists.newArrayList(dataMap.keySet()));
        }
        
        @Override
        public boolean hasCategory(String category) {
            return getCategories().contains(category);
        }
        
        @Override
        public CategoryBuilder addCategory(String category) {
            if (hasCategory(category))
                throw new IllegalArgumentException("The category is already added!");
            dataMap.put(category, Lists.newLinkedList());
            return getCategory(category);
        }
        
        @Override
        public CategoryBuilder getCategory(String category) {
            if (!hasCategory(category))
                throw new IllegalArgumentException("This category doesn't exist!");
            return new Category(category, this);
        }
        
        @Override
        public boolean isEditable() {
            return editable;
        }
        
        @Override
        public void setEditable(boolean editable) {
            this.editable = editable;
        }
        
        @Override
        public void removeCategory(String category) {
            if (!hasCategory(category))
                throw new IllegalArgumentException("The category doesn't exist!");
            dataMap.remove(category);
        }
        
        @Override
        public void addOption(String category, String key, Object object) {
            if (!hasCategory(category))
                throw new IllegalArgumentException("The category doesn't exist!");
            dataMap.get(category).add(new Pair<>(key, object));
        }
        
        @Override
        public void addOption(String category, AbstractListEntry entry) {
            if (!hasCategory(category))
                throw new IllegalArgumentException("The category doesn't exist!");
            dataMap.get(category).add(new Pair<>(entry.getFieldName(), entry));
        }
        
        @SuppressWarnings("deprecation")
        @Override
        public List<Pair<String, Object>> getOptions(String category) {
            if (!hasCategory(category))
                throw new IllegalArgumentException("The category doesn't exist!");
            return Collections.unmodifiableList(dataMap.get(category));
        }
        
        @Override
        public void setDoesConfirmSave(boolean confirmSave) {
            this.confirmSave = confirmSave;
        }
        
        @Override
        public boolean doesConfirmSave() {
            return confirmSave;
        }
        
        @Deprecated
        @Override
        public Map<String, List<Pair<String, Object>>> getDataMap() {
            return dataMap;
        }
        
        @Override
        public boolean isSmoothScrollingTabs() {
            return smoothScrollingTabs;
        }
        
        @Override
        public void setSmoothScrollingTabs(boolean smoothScrolling) {
            this.smoothScrollingTabs = smoothScrolling;
        }
        
        @Override
        public boolean isSmoothScrollingList() {
            return smoothScrollingList;
        }
        
        @Override
        public void setSmoothScrollingList(boolean smoothScrollingList) {
            this.smoothScrollingList = smoothScrollingList;
        }
        
        @Override
        public void setShouldProcessErrors(boolean processErrors) {
            this.displayErrors = processErrors;
        }
        
        @Override
        public boolean shouldProcessErrors() {
            return displayErrors;
        }
        
        @SuppressWarnings("deprecation")
        @Override
        public Map<String, Identifier> getCategoryBackgroundMap() {
            return categoryBackgroundMap;
        }
        
        @Override
        public ClothConfigScreen build(Consumer<ClothConfigScreen> afterInitConsumer) {
            ClothConfigScreen screen = new ClothConfigScreen(parentScreen, title, dataMap, confirmSave, displayErrors, smoothScrollingList, backgroundTexture, categoryBackgroundMap) {
                @Override
                public void onSave(Map<String, List<Pair<String, Object>>> o) {
                    if (getOnSave() != null)
                        getOnSave().accept(new SavedConfig(o));
                }
                
                @Override
                protected void init() {
                    super.init();
                    if (afterInitConsumer != null)
                        afterInitConsumer.accept(this);
                }
                
                @Override
                public boolean isEditable() {
                    return Builder.this.isEditable();
                }
            };
            screen.setSmoothScrollingTabs(isSmoothScrollingTabs());
            return screen;
        }
        
        @Override
        public ClothConfigScreen build() {
            return build(null);
        }
        
        @Override
        public Identifier getCategoryBackgroundTexture(String category) {
            return getCategory(category).getBackgroundTexture();
        }
        
        @Override
        public Identifier getNullableCategoryBackgroundTexture(String category) {
            return getCategory(category).getNullableBackgroundTexture();
        }
        
        public static class Category implements CategoryBuilder {
            private String category;
            private ConfigScreenBuilder builder;
            
            public Category(String category, ConfigScreenBuilder builder) {
                this.category = category;
                this.builder = builder;
            }
            
            @SuppressWarnings("deprecation")
            @Override
            public Identifier getBackgroundTexture() {
                return builder.getCategoryBackgroundMap().getOrDefault(category, builder.getBackgroundTexture());
            }
            
            @SuppressWarnings("deprecation")
            @Override
            public void setBackgroundTexture(Identifier backgroundTexture) {
                builder.getCategoryBackgroundMap().put(category, backgroundTexture);
            }
            
            @SuppressWarnings("deprecation")
            @Override
            public Identifier getNullableBackgroundTexture() {
                return builder.getCategoryBackgroundMap().get(category);
            }
            
            @SuppressWarnings("deprecation")
            @Override
            public List<Pair<String, Object>> getOptions() {
                return builder.getOptions(category);
            }
            
            @Override
            public CategoryBuilder addOption(AbstractListEntry entry) {
                builder.addOption(category, entry);
                return this;
            }
            
            @Deprecated
            @Override
            public CategoryBuilder addOption(String key, Object object) {
                builder.addOption(category, key, object);
                return this;
            }
            
            @Override
            public ConfigScreenBuilder removeFromParent() {
                builder.removeCategory(category);
                return builder;
            }
            
            @Override
            public ConfigScreenBuilder parent() {
                return builder;
            }
            
            @Override
            public String getName() {
                return category;
            }
            
            @Override
            public boolean exists() {
                return builder.hasCategory(category);
            }
        }
        
        public static class SavedConfig implements ConfigScreenBuilder.SavedConfig {
            private Map<String, List<Pair<String, Object>>> map;
            private Map<String, SavedCategory> categories;
            
            public SavedConfig(Map<String, List<Pair<String, Object>>> map) {
                this.map = map;
                categories = Maps.newLinkedHashMap();
                map.forEach((s, pairs) -> categories.put(s, new SavedCategory(this, s)));
            }
            
            @Override
            public boolean containsCategory(String category) {
                return categories.containsKey(category);
            }
            
            @Override
            public ConfigScreenBuilder.SavedCategory getCategory(String category) {
                return categories.getOrDefault(category, new SavedCategory(this, category));
            }
            
            @Override
            public List<ConfigScreenBuilder.SavedCategory> getCategories() {
                return Lists.newArrayList(categories.values());
            }
        }
        
        public static class SavedCategory implements ConfigScreenBuilder.SavedCategory {
            private SavedConfig savedConfig;
            private String category;
            private List<ConfigScreenBuilder.SavedOption> options;
            
            public SavedCategory(SavedConfig savedConfig, String category) {
                this.savedConfig = savedConfig;
                this.category = category;
                this.options = getOptionPairs().stream().map(pair -> new SavedOption(pair.getLeft(), pair.getRight())).collect(Collectors.toList());
            }
            
            @Override
            public boolean exists() {
                return savedConfig.map.containsKey(category);
            }
            
            @Override
            public String getName() {
                return category;
            }
            
            @SuppressWarnings("deprecation")
            @Override
            public List<Pair<String, Object>> getOptionPairs() {
                return savedConfig.map.getOrDefault(category, Collections.emptyList());
            }
            
            @Override
            public List<ConfigScreenBuilder.SavedOption> getOptions() {
                return options;
            }
            
            @Override
            public Optional<ConfigScreenBuilder.SavedOption> getOption(String fieldKey) {
                return options.stream().filter(savedOption -> savedOption.getFieldKey().equals(fieldKey)).findAny();
            }
            
        }
        
        public static class SavedOption implements ConfigScreenBuilder.SavedOption {
            private String key;
            private Object value;
            
            public SavedOption(String key, Object value) {
                this.key = key;
                this.value = value;
            }
            
            @Override
            public String getFieldKey() {
                return key;
            }
            
            @Override
            public Object getValue() {
                return value;
            }
        }
        
    }
    
    public static abstract class ListEntry extends AbstractListEntry {
        private String fieldName;
        private boolean editable = true;
        
        public ListEntry(String fieldName) {
            this.fieldName = fieldName;
        }
        
        public boolean isEditable() {
            return getScreen().isEditable() && editable;
        }
        
        public void setEditable(boolean editable) {
            this.editable = editable;
        }
        
        @Override
        public String getFieldName() {
            return fieldName;
        }
    }
    
    public static abstract class AbstractListEntry extends DynamicElementListWidget.ElementEntry<AbstractListEntry> {
        private ClothConfigScreen screen;
        
        public abstract String getFieldName();
        
        public abstract Object getObject();
        
        public Optional<String> getError() {
            return Optional.empty();
        }
        
        public abstract Optional<Object> getDefaultValue();
        
        public final ListWidget getParent() {
            return screen.listWidget;
        }
        
        public final ClothConfigScreen getScreen() {
            return screen;
        }
        
        public final void setScreen(ClothConfigScreen screen) {
            this.screen = screen;
        }
        
        public void save() {
        
        }
        
        @Override
        public int getItemHeight() {
            return 24;
        }
    }
    
    private class QuitSaveConsumer implements BooleanConsumer {
        @Override
        public void accept(boolean t) {
            if (!t)
                minecraft.openScreen(ClothConfigScreen.this);
            else
                minecraft.openScreen(parent);
            return;
        }
    }
    
    public class ListWidget extends DynamicElementListWidget {
        
        public ListWidget(MinecraftClient client, int width, int height, int top, int bottom, Identifier backgroundLocation) {
            super(client, width, height, top, bottom, backgroundLocation);
            visible = false;
        }
        
        @Override
        public int getItemWidth() {
            return width - 80;
        }
        
        public ClothConfigScreen getScreen() {
            return ClothConfigScreen.this;
        }
        
        @Override
        protected int getScrollbarPosition() {
            return width - 36;
        }
        
        protected final void clearStuff() {
            this.clearItems();
        }
    }
    
}
