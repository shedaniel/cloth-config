package me.shedaniel.clothconfig2.gui.entries;

import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public abstract class TextFieldListEntry<T> extends TooltipListEntry<T> {
    
    protected TextFieldWidget textFieldWidget;
    protected ButtonWidget resetButton;
    protected Supplier<T> defaultValue;
    protected T original;
    protected List<Element> widgets;
    private boolean isSelected = false;
    
    @ApiStatus.Internal
    @Deprecated
    protected TextFieldListEntry(Text fieldName, T original, Text resetButtonKey, Supplier<T> defaultValue) {
        this(fieldName, original, resetButtonKey, defaultValue, null);
    }
    
    @ApiStatus.Internal
    @Deprecated
    protected TextFieldListEntry(Text fieldName, T original, Text resetButtonKey, Supplier<T> defaultValue, Supplier<Optional<Text[]>> tooltipSupplier) {
        this(fieldName, original, resetButtonKey, defaultValue, tooltipSupplier, false);
    }
    
    @ApiStatus.Internal
    @Deprecated
    protected TextFieldListEntry(Text fieldName, T original, Text resetButtonKey, Supplier<T> defaultValue, Supplier<Optional<Text[]>> tooltipSupplier, boolean requiresRestart) {
        super(fieldName, tooltipSupplier, requiresRestart);
        this.defaultValue = defaultValue;
        this.original = original;
        this.textFieldWidget = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, 148, 18, NarratorManager.EMPTY) {
            @Override
            public void render(MatrixStack matrices, int int_1, int int_2, float float_1) {
                setFocused(isSelected && TextFieldListEntry.this.getFocused() == this);
                textFieldPreRender(this);
                super.render(matrices, int_1, int_2, float_1);
            }
            
            @Override
            public void write(String string_1) {
                super.write(stripAddText(string_1));
            }
        };
        textFieldWidget.setMaxLength(999999);
        textFieldWidget.setText(String.valueOf(original));
        this.resetButton = new ButtonWidget(0, 0, MinecraftClient.getInstance().textRenderer.getWidth(resetButtonKey) + 6, 20, resetButtonKey, widget -> {
            TextFieldListEntry.this.textFieldWidget.setText(String.valueOf(defaultValue.get()));
        });
        this.widgets = Lists.newArrayList(textFieldWidget, resetButton);
    }
    
    @Override
    public boolean isEdited() {
        return isChanged(original, textFieldWidget.getText());
    }
    
    protected boolean isChanged(T original, String s) {
        return !String.valueOf(original).equals(s);
    }
    
    protected static void setTextFieldWidth(TextFieldWidget widget, int width) {
        widget.setWidth(width);
    }
    
    @Deprecated
    public void setValue(String s) {
        textFieldWidget.setText(String.valueOf(s));
    }
    
    protected String stripAddText(String s) {
        return s;
    }
    
    protected void textFieldPreRender(TextFieldWidget widget) {
        
    }
    
    @Override
    public void updateSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
    
    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
        Window window = MinecraftClient.getInstance().getWindow();
        this.resetButton.active = isEditable() && getDefaultValue().isPresent() && !isMatchDefault(textFieldWidget.getText());
        this.resetButton.y = y;
        this.textFieldWidget.setEditable(isEditable());
        this.textFieldWidget.y = y + 1;
        Text displayedFieldName = getDisplayedFieldName();
        if (MinecraftClient.getInstance().textRenderer.isRightToLeft()) {
            MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, displayedFieldName, window.getScaledWidth() - x - MinecraftClient.getInstance().textRenderer.getWidth(displayedFieldName), y + 6, getPreferredTextColor());
            this.resetButton.x = x;
            this.textFieldWidget.x = x + resetButton.getWidth();
        } else {
            MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, displayedFieldName, x, y + 6, getPreferredTextColor());
            this.resetButton.x = x + entryWidth - resetButton.getWidth();
            this.textFieldWidget.x = x + entryWidth - 148;
        }
        setTextFieldWidth(textFieldWidget, 148 - resetButton.getWidth() - 4);
        resetButton.render(matrices, mouseX, mouseY, delta);
        textFieldWidget.render(matrices, mouseX, mouseY, delta);
    }
    
    protected abstract boolean isMatchDefault(String text);
    
    @Override
    public Optional<T> getDefaultValue() {
        return defaultValue == null ? Optional.empty() : Optional.ofNullable(defaultValue.get());
    }
    
    @Override
    public List<? extends Element> children() {
        return widgets;
    }
    
}
