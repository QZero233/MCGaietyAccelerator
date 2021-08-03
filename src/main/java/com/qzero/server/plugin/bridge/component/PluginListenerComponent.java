package com.qzero.server.plugin.bridge.component;

import com.qzero.server.runner.ServerOutputListener;

import java.util.ArrayList;
import java.util.List;

public interface PluginListenerComponent {

    default List<ServerOutputListener> getPluginListeners(){
        return new ArrayList<>();
    }

}
