package me.shedaniel.clothconfig2.forge.gui.entries;

import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnumListEntry<T extends Enum<?>> extends SelectionListEntry<T> {
    
    public static final Function<Enum, ITextComponent> DEFAULT_NAME_PROVIDER = t -> new TranslationTextComponent(t instanceof Translatable ? ((Translatable) t).getKey() : t.toString());
    
    @ApiStatus.Internal
    @Deprecated
    public EnumListEntry(ITextComponent fieldName, Class<T> clazz, T value, ITextComponent resetButtonKey, Supplier<T> defaultValue, Consumer<T> saveConsumer) {
        super(fieldName, clazz.getEnumConstants(), value, resetButtonKey, defaultValue, saveConsumer, DEFAULT_NAME_PROVIDER::apply);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public EnumListEntry(ITextComponent fieldName, Class<T> clazz, T value, ITextComponent resetButtonKey, Supplier<T> defaultValue, Consumer<T> saveConsumer, Function<Enum, ITextComponent> enumNameProvider) {
        super(fieldName, clazz.getEnumConstants(), value, resetButtonKey, defaultValue, saveConsumer, enumNameProvider::apply, null);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public EnumListEntry(ITextComponent fieldName, Class<T> clazz, T value, ITextComponent resetButtonKey, Supplier<T> defaultValue, Consumer<T> saveConsumer, Function<Enum, ITextComponent> enumNameProvider, Supplier<Optional<ITextComponent[]>> tooltipSupplier) {
        super(fieldName, clazz.getEnumConstants(), value, resetButtonKey, defaultValue, saveConsumer, enumNameProvider::apply, tooltipSupplier, false);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public EnumListEntry(ITextComponent fieldName, Class<T> clazz, T value, ITextComponent resetButtonKey, Supplier<T> defaultValue, Consumer<T> saveConsumer, Function<Enum, ITextComponent> enumNameProvider, Supplier<Optional<ITextComponent[]>> tooltipSupplier, boolean requiresRestart) {
        super(fieldName, clazz.getEnumConstants(), value, resetButtonKey, defaultValue, saveConsumer, enumNameProvider::apply, tooltipSupplier, requiresRestart);
    }
    
}
