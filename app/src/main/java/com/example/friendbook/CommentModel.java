package com.example.friendbook;

public class CommentModel {

    String UserId,Date,Time,FullName,ProfileImg,CommentText;

    public CommentModel(){

    }

    public CommentModel(String userId, String date, String time, String fullName, String profileImg, String commentText) {
        UserId = userId;
        Date = date;
        Time = time;
        FullName = fullName;
        ProfileImg = profileImg;
        CommentText = commentText;
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

    public String getCommentText() {
        return CommentText;
    }

    public void setCommentText(String commentText) {
        CommentText = commentText;
    }
}
