package me.shedaniel.clothconfig2.api.dependencies;

/**
 * @param <D> the {@link Dependency} type that will be built
 */
public interface FinishDependencyBuilder<D extends Dependency, SELF extends FinishDependencyBuilder<D, SELF>> extends DependencyBuilder<SELF> {
    /**
     * Build a dependency, applying any configuration made to this {@code DependencyBuilder} instance.
     * 
     * @return the built dependency
     */
    D build();
}
