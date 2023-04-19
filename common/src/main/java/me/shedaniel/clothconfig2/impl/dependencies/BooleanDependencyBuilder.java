package me.shedaniel.clothconfig2.impl.dependencies;

import me.shedaniel.clothconfig2.api.dependencies.Dependency;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.impl.dependencies.conditions.StaticConditionBuilder;

public class BooleanDependencyBuilder extends ConfigEntryDependencyBuilder<Boolean, BooleanListEntry, BooleanDependencyBuilder> {
    
    private boolean useActualText = false;
    
    public BooleanDependencyBuilder(BooleanListEntry gui) {
        super(gui);
    }
    
    @Override
    public BooleanDependencyBuilder matching(Boolean value) {
        StaticConditionBuilder<Boolean> builder = new StaticConditionBuilder<>(value);
        if (useActualText)
            builder.describeUsing(gui);
        return matching(builder.build());
    }
    
    public BooleanDependencyBuilder useActualText(boolean useActualText) {
        this.useActualText = useActualText;
        return this;
    }
    
    @Override
    public Dependency build() {
        // Default condition is "true"
        if (conditions.isEmpty())
            this.matching(true);
    
        return finishBuilding(new BooleanDependency(this.gui));
    }
}
