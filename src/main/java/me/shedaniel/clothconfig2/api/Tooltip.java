package me.shedaniel.clothconfig2.api;

import me.shedaniel.math.Point;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;

import java.util.List;

public interface Tooltip {
    static Tooltip of(Point location, Text... text) {
        return QueuedTooltip.create(location, text);
    }
    
    static Tooltip of(Point location, StringVisitable... text) {
        return QueuedTooltip.create(location, text);
    }
    
    static Tooltip of(Point location, OrderedText... text) {
        return QueuedTooltip.create(location, text);
    }
    
    Point getPoint();
    
    default int getX() {
        return getPoint().getX();
    }
    
    default int getY() {
        return getPoint().getY();
    }
    
    List<OrderedText> getText();
}
