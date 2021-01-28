package me.shedaniel.clothconfig2.gui.entries;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.ApiStatus;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class TextListEntry extends TooltipListEntry<Object> {
    
    private int savedWidth = -1;
    private final int color;
    private final Component text;
    
    @ApiStatus.Internal
    @Deprecated
    public TextListEntry(Component fieldName, Component text) {
        this(fieldName, text, -1);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public TextListEntry(Component fieldName, Component text, int color) {
        this(fieldName, text, color, null);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public TextListEntry(Component fieldName, Component text, int color, Supplier<Optional<Component[]>> tooltipSupplier) {
        super(fieldName, tooltipSupplier);
        this.text = text;
        this.color = color;
    }
    
    @Override
    public void render(PoseStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
        this.savedWidth = entryWidth;
        int yy = y + 4;
        List<FormattedCharSequence> strings = Minecraft.getInstance().font.split(text, savedWidth);
        for (FormattedCharSequence string : strings) {
            Minecraft.getInstance().font.drawShadow(matrices, string, x, yy, color);
            yy += Minecraft.getInstance().font.lineHeight + 3;
        }
    }
    
    @Override
    public int getItemHeight() {
        if (savedWidth == -1)
            return 12;
        List<FormattedCharSequence> strings = Minecraft.getInstance().font.split(text, savedWidth);
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
    public List<? extends GuiEventListener> children() {
        return Collections.emptyList();
    }
    
}
