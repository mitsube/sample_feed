package com.skiyaki.hryk32be.sample_feed;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by hryk32be on 2015/03/02.
 */
public class AsyncHttpRequest extends AsyncTask<String, Void, String> {

    public Activity parent;
    private String Response;

    private ArrayList feed_item_list;
    private FeedItemAdapter feed_adapter;

    public AsyncHttpRequest(Activity activity, ArrayList<FeedItem> list, FeedItemAdapter adapter) {
        feed_item_list = list;
        feed_adapter = adapter;
        parent = activity;
    }

    @Override
    protected String doInBackground(String... url) {
        try {
            HttpGet httpGet = new HttpGet(url[0]);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            httpGet.setHeader("Connection", "Keep-Alive");

            HttpResponse response = httpClient.execute(httpGet);
            int status = response.getStatusLine().getStatusCode();
            if (status != HttpStatus.SC_OK) {
                throw new Exception("通信エラー");
            } else {
                Response = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onPostExecute(String result) {

        FeedItem feed_item = null;

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput( new StringReader( Response ));

            int eventType = parser.getEventType();

            while(eventType!=XmlPullParser.END_DOCUMENT) {
                String tag = null;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        tag = parser.getName();
                        if (tag.equals("item")) {
                            feed_item = new FeedItem();
                        } else if (feed_item != null) {
                            if (tag.equals("link")) {
                                feed_item.setUrl(parser.nextText());
                            } else if (tag.equals("title")) {
                                feed_item.setName(parser.nextText());
                            } else if (tag.equals("description")) {
                                feed_item.setDescription(parser.nextText());
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        tag = parser.getName();
                        if (tag.equals("item")) {
                            feed_item_list.add(feed_item);
                            feed_adapter.notifyDataSetChanged();
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e ){
            Log.d("Error!!!!","!!!!!!!!!!!");
            System.out.println(e);
        }

    }
}
