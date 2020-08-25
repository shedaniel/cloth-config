package me.shedaniel.clothconfig2.gui.entries;

import com.google.common.collect.Lists;
import me.shedaniel.clothconfig2.api.ModifierKeyCode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("DuplicatedCode")
@Environment(EnvType.CLIENT)
public class KeyCodeEntry extends TooltipListEntry<ModifierKeyCode> {
    
    private ModifierKeyCode value;
    private ModifierKeyCode original;
    private ButtonWidget buttonWidget, resetButton;
    private Consumer<ModifierKeyCode> saveConsumer;
    private Supplier<ModifierKeyCode> defaultValue;
    private List<Element> widgets;
    private boolean allowMouse = true, allowKey = true, allowModifiers = true;
    
    @ApiStatus.Internal
    @Deprecated
    public KeyCodeEntry(Text fieldName, ModifierKeyCode value, Text resetButtonKey, Supplier<ModifierKeyCode> defaultValue, Consumer<ModifierKeyCode> saveConsumer, Supplier<Optional<Text[]>> tooltipSupplier, boolean requiresRestart) {
        super(fieldName, tooltipSupplier, requiresRestart);
        this.defaultValue = defaultValue;
        this.value = value.copy();
        this.original = value.copy();
        this.buttonWidget = new ButtonWidget(0, 0, 150, 20, NarratorManager.EMPTY, widget -> {
            getConfigScreen().setFocusedBinding(this);
        });
        this.resetButton = new ButtonWidget(0, 0, MinecraftClient.getInstance().textRenderer.getWidth(resetButtonKey) + 6, 20, resetButtonKey, widget -> {
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
    
    private Text getLocalizedName() {
        return this.value.getLocalizedName();
    }
    
    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
        Window window = MinecraftClient.getInstance().getWindow();
        this.resetButton.active = isEditable() && getDefaultValue().isPresent() && !getDefaultValue().get().equals(getValue());
        this.resetButton.y = y;
        this.buttonWidget.active = isEditable();
        this.buttonWidget.y = y;
        this.buttonWidget.setMessage(getLocalizedName());
        if (getConfigScreen().getFocusedBinding() == this)
            this.buttonWidget.setMessage(new LiteralText("> ").formatted(Formatting.WHITE).append(this.buttonWidget.getMessage().copy().formatted(Formatting.YELLOW)).append(new LiteralText(" <").formatted(Formatting.WHITE)));
        Text displayedFieldName = getDisplayedFieldName();
        if (MinecraftClient.getInstance().textRenderer.isRightToLeft()) {
            MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, displayedFieldName.method_30937(), window.getScaledWidth() - x - MinecraftClient.getInstance().textRenderer.getWidth(displayedFieldName), y + 6, 16777215);
            this.resetButton.x = x;
            this.buttonWidget.x = x + resetButton.getWidth() + 2;
        } else {
            MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, displayedFieldName.method_30937(), x, y + 6, getPreferredTextColor());
            this.resetButton.x = x + entryWidth - resetButton.getWidth();
            this.buttonWidget.x = x + entryWidth - 150;
        }
        this.buttonWidget.setWidth(150 - resetButton.getWidth() - 2);
        resetButton.render(matrices, mouseX, mouseY, delta);
        buttonWidget.render(matrices, mouseX, mouseY, delta);
    }
    
    @Override
    public List<? extends Element> children() {
        return widgets;
    }
    
}
