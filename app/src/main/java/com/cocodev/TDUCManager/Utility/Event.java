package com.cocodev.TDUCManager.Utility;

import java.util.ArrayList;

/**
 * Created by Sudarshan on 18-06-2017.
 */

public class Event {

    private ArrayList<String>  categoryList;
    private String UID;
    private String venue;
    private Long time;
    private String Description;

    private String url;
    private Long date;
    private String title;
    private String college;
    private String organiser_uid;

    public Event(){}

    public Event(ArrayList<String> categoryList, String UID, String venue, Long time, String description, String url, Long date, String title, String college, String organiser_uid) {
        this.categoryList = categoryList;
        this.UID = UID;
        this.venue = venue;
        this.time = time;
        Description = description;

        this.url = url;
        this.date = date;
        this.title = title;
        this.college = college;
        this.organiser_uid = organiser_uid;
    }

    public ArrayList<String> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(ArrayList<String> categoryList) {
        this.categoryList = categoryList;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOrganiser_uid() {
        return organiser_uid;
    }

    public void setOrganiser_uid(String organiser_uid) {
        this.organiser_uid = organiser_uid;
    }
}

