package com.example.galgoren.finalproject.fragments;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.galgoren.finalproject.R;
import com.example.galgoren.finalproject.adapters.MyListAdapter;
import com.example.galgoren.finalproject.db.PlacesDBHelper;
import com.example.galgoren.finalproject.db.PlacesProvider;
import com.example.galgoren.finalproject.model.Place;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMainSearch extends Fragment {


    //private SharedPreferences sp;
    private MyListAdapter adapter;


    //----------------------------------------------------------------------------------------------


    public FragmentMainSearch() {
        // Required empty public constructor
    }


    //----------------------------------------------------------------------------------------------


    // onCreate
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main_search, container, false);


        // setting up the search list and the adapter
        RecyclerView searchPlacesList = v.findViewById(R.id.recyclerSearchPlaces);
        searchPlacesList.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MyListAdapter(getContext(), false);
        searchPlacesList.setAdapter(adapter);


        return v;
    }


    //----------------------------------------------------------------------------------------------


    // filling the list onResume
    @Override
    public void onResume() {
        super.onResume();

        readFromSearchDB();
    }


    //----------------------------------------------------------------------------------------------


    // onGooglePlacesResult
    public void onGooglePlacesResult(int resultCode) {

        readFromSearchDB();

    }

    //----------------------------------------------------------------------------------------------


    // reading from searchDB
    public void readFromSearchDB() {

        if (adapter != null) {
            adapter.clear();

            Cursor placeCursor = getContext().getContentResolver().query(PlacesProvider.CONTENT_URI_SEARCH, null, null, null, null);

            if (placeCursor != null) {
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

    //----------------------------------------------------------------------------------------------
}
