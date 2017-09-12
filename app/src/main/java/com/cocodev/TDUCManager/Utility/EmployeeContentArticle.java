package com.cocodev.TDUCManager.Utility;

/**
 * Created by Sudarshan on 26-06-2017.
 */

public class EmployeeContentArticle {
    private String A_Uid;
    private int status;
    private String college;
    public EmployeeContentArticle() {
    }

    public EmployeeContentArticle(String a_Uid, int status) {
        A_Uid = a_Uid;
        this.status = status;
    }

    public String getA_Uid() {
        return A_Uid;
    }

    public void setA_Uid(String a_Uid) {
        A_Uid = a_Uid;
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
