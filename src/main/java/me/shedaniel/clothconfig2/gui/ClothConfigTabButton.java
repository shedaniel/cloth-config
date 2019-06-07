package me.shedaniel.clothconfig2.gui;

import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;

public class ClothConfigTabButton extends AbstractPressableButtonWidget {
    
    private int index = -1;
    private ClothConfigScreen screen;
    
    public ClothConfigTabButton(ClothConfigScreen screen, int index, int int_1, int int_2, int int_3, int int_4, String string_1) {
        super(int_1, int_2, int_3, int_4, string_1);
        this.index = index;
        this.screen = screen;
    }
    
    @Override
    public void onPress() {
        if (index != -1)
            screen.nextTabIndex = index;
        screen.tabsScrollVelocity = 0d;
        screen.init();
    }
    
    @Override
    public void render(int int_1, int int_2, float float_1) {
        active = index != screen.selectedTabIndex;
        super.render(int_1, int_2, float_1);
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
