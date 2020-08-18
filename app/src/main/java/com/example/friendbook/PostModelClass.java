package com.example.friendbook;

public class PostModelClass {

     String UserId ,Date ,Time,FullName,ProfileImg,Description,PostImg;

     public PostModelClass(){

     }
    public PostModelClass(String userId, String date, String time, String fullName, String profileImg, String description, String postImg) {
        UserId = userId;
        Date = date;
        Time = time;
        FullName = fullName;
        ProfileImg = profileImg;
        Description = description;
        PostImg = postImg;
    }


    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getProfileImg() {
        return ProfileImg;
    }

    public void setProfileImg(String profileImg) {
        ProfileImg = profileImg;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPostImg() {
        return PostImg;
    }

    public void setPostImg(String postImg) {
        PostImg = postImg;
    }
}
