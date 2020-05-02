package me.shedaniel.clothconfig2.gui.entries;

import me.shedaniel.clothconfig2.gui.widget.ColorDisplayWidget;
import me.shedaniel.clothconfig2.mixin.ButtonWidgetHooks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ColorEntry extends TextFieldListEntry<Integer> {
    
    private ColorDisplayWidget colorDisplayWidget;
    private Consumer<Integer> saveConsumer;
    private boolean alpha;
    
    @ApiStatus.Internal
    @Deprecated
    public ColorEntry(Text fieldName, int value, Text resetButtonKey, Supplier<Integer> defaultValue, Consumer<Integer> saveConsumer, Supplier<Optional<Text[]>> tooltipSupplier, boolean requiresRestart) {
        super(fieldName, 0, resetButtonKey, defaultValue, tooltipSupplier, requiresRestart);
        this.alpha = true;
        ColorValue colorValue = getColorValue(String.valueOf(value));
        if (colorValue.hasError())
            throw new IllegalArgumentException("Invalid Color: " + colorValue.getError().name());
        this.alpha = false;
        this.saveConsumer = saveConsumer;
        this.textFieldWidget.setText(getHexColorString(value));
        this.colorDisplayWidget = new ColorDisplayWidget(textFieldWidget, 0, 0, 20, getColorValueColor(textFieldWidget.getText()));
        this.original = value;
        ((ButtonWidgetHooks) this.resetButton).setOnPress(button -> {
            this.textFieldWidget.setText(getHexColorString(original));
            getScreen().setEdited(true, isRequiresRestart());
        });
    }
    
    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
        super.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isSelected, delta);
        this.colorDisplayWidget.y = y;
        ColorValue value = getColorValue(textFieldWidget.getText());
        if (!value.hasError())
            colorDisplayWidget.setColor(alpha ? value.getColor() : 0xff000000 | value.getColor());
        if (MinecraftClient.getInstance().textRenderer.isRightToLeft()) {
            this.colorDisplayWidget.x = x + resetButton.getWidth() + textFieldWidget.getWidth();
        } else {
            this.colorDisplayWidget.x = textFieldWidget.x - 23;
        }
        colorDisplayWidget.render(matrices, mouseX, mouseY, delta);
    }
    
    @Override
    protected void textFieldPreRender(TextFieldWidget widget) {
        if (!getError().isPresent()) {
            widget.setEditableColor(14737632);
        } else {
            widget.setEditableColor(16733525);
        }
    }
    
    @Override
    public void save() {
        if (saveConsumer != null)
            saveConsumer.accept(getValue());
    }
    
    @Override
    protected boolean isMatchDefault(String text) {
        if (!getDefaultValue().isPresent())
            return false;
        ColorValue colorValue = getColorValue(text);
        return colorValue.hasError() && colorValue.color == getDefaultValue().get();
    }
    
    @Override
    public Integer getValue() {
        return getColorValueColor(textFieldWidget.getText());
    }
    
    @Deprecated
    public void setValue(int color) {
        textFieldWidget.setText(getHexColorString(color));
    }
    
    @Override
    public Optional<Text> getError() {
        ColorValue colorValue = getColorValue(this.textFieldWidget.getText());
        if (colorValue.hasError())
            return Optional.of(new TranslatableText("text.cloth-config.error.color." + colorValue.getError().name().toLowerCase(Locale.ROOT)));
        return super.getError();
    }
    
    public void withAlpha() {
        if (!alpha) {
            this.alpha = true;
            textFieldWidget.setText(getHexColorString(original));
        }
    }
    
    public void withoutAlpha() {
        if (alpha) {
            alpha = false;
            textFieldWidget.setText(getHexColorString(original));
        }
    }
    
    protected String stripHexStarter(String hex) {
        if (hex.startsWith("#")) {
            return hex.substring(1);
        } else return hex;
    }
    
    protected boolean isValidColorString(String str) {
        return !getColorValue(str).hasError();
    }
    
    protected int getColorValueColor(String str) {
        return getColorValue(str).getColor();
    }
    
    protected ColorValue getColorValue(String str) {
        try {
            int color;
            if (str.startsWith("#")) {
                String stripHexStarter = stripHexStarter(str);
                if (stripHexStarter.length() > 8) return ColorError.INVALID_COLOR.toValue();
                if (!alpha && stripHexStarter.length() > 6) return ColorError.NO_ALPHA_ALLOWED.toValue();
                color = (int) Long.parseLong(stripHexStarter, 16);
            } else {
                color = (int) Long.parseLong(str);
            }
            int a = color >> 24 & 0xFF;
            if (!alpha && a > 0)
                return ColorError.NO_ALPHA_ALLOWED.toValue();
            if (a < 0 || a > 255)
                return ColorError.INVALID_ALPHA.toValue();
            int r = color >> 16 & 0xFF;
            if (r < 0 || r > 255)
                return ColorError.INVALID_RED.toValue();
            int g = color >> 8 & 0xFF;
            if (g < 0 || g > 255)
                return ColorError.INVALID_GREEN.toValue();
            int b = color & 0xFF;
            if (b < 0 || b > 255)
                return ColorError.INVALID_BLUE.toValue();
            return new ColorValue(color);
        } catch (NumberFormatException e) {
            return ColorError.INVALID_COLOR.toValue();
        }
    }
    
    protected String getHexColorString(int color) {
        return "#" + StringUtils.leftPad(Integer.toHexString(color), alpha ? 8 : 6, '0');
    }
    
    protected enum ColorError {
        NO_ALPHA_ALLOWED,
        INVALID_ALPHA,
        INVALID_RED,
        INVALID_GREEN,
        INVALID_BLUE,
        INVALID_COLOR;
        
        private ColorValue value;
        
        ColorError() {
            this.value = new ColorValue(this);
        }
        
        public ColorValue toValue() {
            return value;
        }
    }
    
    protected static class ColorValue {
        private int color = -1;
        @Nullable
        private ColorError error = null;
        
        public ColorValue(int color) {
            this.color = color;
        }
        
        public ColorValue(ColorError error) {
            this.error = error;
        }
        
        public int getColor() {
            return color;
        }
        
        @Nullable
        public ColorError getError() {
            return error;
        }
        
        public boolean hasError() {
            return getError() != null;
        }
    }
}
