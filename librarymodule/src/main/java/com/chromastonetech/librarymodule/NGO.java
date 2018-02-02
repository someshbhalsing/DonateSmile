package com.chromastonetech.librarymodule;

/**
 * Created by somesh on 02/02/2018.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by somesh on 01/02/2018.
 */

public class NGO implements Parcelable{

    private String Name;
    private String PhoneNo;
    private String Email;
    private LatLng latLng;
    private String Description;
    private String ProfilePhotoURI;
    private String PhotoURI1;
    private String PhotoURI2;

    public NGO() {
    }

    public NGO(String name, String phoneNo, String email, LatLng latLng, String description, String profilePhotoURI, String photoURI1, String photoURI2) {
        Name = name;
        PhoneNo = phoneNo;
        Email = email;
        this.latLng = latLng;
        Description = description;
        ProfilePhotoURI = profilePhotoURI;
        PhotoURI1 = photoURI1;
        PhotoURI2 = photoURI2;
    }

    private NGO(Parcel dest){
        Name = dest.readString();
        PhoneNo = dest.readString();
        Email = dest.readString();
        Description = dest.readString();
        ProfilePhotoURI = dest.readString();
        PhotoURI1 = dest.readString();
        PhotoURI2 = dest.readString();
        latLng = (LatLng) dest.readValue(getClass().getClassLoader());
    }


    public static final Creator<NGO> CREATOR = new Creator<NGO>() {
        @Override
        public NGO createFromParcel(Parcel in) {
            return new NGO(in);
        }

        @Override
        public NGO[] newArray(int size) {
            return new NGO[size];
        }
    };

    public String getName() {
        return Name;
    }

    public String getPhoneNo() {
        return PhoneNo;
    }

    public String getEmail() {
        return Email;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public String getDescription() {
        return Description;
    }

    public String getProfilePhotoURI() {
        return ProfilePhotoURI;
    }

    public String getPhotoURI1() {
        return PhotoURI1;
    }

    public String getPhotoURI2() {
        return PhotoURI2;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Name);
        dest.writeString(PhoneNo);
        dest.writeString(Email);
        dest.writeString(Description);
        dest.writeString(ProfilePhotoURI);
        dest.writeString(PhotoURI1);
        dest.writeString(PhotoURI2);
        dest.writeValue(latLng);
    }

}
