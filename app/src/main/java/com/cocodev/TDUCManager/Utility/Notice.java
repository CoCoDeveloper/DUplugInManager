package com.cocodev.TDUCManager.Utility;

import java.util.ArrayList;

/**
 * Created by Sudarshan on 16-06-2017.
 */

public class Notice {

    private String UID;
    private String department;
    private String title;
    private Long time;
    private Long deadline;
    private String description;
    private ArrayList<String> imageUrls = new ArrayList<>();

    public Notice(){
        //default constructor
    }

    public Notice(String UID,String title,String department, Long time, Long deadline, String description,ArrayList<String> imageUrls) {
        this.UID = UID;
        this.title = title;
        this.department = department;
        this.time = time;
        this.deadline = deadline;
        this.description = description;
        this.imageUrls = imageUrls;
    }

    public ArrayList<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(ArrayList<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getDeadline() {
        return deadline;
    }

    public void setDeadline(Long deadline) {
        this.deadline = deadline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }
}
