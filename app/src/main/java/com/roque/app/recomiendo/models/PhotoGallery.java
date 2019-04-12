package com.roque.app.recomiendo.models;

import android.os.Parcel;
import android.os.Parcelable;

public class PhotoGallery implements Parcelable {

    private String photoId;
    private String photoUrl;

    public PhotoGallery() {

    }

    public PhotoGallery(Parcel source) {
        photoId = source.readString();
        photoUrl = source.readString();
    }

    public static final Creator<PhotoGallery> CREATOR = new Creator<PhotoGallery>() {
        @Override
        public PhotoGallery createFromParcel(Parcel in) {
            return new PhotoGallery(in);
        }

        @Override
        public PhotoGallery[] newArray(int size) {
            return new PhotoGallery[size];
        }
    };

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(photoId);
        dest.writeString(photoUrl);
    }
}
