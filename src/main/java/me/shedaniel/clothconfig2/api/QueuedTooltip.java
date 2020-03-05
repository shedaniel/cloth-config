package me.shedaniel.clothconfig2.api;

import com.google.common.collect.Lists;
import me.shedaniel.math.Point;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.List;

public class QueuedTooltip {
    
    private Point location;
    private List<String> text;
    
    private QueuedTooltip(Point location, List<String> text) {
        this.location = location;
        this.text = Collections.unmodifiableList(text);
    }
    
    public static QueuedTooltip create(me.shedaniel.math.api.Point location, List<String> text) {
        return new QueuedTooltip(location, text);
    }
    
    public static QueuedTooltip create(me.shedaniel.math.api.Point location, String... text) {
        return QueuedTooltip.create(location, Lists.newArrayList(text));
    }
    
    public static QueuedTooltip create(Point location, List<String> text) {
        return new QueuedTooltip(location, text);
    }
    
    public static QueuedTooltip create(Point location, String... text) {
        return QueuedTooltip.create(location, Lists.newArrayList(text));
    }
    
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public me.shedaniel.math.api.Point getLocation() {
        return new me.shedaniel.math.api.Point(getPoint());
    }
    
    public Point getPoint() {
        return location;
    }
    
    public int getX() {
        return getLocation().x;
    }
    
    public int getY() {
        return getLocation().y;
    }
    
    public List<String> getText() {
        return text;
    }
    
}