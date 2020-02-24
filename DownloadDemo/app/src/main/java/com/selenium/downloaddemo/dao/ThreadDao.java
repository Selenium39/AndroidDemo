package com.selenium.downloaddemo.dao;

import com.selenium.downloaddemo.bean.DownloadThreadInfo;

import java.util.List;

public interface ThreadDao {
    void insertThread(DownloadThreadInfo threadInfo);

    void deleteThread(String url, int thread_id);

    void updateThread(String url, int thread_id, int progress);

    List<DownloadThreadInfo> getThreads(String url);

    boolean isExists(String url, int thread_id);
}
