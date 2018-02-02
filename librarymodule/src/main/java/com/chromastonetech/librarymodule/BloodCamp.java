package com.chromastonetech.librarymodule;

/**
 * Created by somesh on 02/02/2018.
 */

import com.google.android.gms.maps.model.LatLng;


public class BloodCamp{

    private NGO ngo;
    private String PosterUri;
    private LatLng latLng;
    private String Description;

    public BloodCamp(NGO ngo, String posterUri, LatLng latLng, String description) {
        this.ngo = ngo;
        PosterUri = posterUri;
        this.latLng = latLng;
        Description = description;
    }

    public BloodCamp() {
    }

    public NGO getNgo() {
        return ngo;
    }

    public String getPosterUri() {
        return PosterUri;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public String getDescription() {
        return Description;
    }
}