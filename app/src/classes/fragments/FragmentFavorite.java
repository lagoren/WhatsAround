package com.example.galgoren.finalproject.fragments;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.galgoren.finalproject.R;
import com.example.galgoren.finalproject.adapters.MyListAdapter;
import com.example.galgoren.finalproject.db.PlacesDBHelper;
import com.example.galgoren.finalproject.db.PlacesProvider;
import com.example.galgoren.finalproject.model.Place;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentFavorite extends Fragment {



    private MyListAdapter adapter;


    public FragmentFavorite() {
        // Required empty public constructor
    }


    //----------------------------------------------------------------------------------------------


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_favorite, container, false);

        // setting up the favorite list
        RecyclerView favPlaceList = v.findViewById(R.id.recyclerFavoritehPlaces);
        favPlaceList.setLayoutManager(new LinearLayoutManager(getContext()));


        adapter = new MyListAdapter(getContext(), true);
        favPlaceList.setAdapter(adapter);


        return v;
    }


    //----------------------------------------------------------------------------------------------


    @Override
    public void onResume() {
        super.onResume();

        readFromFavDB();

    }


    //----------------------------------------------------------------------------------------------


    // reading from favoriteDB
    public void readFromFavDB(){

        if (adapter != null) {
            adapter.clear();

            Cursor placeCursor = getContext().getContentResolver().query(PlacesProvider.CONTENT_URI_FAV, null, null, null, null);

            if (placeCursor != null){
                while (placeCursor.moveToNext()) {
                    String placeId = placeCursor.getString(placeCursor.getColumnIndex(PlacesDBHelper.COL_ID));
                    String placeName = placeCursor.getString(placeCursor.getColumnIndex(PlacesDBHelper.COL_NAME));
                    String placeAddress = placeCursor.getString(placeCursor.getColumnIndex(PlacesDBHelper.COL_ADDRESS));
                    String placeIcon = placeCursor.getString(placeCursor.getColumnIndex(PlacesDBHelper.COL_ICON));
                    double placeLat = placeCursor.getDouble(placeCursor.getColumnIndex(PlacesDBHelper.COL_LAT));
                    double placeLng = placeCursor.getDouble(placeCursor.getColumnIndex(PlacesDBHelper.COL_LNG));

                    adapter.add(new Place(placeId, placeName, placeAddress, placeIcon, placeLat, placeLng));
                }
                placeCursor.close();
            }
        }
    }
}
