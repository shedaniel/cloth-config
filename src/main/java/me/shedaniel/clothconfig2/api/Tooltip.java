package me.shedaniel.clothconfig2.api;

import me.shedaniel.math.Point;
import net.minecraft.text.Text;

import java.util.List;

public interface Tooltip {
    static Tooltip of(Point location, Text... text) {
        return QueuedTooltip.create(location, text);
    }
    Point getPoint();
    
    default int getX() {
        return getPoint().getX();
    }
    
    default int getY() {
        return getPoint().getY();
    }
    
    List<Text> getText();
}
