package com.qzero.server.console.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;

public class ClientModeRelayThread extends Thread{

    public interface OnRelayStopCallback{
        void onStop();
    }

    private Logger log= LoggerFactory.getLogger(getClass());

    private InputStream sourceIs;
    private OutputStream dstOs;
    private OnRelayStopCallback callback;

    public ClientModeRelayThread(InputStream sourceIs, OutputStream dstOs, OnRelayStopCallback callback) {
        this.sourceIs = sourceIs;
        this.dstOs = dstOs;
        this.callback = callback;
    }

    public void stopRelay(){
        interrupt();
    }

    @Override
    public void run() {
        super.run();

        try {
            byte[] buf=new byte[102400];
            int len;
            while (!isInterrupted()){
                len=sourceIs.read(buf);
                dstOs.write(buf,0,len);
            }
        }catch (Exception e){
            log.error("Failed to relay, relay thread close",e);
        }

        if(callback!=null)
            callback.onStop();
    }
}
