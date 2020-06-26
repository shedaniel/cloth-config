package me.shedaniel.clothconfig2.forge.api;

import me.shedaniel.math.Point;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PointHelper {
    public static Point ofMouse() {
        Minecraft client = Minecraft.getInstance();
        double mx = client.mouseHelper.getMouseX() * (double) client.getMainWindow().getScaledWidth() / (double) client.getMainWindow().getWidth();
        double my = client.mouseHelper.getMouseY() * (double) client.getMainWindow().getScaledHeight() / (double) client.getMainWindow().getHeight();
        return new Point(mx, my);
    }
    
    public static int getMouseX() {
        return ofMouse().x;
    }
    
    public static int getMouseY() {
        return ofMouse().y;
    }
}
