package com.cocodev.TDUCManager.Utility;

/**
 * Created by manav on 10/2/17.
 */

public class Fest {

    private String UID;
    private String venue;
    private Long time;
    private String description;
    private String url;
    private Long startDate;
    private Long endDate;
    private String title;

    public Fest() {
    }

    public Fest(String UID, String venue,String description, Long time, String url, Long startDate, Long endDate, String title) {
        this.UID = UID;
        this.venue = venue;
        this.description = description;
        this.time = time;
        this.url = url;
        this.startDate = startDate;
        this.endDate = endDate;
        this.title = title;
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
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
