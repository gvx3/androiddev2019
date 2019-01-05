package com.example.lugian.musicplayerver2.searchRelated;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lugian.musicplayerver2.R;
import com.example.lugian.musicplayerver2.Song;


import java.util.ArrayList;
import java.util.List;

public class OnlineRecyclerViewAdapter extends RecyclerView.Adapter<OnlineRecyclerViewAdapter.SearchViewHolder>  {
    //Context menu part
    private int position;
    //Context menu part
    private ArrayList<Song> searchSongList;
    private Context context;
    private static ClickListener clickListener;

    public OnlineRecyclerViewAdapter(ArrayList<Song> songList, Context context) {
        this.searchSongList = songList;
        this.context = context;
    }


    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_search_song, viewGroup, false);
        SearchViewHolder holder = new SearchViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchViewHolder searchViewHolder, int position) {
        Song currentSong = searchSongList.get(position);
        searchViewHolder.thumbNail.setImageBitmap(currentSong.getImageAlbum());
        searchViewHolder.songTitle.setText(currentSong.getTitle());
        searchViewHolder.artistName.setText(currentSong.getArtist());

        //Context menu part
        searchViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener(){

            @Override
            public boolean onLongClick(View v) {
                setPosition(searchViewHolder.getAdapterPosition());
                return false;
            }
        });
        //Context menu part
    }

    @Override
    public int getItemCount() {
        return searchSongList.size();
    }
    //implement OnCreateContextMenu listener
    //implement onClickListener https://stackoverflow.com/questions/24471109/recyclerview-onclick/26196831#26196831
    public static class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, View.OnClickListener{
        ImageView thumbNail;
        TextView artistName, songTitle;


        public SearchViewHolder(View itemView){
            super(itemView);
            thumbNail = itemView.findViewById(R.id.thumbnail_search_result);
            songTitle = itemView.findViewById(R.id.song_name_search_result);
            artistName = itemView.findViewById(R.id.artist_name_search_result);
            itemView.setOnCreateContextMenuListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }

        //Context menu part
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(Menu.NONE, R.id.downloadButton, Menu.NONE, "download");
        }
        //Context menu part
    }

    public void setOnItemClickListener(ClickListener clickListener){
        OnlineRecyclerViewAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);
    }


    //Context menu part
    public int getPosition(){
        return this.position;
    }
    public void setPosition(int position){
        this.position = position;
    }
    //Context menu part


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
