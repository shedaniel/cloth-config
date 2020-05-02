package me.shedaniel.clothconfig2.gui.widget;

import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;

public class ColorDisplayWidget extends AbstractButtonWidget {
    
    protected TextFieldWidget textFieldWidget;
    protected int color;
    protected int size;
    
    public ColorDisplayWidget(TextFieldWidget textFieldWidget, int x, int y, int size, int color) {
        super(x, y, size, size, NarratorManager.EMPTY);
        this.textFieldWidget = textFieldWidget;
        this.color = color;
        this.size = size;
    }
    
    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        fillGradient(matrices, this.x, this.y, this.x + size, this.y + size, textFieldWidget.isFocused() ? -1 : -6250336, textFieldWidget.isFocused() ? -1 : -6250336);
        fillGradient(matrices, this.x + 1, this.y + 1, this.x + size - 1, this.y + size - 1, 0xffffffff, 0xffffffff);
        fillGradient(matrices, this.x + 1, this.y + 1, this.x + size - 1, this.y + size - 1, color, color);
    }
    
    @Override
    public void onClick(double mouseX, double mouseY) {
    }
    
    @Override
    public void onRelease(double mouseX, double mouseY) {
    }
    
    public void setColor(int color) {
        this.color = color;
    }
}
