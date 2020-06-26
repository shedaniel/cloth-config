package me.shedaniel.clothconfig2.forge.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import me.shedaniel.clothconfig2.forge.api.*;
import me.shedaniel.clothconfig2.forge.gui.entries.KeyCodeEntry;
import me.shedaniel.math.Rectangle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.IScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
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
    private ITextComponent defaultFallbackCategory = null;
    public int selectedCategoryIndex = 0;
    private boolean editable = true;
    private KeyCodeEntry focusedBinding;
    private ModifierKeyCode startedKeyCode = null;
    private final List<Tooltip> tooltips = Lists.newArrayList();
    @Nullable
    private Runnable savingRunnable = null;
    @Nullable
    protected Consumer<Screen> afterInitConsumer = null;
    
    protected AbstractConfigScreen(Screen parent, ITextComponent title, ResourceLocation backgroundLocation) {
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
    
    public abstract Map<ITextComponent, List<AbstractConfigEntry<?>>> getCategorizedEntries();
    
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
        return transparentBackground && Minecraft.getInstance().world != null;
    }
    
    @ApiStatus.Internal
    public void setTransparentBackground(boolean transparentBackground) {
        this.transparentBackground = transparentBackground;
    }
    
    public ITextComponent getFallbackCategory() {
        if (defaultFallbackCategory != null)
            return defaultFallbackCategory;
        return getCategorizedEntries().keySet().iterator().next();
    }
    
    @ApiStatus.Internal
    public void setFallbackCategory(@Nullable ITextComponent defaultFallbackCategory) {
        this.defaultFallbackCategory = defaultFallbackCategory;
        List<ITextComponent> categories = Lists.newArrayList(getCategorizedEntries().keySet());
        for (int i = 0; i < categories.size(); i++) {
            ITextComponent category = categories.get(i);
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
                AbstractConfigScreen.this.field_230706_i_.displayGuiScreen(new ClothRequiresRestartScreen(parent));
            else
                AbstractConfigScreen.this.field_230706_i_.displayGuiScreen(parent);
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
            startedKeyCode.setKeyCodeAndModifier(InputMappings.INPUT_INVALID, Modifier.none());
        } else
            startedKeyCode = null;
    }
    
    @Override
    public boolean func_231048_c_(double double_1, double double_2, int int_1) {
        if (this.focusedBinding != null && this.startedKeyCode != null && !this.startedKeyCode.isUnknown() && focusedBinding.isAllowMouse()) {
            focusedBinding.setValue(startedKeyCode);
            setFocusedBinding(null);
            return true;
        }
        return super.func_231048_c_(double_1, double_2, int_1);
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
    public boolean func_231044_a_(double double_1, double double_2, int int_1) {
        if (this.focusedBinding != null && this.startedKeyCode != null && focusedBinding.isAllowMouse()) {
            if (startedKeyCode.isUnknown())
                startedKeyCode.setKeyCode(InputMappings.Type.MOUSE.getOrMakeInput(int_1));
            else if (focusedBinding.isAllowModifiers()) {
                if (startedKeyCode.getType() == InputMappings.Type.KEYSYM) {
                    int code = startedKeyCode.getKeyCode().getKeyCode();
                    if (Minecraft.IS_RUNNING_ON_MAC ? (code == 343 || code == 347) : (code == 341 || code == 345)) {
                        Modifier modifier = startedKeyCode.getModifier();
                        startedKeyCode.setModifier(Modifier.of(modifier.hasAlt(), true, modifier.hasShift()));
                        startedKeyCode.setKeyCode(InputMappings.Type.MOUSE.getOrMakeInput(int_1));
                        return true;
                    } else if (code == 344 || code == 340) {
                        Modifier modifier = startedKeyCode.getModifier();
                        startedKeyCode.setModifier(Modifier.of(modifier.hasAlt(), modifier.hasControl(), true));
                        startedKeyCode.setKeyCode(InputMappings.Type.MOUSE.getOrMakeInput(int_1));
                        return true;
                    } else if (code == 342 || code == 346) {
                        Modifier modifier = startedKeyCode.getModifier();
                        startedKeyCode.setModifier(Modifier.of(true, modifier.hasControl(), modifier.hasShift()));
                        startedKeyCode.setKeyCode(InputMappings.Type.MOUSE.getOrMakeInput(int_1));
                        return true;
                    }
                }
            }
            return true;
        } else {
            if (this.focusedBinding != null)
                return true;
            return super.func_231044_a_(double_1, double_2, int_1);
        }
    }
    
    @Override
    public boolean func_231046_a_(int int_1, int int_2, int int_3) {
        if (this.focusedBinding != null && (focusedBinding.isAllowKey() || int_1 == 256)) {
            if (int_1 != 256) {
                if (startedKeyCode.isUnknown())
                    startedKeyCode.setKeyCode(InputMappings.getInputByCode(int_1, int_2));
                else if (focusedBinding.isAllowModifiers()) {
                    if (startedKeyCode.getType() == InputMappings.Type.KEYSYM) {
                        int code = startedKeyCode.getKeyCode().getKeyCode();
                        if (Minecraft.IS_RUNNING_ON_MAC ? (code == 343 || code == 347) : (code == 341 || code == 345)) {
                            Modifier modifier = startedKeyCode.getModifier();
                            startedKeyCode.setModifier(Modifier.of(modifier.hasAlt(), true, modifier.hasShift()));
                            startedKeyCode.setKeyCode(InputMappings.getInputByCode(int_1, int_2));
                            return true;
                        } else if (code == 344 || code == 340) {
                            Modifier modifier = startedKeyCode.getModifier();
                            startedKeyCode.setModifier(Modifier.of(modifier.hasAlt(), modifier.hasControl(), true));
                            startedKeyCode.setKeyCode(InputMappings.getInputByCode(int_1, int_2));
                            return true;
                        } else if (code == 342 || code == 346) {
                            Modifier modifier = startedKeyCode.getModifier();
                            startedKeyCode.setModifier(Modifier.of(true, modifier.hasControl(), modifier.hasShift()));
                            startedKeyCode.setKeyCode(InputMappings.getInputByCode(int_1, int_2));
                            return true;
                        }
                    }
                    if (Minecraft.IS_RUNNING_ON_MAC ? (int_1 == 343 || int_1 == 347) : (int_1 == 341 || int_1 == 345)) {
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
        if (int_1 == 256 && this.func_231178_ax__()) {
            return quit();
        }
        return super.func_231046_a_(int_1, int_2, int_3);
    }
    
    protected final boolean quit() {
        if (confirmSave && isEdited())
            field_230706_i_.displayGuiScreen(new ConfirmScreen(new QuitSaveConsumer(), new TranslationTextComponent("text.cloth-config.quit_config"), new TranslationTextComponent("text.cloth-config.quit_config_sure"), new TranslationTextComponent("text.cloth-config.quit_discard"), new TranslationTextComponent("gui.cancel")));
        else
            field_230706_i_.displayGuiScreen(parent);
        return true;
    }
    
    private class QuitSaveConsumer implements BooleanConsumer {
        @Override
        public void accept(boolean t) {
            if (!t)
                field_230706_i_.displayGuiScreen(AbstractConfigScreen.this);
            else
                field_230706_i_.displayGuiScreen(parent);
        }
    }
    
    @Override
    public void func_231023_e_() {
        super.func_231023_e_();
        for (IGuiEventListener child : func_231039_at__())
            if (child instanceof IScreen)
                ((IScreen) child).func_231023_e_();
    }
    
    @Override
    public void func_230430_a_(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.func_230430_a_(matrices, mouseX, mouseY, delta);
        for (Tooltip tooltip : tooltips) {
            func_238654_b_(matrices, tooltip.getText(), tooltip.getX(), tooltip.getY(), field_230712_o_);
        }
        this.tooltips.clear();
    }
    
    @Override
    public void addTooltip(Tooltip tooltip) {
        this.tooltips.add(tooltip);
    }
    
    protected void overlayBackground(MatrixStack matrices, Rectangle rect, int red, int green, int blue, int startAlpha, int endAlpha) {
        overlayBackground(matrices.getLast().getMatrix(), rect, red, green, blue, startAlpha, endAlpha);
    }
    
    protected void overlayBackground(Matrix4f matrix, Rectangle rect, int red, int green, int blue, int startAlpha, int endAlpha) {
        if (isTransparentBackground())
            return;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        field_230706_i_.getTextureManager().bindTexture(getBackgroundLocation());
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        buffer.pos(matrix, rect.getMinX(), rect.getMaxY(), 0.0F).tex(rect.getMinX() / 32.0F, rect.getMaxY() / 32.0F).color(red, green, blue, endAlpha).endVertex();
        buffer.pos(matrix, rect.getMaxX(), rect.getMaxY(), 0.0F).tex(rect.getMaxX() / 32.0F, rect.getMaxY() / 32.0F).color(red, green, blue, endAlpha).endVertex();
        buffer.pos(matrix, rect.getMaxX(), rect.getMinY(), 0.0F).tex(rect.getMaxX() / 32.0F, rect.getMinY() / 32.0F).color(red, green, blue, startAlpha).endVertex();
        buffer.pos(matrix, rect.getMinX(), rect.getMinY(), 0.0F).tex(rect.getMinX() / 32.0F, rect.getMinY() / 32.0F).color(red, green, blue, startAlpha).endVertex();
        tessellator.draw();
    }
}
