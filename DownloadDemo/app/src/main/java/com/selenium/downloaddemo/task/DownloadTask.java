package com.selenium.downloaddemo.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.selenium.downloaddemo.MainActivity;
import com.selenium.downloaddemo.bean.DownloadThreadInfo;
import com.selenium.downloaddemo.bean.FileInfo;
import com.selenium.downloaddemo.dao.ThreadDao;
import com.selenium.downloaddemo.impl.ThreadDaoImpl;
import com.selenium.downloaddemo.service.DownloadService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class DownloadTask {
    private static final String TAG = "DownloadTask";
    private Context mContext = null;
    private FileInfo mFileInfo = null;
    private ThreadDao mThreadDao = null;
    private int mProgress = 0;
    public boolean isPause = false;


    public DownloadTask(Context mContext, FileInfo mFileInfo) {
        this.mContext = mContext;
        this.mFileInfo = mFileInfo;
        mThreadDao = new ThreadDaoImpl(mContext);
    }

    public void download() {
        //读取数据库线程信息
        List<DownloadThreadInfo> threadInfos = mThreadDao.getThreads(mFileInfo.getUrl());
        DownloadThreadInfo threadInfo = null;
        if (threadInfos.size() == 0) {
            Log.i(TAG, "download: thread size 0");
            //初始化线程信息
            threadInfo = new DownloadThreadInfo(0, mFileInfo.getUrl(), 0, mFileInfo.getLength(), 0);
        } else {
            Log.i(TAG, "download: thread size > 0");
            //单线程下载
            threadInfo = threadInfos.get(0);
        }
        //创建子线程进行下载
        Log.i(TAG, "download: threadInfo: " + threadInfo);
        new DownloadThread(threadInfo).start();

    }

    class DownloadThread extends Thread {
        private DownloadThreadInfo threadInfo = null;

        public DownloadThread(DownloadThreadInfo threadInfo) {
            this.threadInfo = threadInfo;
        }

        @Override
        public void run() {
            HttpURLConnection connection = null;
            RandomAccessFile randomAccessFile = null;
            InputStream input = null;

            //查询数据库中是否存在记录
            if (!mThreadDao.isExists(threadInfo.getUrl(), threadInfo.getId())) {
                mThreadDao.insertThread(threadInfo);
            }
            try {
                URL url = new URL(threadInfo.getUrl());
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                //设置下载位置
                int start = threadInfo.getBegin() + threadInfo.getProgress();
                //断点续传关键: 请求信息设置range字段
                connection.setRequestProperty("Range", "bytes=" + start + "-" + threadInfo.getEnd());
                //在本地创建文件
                File dir = new File(DownloadService.DOWNLOAD_SAVE_PATH);
                if (!dir.exists()) {
                    dir.mkdir();
                }
                File file = new File(dir, mFileInfo.getFileName());
                //断点续传的关键:RandomAccessFile
                //rwd:read write delete
                randomAccessFile = new RandomAccessFile(file, "rwd");
                randomAccessFile.seek(start);
                Intent intent = new Intent(DownloadService.ACTION_UPDATE);
                mProgress += threadInfo.getProgress();
                Log.i(TAG, "run: connection.getResponseCode: " + connection.getResponseCode());
                //开始下载
                //注意下载时候这里的responseCode是206
                if (HttpURLConnection.HTTP_PARTIAL == connection.getResponseCode()) {
                    //读取数据
                    input = connection.getInputStream();
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = input.read(buffer)) != -1) {
                        //写入文件
                        randomAccessFile.write(buffer, 0, len);
                        //把下载进度发送给Activity
                        mProgress += len;
                        Log.i(TAG, "run: send BroadCast update ui ");
                        intent.putExtra("progress", mProgress * 100 / mFileInfo.getLength());
                        mContext.sendBroadcast(intent);
                        //暂停状态，保存下载信息到数据库
                        if (isPause) {
                            mThreadDao.updateThread(threadInfo.getUrl(), threadInfo.getId(), mProgress);
                            return;
                        }
                    }
                    mThreadDao.deleteThread(threadInfo.getUrl(), threadInfo.getId());

                } else {
                    return;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    input.close();
                    randomAccessFile.close();
                    connection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

}
