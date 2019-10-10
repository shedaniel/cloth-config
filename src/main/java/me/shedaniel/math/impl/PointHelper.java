package me.shedaniel.math.impl;

import me.shedaniel.math.api.Point;
import net.minecraft.client.MinecraftClient;

public class PointHelper {
    public static Point fromMouse() {
        MinecraftClient client = MinecraftClient.getInstance();
        double mx = client.mouse.getX() * (double) client.getWindow().getScaledWidth() / (double) client.getWindow().getWidth();
        double my = client.mouse.getY() * (double) client.getWindow().getScaledHeight() / (double) client.getWindow().getHeight();
        return new Point(mx, my);
    }
    
    public static int getMouseX() {
        return fromMouse().x;
    }
    
    public static int getMouseY() {
        return fromMouse().y;
    }
}
