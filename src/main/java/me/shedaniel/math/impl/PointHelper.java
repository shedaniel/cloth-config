package me.shedaniel.math.impl;

import me.shedaniel.math.Point;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

@Environment(EnvType.CLIENT)
public class PointHelper {
    public static Point ofMouse() {
        Minecraft client = Minecraft.getInstance();
        double mx = client.mouseHandler.xpos() * (double) client.getWindow().getGuiScaledWidth() / (double) client.getWindow().getScreenWidth();
        double my = client.mouseHandler.ypos() * (double) client.getWindow().getGuiScaledHeight() / (double) client.getWindow().getScreenHeight();
        return new Point(mx, my);
    }
    
    public static int getMouseX() {
        return ofMouse().x;
    }
    
    public static int getMouseY() {
        return ofMouse().y;
    }
}
