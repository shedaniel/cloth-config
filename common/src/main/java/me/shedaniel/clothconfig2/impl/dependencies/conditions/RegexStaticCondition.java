package me.shedaniel.clothconfig2.impl.dependencies.conditions;

import net.minecraft.network.chat.Component;

import java.util.regex.Pattern;

public class RegexStaticCondition extends AbstractCondition<String> {
    
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
    public boolean check(String value) {
        return inverted() != pattern.matcher(value).matches();
    }
    
    @Override
    public String getValue() {
        // FIXME this doesn't make sense for a regex matcher
        return pattern.pattern();
    }
    
    @Override
    public Component getText(boolean inverted) {
        // TODO "matches pattern"
        return null;
    }
}
