package com.wantao.syntask;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.wantao.syntask.adapters.NewsAdapter;
import com.wantao.syntask.beans.NewsBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private static String URL = "https://www.imooc.com/api/teacher?type=4&num=30";
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = findViewById(R.id.lv_main);
        new NewsAsynTask().execute(URL);
    }

    //通过API获取到JSON数据并转化我们封装的NewsBean对象
    private List<NewsBean> getJsonData(String url) {
        List<NewsBean> newsBeanList = new ArrayList<>();
        try {
            String jsonData = readStream(new URL(url).openStream());  //new URL().openStream()相当于url.openConnection.getInputStream
            Log.i(TAG, "getJsonData: " + jsonData);
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                NewsBean newsBean = new NewsBean();
                newsBean.setNewsIconUrl(jsonObject1.getString("picSmall"));
                newsBean.setNewsTitle(jsonObject1.getString("name"));
                newsBean.setNewsContent(jsonObject1.getString("description"));
                newsBeanList.add(newsBean);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newsBeanList;

    }

    //通过InputStream解析网页返回的数据
    private String readStream(InputStream is) {
        InputStreamReader isr = null;
        String result = "";
        try {
            isr = new InputStreamReader(is, "utf8");  //将字节流转为字符流
            BufferedReader br = new BufferedReader(isr);  //通过BufferReader读取
            String line = "";
            while ((line = br.readLine()) != null) {
                result += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    //实现网页的异步访问
    class NewsAsynTask extends AsyncTask<String, Void, List<NewsBean>> {
        @Override
        protected List<NewsBean> doInBackground(String... urls) {
            return getJsonData(urls[0]);
        }

        @Override
        protected void onPostExecute(List<NewsBean> newsBeans) {
            super.onPostExecute(newsBeans);
            //构建数据源
            NewsAdapter newsAdapter = new NewsAdapter(MainActivity.this, newsBeans);
            mListView.setAdapter(newsAdapter);
        }
    }


}
