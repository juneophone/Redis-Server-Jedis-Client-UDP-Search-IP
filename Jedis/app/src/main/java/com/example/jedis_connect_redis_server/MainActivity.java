package com.example.jedis_connect_redis_server;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.caleb.sdk.UDP.UDPCallback;
import com.caleb.sdk.UDP.UDPConfigs;
import com.caleb.sdk.UDP.UDPListener;
import com.caleb.sdk.UDP.UDPSend;
import com.example.jedis_connect_redis_server.Jedis.JedisConfigs;
import com.example.jedis_connect_redis_server.Jedis.JedisPoolUtil;

public class MainActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName() + " JW";

    private JedisPoolUtil mJedisPoolUtil = new JedisPoolUtil();
    private JedisConfigs mJedisConfigs = JedisConfigs.getInstance();
    private UDPConfigs mUDPConfig = UDPConfigs.getInstance();
    private UDPListener udpl;


    private Context mContext;

    public static class ContextHolder {
        static Context ApplicationContext;
        public static void initial(Context context) {
            ApplicationContext = context;
        }
        public static Context getContext() {
            return ApplicationContext;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ContextHolder.initial(this);
        mContext = ContextHolder.getContext();
        mJedisConfigs.setContext(mContext);
        regReceiver();

        // UDP Get Redis Server IP Address
        startUDPListener();
        sendUDP();

        mJedisPoolUtil.startServerObserver();
    }

    private void setJedisPool(){
        mJedisPoolUtil.initialize();
        mJedisPoolUtil.setSubscriber();
    }

    private void regReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(mJedisConfigs.JEDIS_BROADCAST_ACTION);
        registerReceiver(mReceiver, filter);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Log.d(TAG, intent.getAction().toString());
            switch (intent.getAction()) {
                case JedisConfigs.JEDIS_BROADCAST_ACTION:
                    Log.d(TAG, "receive[JEDIS_BROADCAST_ACTION]");
                    String channel = intent.getStringExtra("channel");
                    String message = intent.getStringExtra("message");
                    Log.d(TAG, "channel[" + channel + "], message[" + message + "]");
                    break;
            }
        }
    };

    private void startUDPListener(){
        udpl = new UDPListener();
        udpl.setUdpCallback(udpCallback);
        udpl.start();
    }

    private UDPCallback udpCallback = new UDPCallback(){
        @Override
        public void fromServer(String IP_Address, String recData) {
            Log.i(TAG, "fromServer IP[" + IP_Address + "], recData[" + recData + "]");
            mJedisConfigs.setHOST(IP_Address);
            setJedisPool();
        }

        @Override
        public void fromClient(String IP_Address, String recData) {
            //Log.i(TAG, "fromClient IP[" + IP_Address + "], recData[" + recData + "]");
        }
    };

    private void sendUDP(){
        new Thread(new UDPSend(mUDPConfig.TOKEN_CLIENT)).start();
    }

    public void btn_pubClick(View view) {
        mJedisPoolUtil.setPublisher("Testing JW");
    }

    public void btn_subClick(View view) {
        mJedisPoolUtil.setSubscriber();
    }

    public void btn_unsubClick(View view) {
        mJedisPoolUtil.UnSubscriber();
    }

    public void btn_udpClick(View view) {
//        new Thread(new UDPSend("192.168.43.11", mUDPConfig.TOKEN_CLIENT)).start();
        sendUDP();
    }

}