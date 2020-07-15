package me.shedaniel.clothconfig2.forge.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClothConfigTabButton extends AbstractButton {
    
    private final int index;
    private final ClothConfigScreen screen;
    
    public ClothConfigTabButton(ClothConfigScreen screen, int index, int int_1, int int_2, int int_3, int int_4, ITextComponent string_1) {
        super(int_1, int_2, int_3, int_4, string_1);
        this.index = index;
        this.screen = screen;
    }
    
    @Override
    public void onPress() {
        if (index != -1)
            screen.selectedCategoryIndex = index;
        screen.init(Minecraft.getInstance(), screen.width, screen.height);
    }
    
    @Override
    public void render(MatrixStack matrices, int int_1, int int_2, float float_1) {
        active = index != screen.selectedCategoryIndex;
        super.render(matrices, int_1, int_2, float_1);
    }
    
    @Override
    protected boolean clicked(double double_1, double double_2) {
        return visible && active && isMouseOver(double_1, double_2);
    }
    
    @Override
    public boolean isMouseOver(double double_1, double double_2) {
        return this.active && this.visible && double_1 >= this.x && double_2 >= this.y && double_1 < this.x + this.width && double_2 < this.y + this.height && double_1 >= 20 && double_1 < screen.width - 20;
    }
}
