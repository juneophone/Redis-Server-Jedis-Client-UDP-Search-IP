package com.example.jedis_connect_redis_server.Jedis;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import java.io.File;

public class JedisConfigs {
    private final String TAG = this.getClass().getSimpleName() + " JW";

    public static final String JEDIS_BROADCAST_ACTION           = "com.example.jedispool.MESSAGE";
    public static final String JEDIS_REMOTE_SERVER_STATUS       = "com.example.jedispool.server.status";
    public static final String JEDIS_REMOTE_SERVER_DISCONNECT   = "com.example.jedispool.server.disconnect";
    public String STORAGE_FOLDER = Environment.getExternalStorageDirectory().getPath() + File.separator;
    public String PICTURE_FOLDER = STORAGE_FOLDER + "Pictures";
    public String WAVEFORM_PATH = STORAGE_FOLDER + "WF" + File.separator + "waveform.wbf"; // /sdcard/WF/waveform.wbf

    //--Jedis config-----------------------------------------------
    private String HOST = "192.168.43.74";
    private int PORT = 6379;
    private String CHANNEL_NAME = "TouchTaiwan2022-player";
    private String PASSWORDS = "pass";
    public int SERVER_OBSERVER_TIME = 5000;
    //-------------------------------------------------------------
    private Context mContext;

    public void Configs() {

    }

    private static class ConfigsLoader {
        private static final JedisConfigs instance = new JedisConfigs();
    }

    public static JedisConfigs getInstance() {
        return ConfigsLoader.instance;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public void sendMeessage(Intent intent){
        if(mContext != null) {
            mContext.sendBroadcast(intent);
        } else {
            Log.i(TAG, "Please use setContext() to set the Context.");
        }
    }

    public String getHOST() {
        return HOST;
    }

    public void setHOST(String HOST) {
        this.HOST = HOST;
    }

    public int getPORT() {
        return PORT;
    }

    public void setPORT(int PORT) {
        this.PORT = PORT;
    }

    public String getCHANNEL_NAME() {
        return CHANNEL_NAME;
    }

    public void setCHANNEL_NAME(String CHANNEL_NAME) {
        this.CHANNEL_NAME = CHANNEL_NAME;
    }

    public String getPASSWORDS() {
        return PASSWORDS;
    }

    public void setPASSWORDS(String PASSWORDS) {
        this.PASSWORDS = PASSWORDS;
    }

}
