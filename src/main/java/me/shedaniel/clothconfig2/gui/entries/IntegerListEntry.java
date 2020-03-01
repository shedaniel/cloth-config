package me.shedaniel.clothconfig2.gui.entries;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class IntegerListEntry extends TextFieldListEntry<Integer> {
    
    private static Function<String, String> stripCharacters = s -> {
        StringBuilder stringBuilder_1 = new StringBuilder();
        char[] var2 = s.toCharArray();
        int var3 = var2.length;
        
        for (char c : var2)
            if (Character.isDigit(c) || c == '-')
                stringBuilder_1.append(c);
        
        return stringBuilder_1.toString();
    };
    private int minimum, maximum;
    private Consumer<Integer> saveConsumer;
    
    @Deprecated
    public IntegerListEntry(String fieldName, Integer value, Consumer<Integer> saveConsumer) {
        this(fieldName, value, "text.cloth-config.reset_value", null, saveConsumer);
    }
    
    @Deprecated
    public IntegerListEntry(String fieldName, Integer value, String resetButtonKey, Supplier<Integer> defaultValue, Consumer<Integer> saveConsumer) {
        super(fieldName, value, resetButtonKey, defaultValue);
        this.minimum = -Integer.MAX_VALUE;
        this.maximum = Integer.MAX_VALUE;
        this.saveConsumer = saveConsumer;
    }
    
    @Deprecated
    public IntegerListEntry(String fieldName, Integer value, String resetButtonKey, Supplier<Integer> defaultValue, Consumer<Integer> saveConsumer, Supplier<Optional<String[]>> tooltipSupplier) {
        this(fieldName, value, resetButtonKey, defaultValue, saveConsumer, tooltipSupplier, false);
    }
    
    @Deprecated
    public IntegerListEntry(String fieldName, Integer value, String resetButtonKey, Supplier<Integer> defaultValue, Consumer<Integer> saveConsumer, Supplier<Optional<String[]>> tooltipSupplier, boolean requiresRestart) {
        super(fieldName, value, resetButtonKey, defaultValue, tooltipSupplier, requiresRestart);
        this.minimum = -Integer.MAX_VALUE;
        this.maximum = Integer.MAX_VALUE;
        this.saveConsumer = saveConsumer;
    }
    
    @Override
    protected String stripAddText(String s) {
        return stripCharacters.apply(s);
    }
    
    @Override
    protected void textFieldPreRender(TextFieldWidget widget) {
        try {
            double i = Integer.parseInt(textFieldWidget.getText());
            if (i < minimum || i > maximum)
                widget.setEditableColor(16733525);
            else
                widget.setEditableColor(14737632);
        } catch (NumberFormatException ex) {
            widget.setEditableColor(16733525);
        }
    }
    
    @Override
    protected boolean isMatchDefault(String text) {
        return getDefaultValue().isPresent() && text.equals(defaultValue.get().toString());
    }
    
    @Override
    public void save() {
        if (saveConsumer != null)
            saveConsumer.accept(getValue());
    }
    
    public IntegerListEntry setMaximum(int maximum) {
        this.maximum = maximum;
        return this;
    }
    
    public IntegerListEntry setMinimum(int minimum) {
        this.minimum = minimum;
        return this;
    }
    
    @Override
    public Integer getValue() {
        try {
            return Integer.valueOf(textFieldWidget.getText());
        } catch (Exception e) {
            return 0;
        }
    }
    
    @Override
    public Optional<String> getError() {
        try {
            int i = Integer.parseInt(textFieldWidget.getText());
            if (i > maximum)
                return Optional.of(I18n.translate("text.cloth-config.error.too_large", maximum));
            else if (i < minimum)
                return Optional.of(I18n.translate("text.cloth-config.error.too_small", minimum));
        } catch (NumberFormatException ex) {
            return Optional.of(I18n.translate("text.cloth-config.error.not_valid_number_int"));
        }
        return super.getError();
    }
}
