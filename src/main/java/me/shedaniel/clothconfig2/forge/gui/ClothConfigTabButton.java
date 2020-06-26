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
    public void func_230930_b_() {
        if (index != -1)
            screen.selectedCategoryIndex = index;
        screen.func_231158_b_(Minecraft.getInstance(), screen.field_230708_k_, screen.field_230709_l_);
    }
    
    @Override
    public void func_230430_a_(MatrixStack matrices, int int_1, int int_2, float float_1) {
        field_230693_o_ = index != screen.selectedCategoryIndex;
        super.func_230430_a_(matrices, int_1, int_2, float_1);
    }
    
    @Override
    protected boolean func_230992_c_(double double_1, double double_2) {
        return field_230694_p_ && field_230693_o_ && func_231047_b_(double_1, double_2);
    }
    
    @Override
    public boolean func_231047_b_(double double_1, double double_2) {
        return this.field_230693_o_ && this.field_230694_p_ && double_1 >= this.field_230690_l_ && double_2 >= this.field_230691_m_ && double_1 < this.field_230690_l_ + this.field_230688_j_ && double_2 < this.field_230691_m_ + this.field_230689_k_ && double_1 >= 20 && double_1 < screen.field_230708_k_ - 20;
    }
}
