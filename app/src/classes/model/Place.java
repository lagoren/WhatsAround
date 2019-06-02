package com.example.galgoren.finalproject.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by galgoren on 9/17/17.
 */

public class Place implements Parcelable{



    private String placeName, placeAddress, placeIcon, placeId;
    private double placeLat, placeLng, placeDistance;


    // constructor
    public Place(String placeName, String placeAddress, String placeIcon, double placeLat, double placeLng, double placeDistance) {
        this.placeName = placeName;
        this.placeAddress = placeAddress;
        this.placeIcon = placeIcon;
        this.placeLat = placeLat;
        this.placeLng = placeLng;
        this.placeDistance = placeDistance;
    }

    public Place(String placeName, String placeAddress, String placeIcon, double placeLat, double placeLng) {
        this.placeName = placeName;
        this.placeAddress = placeAddress;
        this.placeIcon = placeIcon;
        this.placeLat = placeLat;
        this.placeLng = placeLng;
    }

    public Place(String placeId, String placeName, String placeAddress, String placeIcon, double placeLat, double placeLng) {
        this.placeId = placeId;
        this.placeName = placeName;
        this.placeAddress = placeAddress;
        this.placeIcon = placeIcon;
        this.placeLat = placeLat;
        this.placeLng = placeLng;
    }

    //----------------------------------------------------------------------------------------------


    protected Place(Parcel in) {
        placeName = in.readString();
        placeAddress = in.readString();
        placeIcon = in.readString();
        placeId = in.readString();
        placeLat = in.readDouble();
        placeLng = in.readDouble();
        placeDistance = in.readDouble();
    }

    //----------------------------------------------------------------------------------------------


    // parcelable methods
    public static final Creator<Place> CREATOR = new Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel in) {return new Place(in);}

        @Override
        public Place[] newArray(int size) {return new Place[size];}
    };


    //----------------------------------------------------------------------------------------------



    // getters and setters
    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    public String getPlaceIcon() {
        return placeIcon;
    }

    public void setPlaceIcon(String placeImage) {
        this.placeIcon = placeImage;
    }

    public double getPlaceLat() { return placeLat; }

    public void setPlaceLat(double placeLat) { this.placeLat = placeLat; }

    public double getPlaceLng() {
        return placeLng;
    }

    public void setPlaceLng(double placeLng) { this.placeLng = placeLng; }

    public double getPlaceDistance() {
        return placeDistance;
    }

    public void setPlaceDistance(double placeDistance) {
        this.placeDistance = placeDistance;
    }


    //----------------------------------------------------------------------------------------------


    // parcelable methods
    @Override
    public int describeContents() {return 0;}

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(placeName);
        parcel.writeString(placeAddress);
        parcel.writeString(placeIcon);
        parcel.writeString(placeId);
        parcel.writeDouble(placeLat);
        parcel.writeDouble(placeLng);
        parcel.writeDouble(placeDistance);
    }


    //----------------------------------------------------------------------------------------------
}
