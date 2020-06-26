package me.shedaniel.clothconfig2.forge.gui.entries;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @param <T>    the configuration object type
 * @param <C>    the cell type
 * @param <SELF> the "curiously recurring template pattern" type parameter
 * @see BaseListEntry
 */
@OnlyIn(Dist.CLIENT)
public abstract class AbstractListListEntry<T, C extends AbstractListListEntry.AbstractListCell<T, C, SELF>, SELF extends AbstractListListEntry<T, C, SELF>> extends BaseListEntry<T, C, SELF> {
    
    protected final BiFunction<T, SELF, C> createNewCell;
    protected Function<T, Optional<ITextComponent>> cellErrorSupplier;
    
    @ApiStatus.Internal
    public AbstractListListEntry(ITextComponent fieldName, List<T> value, boolean defaultExpanded, Supplier<Optional<ITextComponent[]>> tooltipSupplier, Consumer<List<T>> saveConsumer, Supplier<List<T>> defaultValue, ITextComponent resetButtonKey, boolean requiresRestart, boolean deleteButtonEnabled, boolean insertInFront, BiFunction<T, SELF, C> createNewCell) {
        super(fieldName, tooltipSupplier, defaultValue, abstractListListEntry -> createNewCell.apply(null, abstractListListEntry), saveConsumer, resetButtonKey, requiresRestart, deleteButtonEnabled, insertInFront);
        this.createNewCell = createNewCell;
        for (T f : value)
            cells.add(createNewCell.apply(f, this.self()));
        this.widgets.addAll(cells);
        setExpanded(defaultExpanded);
    }
    
    public Function<T, Optional<ITextComponent>> getCellErrorSupplier() {
        return cellErrorSupplier;
    }
    
    public void setCellErrorSupplier(Function<T, Optional<ITextComponent>> cellErrorSupplier) {
        this.cellErrorSupplier = cellErrorSupplier;
    }
    
    @Override
    public List<T> getValue() {
        return cells.stream().map(C::getValue).collect(Collectors.toList());
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
    @ApiStatus.Internal
    public static abstract class AbstractListCell<T, SELF extends AbstractListCell<T, SELF, OUTER_SELF>, OUTER_SELF extends AbstractListListEntry<T, SELF, OUTER_SELF>> extends BaseListCell {
        protected final OUTER_SELF listListEntry;
        
        public AbstractListCell(@Nullable T value, OUTER_SELF listListEntry) {
            this.listListEntry = listListEntry;
            this.setErrorSupplier(() -> Optional.ofNullable(listListEntry.cellErrorSupplier).flatMap(cellErrorFn -> cellErrorFn.apply(this.getValue())));
        }
        
        public abstract T getValue();
    }
    
}
