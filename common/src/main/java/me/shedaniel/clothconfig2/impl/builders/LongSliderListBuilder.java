package me.shedaniel.clothconfig2.impl.builders;

import me.shedaniel.clothconfig2.gui.entries.LongSliderListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class LongSliderListBuilder extends AbstractSliderListBuilder<Long, LongSliderListEntry.LongSliderListCell, LongSliderListEntry, LongSliderListBuilder> {
    public LongSliderListBuilder(Component resetButtonKey, Component fieldNameKey, List<Long> value, long min, long max) {
        super(resetButtonKey, fieldNameKey, value, min, max);
    }

    @Override
    protected LongSliderListEntry buildEntry(Component fieldNameKey, Long min, Long max, List<Long> value, boolean expanded, Supplier<Optional<Component[]>> tooltipSupplier, Consumer<List<Long>> saveConsumer, Supplier<List<Long>> defaultValue, Long cellDefaultValue, Component resetButtonKey, boolean requiresRestart, boolean deleteButtonEnabled, boolean insertInFront) {
        return new LongSliderListEntry(fieldNameKey, min, max, value, expanded, tooltipSupplier, saveConsumer, defaultValue, cellDefaultValue, resetButtonKey, requiresRestart, deleteButtonEnabled, insertInFront);
    }
}
