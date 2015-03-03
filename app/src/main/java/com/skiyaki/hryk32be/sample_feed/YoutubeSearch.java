package com.skiyaki.hryk32be.sample_feed;

import android.app.Activity;
import android.os.AsyncTask;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by hryk32be on 2015/03/03.
 */
public class YoutubeSearch extends AsyncTask<String, Void, String>{

    /** Global instance properties filename. */
    private static String PROPERTIES_FILENAME = "youtube.properties";

    /** Global instance of the HTTP transport. */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    /** Global instance of the max number of videos we want returned (50 = upper limit per page). */
    private static final long NUMBER_OF_VIDEOS_RETURNED = 25;

    /** Global instance of Youtube object to make all API requests. */
    private static YouTube youtube;

    public Activity parent;
    private String Response;

    private ArrayList feed_item_list;
    private FeedItemAdapter feed_adapter;

    public YoutubeSearch(Activity activity, ArrayList<FeedItem> list, FeedItemAdapter adapter) {
        feed_item_list = list;
        feed_adapter = adapter;
        parent = activity;
        System.out.println("START!!");
    }

    @Override
    protected String doInBackground(String... words) {
        System.out.println(words[0]);
        String word = words[0];
        try {
            youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {}
            }).setApplicationName("youtube-cmdline-search-sample").build();

            YouTube.Search.List search = youtube.search().list("id,snippet");

            String apiKey = "AIzaSyBc1e0S7NQpoL153SRan0xvI1PqAsKf30k";
            System.out.println(apiKey);
            search.setKey(apiKey);
            search.setQ(word);

            search.setType("video");

            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
            SearchListResponse searchResponse = search.execute();

            List<SearchResult> searchResultList = searchResponse.getItems();
            if (searchResultList != null) {
                prettyPrint(searchResultList.iterator(), word);
            }

        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
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
    private void prettyPrint(Iterator<SearchResult> iteratorSearchResults, String query) {

        System.out.println("\n=============================================================");
        System.out.println(
                "   First " + NUMBER_OF_VIDEOS_RETURNED + " videos for search on \"" + query + "\".");
        System.out.println("=============================================================\n");

        if (!iteratorSearchResults.hasNext()) {
            System.out.println(" There aren't any results for your query.");
        }

        while (iteratorSearchResults.hasNext()) {
            FeedItem feed_item = new FeedItem();

            SearchResult singleVideo = iteratorSearchResults.next();
            ResourceId rId = singleVideo.getId();

            // Double checks the kind is video.
            if (rId.getKind().equals("youtube#video")) {
                Thumbnail thumbnail = (Thumbnail) singleVideo.getSnippet().getThumbnails().get("default");

                feed_item.setUrl("http://m.youtube.com/watch?v="+rId.getVideoId());
                feed_item.setName(singleVideo.getSnippet().getTitle());
                feed_item.setImage(thumbnail.getUrl());

                this.feed_item_list.add(feed_item);
                this.feed_adapter.notifyDataSetChanged();

                System.out.println(" Video Id" + rId.getVideoId());
                System.out.println(" Title: " + singleVideo.getSnippet().getTitle());
                System.out.println(" Thumbnail: " + thumbnail.getUrl());
                System.out.println("\n-------------------------------------------------------------\n");
            }
        }
    }
}
