package me.shedaniel.clothconfig2.gui.entries;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class LongListListEntry extends AbstractTextFieldListListEntry<Long, LongListListEntry.LongListCell, LongListListEntry> {
    
    private long minimum, maximum;
    
    @ApiStatus.Internal
    @Deprecated
    public LongListListEntry(Component fieldName, List<Long> value, boolean defaultExpanded, Supplier<Optional<Component[]>> tooltipSupplier, Consumer<List<Long>> saveConsumer, Supplier<List<Long>> defaultValue, Component resetButtonKey) {
        this(fieldName, value, defaultExpanded, tooltipSupplier, saveConsumer, defaultValue, resetButtonKey, false);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public LongListListEntry(Component fieldName, List<Long> value, boolean defaultExpanded, Supplier<Optional<Component[]>> tooltipSupplier, Consumer<List<Long>> saveConsumer, Supplier<List<Long>> defaultValue, Component resetButtonKey, boolean requiresRestart) {
        this(fieldName, value, defaultExpanded, tooltipSupplier, saveConsumer, defaultValue, resetButtonKey, requiresRestart, true, true);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public LongListListEntry(Component fieldName, List<Long> value, boolean defaultExpanded, Supplier<Optional<Component[]>> tooltipSupplier, Consumer<List<Long>> saveConsumer, Supplier<List<Long>> defaultValue, Component resetButtonKey, boolean requiresRestart, boolean deleteButtonEnabled, boolean insertInFront) {
        super(fieldName, value, defaultExpanded, tooltipSupplier, saveConsumer, defaultValue, resetButtonKey, requiresRestart, deleteButtonEnabled, insertInFront, LongListCell::new);
        this.minimum = Long.MIN_VALUE;
        this.maximum = Long.MAX_VALUE;
    }
    
    public LongListListEntry setMaximum(long maximum) {
        this.maximum = maximum;
        return this;
    }
    
    public LongListListEntry setMinimum(long minimum) {
        this.minimum = minimum;
        return this;
    }
    
    @Override
    public LongListListEntry self() {
        return this;
    }
    
    public static class LongListCell extends AbstractTextFieldListListEntry.AbstractTextFieldListCell<Long, LongListCell, LongListListEntry> {
        
        public LongListCell(Long value, LongListListEntry listListEntry) {
            super(value, listListEntry);
        }
        
        @Nullable
        @Override
        protected Long substituteDefault(@Nullable Long value) {
            if (value == null)
                return 0L;
            else
                return value;
        }
        
        @Override
        protected boolean isValidText(@NotNull String text) {
            return text.chars().allMatch(c -> Character.isDigit(c) || c == '-');
        }
        
        public Long getValue() {
            try {
                return Long.valueOf(widget.getValue());
            } catch (NumberFormatException e) {
                return 0L;
            }
        }
        
        @Override
        public Optional<Component> getError() {
            try {
                long l = Long.parseLong(widget.getValue());
                if (l > listListEntry.maximum)
                    return Optional.of(new TranslatableComponent("text.cloth-config.error.too_large", listListEntry.maximum));
                else if (l < listListEntry.minimum)
                    return Optional.of(new TranslatableComponent("text.cloth-config.error.too_small", listListEntry.minimum));
            } catch (NumberFormatException ex) {
                return Optional.of(new TranslatableComponent("text.cloth-config.error.not_valid_number_long"));
            }
            return Optional.empty();
        }
    }
    
}
