package com.selenium.downloaddemo.impl;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.selenium.downloaddemo.bean.DownloadThreadInfo;
import com.selenium.downloaddemo.dao.ThreadDao;
import com.selenium.downloaddemo.db.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class ThreadDaoImpl implements ThreadDao {

    private DBHelper dbHelper = null;

    public ThreadDaoImpl(Context context) {
        dbHelper = new DBHelper(context);
    }

    @Override
    public void insertThread(DownloadThreadInfo threadInfo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("insert into thread_info(thread_id,url,begin,end,progress) values(?,?,?,?,?)",
                new Object[]{threadInfo.getId(), threadInfo.getUrl(), threadInfo.getBegin(), threadInfo.getEnd(), threadInfo.getProgress()}
        );
        db.close();
    }

    @Override
    public void deleteThread(String url, int thread_id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("delete from thread_info where url = ? and thread_id=? ",
                new Object[]{url, thread_id}
        );
        db.close();
    }

    @Override
    public void updateThread(String url, int thread_id, int progress) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("update thread_info set progress = ? where url=? and thread_id =? ",
                new Object[]{progress, url, thread_id}
        );
        db.close();
    }

    @Override
    public List<DownloadThreadInfo> getThreads(String url) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        List<DownloadThreadInfo> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from thread_info where url = ? ",
                new String[]{url}
        );
        while (cursor.moveToNext()) {
            DownloadThreadInfo threadInfo = new DownloadThreadInfo();
            threadInfo.setId(cursor.getInt(cursor.getColumnIndex("thread_id")));
            threadInfo.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            threadInfo.setBegin(cursor.getInt(cursor.getColumnIndex("begin")));
            threadInfo.setEnd(cursor.getInt(cursor.getColumnIndex("end")));
            threadInfo.setProgress(cursor.getInt(cursor.getColumnIndex("progress")));
            list.add(threadInfo);
        }
        cursor.close();
        db.close();
        return list;
    }

    @Override
    public boolean isExists(String url, int thread_id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from thread_info where url = ? and thread_id = ? ",
                new String[]{url, String.valueOf(thread_id)}
        );
        boolean exist = cursor.moveToNext();
        cursor.close();
        db.close();
        return exist;
    }
}
