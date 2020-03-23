package me.shedaniel.clothconfig2.mixin;

import net.minecraft.client.gui.widget.ButtonWidget;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ButtonWidget.class)
@ApiStatus.Internal
public interface ButtonWidgetHooks {
    @Accessor("onPress")
    void setOnPress(ButtonWidget.PressAction action);
}
