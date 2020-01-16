package me.shedaniel.clothconfig2.gui.entries;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @param <T>    the configuration object type
 * @param <C>    the cell type
 * @param <SELF> the "curiously recurring template pattern" type parameter
 * @see BaseListEntry
 */
abstract class AbstractListListEntry<T, C extends AbstractListListEntry.AbstractListCell<T, C, SELF>, SELF extends AbstractListListEntry<T, C, SELF>>
        extends BaseListEntry<T, C, SELF> {

    protected final BiFunction<T, SELF, C> createNewCell;
    protected Function<T, Optional<String>> cellErrorSupplier;

    @Deprecated
    public AbstractListListEntry(String fieldName, List<T> value, boolean defaultExpended, Supplier<Optional<String[]>> tooltipSupplier, Consumer<List<T>> saveConsumer, Supplier<List<T>> defaultValue, String resetButtonKey) {
        this(fieldName, value, defaultExpended, tooltipSupplier, saveConsumer, defaultValue, resetButtonKey, false, null);
    }

    @Deprecated
    public AbstractListListEntry(String fieldName, List<T> value, boolean defaultExpended, Supplier<Optional<String[]>> tooltipSupplier, Consumer<List<T>> saveConsumer, Supplier<List<T>> defaultValue, String resetButtonKey, boolean requiresRestart, BiFunction<T, SELF, C> createNewCell) {
        super(fieldName, tooltipSupplier, defaultValue, abstractListListEntry -> createNewCell.apply(null, abstractListListEntry), saveConsumer, resetButtonKey, requiresRestart);
        this.createNewCell = createNewCell;
        for (T f : value)
            cells.add(createNewCell.apply(f, this.self()));
        this.widgets.addAll(cells);
        expended = defaultExpended;
    }

    public Function<T, Optional<String>> getCellErrorSupplier() {
        return cellErrorSupplier;
    }

    public void setCellErrorSupplier(Function<T, Optional<String>> cellErrorSupplier) {
        this.cellErrorSupplier = cellErrorSupplier;
    }

    @Override
    public List<T> getValue() {
        return cells.stream().map(AbstractListCell::getValue).collect(Collectors.toList());
    }

    @Override
    protected C getFromValue(T value) {
        return createNewCell.apply(value, this.self());
    }

    /**
     * @param <T>           the configuration object type
     * @param <SELF>        the "curiously recurring template pattern" type parameter for this class
     * @param <OUTER_SELF>> the "curiously recurring template pattern" type parameter for the outer class
     * @see AbstractListListEntry
     */
    static abstract class AbstractListCell<T, SELF extends AbstractListCell<T, SELF, OUTER_SELF>, OUTER_SELF extends AbstractListListEntry<T, SELF, OUTER_SELF>> extends BaseListCell {
        protected final T value;
        protected final AbstractListListEntry<T, SELF, OUTER_SELF> listListEntry;

        public AbstractListCell(T value, AbstractListListEntry<T, SELF, OUTER_SELF> listListEntry) {
            this.value = value;
            this.listListEntry = listListEntry;
        }

        public abstract T getValue();

    }

}
