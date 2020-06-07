package me.shedaniel.clothconfig2.gui.entries;

import com.google.common.collect.Lists;
import me.shedaniel.clothconfig2.api.AbstractConfigEntry;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ReferenceProvider;
import me.shedaniel.clothconfig2.gui.entries.NestedListListEntry.NestedListCell;
import me.shedaniel.clothconfig2.gui.widget.DynamicEntryListWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @param <T>     the configuration object type
 * @param <INNER> the type of the inner config entry
 */
@Environment(EnvType.CLIENT)
public final class NestedListListEntry<T, INNER extends AbstractConfigListEntry<T>> extends AbstractListListEntry<T, NestedListCell<T, INNER>, NestedListListEntry<T, INNER>> {
    private final List<AbstractConfigEntry<?>> referencableEntries = Lists.newArrayList();
    
    @ApiStatus.Internal
    @Deprecated
    public NestedListListEntry(Text fieldName, List<T> value, boolean defaultExpanded, Supplier<Optional<Text[]>> tooltipSupplier, Consumer<List<T>> saveConsumer, Supplier<List<T>> defaultValue, Text resetButtonKey, boolean deleteButtonEnabled, boolean insertInFront, BiFunction<T, NestedListListEntry<T, INNER>, INNER> createNewCell) {
        super(fieldName, value, defaultExpanded, null, null, defaultValue, resetButtonKey, false, deleteButtonEnabled, insertInFront, (t, nestedListListEntry) -> new NestedListCell<>(t, nestedListListEntry, createNewCell.apply(t, nestedListListEntry)));
        for (NestedListCell<T, INNER> cell : cells) {
            referencableEntries.add(cell.nestedEntry);
        }
        setReferencableEntries(referencableEntries);
    }
    
    @Override
    public NestedListListEntry<T, INNER> self() {
        return this;
    }
    
    /**
     * @param <T> the configuration object type
     * @see NestedListListEntry
     */
    public static class NestedListCell<T, INNER extends AbstractConfigListEntry<T>> extends AbstractListListEntry.AbstractListCell<T, NestedListCell<T, INNER>, NestedListListEntry<T, INNER>> implements ReferenceProvider<T> {
        private final INNER nestedEntry;
        
        @ApiStatus.Internal
        public NestedListCell(@Nullable T value, NestedListListEntry<T, INNER> listListEntry, INNER nestedEntry) {
            super(value, listListEntry);
            this.nestedEntry = nestedEntry;
        }
        
        @Override
        public AbstractConfigEntry<T> provideReferenceEntry() {
            return nestedEntry;
        }
        
        @Override
        public T getValue() {
            return nestedEntry.getValue();
        }
        
        @Override
        public Optional<Text> getError() {
            return nestedEntry.getError();
        }
        
        @Override
        public int getCellHeight() {
            return nestedEntry.getItemHeight();
        }
        
        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
            nestedEntry.setParent((DynamicEntryListWidget) listListEntry.getParent());
            nestedEntry.setScreen(listListEntry.getConfigScreen());
            nestedEntry.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isSelected, delta);
        }
        
        @Override
        public List<? extends Element> children() {
            return Collections.singletonList(nestedEntry);
        }
        
        @Override
        public boolean isRequiresRestart() {
            return nestedEntry.isRequiresRestart();
        }
        
        @Override
        public void updateSelected(boolean isSelected) {
            this.nestedEntry.updateSelected(isSelected);
        }
        
        @Override
        public boolean isEdited() {
            return super.isEdited() || nestedEntry.isEdited();
        }
        
        @Override
        public void onAdd() {
            super.onAdd();
            listListEntry.referencableEntries.add(nestedEntry);
            listListEntry.requestReferenceRebuilding();
        }
        
        @Override
        public void onDelete() {
            super.onDelete();
            listListEntry.referencableEntries.remove(nestedEntry);
            listListEntry.requestReferenceRebuilding();
        }
    }
}
