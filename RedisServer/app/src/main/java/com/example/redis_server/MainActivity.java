package com.example.redis_server;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.caleb.sdk.UDP.UDPCallback;
import com.caleb.sdk.UDP.UDPConfigs;
import com.caleb.sdk.UDP.UDPListener;
import com.caleb.sdk.UDP.UDPSend;
import com.github.microwww.redis.RedisServer;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName() + " JW";

    private UDPListener udps;
    private UDPConfigs mUDPConfig = UDPConfigs.getInstance();
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textview1);

        String IP = getIpAddress();
        textView.setText(IP + "\n Port: 6379");

        // UDP Server
        udps = new UDPListener();
        udps.setUdpCallback(udpCallback);
        udps.start();

        // Redis Server
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    RedisServer server = new RedisServer();
                    server.listener(IP, 6379); // Redis runs in the background

                    InetSocketAddress address = (InetSocketAddress) server.getSockets().getServerSocket().getLocalSocketAddress();
                    Log.i(TAG,"Redis start :: [{" + address.getHostName() + "}:{" + address.getPort() + "}]");
                } catch (Exception e) {
                    Log.e(TAG, "Subscribing failed.", e);
                }
            }
        }).start();

    }

    private UDPCallback udpCallback = new UDPCallback() {
        @Override
        public void fromServer(String IP_Address, String recData) {
            Log.i(TAG, "fromServer IP[" + IP_Address + "], recData[" + recData + "]");
        }

        @Override
        public void fromClient(String IP_Address, String recData) {
            Log.i(TAG, "fromClient IP[" + IP_Address + "], recData[" + recData + "]");
            new Thread(new UDPSend(IP_Address, mUDPConfig.TOKEN_SERVER)).start();
        }
    };

    /**
     * @Title: getIpAddress
     *
     * @Description: 取得本機端 IP Address
     *
     * @return String
     */
    public String getIpAddress() {
        try {
            for (Enumeration<NetworkInterface> enNetI = NetworkInterface.getNetworkInterfaces(); enNetI
                    .hasMoreElements(); ) {
                NetworkInterface netI = enNetI.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = netI.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(udps != null) {
            udps.onDestroy();
        }
    }
}