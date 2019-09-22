package com.dawndevelop.blockmonitor7.listeners;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;

import com.dawndevelop.blockmonitor7.BlockMonitor;
import com.dawndevelop.blockmonitor7.api.RecordBuilder;

/**
 * Created by johnfg10 on 14/06/2017.
 */
public class onInteractInventoryEvent {
    @Listener
    public void InteractInventoryEvent(InteractInventoryEvent event){
        RecordBuilder recordBuilder = new RecordBuilder(event);
        BlockMonitor.executor.execute(recordBuilder);
    }
}
