package com.selenium.downloaddemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.selenium.downloaddemo.bean.FileInfo;
import com.selenium.downloaddemo.service.DownloadService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private TextView mFileName;
    private ProgressBar mProgressBar;
    private Button btn_start;
    private Button btn_pause;
    private FileInfo fileInfo = null;
    private BroadcastReceiver mReceiver;
    private static final String DOWNLOAD_URL = "http://online2.tingclass.net/lesson/shi0529/0008/8694/as_it_is_20160523d.mp3";
    private static final String DOWNLOAD_FILE_NAME = "as_it_is_20160523d.mp3";
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFileName = findViewById(R.id.tv_file_name);
        mProgressBar = findViewById(R.id.pb_download);
        mProgressBar.setMax(100);

        btn_start = findViewById(R.id.btn_start);
        btn_pause = findViewById(R.id.btn_pause);
        btn_start.setOnClickListener(this);
        btn_pause.setOnClickListener(this);
        fileInfo = new FileInfo(0, DOWNLOAD_URL, DOWNLOAD_FILE_NAME, 0, 0);
        //更新ui的进度条
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DownloadService.ACTION_UPDATE.equals(action)) {
                    int progress = intent.getIntExtra("progress", 0);
                    Log.i(TAG, "onReceive: progress: " + progress);
                    mProgressBar.setProgress(progress);
                }
            }
        };
        //注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadService.ACTION_UPDATE);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:

                int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
                }

                Intent intent = new Intent(MainActivity.this, DownloadService.class);
                intent.setAction(DownloadService.ACTION_START);
                intent.putExtra("fileInfo", fileInfo);
                startService(intent);
                break;
            case R.id.btn_pause:
                Intent intent1 = new Intent(MainActivity.this, DownloadService.class);
                intent1.setAction(DownloadService.ACTION_PAUSE);
                intent1.putExtra("fileInfo", fileInfo);
                startService(intent1);
                break;
        }
    }
}
