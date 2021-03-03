package me.shedaniel.clothconfig2.impl.builders;

import me.shedaniel.clothconfig2.gui.entries.IntegerSliderListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class IntSliderListBuilder extends AbstractSliderListBuilder<Integer, IntegerSliderListEntry.IntegerSliderListCell, IntegerSliderListEntry, IntSliderListBuilder> {
    public IntSliderListBuilder(Component resetButtonKey, Component fieldNameKey, List<Integer> value, int min, int max) {
        super(resetButtonKey, fieldNameKey, value, min, max);
    }

    @Override
    protected IntegerSliderListEntry buildEntry(Component fieldNameKey, Integer min, Integer max, List<Integer> value, boolean expanded, Supplier<Optional<Component[]>> tooltipSupplier, Consumer<List<Integer>> saveConsumer, Supplier<List<Integer>> defaultValue, Integer cellDefaultValue, Component resetButtonKey, boolean requiresRestart, boolean deleteButtonEnabled, boolean insertInFront) {
        return new IntegerSliderListEntry(fieldNameKey, min, max, value, expanded, tooltipSupplier, saveConsumer, defaultValue, cellDefaultValue, resetButtonKey, requiresRestart, deleteButtonEnabled, insertInFront);
    }
}
