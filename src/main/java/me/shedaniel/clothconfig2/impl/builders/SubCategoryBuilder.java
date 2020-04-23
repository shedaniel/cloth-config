package me.shedaniel.clothconfig2.impl.builders;

import com.google.common.collect.Lists;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.gui.entries.SubCategoryListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class SubCategoryBuilder extends FieldBuilder<Object, SubCategoryListEntry> implements List<AbstractConfigListEntry> {
    
    private final List<AbstractConfigListEntry> entries;
    private Function<List<AbstractConfigListEntry>, Optional<Text[]>> tooltipSupplier = list -> Optional.empty();
    private boolean expanded = false;
    
    public SubCategoryBuilder(Text resetButtonKey, Text fieldNameKey) {
        super(resetButtonKey, fieldNameKey);
        this.entries = Lists.newArrayList();
    }
    
    @Override
    public void requireRestart(boolean requireRestart) {
        throw new UnsupportedOperationException();
    }
    
    public SubCategoryBuilder setTooltipSupplier(Supplier<Optional<Text[]>> tooltipSupplier) {
        this.tooltipSupplier = list -> tooltipSupplier.get();
        return this;
    }
    
    public SubCategoryBuilder setTooltipSupplier(Function<List<AbstractConfigListEntry>, Optional<Text[]>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
        return this;
    }
    
    public SubCategoryBuilder setTooltip(Optional<Text[]> tooltip) {
        this.tooltipSupplier = list -> tooltip;
        return this;
    }
    
    public SubCategoryBuilder setTooltip(Text... tooltip) {
        this.tooltipSupplier = list -> Optional.ofNullable(tooltip);
        return this;
    }
    
    public SubCategoryBuilder setExpanded(boolean expanded) {
        this.expanded = expanded;
        return this;
    }
    
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public SubCategoryBuilder setExpended(boolean expanded) {
        return setExpanded(expanded);
    }
    
    @NotNull
    @Override
    public SubCategoryListEntry build() {
        SubCategoryListEntry entry = new SubCategoryListEntry(getFieldNameKey(), entries, expanded);
        entry.setTooltipSupplier(() -> tooltipSupplier.apply(entry.getValue()));
        return entry;
    }
    
    @Override
    public int size() {
        return entries.size();
    }
    
    @Override
    public boolean isEmpty() {
        return entries.isEmpty();
    }
    
    @Override
    public boolean contains(Object o) {
        return entries.contains(o);
    }
    
    @Override
    public @NotNull Iterator<AbstractConfigListEntry> iterator() {
        return entries.iterator();
    }
    
    @Override
    public Object[] toArray() {
        return entries.toArray();
    }
    
    @Override
    public <T> T[] toArray(T[] a) {
        return entries.toArray(a);
    }
    
    @Override
    public boolean add(AbstractConfigListEntry abstractConfigListEntry) {
        return entries.add(abstractConfigListEntry);
    }
    
    @Override
    public boolean remove(Object o) {
        return entries.remove(o);
    }
    
    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return entries.containsAll(c);
    }
    
    @Override
    public boolean addAll(@NotNull Collection<? extends AbstractConfigListEntry> c) {
        return entries.addAll(c);
    }
    
    @Override
    public boolean addAll(int index, @NotNull Collection<? extends AbstractConfigListEntry> c) {
        return entries.addAll(index, c);
    }
    
    @Override
    public boolean removeAll(Collection<?> c) {
        return entries.removeAll(c);
    }
    
    @Override
    public boolean retainAll(Collection<?> c) {
        return entries.retainAll(c);
    }
    
    @Override
    public void clear() {
        entries.clear();
    }
    
    @Override
    public AbstractConfigListEntry get(int index) {
        return entries.get(index);
    }
    
    @Override
    public AbstractConfigListEntry set(int index, AbstractConfigListEntry element) {
        return entries.set(index, element);
    }
    
    @Override
    public void add(int index, AbstractConfigListEntry element) {
        entries.add(index, element);
    }
    
    @Override
    public AbstractConfigListEntry remove(int index) {
        return entries.remove(index);
    }
    
    @Override
    public int indexOf(Object o) {
        return entries.indexOf(o);
    }
    
    @Override
    public int lastIndexOf(Object o) {
        return entries.lastIndexOf(o);
    }
    
    @Override
    public @NotNull ListIterator<AbstractConfigListEntry> listIterator() {
        return entries.listIterator();
    }
    
    @Override
    public @NotNull ListIterator<AbstractConfigListEntry> listIterator(int index) {
        return entries.listIterator(index);
    }
    
    @Override
    public @NotNull List<AbstractConfigListEntry> subList(int fromIndex, int toIndex) {
        return entries.subList(fromIndex, toIndex);
    }
    
}
