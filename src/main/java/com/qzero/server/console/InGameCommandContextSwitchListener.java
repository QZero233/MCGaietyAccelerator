package com.qzero.server.console;

import com.qzero.server.SpringUtil;
import com.qzero.server.runner.ServerOutputListener;
import com.qzero.server.service.AdminAccountService;
import com.qzero.server.utils.UUIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InGameCommandContextSwitchListener implements ServerOutputListener {

    private String listenerId=UUIDUtils.getRandomUUID();

    private Logger log= LoggerFactory.getLogger(getClass());

    private AdminAccountService adminAccountService;

    public InGameCommandContextSwitchListener() {
        adminAccountService= SpringUtil.getBean(AdminAccountService.class);
    }

    @Override
    public String getListenerId() {
        return listenerId;
    }

    @Override
    public void receivedPlayerEvent(String serverName, String playerName, PlayerEvent event) {
        if(event!=PlayerEvent.JOIN)
            return;

        //If the player is an admin, change its context
        if(adminAccountService.hasAdmin(playerName)){
            ServerCommandContext context=GlobalOperatorContextContainer.getInstance().getContext(playerName);
            if(context==null)
                context=new ServerCommandContext();

            context.setCurrentServer(serverName);
            log.debug(String.format("Changed the context of operator %s (currentServer -> %s)", playerName,serverName));
        }
    }
}
