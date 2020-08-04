package me.shedaniel.clothconfig2.gui;

import me.shedaniel.clothconfig2.api.Tooltip;
import me.shedaniel.math.Point;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class ClothConfigTabButton extends AbstractPressableButtonWidget {
    
    private final int index;
    private final ClothConfigScreen screen;
    @Nullable private final Supplier<Optional<Text[]>> tooltipSupplier;
    
    public ClothConfigTabButton(ClothConfigScreen screen, int index, int int_1, int int_2, int int_3, int int_4, Text string_1, Supplier<Optional<Text[]>> tooltipSupplier) {
        super(int_1, int_2, int_3, int_4, string_1);
        this.index = index;
        this.screen = screen;
        this.tooltipSupplier = tooltipSupplier;
    }

    public ClothConfigTabButton(ClothConfigScreen screen, int index, int int_1, int int_2, int int_3, int int_4, Text string_1) {
        this(screen, index, int_1, int_2, int_3, int_4, string_1, null);
    }
    
    @Override
    public void onPress() {
        if (index != -1)
            screen.selectedCategoryIndex = index;
        screen.init(MinecraftClient.getInstance(), screen.width, screen.height);
    }
    
    @Override
    public void render(MatrixStack matrices, int int_1, int int_2, float float_1) {
        active = index != screen.selectedCategoryIndex;
        super.render(matrices, int_1, int_2, float_1);

        if (isMouseOver(int_1, int_2)) {
            Optional<Text[]> tooltip = getTooltip();
            if (tooltip.isPresent() && tooltip.get().length > 0)
                screen.addTooltip(Tooltip.of(new Point(int_1, int_2), tooltip.get()));
        }
    }
    
    @Override
    protected boolean clicked(double double_1, double double_2) {
        return visible && active && isMouseOver(double_1, double_2);
    }
    
    @Override
    public boolean isMouseOver(double double_1, double double_2) {
        return this.active && this.visible && double_1 >= this.x && double_2 >= this.y && double_1 < this.x + this.width && double_2 < this.y + this.height && double_1 >= 20 && double_1 < screen.width - 20;
    }

    public Optional<Text[]> getTooltip() {
        if (tooltipSupplier != null)
            return tooltipSupplier.get();
        return Optional.empty();
    }

    @Nullable
    public Supplier<Optional<Text[]>> getTooltipSupplier() {
        return tooltipSupplier;
    }

}
