package me.shedaniel.clothconfig2.forge.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;

public class ColorDisplayWidget extends Widget {
    
    protected TextFieldWidget textFieldWidget;
    protected int color;
    protected int size;
    
    public ColorDisplayWidget(TextFieldWidget textFieldWidget, int x, int y, int size, int color) {
        super(x, y, size, size, NarratorChatListener.EMPTY);
        this.textFieldWidget = textFieldWidget;
        this.color = color;
        this.size = size;
    }
    
    @Override
    public void func_230431_b_(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        func_238468_a_(matrices, this.field_230690_l_, this.field_230691_m_, this.field_230690_l_ + size, this.field_230691_m_ + size, textFieldWidget.func_230999_j_() ? -1 : -6250336, textFieldWidget.func_230999_j_() ? -1 : -6250336);
        func_238468_a_(matrices, this.field_230690_l_ + 1, this.field_230691_m_ + 1, this.field_230690_l_ + size - 1, this.field_230691_m_ + size - 1, 0xffffffff, 0xffffffff);
        func_238468_a_(matrices, this.field_230690_l_ + 1, this.field_230691_m_ + 1, this.field_230690_l_ + size - 1, this.field_230691_m_ + size - 1, color, color);
    }
    
    @Override
    public void func_230982_a_(double mouseX, double mouseY) {
    }
    
    @Override
    public void func_231000_a__(double mouseX, double mouseY) {
    }
    
    public void setColor(int color) {
        this.color = color;
    }
}
