package me.shedaniel.clothconfig2.api;

import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.*;

public abstract class Dependency<T, E extends AbstractConfigEntry<T>> {
    
    private final E entry;
    private final Collection<T> values = new ArrayList<>();
    
    private boolean shouldHide = false;
    
    public Dependency(E entry, T value) {
        this.entry = entry;
        setExpectedValue(value);
    }
    
    public boolean check() {
        T value = getEntry().getValue();
        return getValues().stream().anyMatch(value::equals);
    }
    
    public void setExpectedValue(T value) {
        values.clear();
        values.add(value);
    }
    
    public void addExpectedValues(T... values) {
        this.values.addAll(Arrays.asList(values));
    }
    
    public E getEntry() {
        return entry;
    }
    
    public Collection<T> getValues() {
        return values.stream().toList();
    }
    
    public boolean isHiddenWhenDisabled() {
        return shouldHide;
    }
    
    public void setHiddenWhenDisabled(boolean shouldHide) {
        this.shouldHide = shouldHide;
    }
    
    protected abstract Component getValueText(T value);
    
    public <TooltipType> Optional<Component[]> getTooltipFor(TooltipListEntry<TooltipType> tooltipEntry) {
        // TODO consider showing the tooltip whenever a dependency exists?
        if (check())
            return Optional.empty();
        
        E entry = getEntry();
        Collection<T> values = getValues();

        if (values.isEmpty())
            throw new IllegalStateException("Expected at least one value to be set");
        
        Component dependencyName = MutableComponent.create(entry.getFieldName().getContents())
                .withStyle(ChatFormatting.BOLD);
        
        List<MutableComponent> valueNames = values.stream()
                .distinct()
                .map(this::getValueText)
                .map(component -> MutableComponent.create(component.getContents()))
                .map(component -> component.withStyle(ChatFormatting.BOLD))
                .toList();

        List<Component> tooltip = new ArrayList<>();
        tooltip.add(switch (valueNames.size()) {
            case 1 -> Component.translatable("text.cloth-config.dependencies.one", dependencyName, valueNames.get(0));
            case 2 -> Component.translatable("text.cloth-config.dependencies.two", dependencyName, valueNames.get(0), valueNames.get(1));
            default -> Component.translatable("text.cloth-config.dependencies.many", dependencyName);
        });
        
        // If many, give each value its own line
        if (valueNames.size() > 2) {
            valueNames.forEach(value -> {
                tooltip.add(Component.translatable("text.cloth-config.dependencies.listEntry", value));
            });
        }
        
        return Optional.of(tooltip.toArray(new Component[0]));
    }
}
