package jp.co.ricoh.callback;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.greenrobot.eventbus.EventBus;

import jp.co.ricoh.bean.MessageEvent;

public class PushCallback implements MqttCallbackExtended {
    private static final String TAG = "PusherCallback";
    private MqttClient mqttClient = null;
    //订阅的主题
    public static final String TOPIC = "demo";

    public PushCallback(MqttClient client) {
        mqttClient = client;
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.i(TAG, "连接失败");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String msg = new String(message.getPayload());
        Log.i(TAG, "消息到达,message: " + msg);
        EventBus.getDefault().postSticky(new MessageEvent(msg));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.i(TAG, "消息成功发送");
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        Log.i(TAG, "连接完成");
        int[] qos = {1};
        String[] topic = {TOPIC};
        try {
            mqttClient.subscribe(topic, qos);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
