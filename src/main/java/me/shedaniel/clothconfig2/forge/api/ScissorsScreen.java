package me.shedaniel.clothconfig2.forge.api;

import me.shedaniel.math.Rectangle;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;

@ApiStatus.Experimental
public interface ScissorsScreen {
    @Nullable
    Rectangle handleScissor(@Nullable Rectangle scissor);
}