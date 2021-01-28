package me.shedaniel.clothconfig2.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;

public class ColorDisplayWidget extends AbstractWidget {
    
    protected EditBox textFieldWidget;
    protected int color;
    protected int size;
    
    public ColorDisplayWidget(EditBox textFieldWidget, int x, int y, int size, int color) {
        super(x, y, size, size, NarratorChatListener.NO_TITLE);
        this.textFieldWidget = textFieldWidget;
        this.color = color;
        this.size = size;
    }
    
    @Override
    public void renderButton(PoseStack matrices, int mouseX, int mouseY, float delta) {
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
