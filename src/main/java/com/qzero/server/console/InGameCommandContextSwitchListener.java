package com.qzero.server.console;

import com.qzero.server.config.GlobalConfigurationManager;
import com.qzero.server.runner.ServerOutputListener;
import com.qzero.server.utils.UUIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InGameCommandContextSwitchListener implements ServerOutputListener {

    private String listenerId=UUIDUtils.getRandomUUID();

    private Logger log= LoggerFactory.getLogger(getClass());

    @Override
    public String getListenerId() {
        return listenerId;
    }

    @Override
    public void receivedPlayerEvent(String serverName, String playerName, PlayerEvent event) {
        if(event!=PlayerEvent.JOIN)
            return;

        if(GlobalConfigurationManager.getInstance().getAuthorizeConfigurationManager().getAdminConfig(playerName)!=null){
            ServerCommandContext context=GlobalOperatorContextContainer.getInstance().getContext(playerName);
            if(context==null)
                context=new ServerCommandContext();

            context.setCurrentServer(serverName);
            log.debug(String.format("Changed the context of operator %s (currentServer -> %s)", playerName,serverName));
        }
    }
}
