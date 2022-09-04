package me.shedaniel.clothconfig2.gui.entries;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractNumberListEntry<T> extends TextFieldListEntry<T> {
    private static final Function<String, String> stripCharacters = s -> {
        StringBuilder builder = new StringBuilder();
        char[] chars = s.toCharArray();
        for (char c : chars)
            if (Character.isDigit(c) || c == '-' || c == '.')
                builder.append(c);
        
        return builder.toString();
    };
 
    @ApiStatus.Internal
    @Deprecated
    protected AbstractNumberListEntry(Component fieldName, T original, Component resetButtonKey, Supplier<T> defaultValue) {
        super(fieldName, original, resetButtonKey, defaultValue);
    }
    
    @ApiStatus.Internal
    @Deprecated
    protected AbstractNumberListEntry(Component fieldName, T original, Component resetButtonKey, Supplier<T> defaultValue, Supplier<Optional<Component[]>> tooltipSupplier) {
        super(fieldName, original, resetButtonKey, defaultValue, tooltipSupplier);
    }
    
    @ApiStatus.Internal
    @Deprecated
    protected AbstractNumberListEntry(Component fieldName, T original, Component resetButtonKey, Supplier<T> defaultValue, Supplier<Optional<Component[]>> tooltipSupplier, boolean requiresRestart) {
        super(fieldName, original, resetButtonKey, defaultValue, tooltipSupplier, requiresRestart);
    }
    
    @Override
    protected String stripAddText(String s) {
        return stripCharacters.apply(s);
    }
}
