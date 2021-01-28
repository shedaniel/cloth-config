package me.shedaniel.clothconfig2.gui;

import me.shedaniel.clothconfig2.api.Tooltip;
import me.shedaniel.math.Point;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import org.jetbrains.annotations.Nullable;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Optional;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class ClothConfigTabButton extends AbstractButton {
    
    private final int index;
    private final ClothConfigScreen screen;
    @Nullable 
    private final Supplier<Optional<FormattedText[]>> descriptionSupplier;
    
    public ClothConfigTabButton(ClothConfigScreen screen, int index, int int_1, int int_2, int int_3, int int_4, Component string_1, Supplier<Optional<FormattedText[]>> descriptionSupplier) {
        super(int_1, int_2, int_3, int_4, string_1);
        this.index = index;
        this.screen = screen;
        this.descriptionSupplier = descriptionSupplier;
    }

    public ClothConfigTabButton(ClothConfigScreen screen, int index, int int_1, int int_2, int int_3, int int_4, Component string_1) {
        this(screen, index, int_1, int_2, int_3, int_4, string_1, null);
    }
    
    @Override
    public void onPress() {
        if (index != -1)
            screen.selectedCategoryIndex = index;
        screen.init(Minecraft.getInstance(), screen.width, screen.height);
    }
    
    @Override
    public void render(PoseStack matrices, int int_1, int int_2, float float_1) {
        active = index != screen.selectedCategoryIndex;
        super.render(matrices, int_1, int_2, float_1);

        if (isMouseOver(int_1, int_2)) {
            Optional<FormattedText[]> tooltip = getTooltip();
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
        return this.visible && double_1 >= this.x && double_2 >= this.y && double_1 < this.x + this.width && double_2 < this.y + this.height && double_1 >= 20 && double_1 < screen.width - 20;
    }

    public Optional<FormattedText[]> getTooltip() {
        if (descriptionSupplier != null)
            return descriptionSupplier.get();
        return Optional.empty();
    }

}
