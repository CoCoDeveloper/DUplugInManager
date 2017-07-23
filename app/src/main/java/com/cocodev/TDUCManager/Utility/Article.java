package com.cocodev.TDUCManager.Utility;

/**
 * Created by Sudarshan on 16-06-2017.
 */

public class Article  {
    private String uid;
    private String author;
    private String description;
    private long time;
    private String tagLine;
    private String imageUrl;
    private String title;
    private String writerUid;
    private String department;

    public Article(){
        //default Constructor
    }

    public Article(String author, String description, long time, String tagLine, String imageUrl,String title,String writerUid) {
        this.author = author;
        this.description = description;
        this.time = time;
        this.tagLine = tagLine;
        this.imageUrl = imageUrl;
        this.title = title;
        this.writerUid = writerUid;
    }

    public Article(String UID,String author, String description, long time, String tagLine, String imageUrl, String title, String writerUid, String department) {
        this.uid = UID;
        this.author = author;
        this.description = description;
        this.time = time;
        this.tagLine = tagLine;
        this.imageUrl = imageUrl;
        this.title = title;
        this.writerUid = writerUid;
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getWriterUid() {
        return writerUid;
    }

    public void setWriterUid(String writerUid) {
        this.writerUid = writerUid;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUID() {
        return uid;
    }

    public void setUID(String UID) {
        this.uid = UID;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long date) {
        this.time = date;
    }

    public String getTagLine() {
        return tagLine;
    }

    public void setTagLine(String tagLine) {
        this.tagLine = tagLine;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object obj) {
        return getUID()==((Article) obj).getUID();
    }
}
