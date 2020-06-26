package me.shedaniel.clothconfig2.forge.api;

import me.shedaniel.math.Point;
import net.minecraft.util.text.ITextComponent;
import java.util.List;

public interface Tooltip {
    static Tooltip of(Point location, ITextComponent... text) {
        return QueuedTooltip.create(location, text);
    }
    Point getPoint();
    
    default int getX() {
        return getPoint().getX();
    }
    
    default int getY() {
        return getPoint().getY();
    }
    
    List<ITextComponent> getText();
}
