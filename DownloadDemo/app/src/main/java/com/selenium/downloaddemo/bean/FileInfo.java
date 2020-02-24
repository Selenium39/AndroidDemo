package com.selenium.downloaddemo.bean;

import java.io.Serializable;

public class FileInfo implements Serializable {
    private int id;
    private String url;
    private String fileName;
    private int length;
    private int progress;

    public FileInfo() {
    }

    public FileInfo(int id, String url, String fileName, int length, int progress) {
        this.id = id;
        this.url = url;
        this.fileName = fileName;
        this.length = length;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }


    @Override
    public String toString() {
        return "FileInfo{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", fileName='" + fileName + '\'' +
                ", length=" + length +
                ", progress=" + progress +
                '}';
    }
}
