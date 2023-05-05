package me.shedaniel.clothconfig2.impl.dependencies;

/**
 * Represents an abstract dependency.
 * 
 * @param <C> The type used for the condition
 * @param <E> The depended-on element type
 */
public abstract class AbstractElementDependency<C, E> extends AbstractDependency<C> {
    private final E element;
    
    protected AbstractElementDependency(E element) {this.element = element;}
    
    /**
     * @return the element that is depended on
     */
    public final E getElement() {
        return element;
    }
    
}
