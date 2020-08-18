package com.example.friendbook;

public class SearchModel {

    String FullName,ProfileStatus,profileImage;

    public SearchModel(){

    }

    public SearchModel(String fullName, String profileStatus, String profileImage) {
        FullName = fullName;
        ProfileStatus = profileStatus;
        this.profileImage = profileImage;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getProfileStatus() {
        return ProfileStatus;
    }

    public void setProfileStatus(String profileStatus) {
        ProfileStatus = profileStatus;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
