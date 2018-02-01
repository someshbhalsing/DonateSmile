package com.chromastone.donatesmile;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by somesh on 01/02/2018.
 */

public class NGO {

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
}
