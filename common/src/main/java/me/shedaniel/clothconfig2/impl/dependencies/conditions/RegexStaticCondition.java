package me.shedaniel.clothconfig2.impl.dependencies.conditions;

import me.shedaniel.clothconfig2.api.dependencies.conditions.RegexCondition;
import net.minecraft.network.chat.Component;

import java.util.regex.Pattern;

public class RegexStaticCondition extends AbstractCondition<String> implements RegexCondition {
    
    private final Pattern pattern;
    
    public RegexStaticCondition(Pattern pattern) {
        this(pattern, false);
    }
    public RegexStaticCondition(Pattern pattern, boolean inverted) {
        super(inverted);
        this.pattern = pattern;
    }
    
    public static RegexStaticCondition compile(String pattern) {
        return new RegexStaticCondition(Pattern.compile(pattern));
    }
    
    @Override
    public Pattern getValue() {
        return pattern;
    }
    
    @Override
    public Component getText(boolean inverted) {
        // TODO "matches pattern"
        return null;
    }
}
