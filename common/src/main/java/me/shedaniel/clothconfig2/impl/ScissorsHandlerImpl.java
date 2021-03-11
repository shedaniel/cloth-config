/*
 * This file is part of Cloth Config.
 * Copyright (C) 2020 - 2021 shedaniel
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package me.shedaniel.clothconfig2.impl;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.clothconfig2.api.ScissorsHandler;
import me.shedaniel.clothconfig2.api.ScissorsScreen;
import me.shedaniel.math.Rectangle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.opengl.GL11;

import java.util.Collections;
import java.util.List;

@Environment(EnvType.CLIENT)
@ApiStatus.Internal
public final class ScissorsHandlerImpl implements ScissorsHandler {
    @ApiStatus.Internal
    public static final ScissorsHandler INSTANCE = new ScissorsHandlerImpl();
    
    // TODO: should this be reimplemented?
    /*static {
        Executor.runIf(() -> FabricLoader.getInstance().isModLoaded("notenoughcrashes"), () -> () -> {
            try {
                Class.forName("fudge.notenoughcrashes.api.NotEnoughCrashesApi").getDeclaredMethod("onEveryCrash", Runnable.class).invoke(null, (Runnable) () -> {
                    try {
                        ScissorsHandler.INSTANCE.clearScissors();
                    } catch (Throwable t) {
                        ClothConfigInitializer.LOGGER.error("[ClothConfig] Failed clear scissors on game crash!", t);
                    }
                });
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }*/
    
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
            for (int i = 1; i < scissorsAreas.size(); i++) {
                r.setBounds(r.intersection(scissorsAreas.get(i)));
            }
            r.setBounds(Math.min(r.x, r.x + r.width), Math.min(r.y, r.y + r.height), Math.abs(r.width), Math.abs(r.height));
            if (Minecraft.getInstance().screen instanceof ScissorsScreen)
                _applyScissor(((ScissorsScreen) Minecraft.getInstance().screen).handleScissor(r));
            else _applyScissor(r);
        } else {
            if (Minecraft.getInstance().screen instanceof ScissorsScreen)
                _applyScissor(((ScissorsScreen) Minecraft.getInstance().screen).handleScissor(null));
            else _applyScissor(null);
        }
    }
    
    public void _applyScissor(Rectangle r) {
        if (r != null && !r.isEmpty()) {
            Window window = Minecraft.getInstance().getWindow();
            double scaleFactor = window.getGuiScale();
            GlStateManager._enableScissorTest();
            GlStateManager._scissorBox((int) (r.x * scaleFactor), (int) ((window.getGuiScaledHeight() - r.height - r.y) * scaleFactor), (int) (r.width * scaleFactor), (int) (r.height * scaleFactor));
        } else {
            GlStateManager._disableScissorTest();
        }
    }
}
