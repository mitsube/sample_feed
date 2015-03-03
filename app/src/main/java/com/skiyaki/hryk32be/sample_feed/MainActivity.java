package com.skiyaki.hryk32be.sample_feed;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TabHost;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {

    ArrayList<FeedItem> items = null;
    FeedItemAdapter feed_item_adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        renderListView((ListView) findViewById(R.id.feed_items));
        renderListView((ListView) findViewById(R.id.youtube_items));

        requestLivedoorBlog("http://blog.livedoor.jp/gumbycomics/index.rdf");
        requestYoutube("Anthrax");
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

    private void renderListView(ListView feed_items) {

        items = new ArrayList<FeedItem>();
        feed_item_adapter = new FeedItemAdapter(this);
        feed_item_adapter.setFeedItem(items);
        feed_items.setAdapter(feed_item_adapter);

        feed_items.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), ShowActivity.class);
                FeedItem item = (FeedItem) items.get(position);
                startActivity(intent.putExtra("url", item.getUrl()));
            }
        });

    }

    private void requestLivedoorBlog(String url) {
        try {
            AsyncHttpRequest task = new AsyncHttpRequest(this, items, feed_item_adapter);
            task.parent = this;
            task.execute(url);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void requestYoutube(String word) {
        try {
            YoutubeSearch task = new YoutubeSearch(this, items, feed_item_adapter);
            task.parent = this;
            task.execute(word);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
