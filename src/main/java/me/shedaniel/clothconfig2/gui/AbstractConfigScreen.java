package me.shedaniel.clothconfig2.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Matrix4f;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import me.shedaniel.clothconfig2.api.*;
import me.shedaniel.clothconfig2.gui.entries.KeyCodeEntry;
import me.shedaniel.math.Rectangle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class AbstractConfigScreen extends Screen implements ConfigScreen {
    protected static final ResourceLocation CONFIG_TEX = new ResourceLocation("cloth-config2", "textures/gui/cloth_config.png");
    private boolean legacyEdited = false;
    private final ResourceLocation backgroundLocation;
    protected boolean legacyRequiresRestart = false;
    protected boolean confirmSave;
    protected final Screen parent;
    private boolean alwaysShowTabs = false;
    private boolean transparentBackground = false;
    @Nullable
    private Component defaultFallbackCategory = null;
    public int selectedCategoryIndex = 0;
    private boolean editable = true;
    private KeyCodeEntry focusedBinding;
    private ModifierKeyCode startedKeyCode = null;
    private final List<Tooltip> tooltips = Lists.newArrayList();
    @Nullable
    private Runnable savingRunnable = null;
    @Nullable
    protected Consumer<Screen> afterInitConsumer = null;
    
    protected AbstractConfigScreen(Screen parent, Component title, ResourceLocation backgroundLocation) {
        super(title);
        this.parent = parent;
        this.backgroundLocation = backgroundLocation;
    }
    
    @Override
    public void setSavingRunnable(@Nullable Runnable savingRunnable) {
        this.savingRunnable = savingRunnable;
    }
    
    @Override
    public void setAfterInitConsumer(@Nullable Consumer<Screen> afterInitConsumer) {
        this.afterInitConsumer = afterInitConsumer;
    }
    
    @Override
    public ResourceLocation getBackgroundLocation() {
        return backgroundLocation;
    }
    
    @Override
    public boolean isRequiresRestart() {
        if (legacyRequiresRestart) return true;
        for (List<AbstractConfigEntry<?>> entries : getCategorizedEntries().values()) {
            for (AbstractConfigEntry<?> entry : entries) {
                if (!entry.getConfigError().isPresent() && entry.isEdited() && entry.isRequiresRestart()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public abstract Map<Component, List<AbstractConfigEntry<?>>> getCategorizedEntries();
    
    @Override
    public boolean isEdited() {
        if (legacyEdited) return true;
        for (List<AbstractConfigEntry<?>> entries : getCategorizedEntries().values()) {
            for (AbstractConfigEntry<?> entry : entries) {
                if (entry.isEdited()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Override #isEdited please
     */
    @Override
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public void setEdited(boolean edited) {
        this.legacyEdited = edited;
    }
    
    /**
     * Override #isEdited please
     */
    @Override
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public void setEdited(boolean edited, boolean legacyRequiresRestart) {
        setEdited(edited);
        if (!this.legacyRequiresRestart && legacyRequiresRestart)
            this.legacyRequiresRestart = legacyRequiresRestart;
    }
    
    public boolean isShowingTabs() {
        return isAlwaysShowTabs() || getCategorizedEntries().size() > 1;
    }
    
    public boolean isAlwaysShowTabs() {
        return alwaysShowTabs;
    }
    
    @ApiStatus.Internal
    public void setAlwaysShowTabs(boolean alwaysShowTabs) {
        this.alwaysShowTabs = alwaysShowTabs;
    }
    
    public boolean isTransparentBackground() {
        return transparentBackground && Minecraft.getInstance().level != null;
    }
    
    @ApiStatus.Internal
    public void setTransparentBackground(boolean transparentBackground) {
        this.transparentBackground = transparentBackground;
    }
    
    public Component getFallbackCategory() {
        if (defaultFallbackCategory != null)
            return defaultFallbackCategory;
        return getCategorizedEntries().keySet().iterator().next();
    }
    
    @ApiStatus.Internal
    public void setFallbackCategory(@Nullable Component defaultFallbackCategory) {
        this.defaultFallbackCategory = defaultFallbackCategory;
        List<Component> categories = Lists.newArrayList(getCategorizedEntries().keySet());
        for (int i = 0; i < categories.size(); i++) {
            Component category = categories.get(i);
            if (category.equals(getFallbackCategory())) {
                this.selectedCategoryIndex = i;
                break;
            }
        }
    }
    
    @Override
    public void saveAll(boolean openOtherScreens) {
        for (List<AbstractConfigEntry<?>> entries : Lists.newArrayList(getCategorizedEntries().values()))
            for (AbstractConfigEntry<?> entry : entries)
                entry.save();
        save();
        setEdited(false);
        if (openOtherScreens) {
            if (isRequiresRestart())
                AbstractConfigScreen.this.minecraft.setScreen(new ClothRequiresRestartScreen(parent));
            else
                AbstractConfigScreen.this.minecraft.setScreen(parent);
        }
        this.legacyRequiresRestart = false;
    }
    
    public void save() {
        Optional.ofNullable(this.savingRunnable).ifPresent(Runnable::run);
    }
    
    public boolean isEditable() {
        return editable;
    }
    
    @ApiStatus.Internal
    public void setEditable(boolean editable) {
        this.editable = editable;
    }
    
    @ApiStatus.Internal
    public void setConfirmSave(boolean confirmSave) {
        this.confirmSave = confirmSave;
    }
    
    public KeyCodeEntry getFocusedBinding() {
        return focusedBinding;
    }
    
    @ApiStatus.Internal
    public void setFocusedBinding(KeyCodeEntry focusedBinding) {
        this.focusedBinding = focusedBinding;
        if (focusedBinding != null) {
            startedKeyCode = this.focusedBinding.getValue();
            startedKeyCode.setKeyCodeAndModifier(InputConstants.UNKNOWN, Modifier.none());
        } else
            startedKeyCode = null;
    }
    
    @Override
    public boolean mouseReleased(double double_1, double double_2, int int_1) {
        if (this.focusedBinding != null && this.startedKeyCode != null && !this.startedKeyCode.isUnknown() && focusedBinding.isAllowMouse()) {
            focusedBinding.setValue(startedKeyCode);
            setFocusedBinding(null);
            return true;
        }
        return super.mouseReleased(double_1, double_2, int_1);
    }
    
    @Override
    public boolean keyReleased(int int_1, int int_2, int int_3) {
        if (this.focusedBinding != null && this.startedKeyCode != null && focusedBinding.isAllowKey()) {
            focusedBinding.setValue(startedKeyCode);
            setFocusedBinding(null);
            return true;
        }
        return super.keyReleased(int_1, int_2, int_3);
    }
    
    @Override
    public boolean mouseClicked(double double_1, double double_2, int int_1) {
        if (this.focusedBinding != null && this.startedKeyCode != null && focusedBinding.isAllowMouse()) {
            if (startedKeyCode.isUnknown())
                startedKeyCode.setKeyCode(InputConstants.Type.MOUSE.getOrCreate(int_1));
            else if (focusedBinding.isAllowModifiers()) {
                if (startedKeyCode.getType() == InputConstants.Type.KEYSYM) {
                    int code = startedKeyCode.getKeyCode().getValue();
                    if (Minecraft.ON_OSX ? (code == 343 || code == 347) : (code == 341 || code == 345)) {
                        Modifier modifier = startedKeyCode.getModifier();
                        startedKeyCode.setModifier(Modifier.of(modifier.hasAlt(), true, modifier.hasShift()));
                        startedKeyCode.setKeyCode(InputConstants.Type.MOUSE.getOrCreate(int_1));
                        return true;
                    } else if (code == 344 || code == 340) {
                        Modifier modifier = startedKeyCode.getModifier();
                        startedKeyCode.setModifier(Modifier.of(modifier.hasAlt(), modifier.hasControl(), true));
                        startedKeyCode.setKeyCode(InputConstants.Type.MOUSE.getOrCreate(int_1));
                        return true;
                    } else if (code == 342 || code == 346) {
                        Modifier modifier = startedKeyCode.getModifier();
                        startedKeyCode.setModifier(Modifier.of(true, modifier.hasControl(), modifier.hasShift()));
                        startedKeyCode.setKeyCode(InputConstants.Type.MOUSE.getOrCreate(int_1));
                        return true;
                    }
                }
            }
            return true;
        } else {
            if (this.focusedBinding != null)
                return true;
            return super.mouseClicked(double_1, double_2, int_1);
        }
    }
    
    @Override
    public boolean keyPressed(int int_1, int int_2, int int_3) {
        if (this.focusedBinding != null && (focusedBinding.isAllowKey() || int_1 == 256)) {
            if (int_1 != 256) {
                if (startedKeyCode.isUnknown())
                    startedKeyCode.setKeyCode(InputConstants.getKey(int_1, int_2));
                else if (focusedBinding.isAllowModifiers()) {
                    if (startedKeyCode.getType() == InputConstants.Type.KEYSYM) {
                        int code = startedKeyCode.getKeyCode().getValue();
                        if (Minecraft.ON_OSX ? (code == 343 || code == 347) : (code == 341 || code == 345)) {
                            Modifier modifier = startedKeyCode.getModifier();
                            startedKeyCode.setModifier(Modifier.of(modifier.hasAlt(), true, modifier.hasShift()));
                            startedKeyCode.setKeyCode(InputConstants.getKey(int_1, int_2));
                            return true;
                        } else if (code == 344 || code == 340) {
                            Modifier modifier = startedKeyCode.getModifier();
                            startedKeyCode.setModifier(Modifier.of(modifier.hasAlt(), modifier.hasControl(), true));
                            startedKeyCode.setKeyCode(InputConstants.getKey(int_1, int_2));
                            return true;
                        } else if (code == 342 || code == 346) {
                            Modifier modifier = startedKeyCode.getModifier();
                            startedKeyCode.setModifier(Modifier.of(true, modifier.hasControl(), modifier.hasShift()));
                            startedKeyCode.setKeyCode(InputConstants.getKey(int_1, int_2));
                            return true;
                        }
                    }
                    if (Minecraft.ON_OSX ? (int_1 == 343 || int_1 == 347) : (int_1 == 341 || int_1 == 345)) {
                        Modifier modifier = startedKeyCode.getModifier();
                        startedKeyCode.setModifier(Modifier.of(modifier.hasAlt(), true, modifier.hasShift()));
                        return true;
                    } else if (int_1 == 344 || int_1 == 340) {
                        Modifier modifier = startedKeyCode.getModifier();
                        startedKeyCode.setModifier(Modifier.of(modifier.hasAlt(), modifier.hasControl(), true));
                        return true;
                    } else if (int_1 == 342 || int_1 == 346) {
                        Modifier modifier = startedKeyCode.getModifier();
                        startedKeyCode.setModifier(Modifier.of(true, modifier.hasControl(), modifier.hasShift()));
                        return true;
                    }
                }
            } else {
                focusedBinding.setValue(ModifierKeyCode.unknown());
                setFocusedBinding(null);
            }
            return true;
        }
        if (this.focusedBinding != null && int_1 != 256)
            return true;
        if (int_1 == 256 && this.shouldCloseOnEsc()) {
            return quit();
        }
        return super.keyPressed(int_1, int_2, int_3);
    }
    
    protected final boolean quit() {
        if (confirmSave && isEdited())
            minecraft.setScreen(new ConfirmScreen(new QuitSaveConsumer(), new TranslatableComponent("text.cloth-config.quit_config"), new TranslatableComponent("text.cloth-config.quit_config_sure"), new TranslatableComponent("text.cloth-config.quit_discard"), new TranslatableComponent("gui.cancel")));
        else
            minecraft.setScreen(parent);
        return true;
    }
    
    private class QuitSaveConsumer implements BooleanConsumer {
        @Override
        public void accept(boolean t) {
            if (!t)
                minecraft.setScreen(AbstractConfigScreen.this);
            else
                minecraft.setScreen(parent);
        }
    }
    
    @Override
    public void tick() {
        super.tick();
        boolean edited = isEdited();
        Optional.ofNullable(getQuitButton()).ifPresent(button -> button.setMessage(edited ? new TranslatableComponent("text.cloth-config.cancel_discard") : new TranslatableComponent("gui.cancel")));
        for (GuiEventListener child : children())
            if (child instanceof TickableBlockEntity)
                ((TickableBlockEntity) child).tick();
    }
    
    @Nullable
    protected AbstractWidget getQuitButton() {
        return null;
    }
    
    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        for (Tooltip tooltip : tooltips) {
            renderTooltip(matrices, tooltip.getText(), tooltip.getX(), tooltip.getY());
        }
        this.tooltips.clear();
    }
    
    @Override
    public void addTooltip(Tooltip tooltip) {
        this.tooltips.add(tooltip);
    }
    
    protected void overlayBackground(PoseStack matrices, Rectangle rect, int red, int green, int blue, int startAlpha, int endAlpha) {
        overlayBackground(matrices.last().pose(), rect, red, green, blue, startAlpha, endAlpha);
    }
    
    protected void overlayBackground(Matrix4f matrix, Rectangle rect, int red, int green, int blue, int startAlpha, int endAlpha) {
        if (isTransparentBackground())
            return;
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        minecraft.getTextureManager().bind(getBackgroundLocation());
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        buffer.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
        buffer.vertex(matrix, rect.getMinX(), rect.getMaxY(), 0.0F).uv(rect.getMinX() / 32.0F, rect.getMaxY() / 32.0F).color(red, green, blue, endAlpha).endVertex();
        buffer.vertex(matrix, rect.getMaxX(), rect.getMaxY(), 0.0F).uv(rect.getMaxX() / 32.0F, rect.getMaxY() / 32.0F).color(red, green, blue, endAlpha).endVertex();
        buffer.vertex(matrix, rect.getMaxX(), rect.getMinY(), 0.0F).uv(rect.getMaxX() / 32.0F, rect.getMinY() / 32.0F).color(red, green, blue, startAlpha).endVertex();
        buffer.vertex(matrix, rect.getMinX(), rect.getMinY(), 0.0F).uv(rect.getMinX() / 32.0F, rect.getMinY() / 32.0F).color(red, green, blue, startAlpha).endVertex();
        tessellator.end();
    }
}
