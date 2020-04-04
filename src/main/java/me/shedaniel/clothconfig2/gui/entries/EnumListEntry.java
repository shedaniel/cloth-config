package me.shedaniel.clothconfig2.gui.entries;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class EnumListEntry<T extends Enum<?>> extends SelectionListEntry<T> {
    
    public static final Function<Enum, String> DEFAULT_NAME_PROVIDER = t -> I18n.translate(t instanceof Translatable ? ((Translatable) t).getKey() : t.toString());
    
    @ApiStatus.Internal
    @Deprecated
    public EnumListEntry(String fieldName, Class<T> clazz, T value, Consumer<T> saveConsumer) {
        super(fieldName, clazz.getEnumConstants(), value, "text.cloth-config.reset_value", null, saveConsumer);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public EnumListEntry(String fieldName, Class<T> clazz, T value, String resetButtonKey, Supplier<T> defaultValue, Consumer<T> saveConsumer) {
        super(fieldName, clazz.getEnumConstants(), value, resetButtonKey, defaultValue, saveConsumer, DEFAULT_NAME_PROVIDER::apply);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public EnumListEntry(String fieldName, Class<T> clazz, T value, String resetButtonKey, Supplier<T> defaultValue, Consumer<T> saveConsumer, Function<Enum, String> enumNameProvider) {
        super(fieldName, clazz.getEnumConstants(), value, resetButtonKey, defaultValue, saveConsumer, enumNameProvider::apply, null);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public EnumListEntry(String fieldName, Class<T> clazz, T value, String resetButtonKey, Supplier<T> defaultValue, Consumer<T> saveConsumer, Function<Enum, String> enumNameProvider, Supplier<Optional<String[]>> tooltipSupplier) {
        super(fieldName, clazz.getEnumConstants(), value, resetButtonKey, defaultValue, saveConsumer, enumNameProvider::apply, tooltipSupplier, false);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public EnumListEntry(String fieldName, Class<T> clazz, T value, String resetButtonKey, Supplier<T> defaultValue, Consumer<T> saveConsumer, Function<Enum, String> enumNameProvider, Supplier<Optional<String[]>> tooltipSupplier, boolean requiresRestart) {
        super(fieldName, clazz.getEnumConstants(), value, resetButtonKey, defaultValue, saveConsumer, enumNameProvider::apply, tooltipSupplier, requiresRestart);
    }
    
}
