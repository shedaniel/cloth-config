package me.shedaniel.clothconfig2.api;

import me.shedaniel.clothconfig2.impl.ScissorsHandlerImpl;
import me.shedaniel.math.api.Rectangle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;

@Environment(EnvType.CLIENT)
public interface ScissorsHandler {
    ScissorsHandler INSTANCE = ScissorsHandlerImpl.INSTANCE;
    
    void clearScissors();
    
    List<Rectangle> getScissorsAreas();
    
    void scissor(Rectangle rectangle);
    
    void removeLastScissor();
    
    void applyScissors();
}
