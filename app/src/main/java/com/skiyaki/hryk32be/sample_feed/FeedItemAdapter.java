package com.skiyaki.hryk32be.sample_feed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by hryk32be on 2015/03/02.
 */
public class FeedItemAdapter extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater = null;
    ArrayList<FeedItem> feedItem;

    public FeedItemAdapter(Context context) {
        this.context = context;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setFeedItem(ArrayList<FeedItem> feedItem) {
        this.feedItem = feedItem;
    }

    @Override
    public int getCount() {
        return feedItem.size();
    }

    @Override
    public Object getItem(int position) {
        return feedItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return feedItem.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.feed_item,parent,false);
        ((TextView)convertView.findViewById(R.id.name)).setText(feedItem.get(position).getName());
        ((TextView)convertView.findViewById(R.id.description)).setText(feedItem.get(position).getDescription());

        return convertView;
    }

}
