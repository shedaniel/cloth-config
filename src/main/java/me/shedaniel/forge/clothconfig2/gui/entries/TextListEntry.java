package me.shedaniel.forge.clothconfig2.gui.entries;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class TextListEntry extends TooltipListEntry<Object> {
    
    private int savedWidth = -1;
    private int color;
    private String text;
    
    
    @Deprecated
    public TextListEntry(String fieldName, String text) {
        this(fieldName, text, -1);
    }
    
    
    @Deprecated
    public TextListEntry(String fieldName, String text, int color) {
        this(fieldName, text, color, null);
    }
    
    
    @Deprecated
    public TextListEntry(String fieldName, String text, int color, Supplier<Optional<String[]>> tooltipSupplier) {
        super(fieldName, tooltipSupplier);
        this.text = text;
        this.color = color;
    }
    
    @Override
    public void render(int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
        super.render(index, y, x, entryWidth, entryHeight, mouseX, mouseY, isSelected, delta);
        this.savedWidth = entryWidth;
        int yy = y + 4;
        List<String> strings = Minecraft.getInstance().fontRenderer.listFormattedStringToWidth(text, savedWidth);
        for (String string : strings) {
            Minecraft.getInstance().fontRenderer.drawStringWithShadow(string, x, yy, color);
            yy += Minecraft.getInstance().fontRenderer.FONT_HEIGHT + 3;
        }
    }
    
    @Override
    public int getItemHeight() {
        if (savedWidth == -1)
            return 12;
        List<String> strings = Minecraft.getInstance().fontRenderer.listFormattedStringToWidth(text, savedWidth);
        if (strings.isEmpty())
            return 0;
        return 15 + strings.size() * 12;
    }
    
    @Override
    public void save() {
        
    }
    
    @Override
    public Object getValue() {
        return null;
    }
    
    @Override
    public Optional<Object> getDefaultValue() {
        return Optional.empty();
    }
    
    @Override
    public List<? extends IGuiEventListener> children() {
        return Collections.emptyList();
    }
    
}
