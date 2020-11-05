package me.shedaniel.clothconfig2.api;

import me.shedaniel.math.Rectangle;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Experimental
public interface ScissorsScreen {
    @Nullable
    Rectangle handleScissor(@Nullable Rectangle scissor);
}
