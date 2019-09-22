package com.dawndevelop.blockmonitor7.listeners;

import org.spongepowered.api.event.item.inventory.UseItemStackEvent;

import com.dawndevelop.blockmonitor7.BlockMonitor;
import com.dawndevelop.blockmonitor7.api.RecordBuilder;

/**
 * Created by johnfg10 on 19/06/2017.
 */
public class onUseItemStackEvent {

    public void UseItemStackEvent(UseItemStackEvent event){
        RecordBuilder recordBuilder = new RecordBuilder(event);
        BlockMonitor.executor.execute(recordBuilder);
    }
}
