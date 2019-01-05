package com.example.lugian.musicplayerver2;

import android.content.Context;
import android.graphics.Bitmap;

public class Song {
    private String Title;
    private String Artist;
    private Bitmap ImageAlbum = null;
    private int File;
    private String pageURL;
    private String thumbURL;
    private String rankNum;
    private String songKey;
    private String jsonData;
    private String sourceLink;
    private String filePath;
    private long id;


    Context context;


    public Song(String title, Bitmap imageAlbum, String artist, int file) {
        Artist = artist;
        Title = title;
        ImageAlbum = imageAlbum;
        File = file;
    }
    public Song(long songID, String title, String artist){
        this.id=songID;
        this.Title = title;
        this.Artist = artist;
    }

    public Song(String title, String artist) {
        Title = title;
        Artist = artist;
        //Mac dinh

    }

    public void setOnlineSongInfo(String pageURL, String thumbURL, String rankNum){
        this.thumbURL = thumbURL;
        this.pageURL = pageURL;
        this.rankNum = rankNum;
    }

    //Setter
    public void setTitle(String title) {
        Title = title;
    }
    public void setArtist(String artist) {
        Artist = artist;
    }
    public void setFile(int file) {
        File = file;
    }
    public void setImageAlbum(Bitmap imageAlbum) {
        ImageAlbum = imageAlbum;
    }

    public void setPageURL(String pageURL) {
        this.pageURL = pageURL;
    }

    public void setThumbURL(String thumbURL) {
        this.thumbURL = thumbURL;
    }

    public void setRankNum(String rankNum) {
        this.rankNum = rankNum;
    }

    public void setSongKey(String songKey) {
        this.songKey = songKey;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public void setSourceLink(String sourceLink) {
        this.sourceLink = sourceLink;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    // Getter
    public String getTitle() {
        return Title;
    }
    public String getArtist() {
        return Artist;
    }
    public int getFile() {
        return File;
    }
    public Bitmap getImageAlbum() {
        return ImageAlbum;
    }

    public String getPageURL() {
        return pageURL;
    }

    public String getThumbURL() {
        return thumbURL;
    }

    public String getRankNum() {
        return rankNum;
    }

    public String getSongKey() {
        return songKey;
    }

    public String getJsonData() {
        return jsonData;
    }

    public String getSourceLink() {
        return sourceLink;
    }

    public String getFilePath() {
        return filePath;
    }

    public Context getContext() {
        return context;
    }


}
