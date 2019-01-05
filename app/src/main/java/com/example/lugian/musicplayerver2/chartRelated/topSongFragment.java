package com.example.lugian.musicplayerver2.chartRelated;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.example.lugian.musicplayerver2.MainActivity;
import com.example.lugian.musicplayerver2.R;
import com.example.lugian.musicplayerver2.RequestSingleton.RequestQueueSingleton;
import com.example.lugian.musicplayerver2.Song;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class topSongFragment extends Fragment {
    private RecyclerView recyclerViewTopSong;
    private ArrayList<Song> topSongList;
    private RecyclerViewAdapter adapter;
    LinearLayoutManager layoutManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //new top song list
        topSongList = new ArrayList<>();
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_top_song, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //RecyclerView set ups
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewTopSong = (RecyclerView)view.findViewById(R.id.recycler_view);

        recyclerViewTopSong.setLayoutManager(layoutManager);
        adapter = new RecyclerViewAdapter(topSongList, getActivity());
        adapter.setOnItemClickListener(new RecyclerViewAdapter.topClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                ((MainActivity) getActivity()).songPicked(position);
            }
        });
        recyclerViewTopSong.setAdapter(adapter);
        //Add context menu for recyclerView
        registerForContextMenu(recyclerViewTopSong);
        //Get song chart in mp3 zing
        getMp3TopSongs();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = -1;
        try {
            position = ((RecyclerViewAdapter)recyclerViewTopSong.getAdapter()).getPosition();
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

    public void getMp3TopSongs(){
        String url = "https://mp3.zing.vn/xhr/chart-realtime?chart=song&time=-1&count=10";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONObject("data").getJSONArray("song");
                    for(int i = 0; i < jsonArray.length(); ++i){
                        JSONObject currentJSONObject = jsonArray.getJSONObject(i);
                        parseJSONSongToTopSongList(currentJSONObject, Integer.toString(i+1), i);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
                for(int i = 0; i < topSongList.size(); ++i){
                    getSongThumbnail(topSongList.get(i));
                }
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueueSingleton.getInstance(getActivity()).addToRequestQueue(request);
    }

    public void parseJSONSongToTopSongList(JSONObject currentJSONObject, String rank, int position){
        try {
            String songName = currentJSONObject.getString("name");
            String artistName = currentJSONObject.getString("artists_names");
            //Remember to update RecyclerViewAdapter onViewBinder
            String key = currentJSONObject.getString("code");
            String pageURL = "https://mp3.zing.vn" + currentJSONObject.getString("link");
            String thumbURL = currentJSONObject.getString("thumbnail");
            //Adding info to song
            final Song currentSong = new Song(songName, artistName);
            currentSong.setOnlineSongInfo(pageURL, thumbURL, rank);
            getSourceLinkByKey(key, new VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    currentSong.setSourceLink(result);
                }
            });
            topSongList.add(position, currentSong);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getSongThumbnail(final Song song){
        ImageRequest imageRequest = new ImageRequest(song.getThumbURL(), new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                song.setImageAlbum(response);
                adapter.notifyDataSetChanged();
            }
        }, 0, 0, ImageView.ScaleType.CENTER, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueueSingleton.getInstance(getActivity()).addToRequestQueue(imageRequest);
    }

    public void getSourceLinkByKey(String key, final VolleyCallback volleyCallback){
        String urlKey = "https://mp3.zing.vn/xhr/media/get-source?type=audio&key=" + key;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlKey, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject songJsonObject = response.getJSONObject("data");
                    volleyCallback.onSuccess(songJsonObject.getJSONObject("source").getString("128"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueueSingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
    }

    public interface VolleyCallback{
        void onSuccess(String result);
    }

    public ArrayList<Song> getTopSongList() {
        return topSongList;
    }
}
