package com.wantao.syntask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ImageLoader {

    private ImageView mImageView;
    private String mUrl;
    private LruCache<String, Bitmap> mLruCache;

    public ImageLoader() {
        //获取最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 4;
        mLruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //在每次存入缓存中调用,告诉我们的系统存入的对象有多大
                return value.getByteCount();
            }
        };
    }

    //增加到缓存
    public void addBitmapToCache(String url, Bitmap bitmap) {
        if (getBitmapFromCache(url) == null) {
            mLruCache.put(url, bitmap);
        }
    }

    //从缓存中获取数据
    public Bitmap getBitmapFromCache(String url) {
        return mLruCache.get(url);
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (mImageView.getTag().equals(mUrl))  //只有ImageView的tag为当前url时,才进行设置
                mImageView.setImageBitmap((Bitmap) msg.obj);
        }
    };


    //新建线程加载图片
    public void showImageByThread(ImageView imageView, final String url) {
        mImageView = imageView;
        mUrl = url;
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    //Android中非主线程无法在线程中更新ui,可通过Handler把数据传递到主线程
                    Bitmap bitmap = getBitmapFromUrl(url);
                    Message message = Message.obtain();
                    message.obj = bitmap;
                    Thread.sleep(1000);
                    mHandler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    //通过AsynTask异步加载图片
    public void showImageByAsynTask(ImageView imageView, String urlString) {
        Bitmap bitmap = null;
        //从缓存中取出对应的图片，如果缓存中没有，我们就从网络中去下载
        bitmap = mLruCache.get(urlString);
        if (bitmap == null) {
            new loadImageAsynTask(imageView, urlString).execute(urlString);
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }


    public class loadImageAsynTask extends AsyncTask<String, Void, Bitmap> {

        private ImageView mImageView;
        private String mUrl;

        public loadImageAsynTask(ImageView imageView, String url) {
            mImageView = imageView;
            mUrl = url;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap bitmap = null;
            try {
                //从网络上获取图片
                bitmap = getBitmapFromUrl(urls[0]);
                if (bitmap != null) {
                    //将下载好的图片保存到LruCache中s
                    mLruCache.put(urls[0], bitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (mImageView.getTag().equals(mUrl))
                mImageView.setImageBitmap(bitmap);
        }
    }


    //从Url中读取Bitmap
    public Bitmap getBitmapFromUrl(String urlString) throws IOException {
        Bitmap bitmap = null;
        InputStream inputStream = null;
        try {
            inputStream = new URL(urlString).openStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            inputStream.close();
        }
        return bitmap;
    }
}
