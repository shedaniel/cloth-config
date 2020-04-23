package me.shedaniel.clothconfig2.api;

import com.google.common.collect.Lists;
import me.shedaniel.math.Point;
import net.minecraft.text.Text;

import java.util.Collections;
import java.util.List;

public class QueuedTooltip {
    
    private Point location;
    private List<Text> text;
    
    private QueuedTooltip(Point location, List<Text> text) {
        this.location = location;
        this.text = Collections.unmodifiableList(text);
    }
    
    public static QueuedTooltip create(Point location, List<Text> text) {
        return new QueuedTooltip(location, text);
    }
    
    public static QueuedTooltip create(Point location, Text... text) {
        return QueuedTooltip.create(location, Lists.newArrayList(text));
    }
    
    public Point getPoint() {
        return location;
    }
    
    public int getX() {
        return location.x;
    }
    
    public int getY() {
        return location.y;
    }
    
    public List<Text> getText() {
        return text;
    }
    
}