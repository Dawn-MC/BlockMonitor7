package com.dawndevelop.blockmonitor.api;

/**
 * Created by johnfg10 on 04/06/2017.
 */
public enum EventType {
    Unknown,
    ConnectionEvent,
    DisconnectionEvent,
    BlockBreak,
    BlockPlace,
    BlockModify,
    BlockGrow,
    InteractInventoryEvent,
    UseItemStackEventStart,
    UseItemStackEventStop
}
