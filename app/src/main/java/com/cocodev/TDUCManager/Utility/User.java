package com.cocodev.TDUCManager.Utility;

/**
 * Created by Sudarshan on 26-06-2017.
 */

public class User {
    private String Uid;
    private int clearenceLevel;
    private String fullName;


    public User() {
    }

    public User(String uid, int clearenceLevel, String fullName) {
        Uid = uid;
        this.clearenceLevel = clearenceLevel;
        this.fullName = fullName;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public int getClearenceLevel() {
        return clearenceLevel;
    }

    public void setClearenceLevel(int clearenceLevel) {
        this.clearenceLevel = clearenceLevel;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
