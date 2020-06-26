package me.shedaniel.clothconfig2.forge.api;

import me.shedaniel.clothconfig2.forge.impl.ScissorsHandlerImpl;
import me.shedaniel.math.Rectangle;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public interface ScissorsHandler {
    ScissorsHandler INSTANCE = ScissorsHandlerImpl.INSTANCE;
    
    void clearScissors();
    
    List<Rectangle> getScissorsAreas();
    
    void scissor(Rectangle rectangle);
    
    void removeLastScissor();
    
    void applyScissors();
}
