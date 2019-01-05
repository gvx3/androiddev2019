package com.example.lugian.musicplayerver2.serviceRelated;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.lugian.musicplayerver2.MainActivity;
import com.example.lugian.musicplayerver2.R;
import com.example.lugian.musicplayerver2.Song;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    public MediaPlayer mediaPlaye;
    private ArrayList<Song> songs;
    private int songPosition;
    private final IBinder musicBind = new MusicBinder();
    private String songName;
    // pass the list of songs into the Service class, playing from it using the MediaPlayer class and keeping track of the position of the current song using the songPosn
    private String CHANNEL_ID = "com.example.lugian.musicplayerver2.ANDROID";
    private static final int NOTIFY_ID = 1;
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mediaPlaye.stop();
        mediaPlaye.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //check if playback has reached the end of a track
        if (mediaPlaye.getCurrentPosition() > 0) {
            mp.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();

        mp.start();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.play)
                        .setTicker("Title")
                        .setOngoing(true)
                        .setContentTitle("Playing")
                        .setContentText("Title");

        mBuilder.setContentIntent(pendingIntent);

        Notification not = mBuilder.build();

        startForeground(NOTIFY_ID, not);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        songPosition = 0;
        mediaPlaye = new MediaPlayer();
        initMusicPlayer();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        stopForeground(true);
    }



    public void initMusicPlayer(){
        mediaPlaye.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlaye.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlaye.setOnPreparedListener(this);
        mediaPlaye.setOnCompletionListener(this);
        mediaPlaye.setOnErrorListener(this);

    }

    public void playSong(){
        mediaPlaye.reset();
        Song playedSong = songs.get(songPosition);
        songName = playedSong.getTitle();
        getSongSourceAndPlay(playedSong);

    }



    public void getSongSourceAndPlay(Song playedSong){

            String source = "http:"+playedSong.getSourceLink();
            Log.i("SongSource", source);
        try {
            mediaPlaye.setDataSource(source);
            mediaPlaye.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    //Set list of songs being played
    public void setList(ArrayList<Song> songs){
        this.songs = songs;
    }

    public class MusicBinder extends Binder {
        public MusicService getService(){
            return MusicService.this;
        }
    }
    public void setSong(int songIndex){
        songPosition = songIndex;
    }

    public int getPosn(){
        return mediaPlaye.getCurrentPosition();
    }
    public int getDur(){
        return mediaPlaye.getDuration();
    }
    public boolean isPlaying(){
        return mediaPlaye.isPlaying();
    }
    public void pausePlayer(){
        mediaPlaye.pause();
    }
    public void seek(int songPosition){
        mediaPlaye.seekTo(songPosition);
    }
    public void go(){
        mediaPlaye.start();
    }
    public void playPrev(){
        songPosition--;
        if(songPosition < 0) songPosition = songs.size()-1;
        playSong();
    }
    public void playNext(){
        songPosition++;
        if(songPosition >= songs.size()) songPosition = 0;
        playSong();
    }
    /*
    private void startDownload(String url) {
        new DownloadFileAsync().execute(url);
    }

    class DownloadFileAsync extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... aurl) {
            String songname = songs.get(songPosition).getSourceLink();
            int count;
            try {
                URL url = new URL(aurl[0]);
                URLConnection conexion = url.openConnection();
                conexion.connect();
                int lenghtOfFile = conexion.getContentLength();
                Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);
                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream("/sdcard/"+songname+".mp3");
                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress(""+(int)((total*100)/lenghtOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {}
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC",progress[0]);

        }

        @Override
        protected void onPostExecute(String unused) {
            try {
                mediaPlaye.setDataSource("/mnt/sdcard/"+songName);
                mediaPlaye.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //mediaPlaye.prepareAsync();

            mediaPlaye.start();
        }
    }
    */
}
