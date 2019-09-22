package com.dawndevelop.blockmonitor2.Storage;

import com.dawndevelop.blockmonitor2.api.Event;

public interface IStorageHandler {
    void Setup(String InstallPath, String username, String password);

    void Insert(Event event);

    void Remove(long ID);

    void Shutdown();
}
