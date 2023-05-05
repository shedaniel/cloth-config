package me.shedaniel.clothconfig2.api;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Experimental
public interface HideableWidget {
    
    /**
     * Checks whether this config entry gui is shown on screen.
     * 
     * <p>Requirements are checked independently (once per tick). This method simply reads the result of the latest
     * check, making it extremely cheap to run.
     * 
     * @return whether to display the config entry
     * @see DisableableWidget#isEnabled()
     * @see TickableWidget#tick()
     */
    boolean isDisplayed();
    
    void setDisplayRequirement(@Nullable Requirement requirement);
    
    @Nullable Requirement getDisplayRequirement();
}
