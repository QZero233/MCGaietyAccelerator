package com.qzero.server.console.commands;

import com.qzero.server.console.ServerCommandContext;

public interface ConsoleCommand {

    int getCommandParameterCount();

    boolean needServerSelected();

    /**
     *
     * @param commandParts index 0 is the command name, parameters begin with index 1
     * @param fullCommand
     * @return
     */
    String execute(String[] commandParts, String fullCommand, ServerCommandContext context);

}
