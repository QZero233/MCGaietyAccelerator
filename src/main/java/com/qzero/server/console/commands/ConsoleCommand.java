package com.qzero.server.console.commands;

import com.qzero.server.console.ServerCommandContext;

public interface ConsoleCommand {

    int getCommandParameterCount();

    boolean needServerSelected();

    //TODO apply this for plugins
    /**
     * The min admin level of the executor who can execute the command
     * If it's less than 0, it means everyone can execute
     * If it's 0, it means only admin can execute
     * @return
     */
    default int minAdminPermission(){
        return 0;
    }

    /**
     *
     * @param commandParts index 0 is the command name, parameters begin with index 1
     * @param fullCommand
     * @return
     */
    String execute(String[] commandParts, String fullCommand, ServerCommandContext context);

}
