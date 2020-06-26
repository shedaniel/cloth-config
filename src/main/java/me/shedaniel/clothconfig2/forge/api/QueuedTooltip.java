package me.shedaniel.clothconfig2.forge.api;

import me.shedaniel.math.Point;
import net.minecraft.util.text.ITextComponent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class QueuedTooltip implements Tooltip {
    private Point location;
    private List<ITextComponent> text;
    
    private QueuedTooltip(Point location, List<ITextComponent> text) {
        this.location = location;
        this.text = Collections.unmodifiableList(text);
    }
    
    public static QueuedTooltip create(Point location, List<ITextComponent> text) {
        return new QueuedTooltip(location, text);
    }
    
    public static QueuedTooltip create(Point location, ITextComponent... text) {
        return QueuedTooltip.create(location, Arrays.asList(text));
    }
    
    @Override
    public Point getPoint() {
        return location;
    }
    
    @Override
    public List<ITextComponent> getText() {
        return text;
    }
}