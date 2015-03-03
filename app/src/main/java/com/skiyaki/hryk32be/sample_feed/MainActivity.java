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

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {

    public ListView feed_items;
    public ArrayList items;
    public FeedItemAdapter feed_item_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        feed_items = (ListView) findViewById(R.id.feed_items);
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

        try {
            String url = "http://blog.livedoor.jp/gumbycomics/index.rdf";
            AsyncHttpRequest task = new AsyncHttpRequest(this,items,feed_item_adapter);
            task.parent = this;
            task.execute(url);
        } catch (Exception e) {
            System.out.println(e);
        }

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

}
