package me.shedaniel.math.api;

import org.jetbrains.annotations.ApiStatus;

/**
 * @deprecated Use {@link me.shedaniel.math.Rectangle}
 */
@Deprecated
@ApiStatus.ScheduledForRemoval
public class Rectangle extends me.shedaniel.math.Rectangle {
    public Rectangle() {
        super();
    }
    
    public Rectangle(Rectangle r) {
        super(r);
    }
    
    public Rectangle(int width, int height) {
        super(width, height);
    }
    
    public Rectangle(Point p, Dimension d) {
        super(p, d);
    }
    
    public Rectangle(Point p) {
        super(p);
    }
    
    public Rectangle(Dimension d) {
        super(d);
    }
    
    public Rectangle(me.shedaniel.math.Rectangle r) {
        super(r);
    }
    
    public Rectangle(me.shedaniel.math.Point p, me.shedaniel.math.Dimension d) {
        super(p, d);
    }
    
    public Rectangle(me.shedaniel.math.Point p) {
        super(p);
    }
    
    public Rectangle(me.shedaniel.math.Dimension d) {
        super(d);
    }
    
    public Rectangle(int x, int y, int width, int height) {
        super(x, y, width, height);
    }
    
    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
    
    public void setBounds(Rectangle r) {
        setBounds((me.shedaniel.math.Rectangle) r);
    }
    
    @Override
    public Point getLocation() {
        return new Point(x, y);
    }
    
    public void setLocation(Point p) {
        setLocation((me.shedaniel.math.Point) p);
    }
    
    @Override
    public Rectangle clone() {
        return getBounds();
    }
    
    public Dimension getSize() {
        return new Dimension(width, height);
    }
    
    public void setSize(Dimension d) {
        setSize((me.shedaniel.math.Dimension) d);
    }
    
    public boolean contains(Point p) {
        return contains(p.x, p.y);
    }
    
    public boolean contains(Rectangle r) {
        return contains(r.x, r.y, r.width, r.height);
    }
    
    public boolean intersects(Rectangle r) {
        return intersects((me.shedaniel.math.Rectangle) r);
    }
    
    public Rectangle intersection(Rectangle r) {
        return new Rectangle(intersection((me.shedaniel.math.Rectangle) r));
    }
    
    public Rectangle union(Rectangle r) {
        return new Rectangle(union((me.shedaniel.math.Rectangle) r));
    }
    
    public void add(Point r) {
        add((me.shedaniel.math.Point) r);
    }
    
    public void add(Rectangle r) {
        add((me.shedaniel.math.Rectangle) r);
    }
}