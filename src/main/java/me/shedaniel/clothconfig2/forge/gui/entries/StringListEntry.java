package me.shedaniel.clothconfig2.forge.gui.entries;

import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StringListEntry extends TextFieldListEntry<String> {
    
    private Consumer<String> saveConsumer;
    
    @ApiStatus.Internal
    @Deprecated
    public StringListEntry(ITextComponent fieldName, String value, ITextComponent resetButtonKey, Supplier<String> defaultValue, Consumer<String> saveConsumer) {
        super(fieldName, value, resetButtonKey, defaultValue);
        this.saveConsumer = saveConsumer;
    }
    
    @ApiStatus.Internal
    @Deprecated
    public StringListEntry(ITextComponent fieldName, String value, ITextComponent resetButtonKey, Supplier<String> defaultValue, Consumer<String> saveConsumer, Supplier<Optional<ITextComponent[]>> tooltipSupplier) {
        this(fieldName, value, resetButtonKey, defaultValue, saveConsumer, tooltipSupplier, false);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public StringListEntry(ITextComponent fieldName, String value, ITextComponent resetButtonKey, Supplier<String> defaultValue, Consumer<String> saveConsumer, Supplier<Optional<ITextComponent[]>> tooltipSupplier, boolean requiresRestart) {
        super(fieldName, value, resetButtonKey, defaultValue, tooltipSupplier, requiresRestart);
        this.saveConsumer = saveConsumer;
    }
    
    @Override
    public String getValue() {
        return textFieldWidget.getText();
    }
    
    @Override
    public void save() {
        if (saveConsumer != null)
            saveConsumer.accept(getValue());
    }
    
    @Override
    protected boolean isMatchDefault(String text) {
        return getDefaultValue().isPresent() && text.equals(getDefaultValue().get());
    }
    
}
