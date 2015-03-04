package com.skiyaki.hryk32be.sample_feed;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TabHost;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {

    protected ListView blog_feed;
    protected ListView youtube_feed;
    private FeedItemAdapter blog_adapter;
    private FeedItemAdapter youtube_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        blog_adapter = new FeedItemAdapter(this);
        blog_feed = (ListView) findViewById(R.id.feed_items);
        adaptListView(blog_feed, blog_adapter);
        requestLivedoorBlog("http://blog.livedoor.jp/gumbycomics/index.rdf");

        youtube_adapter = new FeedItemAdapter(this);
        youtube_feed = (ListView) findViewById(R.id.youtube_items);
        adaptListView(youtube_feed, youtube_adapter);
        requestYoutube("あおむろひろゆき");
        renderingTabView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar feed_item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void renderingTabView() {

        //TabHostオブジェクト取得
        TabHost tabhost = (TabHost)findViewById(R.id.tabHost);
        tabhost.setup();

        TabHost.TabSpec tab1 = tabhost.newTabSpec("tab1");
        tab1.setIndicator("ライブドア");
        tab1.setContent(R.id.tab1);
        tabhost.addTab(tab1);

        TabHost.TabSpec tab2 = tabhost.newTabSpec("tab2");
        tab2.setIndicator("Youtube");
        tab2.setContent(R.id.tab2);
        tabhost.addTab(tab2);

        tabhost.setCurrentTab(0);

    }

    private void adaptListView(ListView feed_items, FeedItemAdapter feed_item_adapter) {

        ArrayList<FeedItem> items = new ArrayList<>();
        feed_item_adapter.setFeedItem(items);
        feed_items.setAdapter(feed_item_adapter);

        feed_items.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), ShowActivity.class);
                FeedItem item = (FeedItem) parent.getItemAtPosition(position);
                startActivity(intent.putExtra("url", item.getUrl()));
            }
        });

    }

    private void requestLivedoorBlog(String url) {
        try {
            AsyncHttpRequest task = new AsyncHttpRequest(blog_adapter);
            task.execute(url);
        } catch (Exception e) {
            Log.d("AsyncHttpRequest", String.valueOf(e.getMessage()));
        }
    }

    private void requestYoutube(String word) {
        try {
            YoutubeSearch task = new YoutubeSearch(youtube_adapter);
            task.execute(word);
        } catch (Exception e) {
            Log.d("YoutubeSearch", String.valueOf(e.getMessage()));
        }
    }

}
