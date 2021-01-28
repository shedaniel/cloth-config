package me.shedaniel.clothconfig2.api;

import me.shedaniel.math.Point;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import java.util.List;

public interface Tooltip {
    static Tooltip of(Point location, Component... text) {
        return QueuedTooltip.create(location, text);
    }
    
    static Tooltip of(Point location, FormattedText... text) {
        return QueuedTooltip.create(location, text);
    }
    
    static Tooltip of(Point location, FormattedCharSequence... text) {
        return QueuedTooltip.create(location, text);
    }
    
    Point getPoint();
    
    default int getX() {
        return getPoint().getX();
    }
    
    default int getY() {
        return getPoint().getY();
    }
    
    List<FormattedCharSequence> getText();
}
