package me.shedaniel.clothconfig2.forge.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClothRequiresRestartScreen extends ConfirmScreen {
    public ClothRequiresRestartScreen(Screen parent) {
        super(t -> {
            if (t)
                Minecraft.getInstance().shutdown();
            else
                Minecraft.getInstance().displayGuiScreen(parent);
        }, new TranslationTextComponent("text.cloth-config.restart_required"), new TranslationTextComponent("text.cloth-config.restart_required_sub"), new TranslationTextComponent("text.cloth-config.exit_minecraft"), new TranslationTextComponent("text.cloth-config.ignore_restart"));
    }
}
