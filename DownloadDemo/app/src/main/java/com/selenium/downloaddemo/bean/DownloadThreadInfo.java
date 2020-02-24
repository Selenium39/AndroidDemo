package com.selenium.downloaddemo.bean;

public class DownloadThreadInfo {
    private int id;
    private String url;
    private int begin;
    private int end;
    private int progress;

    public DownloadThreadInfo() {
    }

    public DownloadThreadInfo(int id, String url, int begin, int end, int progress) {
        this.id = id;
        this.url = url;
        this.begin = begin;
        this.end = end;
        this.progress = progress;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getBegin() {
        return begin;
    }

    public void setBegin(int begin) {
        this.begin = begin;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    @Override
    public String toString() {
        return "DownloadThreadInfo{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", begin=" + begin +
                ", end=" + end +
                ", progress=" + progress +
                '}';
    }
}
