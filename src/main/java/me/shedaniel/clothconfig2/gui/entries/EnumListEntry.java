package me.shedaniel.clothconfig2.gui.entries;

import net.minecraft.client.resource.language.I18n;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class EnumListEntry<T extends Enum<?>> extends SelectionListEntry<T> {
    
    public static final Function<Enum, String> DEFAULT_NAME_PROVIDER = t -> I18n.translate(t instanceof Translatable ? ((Translatable) t).getKey() : t.toString());
    
    @Deprecated
    public EnumListEntry(String fieldName, Class<T> clazz, T value, Consumer<T> saveConsumer) {
        super(fieldName, clazz.getEnumConstants(), value, "text.cloth-config.reset_value", null, saveConsumer);
    }
    
    @Deprecated
    public EnumListEntry(String fieldName, Class<T> clazz, T value, String resetButtonKey, Supplier<T> defaultValue, Consumer<T> saveConsumer) {
        super(fieldName, clazz.getEnumConstants(), value, resetButtonKey, defaultValue, saveConsumer, v -> DEFAULT_NAME_PROVIDER.apply(v));
    }
    
    @Deprecated
    public EnumListEntry(String fieldName, Class<T> clazz, T value, String resetButtonKey, Supplier<T> defaultValue, Consumer<T> saveConsumer, Function<Enum, String> enumNameProvider) {
        super(fieldName, clazz.getEnumConstants(), value, resetButtonKey, defaultValue, saveConsumer, v -> enumNameProvider.apply(v), null);
    }
    
    @Deprecated
    public EnumListEntry(String fieldName, Class<T> clazz, T value, String resetButtonKey, Supplier<T> defaultValue, Consumer<T> saveConsumer, Function<Enum, String> enumNameProvider, Supplier<Optional<String[]>> tooltipSupplier) {
        super(fieldName, clazz.getEnumConstants(), value, resetButtonKey, defaultValue, saveConsumer, v -> enumNameProvider.apply(v), tooltipSupplier, false);
    }
    
    @Deprecated
    public EnumListEntry(String fieldName, Class<T> clazz, T value, String resetButtonKey, Supplier<T> defaultValue, Consumer<T> saveConsumer, Function<Enum, String> enumNameProvider, Supplier<Optional<String[]>> tooltipSupplier, boolean requiresRestart) {
        super(fieldName, clazz.getEnumConstants(), value, resetButtonKey, defaultValue, saveConsumer, v -> enumNameProvider.apply(v), tooltipSupplier, requiresRestart);
    }
    
}
