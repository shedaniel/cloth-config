package me.shedaniel.clothconfig2.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import me.shedaniel.clothconfig2.api.*;
import me.shedaniel.clothconfig2.gui.entries.KeyCodeEntry;
import me.shedaniel.math.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TickableElement;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class AbstractConfigScreen extends Screen implements ConfigScreen {
    protected static final Identifier CONFIG_TEX = new Identifier("cloth-config2", "textures/gui/cloth_config.png");
    private boolean legacyEdited = false;
    private final Identifier backgroundLocation;
    protected boolean legacyRequiresRestart = false;
    protected boolean confirmSave;
    protected final Screen parent;
    private boolean alwaysShowTabs = false;
    private boolean transparentBackground = false;
    @Nullable
    private Text defaultFallbackCategory = null;
    public int selectedCategoryIndex = 0;
    private boolean editable = true;
    private KeyCodeEntry focusedBinding;
    private ModifierKeyCode startedKeyCode = null;
    private final List<Tooltip> tooltips = Lists.newArrayList();
    @Nullable
    private Runnable savingRunnable = null;
    @Nullable
    protected Consumer<Screen> afterInitConsumer = null;
    
    protected AbstractConfigScreen(Screen parent, Text title, Identifier backgroundLocation) {
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
    public Identifier getBackgroundLocation() {
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
    
    public abstract Map<Text, List<AbstractConfigEntry<?>>> getCategorizedEntries();
    
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
        return transparentBackground && MinecraftClient.getInstance().world != null;
    }
    
    @ApiStatus.Internal
    public void setTransparentBackground(boolean transparentBackground) {
        this.transparentBackground = transparentBackground;
    }
    
    public Text getFallbackCategory() {
        if (defaultFallbackCategory != null)
            return defaultFallbackCategory;
        return getCategorizedEntries().keySet().iterator().next();
    }
    
    @ApiStatus.Internal
    public void setFallbackCategory(@Nullable Text defaultFallbackCategory) {
        this.defaultFallbackCategory = defaultFallbackCategory;
        List<Text> categories = Lists.newArrayList(getCategorizedEntries().keySet());
        for (int i = 0; i < categories.size(); i++) {
            Text category = categories.get(i);
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
                AbstractConfigScreen.this.client.openScreen(new ClothRequiresRestartScreen(parent));
            else
                AbstractConfigScreen.this.client.openScreen(parent);
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
            startedKeyCode.setKeyCodeAndModifier(InputUtil.UNKNOWN_KEY, Modifier.none());
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
                startedKeyCode.setKeyCode(InputUtil.Type.MOUSE.createFromCode(int_1));
            else if (focusedBinding.isAllowModifiers()) {
                if (startedKeyCode.getType() == InputUtil.Type.KEYSYM) {
                    int code = startedKeyCode.getKeyCode().getCode();
                    if (MinecraftClient.IS_SYSTEM_MAC ? (code == 343 || code == 347) : (code == 341 || code == 345)) {
                        Modifier modifier = startedKeyCode.getModifier();
                        startedKeyCode.setModifier(Modifier.of(modifier.hasAlt(), true, modifier.hasShift()));
                        startedKeyCode.setKeyCode(InputUtil.Type.MOUSE.createFromCode(int_1));
                        return true;
                    } else if (code == 344 || code == 340) {
                        Modifier modifier = startedKeyCode.getModifier();
                        startedKeyCode.setModifier(Modifier.of(modifier.hasAlt(), modifier.hasControl(), true));
                        startedKeyCode.setKeyCode(InputUtil.Type.MOUSE.createFromCode(int_1));
                        return true;
                    } else if (code == 342 || code == 346) {
                        Modifier modifier = startedKeyCode.getModifier();
                        startedKeyCode.setModifier(Modifier.of(true, modifier.hasControl(), modifier.hasShift()));
                        startedKeyCode.setKeyCode(InputUtil.Type.MOUSE.createFromCode(int_1));
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
                    startedKeyCode.setKeyCode(InputUtil.fromKeyCode(int_1, int_2));
                else if (focusedBinding.isAllowModifiers()) {
                    if (startedKeyCode.getType() == InputUtil.Type.KEYSYM) {
                        int code = startedKeyCode.getKeyCode().getCode();
                        if (MinecraftClient.IS_SYSTEM_MAC ? (code == 343 || code == 347) : (code == 341 || code == 345)) {
                            Modifier modifier = startedKeyCode.getModifier();
                            startedKeyCode.setModifier(Modifier.of(modifier.hasAlt(), true, modifier.hasShift()));
                            startedKeyCode.setKeyCode(InputUtil.fromKeyCode(int_1, int_2));
                            return true;
                        } else if (code == 344 || code == 340) {
                            Modifier modifier = startedKeyCode.getModifier();
                            startedKeyCode.setModifier(Modifier.of(modifier.hasAlt(), modifier.hasControl(), true));
                            startedKeyCode.setKeyCode(InputUtil.fromKeyCode(int_1, int_2));
                            return true;
                        } else if (code == 342 || code == 346) {
                            Modifier modifier = startedKeyCode.getModifier();
                            startedKeyCode.setModifier(Modifier.of(true, modifier.hasControl(), modifier.hasShift()));
                            startedKeyCode.setKeyCode(InputUtil.fromKeyCode(int_1, int_2));
                            return true;
                        }
                    }
                    if (MinecraftClient.IS_SYSTEM_MAC ? (int_1 == 343 || int_1 == 347) : (int_1 == 341 || int_1 == 345)) {
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
            client.openScreen(new ConfirmScreen(new QuitSaveConsumer(), new TranslatableText("text.cloth-config.quit_config"), new TranslatableText("text.cloth-config.quit_config_sure"), new TranslatableText("text.cloth-config.quit_discard"), new TranslatableText("gui.cancel")));
        else
            client.openScreen(parent);
        return true;
    }
    
    private class QuitSaveConsumer implements BooleanConsumer {
        @Override
        public void accept(boolean t) {
            if (!t)
                client.openScreen(AbstractConfigScreen.this);
            else
                client.openScreen(parent);
        }
    }
    
    @Override
    public void tick() {
        super.tick();
        boolean edited = isEdited();
        Optional.ofNullable(getQuitButton()).ifPresent(button -> button.setMessage(edited ? new TranslatableText("text.cloth-config.cancel_discard") : new TranslatableText("gui.cancel")));
        for (Element child : children())
            if (child instanceof TickableElement)
                ((TickableElement) child).tick();
    }
    
    @Nullable
    protected AbstractButtonWidget getQuitButton() {
        return null;
    }
    
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        for (Tooltip tooltip : tooltips) {
            renderOrderedTooltip(matrices, tooltip.getText(), tooltip.getX(), tooltip.getY());
        }
        this.tooltips.clear();
    }
    
    @Override
    public void addTooltip(Tooltip tooltip) {
        this.tooltips.add(tooltip);
    }
    
    protected void overlayBackground(MatrixStack matrices, Rectangle rect, int red, int green, int blue, int startAlpha, int endAlpha) {
        overlayBackground(matrices.peek().getModel(), rect, red, green, blue, startAlpha, endAlpha);
    }
    
    protected void overlayBackground(Matrix4f matrix, Rectangle rect, int red, int green, int blue, int startAlpha, int endAlpha) {
        if (isTransparentBackground())
            return;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        client.getTextureManager().bindTexture(getBackgroundLocation());
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        buffer.begin(VertexFormat.DrawMode.QUADS /* TODO: figure out whats the deal with drawmode 7 */, VertexFormats.POSITION_TEXTURE_COLOR);
        buffer.vertex(matrix, rect.getMinX(), rect.getMaxY(), 0.0F).texture(rect.getMinX() / 32.0F, rect.getMaxY() / 32.0F).color(red, green, blue, endAlpha).next();
        buffer.vertex(matrix, rect.getMaxX(), rect.getMaxY(), 0.0F).texture(rect.getMaxX() / 32.0F, rect.getMaxY() / 32.0F).color(red, green, blue, endAlpha).next();
        buffer.vertex(matrix, rect.getMaxX(), rect.getMinY(), 0.0F).texture(rect.getMaxX() / 32.0F, rect.getMinY() / 32.0F).color(red, green, blue, startAlpha).next();
        buffer.vertex(matrix, rect.getMinX(), rect.getMinY(), 0.0F).texture(rect.getMinX() / 32.0F, rect.getMinY() / 32.0F).color(red, green, blue, startAlpha).next();
        tessellator.draw();
    }
}
