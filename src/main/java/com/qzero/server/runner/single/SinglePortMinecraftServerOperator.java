package com.qzero.server.runner.single;

import com.qzero.server.config.minecraft.MinecraftServerConfiguration;
import com.qzero.server.runner.ServerOutputListener;
import com.qzero.server.runner.common.CommonMinecraftServerOperator;
import com.qzero.server.utils.UUIDUtils;

import java.io.IOException;

public class SinglePortMinecraftServerOperator extends CommonMinecraftServerOperator {

    private SinglePortMinecraftServerContainer container;

    private ServerOutputListener outputListener=new ServerOutputListener() {
        private String listenerId= UUIDUtils.getRandomUUID();

        @Override
        public String getListenerId() {
            return listenerId;
        }

        @Override
        public void receivedOutputLine(String serverName, String outputLine, OutputType outputType) {

        }

        @Override
        public void receivedServerEvent(String serverName, ServerEvent event) {
            switch (event){
                case SERVER_STOPPED:
                    container.markMinecraftServerStopped();
            }
        }
    };

    public SinglePortMinecraftServerOperator(SinglePortMinecraftServerContainer container, MinecraftServerConfiguration configuration) {
        super(configuration);
        this.container = container;
        runner.registerOutputListener(outputListener);
    }

    @Override
    public void startServer() throws IOException {
        if(!container.requestToStartMinecraftServer()){
            throw new IllegalStateException("Server port is occupied, can not start server");
        }
        super.startServer();
    }

}
