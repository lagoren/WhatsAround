package com.example.galgoren.finalproject.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.galgoren.finalproject.R;
import com.example.galgoren.finalproject.model.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class FragmentMap extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {


    private static final int PENDING_ACTION_NONE = 0;
    private static final int PENDING_ACTION_USER_LOCATION = 1;
    private static final int PENDING_ACTION_PLACE_LOCATION = 2;

    private GoogleMap myMap;
    private int pendingAction = PENDING_ACTION_NONE;
    private SharedPreferences sp;
    private Circle bigCircle;
    private Circle smallCircle;
    private Marker placeMarker;
    private Place place;





    //----------------------------------------------------------------------------------------------


    // onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_maps, container, false);


        // connecting to SharedPreferences file of the application
        sp = PreferenceManager.getDefaultSharedPreferences(getContext());



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        return v;
    }


    //----------------------------------------------------------------------------------------------


    //
    @Override
    public void onResume() {
        super.onResume();

        // calling onUserLocationChange
        onUserLocationChange(false);
    }


    //----------------------------------------------------------------------------------------------


    // onMapReady method
    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        myMap.setOnMarkerClickListener(this);

        // when map ready, use methods
        int pendingAction = this.pendingAction;
        this.pendingAction = PENDING_ACTION_NONE;

        switch (pendingAction){

            case PENDING_ACTION_USER_LOCATION:
                userLocation();
                break;
            case PENDING_ACTION_PLACE_LOCATION:
                placeLocation(this.place);
                break;
        }

    }

    //----------------------------------------------------------------------------------------------


    // showing user location method
    public void userLocation() {


        // if map not ready, wait for onMapReady
        if (myMap == null){
            pendingAction = PENDING_ACTION_USER_LOCATION;
            return;
        }

        // not in place location mode
        place = null;

        pendingAction = PENDING_ACTION_NONE;

        // user location
        if (bigCircle != null) {
            bigCircle.remove();
            smallCircle.remove();
            bigCircle = null;
            smallCircle = null;
        }

        if (placeMarker != null) {
            placeMarker.remove();
            placeMarker = null;
        }

        int radius = sp.getInt("radius_key", 500);
        LatLng myLocation = new LatLng(sp.getFloat("lat", 0), sp.getFloat("lng", 0));

        bigCircle = myMap.addCircle(new CircleOptions().center(myLocation)
                .radius(radius).fillColor(Color.argb(100, 36, 103, 255)).strokeWidth(0));
        smallCircle = myMap.addCircle(new CircleOptions().center(myLocation)
                .radius(10).fillColor(Color.argb(200, 36, 103, 255)).strokeWidth(0));

        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 16));
    }


    //----------------------------------------------------------------------------------------------


    // updates the user location
    public void onUserLocationChange(boolean animateCamera) {


        // if map not ready, return
        if (myMap == null){

            return;
        }


        // calling user location
        if (bigCircle != null) {

            int radius = sp.getInt("radius_key", 500);
            LatLng myLocation = new LatLng(sp.getFloat("lat", 0), sp.getFloat("lng", 0));
            bigCircle.setCenter(myLocation);
            smallCircle.setCenter(myLocation);
            bigCircle.setRadius(radius);

            if (place == null && animateCamera){
                myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 16));
            }
        }
    }


    //----------------------------------------------------------------------------------------------


    // showing place and user on map method
    public void placeLocation(Place place) {

        this.place = place;

        // if map not ready, wait for onMapReady
        if (myMap == null){
            pendingAction = PENDING_ACTION_PLACE_LOCATION;
            return;
        }

        pendingAction = PENDING_ACTION_NONE;

        // user location
        if (bigCircle != null) {
            bigCircle.remove();
            smallCircle.remove();
            bigCircle = null;
            smallCircle = null;
        }

        int radius = sp.getInt("radius_key", 500);

        LatLng myLocation = new LatLng(sp.getFloat("lat", 0), sp.getFloat("lng", 0));

        bigCircle = myMap.addCircle(new CircleOptions().center(myLocation).radius(radius)
                .fillColor(Color.argb(100, 36, 103, 255)).strokeWidth(0));
        smallCircle = myMap.addCircle(new CircleOptions().center(myLocation).radius(10)
                .fillColor(Color.argb(200, 36, 103, 255)).strokeWidth(0));

        // place location
        if (placeMarker != null) {
            placeMarker.remove();
            placeMarker = null;
        }

        LatLng placeLocation = new LatLng(place.getPlaceLat(), place.getPlaceLng());
        placeMarker = myMap.addMarker(new MarkerOptions().position(placeLocation).title(place.getPlaceName()).alpha(0.7f));
        placeMarker.showInfoWindow();
        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(placeLocation, 16));


    }

    //----------------------------------------------------------------------------------------------


    // onMarker Click
    @Override
    public boolean onMarkerClick(Marker marker) {


        Intent moreInfoIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/search/?api=1&query=" + place.getPlaceLat() + "," +
        place.getPlaceLng() + "&query_place_id=" + place.getPlaceId()));
        startActivity(moreInfoIntent);

        return false;
    }


    //----------------------------------------------------------------------------------------------

}

