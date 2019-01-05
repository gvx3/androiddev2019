package com.example.lugian.musicplayerver2;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.MediaController.MediaPlayerControl;

import com.example.lugian.musicplayerver2.chartRelated.topSongFragment;
import com.example.lugian.musicplayerver2.searchRelated.OnlineSearchFragment;
import com.example.lugian.musicplayerver2.serviceRelated.MusicService;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MediaPlayerControl {
    private OnlineSearchFragment onlineSearchFragment;
    private homeAdapter homeAdapter;
    private ViewPager pager;
    //Music service related
    private MusicService musicService;
    private Intent playIntent;
    private boolean musicBound = false;
    //Controller
    private MusicController musicController;
    //Position relating to service and controller
    private int postn = 0;
    private int durtn = 0;
    //pause and playback
    private boolean paused = false;
    private boolean playBackPaused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        homeAdapter = new homeAdapter(getSupportFragmentManager());
        pager = (ViewPager)findViewById(R.id.pager);
        pager.setOffscreenPageLimit(1);
        pager.setAdapter(homeAdapter);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab);
        tabLayout.setupWithViewPager(pager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        //Set controller
        setMusicController();


    }

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            //get service
            musicService = binder.getService();
            //pass the list
            if(pager.getCurrentItem() == 0){
                musicService.setList(((OnlineSearchFragment)homeAdapter.getMyFragments(0)).getResultSongs());
            }
            else {
                musicService.setList(((topSongFragment)homeAdapter.getMyFragments(1)).getTopSongList());
            }
            musicBound = true;
            musicService.mediaPlaye.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    musicService.onPrepared(mp);
                    musicController.show();
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search_icon);
        SearchView searchView = (SearchView) searchItem.getActionView();

        //Search info and event listeners
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String inputQuery = query.replace(" ", "+");
                String searchURL = "https://mp3.zing.vn/tim-kiem/bai-hat.html?q="+inputQuery;
                onlineSearchFragment = (OnlineSearchFragment)homeAdapter.getMyFragments(0);
                onlineSearchFragment.getResultSongs().clear();
                onlineSearchFragment.searchMusicOnline(searchURL);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.search_icon:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent == null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        musicService = null;
        super.onDestroy();
    }

    public void songPicked(int position){
        //Can the if be removed
        if(pager.getCurrentItem() == 0){
            musicService.setList(((OnlineSearchFragment)homeAdapter.getMyFragments(0)).getResultSongs());
        }
        else {
            musicService.setList(((topSongFragment)homeAdapter.getMyFragments(1)).getTopSongList());
        }
        musicService.setSong(position);
        musicService.playSong();
        if(playBackPaused){
            setMusicController();
            playBackPaused = false;
        }
        musicController.show(0);
    }

    private void setMusicController(){
        musicController = new MusicController(this);
        musicController.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });
        musicController.setMediaPlayer(this);
        musicController.setAnchorView(findViewById(R.id.pager));
        musicController.setEnabled(true);
    }

    private void playNext(){
        musicService.playNext();
        if(playBackPaused){
            setMusicController();
            playBackPaused = false;
        }
        musicController.show(0);
    }
    private void playPrev(){
        musicService.playPrev();
        if(playBackPaused){
            setMusicController();
            playBackPaused = false;
        }
        musicController.show(0);
    }
    @Override
    public void start() {
        musicService.go();
    }

    @Override
    public void pause() {
        playBackPaused = true;
        musicService.pausePlayer();
    }

    @Override
    public int getDuration() {
        if (musicService != null && musicBound && musicService.isPlaying()) {
            return durtn = musicService.getDur();
        }
//        else durtn = 0;
        return durtn;
    }

    @Override
    public int getCurrentPosition() {
        if(musicService != null && musicBound && musicService.isPlaying()){
            return postn = musicService.getPosn();
        }
        else {
            return postn;
        }
    }

    @Override
    public void seekTo(int pos) {
        musicService.seek(pos);
        postn = pos;
    }


    @Override
    public boolean isPlaying() {
        if(musicService != null && musicBound){
            return musicService.isPlaying();
        }
        else {
            return false;
        }
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(paused){
            setMusicController();
            paused = false;
        }
    }
    @Override
    protected void onStop(){
        musicController.hide();
        super.onStop();
    }

}
