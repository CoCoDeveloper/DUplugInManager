package com.cocodev.TDUCManager.Utility;

/**
 * Created by Sudarshan on 18-06-2017.
 */

public class Event {
    private String UID;
    private String venue;
    private Long time;
    private String Description;
    private String department;
    private String url;
    private Long date;
    private String title;

    public Event(){}

    public Event(String UID,String venue, Long time, String description, String url,String title,String department,Long date) {

        this.UID = UID;
        this.venue = venue;
        this.time = time;
        this.Description = description;
        this.url = url;
        this.title = title;
        this.date = date;
        this.department = department;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
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
}
