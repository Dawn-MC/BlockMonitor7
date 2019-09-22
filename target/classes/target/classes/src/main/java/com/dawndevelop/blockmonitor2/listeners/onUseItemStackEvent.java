package com.dawndevelop.blockmonitor2.listeners;

import com.dawndevelop.blockmonitor2.api.RecordBuilder;
import com.dawndevelop.blockmonitor2.BlockMonitor;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;

/**
 * Created by johnfg10 on 19/06/2017.
 */
public class onUseItemStackEvent {

    public void UseItemStackEvent(UseItemStackEvent event){
        RecordBuilder recordBuilder = new RecordBuilder(event);
        BlockMonitor.executor.execute(recordBuilder);
    }
}
