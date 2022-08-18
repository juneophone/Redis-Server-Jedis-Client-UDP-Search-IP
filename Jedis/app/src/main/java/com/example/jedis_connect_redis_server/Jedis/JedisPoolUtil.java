package com.example.jedis_connect_redis_server.Jedis;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;

public final class JedisPoolUtil {
    private final String TAG = this.getClass().getSimpleName() + " JW";

    private final JedisConfigs mConfigs = JedisConfigs.getInstance();
    private Jedis mJedis = null;
    private JedisPoolConfig mPoolConfig = null;
    private JedisPool mJedisPool = null;
    private Subscriber subscriber = new Subscriber();

    private Handler mHandler;
    private boolean mDisconnectFlag = false;

    public void initialize() {
        Log.d(TAG, "Redis server Host: "+ mConfigs.getHOST());
        mPoolConfig = new JedisPoolConfig();
        mPoolConfig.setJmxEnabled(false);
        mJedisPool = new JedisPool(mPoolConfig, mConfigs.getHOST(), mConfigs.getPORT());
    }

    public JedisPool getJedisPool() {
        return mJedisPool;
    }

    public Jedis getJedis() {
        if (mJedisPool != null) {
            Jedis resource = mJedisPool.getResource();
            return resource;
        } else {
            return null;
        }
    }

    public void setSubscriber() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (isJedisHostConnect()) {
                        Jedis subscriberJedis = getJedis();
                        Log.d(TAG, "Subscribing to [" + mConfigs.getCHANNEL_NAME() + "].");
                        subscriberJedis.subscribe(subscriber, mConfigs.getCHANNEL_NAME());
                    }
                } catch (JedisConnectionException e) {
                    Log.w(TAG, "Wi-Fi network status needs to be checked.");
                } catch (Exception e) {
                    Log.e(TAG, "Subscribing failed.", e);
                }
            }
        }).start();
    }

    public void UnSubscriber() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (isJedisHostConnect()) {
                        Log.d(TAG, "UnSubscribing to [" + mConfigs.getCHANNEL_NAME() + "].");
                        subscriber.unsubscribe(mConfigs.getCHANNEL_NAME());
                    }
                } catch (JedisConnectionException e) {
                    Log.w(TAG, "Wi-Fi network status needs to be checked.");
                } catch (Exception e) {
                    Log.e(TAG, "UnSubscribing failed.", e);
                }
            }
        }).start();
    }

    public void setPublisher(String msg) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (isJedisHostConnect()) {
                        Jedis publisherJedis = getJedis();
                        Publisher publisher = new Publisher(publisherJedis, mConfigs.getCHANNEL_NAME());
                        publisher.start(msg);
                    }
                } catch (JedisConnectionException e) {
                    Log.w(TAG, "Wi-Fi network status needs to be checked.");
                } catch (Exception e) {
                    Log.e(TAG, "Subscribing failed.", e);
                }
            }
        }).start();
    }

    // Detect host network status
    public boolean isJedisHostConnect() {
        boolean ret = false;
        try {
            mJedis = new Jedis(mConfigs.getHOST(), mConfigs.getPORT());
            if (mJedis.ping().equalsIgnoreCase("PONG")) {
                ret = true;
            }
        } catch (JedisConnectionException e) {
            ret = false;
        } finally {
            mJedis.close();
        }
        return ret;
    }

    // auto detect remote server activity status
    public void startServerObserver(){
        mHandler = new Handler();
        mHandler.post(server_observer_runnable);
    }

    final Runnable server_observer_runnable = new Runnable() {
        @Override
        public void run() {
            if (isServerStatus()) {
                if(mDisconnectFlag) {
                    Log.w(TAG, "Reonnecting Jedis Server~~~~");
                    Intent intent = new Intent();
                    intent.setAction(mConfigs.JEDIS_REMOTE_SERVER_STATUS);
                    mConfigs.sendMeessage(intent);
                }
                mDisconnectFlag = false;
            } else {
                mDisconnectFlag = true;
                Log.d(TAG, "retry connection...");
                Intent intent = new Intent();
                intent.setAction(mConfigs.JEDIS_REMOTE_SERVER_DISCONNECT);
                mConfigs.sendMeessage(intent);
            }
            mHandler.postDelayed(server_observer_runnable, mConfigs.SERVER_OBSERVER_TIME);
        }
    };

    private boolean isServerStatus() {
        DetectServerStatus ds = new DetectServerStatus();
        Thread th = new Thread(ds);
        try {
            th.start();
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return ds.getReturnValue();
    }

    public class DetectServerStatus implements Runnable {
        private boolean ret = false;

        @Override
        public void run() {
            try {
                mJedis = new Jedis(mConfigs.getHOST(), mConfigs.getPORT());
                if (mJedis.ping().equalsIgnoreCase("PONG")) {
                    ret = true;
                }
            } catch (JedisDataException e) {
                Log.w(TAG, "Data Exception: " + e);
                ret = false;
            } catch (JedisConnectionException e) {
                ret = false;
            } finally {
                mJedis.close();
            }
        }

        public boolean getReturnValue() {
            return ret;
        }
    }

}
