package me.shedaniel.clothconfig2.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class ClothRequiresRestartScreen extends ConfirmScreen {
    public ClothRequiresRestartScreen(Screen parent) {
        super(t -> {
            if (t)
                MinecraftClient.getInstance().scheduleStop();
            else
                MinecraftClient.getInstance().openScreen(parent);
        }, new TranslatableText("text.cloth-config.restart_required"), new TranslatableText("text.cloth-config.restart_required_sub"), new TranslatableText("text.cloth-config.exit_minecraft"), new TranslatableText("text.cloth-config.ignore_restart"));
    }
}
