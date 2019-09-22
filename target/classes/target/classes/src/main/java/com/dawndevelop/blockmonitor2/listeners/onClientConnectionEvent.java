package com.dawndevelop.blockmonitor2.listeners;

import com.dawndevelop.blockmonitor2.api.RecordBuilder;
import com.dawndevelop.blockmonitor2.BlockMonitor;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.network.ClientConnectionEvent;

/**
 * Created by johnfg10 on 04/06/2017.
 */
public class onClientConnectionEvent {
    @Listener(order = Order.LAST)
    public void clientConnectionEvent(ClientConnectionEvent event){
        RecordBuilder recordBuilder = new RecordBuilder(event);
        BlockMonitor.executor.execute(recordBuilder);
    }
}
