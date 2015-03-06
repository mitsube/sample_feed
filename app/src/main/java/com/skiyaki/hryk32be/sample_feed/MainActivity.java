package com.skiyaki.hryk32be.sample_feed;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TabHost;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements AbsListView.OnScrollListener, Callback {

    protected ListView blog_feed;
    protected ListView youtube_feed;
    private FeedItemAdapter blog_adapter;
    private FeedItemAdapter youtube_adapter;
    private YoutubeSearch youtube_search;
    private String SEARCH_WORD = "Anthax";
    private String next_page_token = null;
    private Integer current_page = 1;

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
        youtube_feed.setOnScrollListener(this);
        youtube_feed.addFooterView(getFooter());
        additionalReading();
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

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (totalItemCount == firstVisibleItem + visibleItemCount) {
            additionalReading();
        }
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
            youtube_search = new YoutubeSearch(MainActivity.this, youtube_feed, youtube_adapter);
            youtube_search.execute(word, String.valueOf(current_page), next_page_token);
        } catch (Exception e) {
            Log.d("YoutubeSearch", String.valueOf(e.getMessage()));
        }
        current_page += 1;
    }

    private View getFooter() {
        View mFooter = null;
        if (mFooter == null) {
            mFooter = getLayoutInflater().inflate(R.layout.loading_footer,null);
        }
        return mFooter;
    }

    private void invisibleFooter() {
        youtube_feed.removeFooterView(getFooter());
    }

    private void additionalReading() {
        if (youtube_search != null && youtube_search.getStatus() == YoutubeSearch.Status.RUNNING) {
            return;
        } else {
            requestYoutube(SEARCH_WORD);
        }
    }

    @Override
    public void callback(String token) {
        next_page_token = token;
    }
}
