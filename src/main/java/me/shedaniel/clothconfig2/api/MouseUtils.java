package me.shedaniel.clothconfig2.api;

import net.minecraft.client.MinecraftClient;

import java.awt.*;

public interface MouseUtils {
    
    static MinecraftClient client = MinecraftClient.getInstance();
    
    static Point getMouseLocation() {
        return new Point((int) getMouseX(), (int) getMouseY());
    }
    
    static double getMouseX() {
        return client.mouse.getX() * (double) client.window.getScaledWidth() / (double) client.window.getWidth();
    }
    
    static double getMouseY() {
        return client.mouse.getY() * (double) client.window.getScaledWidth() / (double) client.window.getWidth();
    }
    
}