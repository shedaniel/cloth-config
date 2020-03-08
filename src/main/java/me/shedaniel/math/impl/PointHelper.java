package me.shedaniel.math.impl;

import me.shedaniel.math.Point;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public class PointHelper {
    public static me.shedaniel.math.api.Point fromMouse() {
        MinecraftClient client = MinecraftClient.getInstance();
        double mx = client.mouse.getX() * (double) client.getWindow().getScaledWidth() / (double) client.getWindow().getWidth();
        double my = client.mouse.getY() * (double) client.getWindow().getScaledHeight() / (double) client.getWindow().getHeight();
        return new me.shedaniel.math.api.Point(mx, my);
    }
    
    public static Point ofMouse() {
        return fromMouse();
    }
    
    public static int getMouseX() {
        return fromMouse().x;
    }
    
    public static int getMouseY() {
        return fromMouse().y;
    }
}
