package com.wantao.syntask.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wantao.syntask.ImageLoader;
import com.wantao.syntask.R;
import com.wantao.syntask.beans.NewsBean;

import java.util.List;

public class NewsAdapter extends BaseAdapter {

    private List<NewsBean> mList;
    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;
    public NewsAdapter(Context context, List<NewsBean> data) {
        mList = data;
        //加载布局填充器
        mInflater = LayoutInflater.from(context);
        mImageLoader = new ImageLoader();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            //通过布局填充器将layout转为view对象
            convertView = mInflater.inflate(R.layout.item_layout, null);
            //通过view对象，找到对应的组件
            viewHolder.ivIcon = convertView.findViewById(R.id.iv_icon);
            viewHolder.tvTitle = convertView.findViewById(R.id.tv_title);
            viewHolder.tvContent = convertView.findViewById(R.id.tv_content);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.ivIcon.setImageResource(R.mipmap.ic_launcher);
        viewHolder.ivIcon.setTag(mList.get(i).getNewsIconUrl());
        //mImageLoader.showImageByThread(viewHolder.ivIcon, mList.get(i).getNewsIconUrl());
        mImageLoader.showImageByAsynTask(viewHolder.ivIcon, mList.get(i).getNewsIconUrl());
        viewHolder.tvTitle.setText(mList.get(i).getNewsTitle());
        viewHolder.tvContent.setText(mList.get(i).getNewsContent());

        return convertView;
    }

    class ViewHolder {
        private ImageView ivIcon;
        private TextView tvTitle;
        private TextView tvContent;
    }
}
