package com.example.jedis_connect_redis_server.Jedis;

import android.content.Intent;
import android.util.Log;

import redis.clients.jedis.JedisPubSub;

public class Subscriber extends JedisPubSub {
    private final String TAG = this.getClass().getSimpleName() + " JW";
    private JedisConfigs mConfigs = JedisConfigs.getInstance();

    @Override
    public void onMessage(String channel, String message) {
        Log.d(TAG, "onMessage received. Channel: [" + channel + "], Msg: [" + message + "]");
        Intent intent = new Intent();
        intent.setAction(mConfigs.JEDIS_BROADCAST_ACTION);
        intent.putExtra("channel", channel);
        intent.putExtra("message", message);
        mConfigs.sendMeessage(intent);
    }

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        Log.d(TAG, "onPMessage received. Channel: [" + channel + "], Msg: [" + message + "]");
    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        Log.w(TAG, "onSubscribe received. Channel: [" + channel + "], subscribedChannels: [" + subscribedChannels + "]");
    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        Log.d(TAG, "onUnsubscribe received. Channel: [" + channel + "], subscribedChannels: [" + subscribedChannels + "]");
    }

    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels) {
        Log.d(TAG, "onPUnsubscribe received. pattern: [" + pattern + "], subscribedChannels: [" + subscribedChannels + "]");
    }

    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
        Log.d(TAG, "onPSubscribe received. pattern: [" + pattern + "], subscribedChannels: [" + subscribedChannels + "]");
    }
}
