package me.shedaniel.clothconfig2.gui.entries;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class EnumListEntry<T extends Enum<?>> extends SelectionListEntry<T> {
    
    public static final Function<Enum, Text> DEFAULT_NAME_PROVIDER = t -> new TranslatableText(t instanceof Translatable ? ((Translatable) t).getKey() : t.toString());
    
    @ApiStatus.Internal
    @Deprecated
    public EnumListEntry(Text fieldName, Class<T> clazz, T value, Text resetButtonKey, Supplier<T> defaultValue, Consumer<T> saveConsumer) {
        super(fieldName, clazz.getEnumConstants(), value, resetButtonKey, defaultValue, saveConsumer, DEFAULT_NAME_PROVIDER::apply);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public EnumListEntry(Text fieldName, Class<T> clazz, T value, Text resetButtonKey, Supplier<T> defaultValue, Consumer<T> saveConsumer, Function<Enum, Text> enumNameProvider) {
        super(fieldName, clazz.getEnumConstants(), value, resetButtonKey, defaultValue, saveConsumer, enumNameProvider::apply, null);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public EnumListEntry(Text fieldName, Class<T> clazz, T value, Text resetButtonKey, Supplier<T> defaultValue, Consumer<T> saveConsumer, Function<Enum, Text> enumNameProvider, Supplier<Optional<Text[]>> tooltipSupplier) {
        super(fieldName, clazz.getEnumConstants(), value, resetButtonKey, defaultValue, saveConsumer, enumNameProvider::apply, tooltipSupplier, false);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public EnumListEntry(Text fieldName, Class<T> clazz, T value, Text resetButtonKey, Supplier<T> defaultValue, Consumer<T> saveConsumer, Function<Enum, Text> enumNameProvider, Supplier<Optional<Text[]>> tooltipSupplier, boolean requiresRestart) {
        super(fieldName, clazz.getEnumConstants(), value, resetButtonKey, defaultValue, saveConsumer, enumNameProvider::apply, tooltipSupplier, requiresRestart);
    }
    
}
