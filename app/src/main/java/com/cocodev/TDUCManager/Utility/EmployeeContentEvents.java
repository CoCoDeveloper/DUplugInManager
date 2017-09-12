package com.cocodev.TDUCManager.Utility;

/**
 * Created by Sudarshan on 11-09-2017.
 */

public class EmployeeContentEvents {
    String e_uid;
    int status;
    String college;
    public EmployeeContentEvents(){}

    public EmployeeContentEvents(String e_uid, int status, String college) {
        this.e_uid = e_uid;
        this.status = status;
        this.college = college;
    }

    public String getE_uid() {
        return e_uid;
    }

    public void setE_uid(String e_uid) {
        this.e_uid = e_uid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }
}
