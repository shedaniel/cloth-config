package me.shedaniel.clothconfig2.api.dependencies;

/**
 * @param <D> the {@link Dependency} type that will be built
 */
public interface DependencyBuilder<D extends Dependency> {
    /**
     * Build a dependency, applying any configuration made to this {@code DependencyBuilder} instance.
     * 
     * @return the built dependency
     */
    D build();
}
