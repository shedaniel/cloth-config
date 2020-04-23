package me.shedaniel.forge.clothconfig2.impl;

import com.google.common.collect.Lists;
import me.shedaniel.forge.clothconfig2.api.ScissorsHandler;
import me.shedaniel.forge.math.Rectangle;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import java.util.Collections;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public final class ScissorsHandlerImpl implements ScissorsHandler {
    
    @Deprecated public static final ScissorsHandler INSTANCE = new ScissorsHandlerImpl();
    
    private final List<Rectangle> scissorsAreas;
    
    public ScissorsHandlerImpl() {
        this.scissorsAreas = Lists.newArrayList();
    }
    
    @Override
    public void clearScissors() {
        scissorsAreas.clear();
        applyScissors();
    }
    
    @Override
    public List<Rectangle> getScissorsAreas() {
        return Collections.unmodifiableList(scissorsAreas);
    }
    
    @Override
    public void scissor(Rectangle rectangle) {
        scissorsAreas.add(rectangle);
        applyScissors();
    }
    
    @Override
    public void removeLastScissor() {
        if (!scissorsAreas.isEmpty())
            scissorsAreas.remove(scissorsAreas.size() - 1);
        applyScissors();
    }
    
    @Override
    public void applyScissors() {
        if (!scissorsAreas.isEmpty()) {
            Rectangle r = scissorsAreas.get(0).clone();
            scissorsAreas.stream().skip(1L).forEach(rectangle -> r.setBounds(r.intersects(rectangle) ? r.intersection(rectangle) : new Rectangle()));
            MainWindow window = Minecraft.getInstance().getMainWindow();
            double scaleFactor = window.getGuiScaleFactor();
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glScissor((int) (r.x * scaleFactor), (int) ((window.getScaledHeight() - r.height - r.y) * scaleFactor), (int) (r.width * scaleFactor), (int) (r.height * scaleFactor));
        } else
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }
}
