package com.dawndevelop.blockmonitor2.listeners;

import com.dawndevelop.blockmonitor2.api.RecordBuilder;
import com.dawndevelop.blockmonitor2.BlockMonitor;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;

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
