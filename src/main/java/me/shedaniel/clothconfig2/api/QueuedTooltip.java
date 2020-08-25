package me.shedaniel.clothconfig2.api;

import me.shedaniel.math.Point;
import net.minecraft.class_5481;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class QueuedTooltip implements Tooltip {
    private Point location;
    private List<class_5481> text;
    
    private QueuedTooltip(Point location, List<class_5481> text) {
        this.location = location;
        this.text = Collections.unmodifiableList(text);
    }
    
    public static QueuedTooltip create(Point location, List<Text> text) {
        return new QueuedTooltip(location, Language.getInstance().method_30933((List) text));
    }
    
    public static QueuedTooltip create(Point location, Text... text) {
        return QueuedTooltip.create(location, Arrays.asList(text));
    }
    
    public static QueuedTooltip create(Point location, class_5481... text) {
        return new QueuedTooltip(location, Arrays.asList(text));
    }
    
    public static QueuedTooltip create(Point location, StringRenderable... text) {
        return new QueuedTooltip(location, Language.getInstance().method_30933(Arrays.asList(text)));
    }
    
    @Override
    public Point getPoint() {
        return location;
    }
    
    @ApiStatus.Internal
    @Override
    public List<class_5481> getText() {
        return text;
    }
}