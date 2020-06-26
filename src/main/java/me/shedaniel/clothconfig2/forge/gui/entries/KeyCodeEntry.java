package me.shedaniel.clothconfig2.forge.gui.entries;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import me.shedaniel.clothconfig2.forge.api.ModifierKeyCode;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("DuplicatedCode")
@OnlyIn(Dist.CLIENT)
public class KeyCodeEntry extends TooltipListEntry<ModifierKeyCode> {
    
    private ModifierKeyCode value;
    private ModifierKeyCode original;
    private Button buttonWidget, resetButton;
    private Consumer<ModifierKeyCode> saveConsumer;
    private Supplier<ModifierKeyCode> defaultValue;
    private List<IGuiEventListener> widgets;
    private boolean allowMouse = true, allowKey = true, allowModifiers = true;
    
    @ApiStatus.Internal
    @Deprecated
    public KeyCodeEntry(ITextComponent fieldName, ModifierKeyCode value, ITextComponent resetButtonKey, Supplier<ModifierKeyCode> defaultValue, Consumer<ModifierKeyCode> saveConsumer, Supplier<Optional<ITextComponent[]>> tooltipSupplier, boolean requiresRestart) {
        super(fieldName, tooltipSupplier, requiresRestart);
        this.defaultValue = defaultValue;
        this.value = value.copy();
        this.original = value.copy();
        this.buttonWidget = new Button(0, 0, 150, 20, NarratorChatListener.EMPTY, widget -> {
            getConfigScreen().setFocusedBinding(this);
        });
        this.resetButton = new Button(0, 0, Minecraft.getInstance().fontRenderer.func_238414_a_(resetButtonKey) + 6, 20, resetButtonKey, widget -> {
            KeyCodeEntry.this.value = getDefaultValue().orElse(null).copy();
            getConfigScreen().setFocusedBinding(null);
        });
        this.saveConsumer = saveConsumer;
        this.widgets = Lists.newArrayList(buttonWidget, resetButton);
    }
    
    @Override
    public boolean isEdited() {
        return super.isEdited() || !this.original.equals(getValue());
    }
    
    public boolean isAllowModifiers() {
        return allowModifiers;
    }
    
    public void setAllowModifiers(boolean allowModifiers) {
        this.allowModifiers = allowModifiers;
    }
    
    public boolean isAllowKey() {
        return allowKey;
    }
    
    public void setAllowKey(boolean allowKey) {
        this.allowKey = allowKey;
    }
    
    public boolean isAllowMouse() {
        return allowMouse;
    }
    
    public void setAllowMouse(boolean allowMouse) {
        this.allowMouse = allowMouse;
    }
    
    @Override
    public void save() {
        if (saveConsumer != null)
            saveConsumer.accept(getValue());
    }
    
    @Override
    public ModifierKeyCode getValue() {
        return value;
    }
    
    public void setValue(ModifierKeyCode value) {
        this.value = value;
    }
    
    @Override
    public Optional<ModifierKeyCode> getDefaultValue() {
        return Optional.ofNullable(defaultValue).map(Supplier::get).map(ModifierKeyCode::copy);
    }
    
    private ITextComponent getLocalizedName() {
        return this.value.getLocalizedName();
    }
    
    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
        super.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isSelected, delta);
        MainWindow window = Minecraft.getInstance().getMainWindow();
        this.resetButton.field_230693_o_ = isEditable() && getDefaultValue().isPresent() && !getDefaultValue().get().equals(getValue());
        this.resetButton.field_230691_m_ = y;
        this.buttonWidget.field_230693_o_ = isEditable();
        this.buttonWidget.field_230691_m_ = y;
        this.buttonWidget.func_238482_a_(getLocalizedName());
        if (getConfigScreen().getFocusedBinding() == this)
            this.buttonWidget.func_238482_a_(new StringTextComponent("> ").func_240699_a_(TextFormatting.WHITE).func_230529_a_(this.buttonWidget.func_230458_i_().func_230532_e_().func_240699_a_(TextFormatting.YELLOW)).func_230529_a_(new StringTextComponent(" <").func_240699_a_(TextFormatting.WHITE)));
        ITextComponent displayedFieldName = getDisplayedFieldName();
        if (Minecraft.getInstance().fontRenderer.getBidiFlag()) {
            Minecraft.getInstance().fontRenderer.func_238407_a_(matrices, displayedFieldName, window.getScaledWidth() - x - Minecraft.getInstance().fontRenderer.func_238414_a_(displayedFieldName), y + 5, 16777215);
            this.resetButton.field_230690_l_ = x;
            this.buttonWidget.field_230690_l_ = x + resetButton.func_230998_h_() + 2;
        } else {
            Minecraft.getInstance().fontRenderer.func_238407_a_(matrices, displayedFieldName, x, y + 5, getPreferredTextColor());
            this.resetButton.field_230690_l_ = x + entryWidth - resetButton.func_230998_h_();
            this.buttonWidget.field_230690_l_ = x + entryWidth - 150;
        }
        this.buttonWidget.func_230991_b_(150 - resetButton.func_230998_h_() - 2);
        resetButton.func_230430_a_(matrices, mouseX, mouseY, delta);
        buttonWidget.func_230430_a_(matrices, mouseX, mouseY, delta);
    }
    
    @Override
    public List<? extends IGuiEventListener> func_231039_at__() {
        return widgets;
    }
    
}
