package me.shedaniel.clothconfig2.forge.gui.entries;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public abstract class TextFieldListEntry<T> extends TooltipListEntry<T> {
    
    protected TextFieldWidget textFieldWidget;
    protected Button resetButton;
    protected Supplier<T> defaultValue;
    protected T original;
    protected List<IGuiEventListener> widgets;
    private boolean isSelected = false;
    
    @ApiStatus.Internal
    @Deprecated
    protected TextFieldListEntry(ITextComponent fieldName, T original, ITextComponent resetButtonKey, Supplier<T> defaultValue) {
        this(fieldName, original, resetButtonKey, defaultValue, null);
    }
    
    @ApiStatus.Internal
    @Deprecated
    protected TextFieldListEntry(ITextComponent fieldName, T original, ITextComponent resetButtonKey, Supplier<T> defaultValue, Supplier<Optional<ITextComponent[]>> tooltipSupplier) {
        this(fieldName, original, resetButtonKey, defaultValue, tooltipSupplier, false);
    }
    
    @ApiStatus.Internal
    @Deprecated
    protected TextFieldListEntry(ITextComponent fieldName, T original, ITextComponent resetButtonKey, Supplier<T> defaultValue, Supplier<Optional<ITextComponent[]>> tooltipSupplier, boolean requiresRestart) {
        super(fieldName, tooltipSupplier, requiresRestart);
        this.defaultValue = defaultValue;
        this.original = original;
        this.textFieldWidget = new TextFieldWidget(Minecraft.getInstance().fontRenderer, 0, 0, 148, 18, NarratorChatListener.EMPTY) {
            @Override
            public void render(MatrixStack matrices, int int_1, int int_2, float float_1) {
                setFocused2(isSelected && TextFieldListEntry.this.getFocused() == this);
                textFieldPreRender(this);
                super.render(matrices, int_1, int_2, float_1);
            }
            
            @Override
            public void writeText(String string_1) {
                super.writeText(stripAddText(string_1));
            }
        };
        textFieldWidget.setMaxStringLength(999999);
        textFieldWidget.setText(String.valueOf(original));
        this.resetButton = new Button(0, 0, Minecraft.getInstance().fontRenderer.func_238414_a_(resetButtonKey) + 6, 20, resetButtonKey, widget -> {
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
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
        super.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isSelected, delta);
        MainWindow window = Minecraft.getInstance().getMainWindow();
        this.resetButton.active = isEditable() && getDefaultValue().isPresent() && !isMatchDefault(textFieldWidget.getText());
        this.resetButton.y = y;
        this.textFieldWidget.setEnabled(isEditable());
        this.textFieldWidget.y = y + 1;
        ITextComponent displayedFieldName = getDisplayedFieldName();
        if (Minecraft.getInstance().fontRenderer.getBidiFlag()) {
            Minecraft.getInstance().fontRenderer.func_238407_a_(matrices, displayedFieldName, window.getScaledWidth() - x - Minecraft.getInstance().fontRenderer.func_238414_a_(displayedFieldName), y + 5, getPreferredTextColor());
            this.resetButton.x = x;
            this.textFieldWidget.x = x + resetButton.getWidth();
        } else {
            Minecraft.getInstance().fontRenderer.func_238407_a_(matrices, displayedFieldName, x, y + 5, getPreferredTextColor());
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
    public List<? extends IGuiEventListener> children() {
        return widgets;
    }
    
}
