package com.example.friendbook;

public class FriendListModel {
    public FriendListModel( ) {

    }
    String date;

    public FriendListModel(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
