package com.qzero.server.console;

import com.qzero.server.config.GlobalConfigurationManager;
import com.qzero.server.runner.ServerOutputListener;
import com.qzero.server.utils.UUIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InGameCommandContextSwitchListener implements ServerOutputListener {

    private String listenerId=UUIDUtils.getRandomUUID();

    private Logger log= LoggerFactory.getLogger(getClass());

    @Override
    public String getListenerId() {
        return listenerId;
    }

    @Override
    public void receivedOutputLine(String serverName, String outputLine, OutputType outputType) {
        if(outputLine.matches(".*: .* joined the game")){
            Pattern pattern=Pattern.compile("(?<=: ).*(?= joined the game)");
            Matcher matcher=pattern.matcher(outputLine);

            if(matcher.find()){
                String operatorName=matcher.group();
                if(GlobalConfigurationManager.getInstance().getAuthorizeConfigurationManager().getAdminConfig(operatorName)!=null){
                    ServerCommandContext context=GlobalOperatorContextContainer.getInstance().getContext(operatorName);
                    if(context==null)
                        context=new ServerCommandContext();

                    context.setCurrentServer(serverName);
                    log.debug(String.format("Changed the context of operator %d (currentServer -> %d)", operatorName,serverName));
                }
            }
        }
    }

    @Override
    public void receivedServerEvent(String serverName, ServerEvent event) {

    }
}
