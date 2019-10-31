package me.shedaniel.clothconfig2.api;

import me.shedaniel.clothconfig2.impl.ScissorsHandlerImpl;
import me.shedaniel.math.api.Rectangle;

import java.util.List;

public interface ScissorsHandler {
    ScissorsHandler INSTANCE = ScissorsHandlerImpl.INSTANCE;
    
    void clearScissors();
    
    List<Rectangle> getScissorsAreas();
    
    void scissor(Rectangle rectangle);
    
    void removeLastScissor();
    
    void applyScissors();
}
