package me.shedaniel.clothconfig2.api;

import me.shedaniel.math.Point;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class QueuedTooltip implements Tooltip {
    private Point location;
    private List<OrderedText> text;
    
    private QueuedTooltip(Point location, List<OrderedText> text) {
        this.location = location;
        this.text = Collections.unmodifiableList(text);
    }
    
    public static QueuedTooltip create(Point location, List<Text> text) {
        return new QueuedTooltip(location, Language.getInstance().reorder((List) text));
    }
    
    public static QueuedTooltip create(Point location, Text... text) {
        return QueuedTooltip.create(location, Arrays.asList(text));
    }
    
    public static QueuedTooltip create(Point location, OrderedText... text) {
        return new QueuedTooltip(location, Arrays.asList(text));
    }
    
    public static QueuedTooltip create(Point location, StringVisitable... text) {
        return new QueuedTooltip(location, Language.getInstance().reorder(Arrays.asList(text)));
    }
    
    @Override
    public Point getPoint() {
        return location;
    }
    
    @ApiStatus.Internal
    @Override
    public List<OrderedText> getText() {
        return text;
    }
}