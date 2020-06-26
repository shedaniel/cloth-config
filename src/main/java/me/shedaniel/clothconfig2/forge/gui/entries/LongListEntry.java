package me.shedaniel.clothconfig2.forge.gui.entries;

import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class LongListEntry extends TextFieldListEntry<Long> {
    
    private static Function<String, String> stripCharacters = s -> {
        StringBuilder stringBuilder_1 = new StringBuilder();
        char[] var2 = s.toCharArray();
        int var3 = var2.length;
        
        for (char c : var2)
            if (Character.isDigit(c) || c == '-')
                stringBuilder_1.append(c);
        
        return stringBuilder_1.toString();
    };
    private long minimum, maximum;
    private Consumer<Long> saveConsumer;
    
    @ApiStatus.Internal
    @Deprecated
    public LongListEntry(ITextComponent fieldName, Long value, ITextComponent resetButtonKey, Supplier<Long> defaultValue, Consumer<Long> saveConsumer) {
        super(fieldName, value, resetButtonKey, defaultValue);
        this.minimum = -Long.MAX_VALUE;
        this.maximum = Long.MAX_VALUE;
        this.saveConsumer = saveConsumer;
    }
    
    @ApiStatus.Internal
    @Deprecated
    public LongListEntry(ITextComponent fieldName, Long value, ITextComponent resetButtonKey, Supplier<Long> defaultValue, Consumer<Long> saveConsumer, Supplier<Optional<ITextComponent[]>> tooltipSupplier) {
        this(fieldName, value, resetButtonKey, defaultValue, saveConsumer, tooltipSupplier, false);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public LongListEntry(ITextComponent fieldName, Long value, ITextComponent resetButtonKey, Supplier<Long> defaultValue, Consumer<Long> saveConsumer, Supplier<Optional<ITextComponent[]>> tooltipSupplier, boolean requiresRestart) {
        super(fieldName, value, resetButtonKey, defaultValue, tooltipSupplier, requiresRestart);
        this.minimum = -Long.MAX_VALUE;
        this.maximum = Long.MAX_VALUE;
        this.saveConsumer = saveConsumer;
    }
    
    @Override
    protected String stripAddText(String s) {
        return stripCharacters.apply(s);
    }
    
    @Override
    protected void textFieldPreRender(TextFieldWidget widget) {
        try {
            double i = Long.parseLong(textFieldWidget.getText());
            if (i < minimum || i > maximum)
                widget.setTextColor(16733525);
            else
                widget.setTextColor(14737632);
        } catch (NumberFormatException ex) {
            widget.setTextColor(16733525);
        }
    }
    
    @Override
    public void save() {
        if (saveConsumer != null)
            saveConsumer.accept(getValue());
    }
    
    @Override
    protected boolean isMatchDefault(String text) {
        return getDefaultValue().isPresent() && text.equals(defaultValue.get().toString());
    }
    
    public LongListEntry setMinimum(long minimum) {
        this.minimum = minimum;
        return this;
    }
    
    public LongListEntry setMaximum(long maximum) {
        this.maximum = maximum;
        return this;
    }
    
    @Override
    public Long getValue() {
        try {
            return Long.valueOf(textFieldWidget.getText());
        } catch (Exception e) {
            return 0L;
        }
    }
    
    @Override
    public Optional<ITextComponent> getError() {
        try {
            long i = Long.parseLong(textFieldWidget.getText());
            if (i > maximum)
                return Optional.of(new TranslationTextComponent("text.cloth-config.error.too_large", maximum));
            else if (i < minimum)
                return Optional.of(new TranslationTextComponent("text.cloth-config.error.too_small", minimum));
        } catch (NumberFormatException ex) {
            return Optional.of(new TranslationTextComponent("text.cloth-config.error.not_valid_number_long"));
        }
        return super.getError();
    }
}
