package me.shedaniel.math.impl;

import me.shedaniel.math.api.Point;
import net.minecraft.client.MinecraftClient;

public class PointHelper {
    public static Point fromMouse() {
        MinecraftClient client = MinecraftClient.getInstance();
        double mx = client.mouse.getX() * (double) client.window.getScaledWidth() / (double) client.window.getWidth();
        double my = client.mouse.getY() * (double) client.window.getScaledHeight() / (double) client.window.getHeight();
        return new Point(mx, my);
    }
    
    public static int getMouseX() {
        return fromMouse().x;
    }
    
    public static int getMouseY() {
        return fromMouse().y;
    }
}
