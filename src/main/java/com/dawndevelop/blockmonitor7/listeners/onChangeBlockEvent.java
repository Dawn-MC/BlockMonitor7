package com.dawndevelop.blockmonitor7.listeners;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;

import com.dawndevelop.blockmonitor7.BlockMonitor;
import com.dawndevelop.blockmonitor7.api.RecordBuilder;

/**
 * Created by johnfg10 on 06/06/2017.
 */
public class onChangeBlockEvent{
    @Listener
    public void onChangeBlockEvent(ChangeBlockEvent event){
        RecordBuilder recordBuilder = new RecordBuilder(event);
        BlockMonitor.executor.execute(recordBuilder);
    }
}
