package me.shedaniel.math.api;

import org.jetbrains.annotations.ApiStatus;

/**
 * @deprecated Use {@link me.shedaniel.math.Dimension}
 */
@Deprecated
@ApiStatus.ScheduledForRemoval
public class Dimension extends me.shedaniel.math.Dimension {
    public Dimension() {
        super();
    }
    
    public Dimension(Dimension d) {
        super(d);
    }
    
    public Dimension(me.shedaniel.math.Dimension d) {
        super(d);
    }
    
    public Dimension(int width, int height) {
        super(width, height);
    }
    
    public Dimension getSize() {
        return new Dimension(width, height);
    }
    
    public void setSize(Dimension d) {
        setSize(d.width, d.height);
    }
}