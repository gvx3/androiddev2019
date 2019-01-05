package com.example.lugian.musicplayerver2.searchRelated;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.lugian.musicplayerver2.MainActivity;
import com.example.lugian.musicplayerver2.R;
import com.example.lugian.musicplayerver2.RequestSingleton.RequestQueueSingleton;
import com.example.lugian.musicplayerver2.Song;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class OnlineSearchFragment extends Fragment {
    private RecyclerView recyclerViewSearchSong;
    private ArrayList<Song> resultSongs;
    private OnlineRecyclerViewAdapter adapter;
    LinearLayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //new song list
        resultSongs = new ArrayList<>();
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.online_search, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //RecyclerView set ups
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewSearchSong = (RecyclerView)view.findViewById(R.id.online_recycler_view);

        recyclerViewSearchSong.setLayoutManager(layoutManager);
        adapter = new OnlineRecyclerViewAdapter(resultSongs, getActivity());
        adapter.setOnItemClickListener(new OnlineRecyclerViewAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Toast.makeText(getActivity(), "onItemClickPosition" + position, Toast.LENGTH_SHORT).show();
                ((MainActivity) getActivity()).songPicked(position);
            }

            @Override
            public void onItemLongClick(int position, View v) {

            }
        });
        recyclerViewSearchSong.setAdapter(adapter);
        //Add context menu for recyclerView
        registerForContextMenu(recyclerViewSearchSong);
        //Get song chart in mp3 zing

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = -1;
        try {
            position = ((OnlineRecyclerViewAdapter)recyclerViewSearchSong.getAdapter()).getPosition();
        } catch (Exception e) {
            return super.onContextItemSelected(item);
        }
        switch (item.getItemId()) {
            case R.id.downloadButton:
                Toast.makeText(getActivity(), "Downloading", Toast.LENGTH_SHORT).show();

                break;
        }
        return super.onContextItemSelected(item);
    }

    public void searchMusicOnline(String urlSearch){
        //adapter.notifyDataSetChanged();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlSearch,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Document mp3zing = Jsoup.parse(response);
                        Elements searchlist = mp3zing.getElementsByClass("item-song");
                        for(int i = 0; i < searchlist.size(); ++i){
                            Element itemElement = searchlist.get(i);
                            String itemsongkey = itemElement.attr("data-code");
                            String urlkey = "https://mp3.zing.vn/xhr/media/get-source?type=audio&key=" + itemsongkey;
                            getSongByKey(urlkey);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );
        RequestQueueSingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }
    public ArrayList<Song> getResultSongs() {
        return resultSongs;
    }

    public void getSongByKey(String urlKey){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlKey, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject songJsonObject = response.getJSONObject("data");
                    String name = songJsonObject.getString("name");
                    String artist = songJsonObject.getString("artists_names");
                    String pageURL = "https://mp3.zing.vn" + songJsonObject.getString("link");
                    String thumbURL = songJsonObject.getString("thumbnail");
                    String sourceLink = songJsonObject.getJSONObject("source").getString("128");

                    final Song newSong = new Song(name, artist);
                    newSong.setOnlineSongInfo(pageURL, thumbURL,  "");
                    newSong.setSourceLink(sourceLink);
                    resultSongs.add(newSong);
                    adapter.notifyDataSetChanged();

                    ImageRequest imageRequest = new ImageRequest(newSong.getThumbURL(), new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            newSong.setImageAlbum(response);
                            adapter.notifyDataSetChanged();
                        }
                    }, 0, 0, ImageView.ScaleType.CENTER, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });

                    RequestQueueSingleton.getInstance(getActivity()).addToRequestQueue(imageRequest);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueueSingleton.getInstance(getActivity()).addToRequestQueue(request);
    }

    public OnlineRecyclerViewAdapter getAdapter() {
        return adapter;
    }

}
