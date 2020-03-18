package me.shedaniel.math.api;

import org.jetbrains.annotations.ApiStatus;

/**
 * @deprecated Use {@link me.shedaniel.math.Point}
 */
@Deprecated
@ApiStatus.ScheduledForRemoval
public class Point extends me.shedaniel.math.Point {
    public Point() {
        super();
    }
    
    public Point(Point p) {
        super(p);
    }
    
    public Point(me.shedaniel.math.Point p) {
        super(p);
    }
    
    public Point(double x, double y) {
        super(x, y);
    }
    
    public Point(int x, int y) {
        super(x, y);
    }
    
    public Point getLocation() {
        return new Point(x, y);
    }
    
    public Point clone() {
        return getLocation();
    }
}