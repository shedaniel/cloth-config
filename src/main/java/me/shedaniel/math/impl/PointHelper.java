package me.shedaniel.math.impl;

import me.shedaniel.math.api.Point;
import net.minecraft.client.MinecraftClient;

public class PointHelper {
    public static Point fromMouse() {
        MinecraftClient client = MinecraftClient.getInstance();
        double mx = client.mouse.getX() * (double) client.method_22683().getScaledWidth() / (double) client.method_22683().getWidth();
        double my = client.mouse.getY() * (double) client.method_22683().getScaledHeight() / (double) client.method_22683().getHeight();
        return new Point(mx, my);
    }
    
    public static int getMouseX() {
        return fromMouse().x;
    }
    
    public static int getMouseY() {
        return fromMouse().y;
    }
}
