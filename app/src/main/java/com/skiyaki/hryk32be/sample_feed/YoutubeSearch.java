package com.skiyaki.hryk32be.sample_feed;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.ListView;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PageInfo;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by hryk32be on 2015/03/03.
 */
public class YoutubeSearch extends AsyncTask<String, Void, String>{

    /** Global instance of the HTTP transport. */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    /** Global instance of the max number of videos we want returned (50 = upper limit per page). */
    private static final long NUMBER_OF_VIDEOS_RETURNED = 10;

    /** Global instance of Youtube object to make all API requests. */
    private static YouTube youtube;
    private YouTube.Search.List search;

    private FeedItemAdapter youtube_adapter;
    private ListView youtube_feed;

    private String APIKEY = "AIzaSyBc1e0S7NQpoL153SRan0xvI1PqAsKf30k";

    private PageInfo page_info;
    private String next_page_token;
    private String current_page;
    private Callback callback;

    public YoutubeSearch(Callback callback, ListView list, FeedItemAdapter adapter) {
        this.callback = callback;
        youtube_adapter = adapter;
        youtube_feed = list;

        youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
            public void initialize(HttpRequest request) throws IOException {}
        }).setApplicationName("skiyaki.tokyo").build();
    }

    @Override
    protected String doInBackground(String... args) {
        String word = args[0];
        String token = args[2];
        this.current_page = args[1];

        doSearch(word,token);
        return null;
    }

    /*
     * Prints out all SearchResults in the Iterator. Each printed line includes title, id, and
     * thumbnail.
     *
     * @param iteratorSearchResults Iterator of SearchResults to print
     *
     * @param query Search query (String)
     */
    private void rendering(Iterator<SearchResult> iteratorSearchResults, String query) {

        System.out.println("\n=============================================================");
        System.out.println(
                "   First " + NUMBER_OF_VIDEOS_RETURNED + " videos for search on \"" + query + "\".");
        System.out.println("=============================================================\n");

        if (!iteratorSearchResults.hasNext()) {
            System.out.println(" There aren't any results for your query.");
        }

        FeedItem feed_item = new FeedItem();
        feed_item.setName(page_info.getTotalResults().toString() + "件中" + current_page + "ページ目");
        youtube_adapter.feedItem.add(feed_item);

        while (iteratorSearchResults.hasNext()) {

            SearchResult singleVideo = iteratorSearchResults.next();
            ResourceId rId = singleVideo.getId();

            // Double checks the kind is video.
            if (rId.getKind().equals("youtube#video")) {
                feed_item = new FeedItem();
               // Thumbnail thumbnail = (Thumbnail) singleVideo.getSnippet().getThumbnails().get("default");

                feed_item.setUrl("http://m.youtube.com/watch?v="+rId.getVideoId());
                feed_item.setName(singleVideo.getSnippet().getTitle());
                //feed_item.setImage(thumbnail.getUrl());

                youtube_adapter.feedItem.add(feed_item);

                System.out.println(" Video Id" + rId.getVideoId());
                System.out.println(" Title: " + singleVideo.getSnippet().getTitle());
              //  System.out.println(" Thumbnail: " + thumbnail.getUrl());
                System.out.println("\n-------------------------------------------------------------\n");

            }
        }

    }

    @Override
    protected void onPostExecute(String result) {
        youtube_adapter.notifyDataSetChanged();
        youtube_feed.invalidateViews();
        callback.callback(next_page_token);
    }

    private void doSearch(String word, String page_token){
        try {

            search = youtube.search().list("snippet");
            search.setKey(APIKEY);
            search.setQ(word);
            search.setType("video");
            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

            if (page_token != null) search.setPageToken(page_token);
            SearchListResponse searchResponse = search.execute();

            page_info = searchResponse.getPageInfo();
            next_page_token = searchResponse.getNextPageToken();

            List<SearchResult> searchResultList = searchResponse.getItems();
            if (searchResultList != null) {
                rendering(searchResultList.iterator(), word);
            }


        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
