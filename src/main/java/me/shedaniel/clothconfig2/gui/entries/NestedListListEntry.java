package me.shedaniel.clothconfig2.gui.entries;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.gui.entries.NestedListListEntry.NestedListCell;
import net.minecraft.client.gui.Element;
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
public final class NestedListListEntry<T, INNER extends AbstractConfigListEntry<T>> extends AbstractListListEntry<T, NestedListCell<T, INNER>, NestedListListEntry<T, INNER>> {
    
    @ApiStatus.Internal
    public NestedListListEntry(String fieldName, List<T> value, boolean defaultExpanded, Supplier<Optional<String[]>> tooltipSupplier, Consumer<List<T>> saveConsumer, Supplier<List<T>> defaultValue, String resetButtonKey, boolean deleteButtonEnabled, boolean insertInFront, BiFunction<T, NestedListListEntry<T, INNER>, INNER> createNewCell) {
        super(fieldName, value, defaultExpanded, null, null, defaultValue, resetButtonKey, false, deleteButtonEnabled, insertInFront, (t, nestedListListEntry) -> new NestedListCell<>(t, nestedListListEntry, createNewCell.apply(t, nestedListListEntry)));
    }
    
    @Override
    public boolean isRequiresRestart() {
        return cells.stream().anyMatch(NestedListCell::isRequiresRestart);
    }
    
    @Override
    public void setRequiresRestart(boolean requiresRestart) {
    }
    
    @Override
    public NestedListListEntry<T, INNER> self() {
        return this;
    }
    
    /**
     * @param <T> the configuration object type
     * @see NestedListListEntry
     */
    public static class NestedListCell<T, INNER extends AbstractConfigListEntry<T>> extends AbstractListListEntry.AbstractListCell<T, NestedListCell<T, INNER>, NestedListListEntry<T, INNER>> {
        
        private final INNER nestedEntry;
        
        @ApiStatus.Internal
        public NestedListCell(@Nullable T value, NestedListListEntry<T, INNER> listListEntry, INNER nestedEntry) {
            super(value, listListEntry);
            this.nestedEntry = nestedEntry;
        }
        
        @Override
        public T getValue() {
            return nestedEntry.getValue();
        }
        
        @Override
        public Optional<String> getError() {
            return nestedEntry.getError();
        }
        
        @Override
        public int getCellHeight() {
            return nestedEntry.getItemHeight();
        }
        
        @Override
        public void render(int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
            nestedEntry.setScreen(listListEntry.getScreen());
            nestedEntry.render(index, y, x, entryWidth, entryHeight, mouseX, mouseY, isSelected, delta);
        }
        
        @Override
        public List<? extends Element> children() {
            return Collections.singletonList(nestedEntry);
        }
        
        private boolean isRequiresRestart() {
            return nestedEntry.isRequiresRestart();
        }
    }
    
}
