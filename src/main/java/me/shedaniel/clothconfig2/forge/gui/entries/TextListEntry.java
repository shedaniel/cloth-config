package me.shedaniel.clothconfig2.forge.gui.entries;

import net.minecraft.util.IReorderingProcessor;
import org.jetbrains.annotations.ApiStatus;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextListEntry extends TooltipListEntry<Object> {
    
    private int savedWidth = -1;
    private int color;
    private ITextComponent text;
    
    @ApiStatus.Internal
    @Deprecated
    public TextListEntry(ITextComponent fieldName, ITextComponent text) {
        this(fieldName, text, -1);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public TextListEntry(ITextComponent fieldName, ITextComponent text, int color) {
        this(fieldName, text, color, null);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public TextListEntry(ITextComponent fieldName, ITextComponent text, int color, Supplier<Optional<ITextComponent[]>> tooltipSupplier) {
        super(fieldName, tooltipSupplier);
        this.text = text;
        this.color = color;
    }
    
    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
        super.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isSelected, delta);
        this.savedWidth = entryWidth;
        int yy = y + 4;
        List<IReorderingProcessor> strings = Minecraft.getInstance().fontRenderer.func_238425_b_(text, savedWidth);
        for (IReorderingProcessor string : strings) {
            Minecraft.getInstance().fontRenderer.func_238407_a_(matrices, string, x, yy, color);
            yy += Minecraft.getInstance().fontRenderer.FONT_HEIGHT + 3;
        }
    }
    
    @Override
    public int getItemHeight() {
        if (savedWidth == -1)
            return 12;
        List<IReorderingProcessor> strings = Minecraft.getInstance().fontRenderer.func_238425_b_(text, savedWidth);
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
    public List<? extends IGuiEventListener> getEventListeners() {
        return Collections.emptyList();
    }
    
}
