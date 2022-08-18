package com.example.jedis_connect_redis_server.Jedis;

import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import redis.clients.jedis.Jedis;

public class Publisher {
    private final String TAG = this.getClass().getSimpleName() + " JW";

    private final Jedis publisherJedis;
    private final String channel;

    public Publisher(Jedis publisherJedis, String channel) {
        this.publisherJedis = publisherJedis;
        this.channel = channel;
    }

    public void start() {
        Log.i(TAG, "Type your message (quit for terminate)");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                String line = reader.readLine();

                if (!"quit".equals(line)) {
                    publisherJedis.publish(channel, line);
                } else {
                    break;
                }
            }

        } catch (IOException e) {
            Log.e(TAG, "IO failure while reading input, e");
        }
    }

    public void start(String msg) {
        if (!msg.isEmpty()) {
            Log.d(TAG, "publish[" + msg + "]");
            publisherJedis.publish(channel, msg);
        }
    }
}
