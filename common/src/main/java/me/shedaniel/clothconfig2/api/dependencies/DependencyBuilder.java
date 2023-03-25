package me.shedaniel.clothconfig2.api.dependencies;

/**
 * @param <D> the {@link Dependency} type that will be built
 * @param <SELF> the type to be returned by chainable methods
 */
public interface DependencyBuilder<D extends Dependency, SELF extends DependencyBuilder<D, SELF>> {
    
    /**
     * Build a dependency, applying any configuration made to this {@code DependencyBuilder} instance.
     * 
     * @return the built dependency
     */
    D build();
    
    /**
     * @param shouldHide whether config entries with the dependency should completely disappear instead of appearing disabled
     * @return this instance, for chaining
     */
    SELF hideWhenNotMet(boolean shouldHide);
    
    /**
     * @return this instance, for chaining
     * @see #hideWhenNotMet(boolean) 
     */
    default SELF hideWhenNotMet() {
        @SuppressWarnings("unchecked") SELF self = (SELF) this;
        
        this.hideWhenNotMet(true);
        
        return self;
    }
    
    /**
     * @return this instance, for chaining
      @see #hideWhenNotMet(boolean) 
     */
    default SELF disableWhenNotMet() {
        @SuppressWarnings("unchecked") SELF self = (SELF) this;
        
        this.hideWhenNotMet(false);
        
        return self;
    }
}
