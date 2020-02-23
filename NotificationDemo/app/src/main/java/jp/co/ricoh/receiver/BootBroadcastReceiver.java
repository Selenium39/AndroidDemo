package jp.co.ricoh.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import jp.co.ricoh.service.MQTTService;

public class BootBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "BootBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive: 开机启动service");
        Intent serviceIntent = new Intent(context, MQTTService.class);
        context.startService(serviceIntent);
    }
}
