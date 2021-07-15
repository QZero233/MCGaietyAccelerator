package com.qzero.server.plugin.api;

import com.qzero.server.console.ServerCommandContext;

public interface PluginCommand {

    String getCommandName();

    default int getParameterCount(){
        return 0;
    }

    default boolean needServerSelected(){
        return true;
    }

    String execute(String[] commandParts, String commandLine, ServerCommandContext context, PluginOperateHelper serverOperateHelper);


}
