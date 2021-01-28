package me.shedaniel.clothconfig2.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;

@Environment(EnvType.CLIENT)
public class ClothRequiresRestartScreen extends ConfirmScreen {
    public ClothRequiresRestartScreen(Screen parent) {
        super(t -> {
            if (t)
                Minecraft.getInstance().stop();
            else
                Minecraft.getInstance().setScreen(parent);
        }, new TranslatableComponent("text.cloth-config.restart_required"), new TranslatableComponent("text.cloth-config.restart_required_sub"), new TranslatableComponent("text.cloth-config.exit_minecraft"), new TranslatableComponent("text.cloth-config.ignore_restart"));
    }
}
