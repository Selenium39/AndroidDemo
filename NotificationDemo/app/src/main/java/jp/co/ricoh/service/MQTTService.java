package jp.co.ricoh.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.FileDescriptor;

import jp.co.ricoh.callback.PushCallback;

public class MQTTService extends Service {
    private static final String TAG = "MQTTService";
    public static final String BROKER_URL = "tcp://192.168.56.1:61616";
    public static final String CLIENT_ID = "admin";
    //订阅的主题
    public static final String TOPIC = "demo";
    private MqttClient mqttClient;
    //mqtt连接配置
    private MqttConnectOptions mqttOptions;
    private String username = "admin";
    private String password = "admin";

    public MQTTService() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: begin");

        try {
            //第三个参数代表持久化客户端，如果为null，则不持久化
            mqttClient = new MqttClient(BROKER_URL, CLIENT_ID, new MemoryPersistence());
            //mqtt连接设置
            mqttOptions = new MqttConnectOptions();
            mqttOptions.setUserName(username);
            mqttOptions.setPassword(password.toCharArray());
            //超时连接，单位为秒
            mqttOptions.setConnectionTimeout(10);
            mqttOptions.setKeepAliveInterval(20);
            //false代表可以接受离线消息
            mqttOptions.setCleanSession(false);
           // mqttOptions.setAutomaticReconnect(true);
            // 设置回调
            mqttClient.setCallback(new PushCallback(mqttClient));
            Log.i(TAG, "onStartCommand: before connect");
            //客户端下线，其它客户端或者自己再次上线可以接收"遗嘱"消息
//            MqttTopic topic1 = mqttClient.getTopic(TOPIC);
//            mqttOptions.setWill(topic1, "close".getBytes(), 2, true);
            mqttClient.connect(mqttOptions);
            Log.i(TAG, "onStartCommand: after connect");

            //mqtt客户端订阅主题
            //在mqtt中用QoS来标识服务质量
            //QoS=0时，报文最多发送一次，有可能丢失
            //QoS=1时，报文至少发送一次，有可能重复
            //QoS=2时，报文只发送一次，并且确保消息只到达一次。
            int[] qos = {1};
            String[] topic = {TOPIC};
            mqttClient.subscribe(topic, qos);
        } catch (MqttException e) {
            e.printStackTrace();
            Log.i(TAG, e.getMessage());
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        try {
            mqttClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
