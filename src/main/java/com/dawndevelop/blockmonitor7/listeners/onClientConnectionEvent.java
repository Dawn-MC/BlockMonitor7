package com.dawndevelop.blockmonitor7.listeners;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import com.dawndevelop.blockmonitor7.BlockMonitor;
import com.dawndevelop.blockmonitor7.api.RecordBuilder;

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
