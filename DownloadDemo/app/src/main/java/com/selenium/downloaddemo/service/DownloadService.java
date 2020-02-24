package com.selenium.downloaddemo.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.selenium.downloaddemo.MainActivity;
import com.selenium.downloaddemo.bean.FileInfo;
import com.selenium.downloaddemo.task.DownloadTask;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class DownloadService extends Service {

    private static final String TAG = "DownloadService";
    public static final String ACTION_START = "start";
    public static final String ACTION_PAUSE = "pause";
    public static final String ACTION_UPDATE = "update";
    public static final String DOWNLOAD_SAVE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/downloads/";
    private static final int MSG_INIT = 0;
    private DownloadTask downloadTask = null;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MSG_INIT:
                    FileInfo fileInfo = (FileInfo) msg.obj;
                    Log.i(TAG, "handleMessage: length: " + fileInfo.getLength());
                    //启动下载任务
                    downloadTask = new DownloadTask(DownloadService.this, fileInfo);
                    downloadTask.download();
            }
        }
    };

    //接受来自Activity的参数
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null == intent) {
            return START_NOT_STICKY;
        }
        String action = intent.getAction();
        if (ACTION_START.equals(action)) {
            FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
            Log.i(TAG, "onStartCommand: Start: " + fileInfo.toString());
            //启动初始化线程
            new DownloadInitThread(fileInfo).start();

        } else if (ACTION_PAUSE.equals(action)) {
            FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
            Log.i(TAG, "onStartCommand: Pause: " + fileInfo.toString());
            if (downloadTask != null) {
                downloadTask.isPause = true;
            }
        }

        return super.onStartCommand(intent, Service.START_FLAG_REDELIVERY, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //下载初始化线程，获得下载文件的大小和名称并在本地创建文件
    class DownloadInitThread extends Thread {
        private FileInfo fileInfo;

        public DownloadInitThread(FileInfo fileInfo) {
            this.fileInfo = fileInfo;
        }

        @Override
        public void run() {
            try {
                URL url = new URL(fileInfo.getUrl());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //注意这里要使用GET不能写get
                connection.setRequestMethod("GET");
                int responseCode = connection.getResponseCode();
                int length = -1;
                if (HttpURLConnection.HTTP_OK == responseCode) {
                    length = connection.getContentLength();
                    if (length <= 0) {
                        return;
                    }
                    //在本地创建文件
                    File dir = new File(DOWNLOAD_SAVE_PATH);
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    File file = new File(dir, fileInfo.getFileName());
                    //断点续传的核心类RandomAccessFile
                    //rwd:read write delete
                    RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rwd");
                    //设置本地保存文件大小
                    randomAccessFile.setLength(length);
                    //将FileInfo对象传递出去
                    fileInfo.setLength(length);
                    mHandler.obtainMessage(MSG_INIT, fileInfo).sendToTarget();
                } else {
                    return;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
