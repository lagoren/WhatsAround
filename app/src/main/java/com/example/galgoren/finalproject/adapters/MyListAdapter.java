package com.example.galgoren.finalproject.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.galgoren.finalproject.R;
import com.example.galgoren.finalproject.db.PlacesDBHelper;
import com.example.galgoren.finalproject.db.PlacesProvider;
import com.example.galgoren.finalproject.model.Place;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by galgoren on 9/17/17.
 */


// creating the Adapter class, extends RecyclerView.Adapter<>
public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.MyHolder> {


    // declaring vars
    private Context context;
    private ArrayList<Place> places = new ArrayList<>();
    private OnMyClickListener onMyClicklistener;
    private SharedPreferences sp;
    private boolean isFragmentFavorite;
    private float myLat;
    private float myLng;


    // constructors
    public MyListAdapter(Context context, boolean isFragmentFavorite) {
        this.context = context;
        onMyClicklistener = (OnMyClickListener) context;
        this.isFragmentFavorite = isFragmentFavorite;
    }


    //----------------------------------------------------------------------------------------------


    // adapter methods:

    // add
    public void add(Place place) {
        places.add(place);
        notifyDataSetChanged();
    }

    // addAll
    public void addAll(ArrayList<Place> places) {
        this.places.addAll(places);
        notifyDataSetChanged();
    }

    // remove
    public void remove(Place place) {
        places.remove(place);
        notifyDataSetChanged();
    }

    // clear
    public void clear() {
        places.clear();
        notifyDataSetChanged();
    }


    //----------------------------------------------------------------------------------------------


    // 3 methods: onCreateViewHolder, onBindViewHolder, getItemCount
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_layout, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        holder.bind(places.get(position));
    }

    @Override
    public int getItemCount() {
        return places.size();
    }


    //----------------------------------------------------------------------------------------------


    // creating ViewHolder class
    public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        // declaring the views
        private ImageView placeIcon, imageListItemMenu;
        private TextView placeName, placeAddress, placeDistance;
        private Place place;


        // constructor that get the View (item layout)
        public MyHolder(View itemView) {
            super(itemView);


            // finding the views
            placeIcon = itemView.findViewById(R.id.placeIcon);
            placeName = itemView.findViewById(R.id.placeName);
            placeAddress = itemView.findViewById(R.id.placeAddress);
            imageListItemMenu = itemView.findViewById(R.id.imageListItemMenu);
            placeDistance = itemView.findViewById(R.id.placeDistance);


            // connecting to the default SharedPreferences file of the application
            sp = PreferenceManager.getDefaultSharedPreferences(context);
            myLat = sp.getFloat("lat", (float) -1);
            myLng = sp.getFloat("lng", (float) -1);


            // connecting to click event
            itemView.setOnClickListener(this);

            imageListItemMenu.setOnCreateContextMenuListener(this);
        }


        //----------------------------------------------------------------------------------------------


        // bind method that gets an item
        public void bind(Place place) {

            this.place = place;
            Picasso
                    .with(context)
                    .load(place.getPlaceIcon())
                    .into(placeIcon);
            placeName.setText(place.getPlaceName());
            placeAddress.setText(place.getPlaceAddress());


            // calculating and inserting the distance to the view
            float pLat = (float) place.getPlaceLat();
            float pLng = (float) place.getPlaceLng();
            String unit = sp.getString("search_unites_key", "Km");

            Locale locale = new Locale("il", "IL");
            NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
            numberFormat.setMaximumFractionDigits(1);

            double theta = myLng - pLng;
            double dist = Math.sin(deg2rad(myLat)) * Math.sin(deg2rad(pLat)) + Math.cos(deg2rad(myLat)) * Math.cos(deg2rad(pLat)) * Math.cos(deg2rad(theta));
            dist = Math.acos(dist);
            dist = rad2deg(dist);
            dist = dist * 60 * 1.1515;

            if (unit.equals("Km")) {
                dist = dist * 1.609344;

                if (dist < 1) {
                    dist = dist * 1000;
                    unit = "Meters";
                    numberFormat.setMaximumFractionDigits(0);
                }
            }

            if (dist < 0.3) {
                dist = dist * 1760;
                unit = "Yards";
                numberFormat.setMaximumFractionDigits(0);
            }

            placeDistance.setText(numberFormat.format(dist) + " " + unit);

        }


        //------------------------------------------------------------------------------------------


        // onClick
        @Override
        public void onClick(View view) {

            onMyClicklistener.findLocationOnMap(place);

        }


        //------------------------------------------------------------------------------------------


        // methods for calculating the distance

        // converting decimal degrees to radians
        private double deg2rad(double deg) {
            return (deg * Math.PI / 180.0);
        }

        // converting radians to decimal degrees
        private double rad2deg(double rad) {
            return (rad * 180 / Math.PI);
        }


        //------------------------------------------------------------------------------------------


        // onCreate context menu
        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

            contextMenu.setHeaderTitle("Select The Action");

            String itemTitle = "Add to Favorite";

            if (isFragmentFavorite) {

                itemTitle = "Remove";
            }

            // menu on search or favorite fragment
            contextMenu.add(0, 1, 0, itemTitle).setOnMenuItemClickListener(this);
            contextMenu.add(0, 2, 0, "Share").setOnMenuItemClickListener(this);

        }


        //------------------------------------------------------------------------------------------


        // onMenuItem Click
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {

            context.getContentResolver().delete(PlacesProvider.CONTENT_URI_FAV, "id = '" + place.getPlaceId() + "'", null);

            switch (menuItem.getItemId()) {

                case 1:
                    // delete from favoriteDB
                    if (isFragmentFavorite) {
                        context.getContentResolver().delete(PlacesProvider.CONTENT_URI_FAV, "id = '" + place.getPlaceId() + "'", null);
                        remove(place);

                    // insert to favoriteDB
                    } else {
                        ContentValues values = new ContentValues();
                        values.put(PlacesDBHelper.COL_ID, place.getPlaceId());
                        values.put(PlacesDBHelper.COL_ICON, place.getPlaceIcon());
                        values.put(PlacesDBHelper.COL_NAME, place.getPlaceName());
                        values.put(PlacesDBHelper.COL_ADDRESS, place.getPlaceDistance());
                        values.put(PlacesDBHelper.COL_LAT, place.getPlaceLat());
                        values.put(PlacesDBHelper.COL_LNG, place.getPlaceLng());

                        context.getContentResolver().insert(PlacesProvider.CONTENT_URI_FAV, values);
                        onMyClicklistener.favoriteAdded(place);
                    }

                    break;

                case 2:
                    // share place
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, place.getPlaceName() +
                            "\n\nhttps://www.google.com/maps/search/?api=1&query=" + place.getPlaceLat() + "," +
                            place.getPlaceLng() + "&query_place_id=" + place.getPlaceId());
                    context.startActivity(Intent.createChooser(shareIntent, "SHARE:"));

                    break;
            }

            return true;
        }


        //------------------------------------------------------------------------------------------

    }

    // interface for onClick
    public interface OnMyClickListener {

        void findLocationOnMap(Place place);

        void favoriteAdded(Place place);
    }


    //----------------------------------------------------------------------------------------------

}
