package com.example.lugian.musicplayerver2.chartRelated;

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
import com.example.lugian.musicplayerver2.searchRelated.OnlineRecyclerViewAdapter;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    //Context menu part
    private int position;
    //Context menu part
    private ArrayList<Song> topSongList;
    private Context context;
    private static topClickListener clickListener;

    public RecyclerViewAdapter(ArrayList<Song> topSongList, Context context) {
        this.topSongList = topSongList;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_view_song, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }



    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        Song currentSong = topSongList.get(position);
        viewHolder.thumbNail.setImageBitmap(currentSong.getImageAlbum());
        viewHolder.songTitle.setText(currentSong.getTitle());
        viewHolder.artistName.setText(currentSong.getArtist());
        viewHolder.rankNum.setText(currentSong.getRankNum());

        //Context menu part
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener(){

            @Override
            public boolean onLongClick(View v) {
                setPosition(viewHolder.getAdapterPosition());
                return false;
            }
        });
        //Context menu part

    }
    //Context menu part
    //https://stackoverflow.com/questions/26466877/how-to-create-context-menu-for-recyclerview

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }


    //Context menu part

    @Override
    public int getItemCount() {
        return topSongList.size();
    }
    //implement OnCreateContextMenu listener
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, View.OnClickListener{
        ImageView thumbNail;
        TextView artistName, songTitle;
        TextView rankNum;

        public ViewHolder(View itemView){
            super(itemView);
            rankNum = itemView.findViewById(R.id.rankNumber);
            thumbNail = itemView.findViewById(R.id.thumbnail);
            songTitle = itemView.findViewById(R.id.song_name);
            artistName = itemView.findViewById(R.id.artist_name);
            itemView.setOnCreateContextMenuListener(this);
            itemView.setOnClickListener(this);

        }
        //Context menu part
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(Menu.NONE, R.id.downloadButton, Menu.NONE, "download");
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }
        //Context menu part
    }
    public void setOnItemClickListener(topClickListener clickListener){
        RecyclerViewAdapter.clickListener = clickListener;
    }
    public interface topClickListener{
        void onItemClick(int position, View v);
    }


    //Context menu part
    public int getPosition(){
        return this.position;
    }
    public void setPosition(int position){
        this.position = position;
    }
    //Context menu part
}
