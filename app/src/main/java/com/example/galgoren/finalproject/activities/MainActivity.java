package com.example.galgoren.finalproject.activities;


import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.example.galgoren.finalproject.Service.IntentServiceGooglePlaces;
import com.example.galgoren.finalproject.MyBatteryReceiver;
import com.example.galgoren.finalproject.R;
import com.example.galgoren.finalproject.Util.ConnectionUtil;
import com.example.galgoren.finalproject.fragments.FragmentMap;

import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.galgoren.finalproject.adapters.MyFragmentAdapter;
import com.example.galgoren.finalproject.adapters.MyListAdapter;
import com.example.galgoren.finalproject.db.PlacesProvider;
import com.example.galgoren.finalproject.fragments.FragmentFavorite;
import com.example.galgoren.finalproject.fragments.FragmentMainSearch;
import com.example.galgoren.finalproject.model.Place;
import com.mancj.materialsearchbar.MaterialSearchBar;

public class MainActivity extends AppCompatActivity implements LocationListener,
        MyListAdapter.OnMyClickListener, MyBatteryReceiver.OnBatteryChangedListener {

    private SharedPreferences sp;
    private LocationManager locationManager;
    private MyFragmentAdapter myFragmentAdapter;
    private ViewPager pager;
    private RelativeLayout mapLayout;
    private MyBatteryReceiver receiver;
    private MaterialSearchBar searchBar;
    private GooplePlacesReceiver googlePlacesReceiver;
    private FragmentFavorite fragmentFavorite;
    private FragmentMap fragmentMap;
    private boolean isTabletLand;
    private MenuItem searchBtn;
    private ProgressDialog searchProgressDialog;


    //----------------------------------------------------------------------------------------------


    // onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // checking if tabletContainer is found
        if (findViewById(R.id.mapLayout) == null) {
            isTabletLand = true;
        }

        // finding mapLayout
        mapLayout = findViewById(R.id.mapLayout);
        sp = PreferenceManager.getDefaultSharedPreferences(this);


        // getting location service
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        // listening with the receiver to the googleSearchService
        googlePlacesReceiver = new GooplePlacesReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(googlePlacesReceiver, new IntentFilter
                (IntentServiceGooglePlaces.ACTION_GOOGLEPLACES));


        // registering and connecting to battery receiver
        receiver = new MyBatteryReceiver(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        registerReceiver(receiver, intentFilter);


        // setting the toolbar
        Toolbar myToolBar = (Toolbar) findViewById(R.id.myToolBar);
        setSupportActionBar(myToolBar);

        // finding the search view
        searchBar = findViewById(R.id.searchBar);


        // getting mapFragment
        fragmentMap = (FragmentMap) getSupportFragmentManager().findFragmentById(R.id.mapContainer);


        // connecting the fragments to the view pager and displaying search fragment
        myFragmentAdapter = new MyFragmentAdapter(getSupportFragmentManager());
        pager = findViewById(R.id.pager);
        pager.setAdapter(myFragmentAdapter);

        pager.setCurrentItem(1);

        // connecting the tabLayout to the pager
        TabLayout tabs = findViewById(R.id.tabLayout);
        tabs.setupWithViewPager(pager);


        // getting fragmentFavorite
        fragmentFavorite = (FragmentFavorite) myFragmentAdapter.getItem(0);


        getLocation();


        // progressDialog for search progress
        searchProgressDialog = new ProgressDialog(this);
        searchProgressDialog.setTitle("SEARCHING PLACES...");
        searchProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        searchProgressDialog.setCancelable(false);

    }


    //----------------------------------------------------------------------------------------------


    // onResume mapLayout down
    @Override
    protected void onResume() {
        super.onResume();

        if (!isTabletLand) {
            mapLayout.setY(1500);
        }
    }


    //----------------------------------------------------------------------------------------------


    // onDestroy unregistering the receiver, and removing updates
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // unregistering battery receiver
        unregisterReceiver(receiver);

        // unregistering places receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(googlePlacesReceiver);

        // removing location updates
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }


    //----------------------------------------------------------------------------------------------


    // onBackPressed closing map fragment or finish();
    @Override
    public void onBackPressed() {


        if (!isTabletLand) {
            if (mapLayout.getY() == 0) {
                animateMap(false);

            } else {
                finish();
            }
        } else {
            finish();
        }
    }


    //----------------------------------------------------------------------------------------------


    // on location changed showing user location
    @Override
    public void onLocationChanged(Location location) {


        // getting user location
        float lat = (float) location.getLatitude();
        float lng = (float) location.getLongitude();


        // saving user location in shared preferences
        sp.edit().putFloat("lat", lat)
                .putFloat("lng", lng)
                .apply();

        fragmentMap.onUserLocationChange(true);
    }


    //----------------------------------------------------------------------------------------------


    // implements the OnFindLocationMapListener to display the place on the map
    @Override
    public void findLocationOnMap(Place place) {


        if (ConnectionUtil.isOnline(this)) {

            if (!isTabletLand) {
                animateMap(true);
            }

            fragmentMap.placeLocation(place);

        } else {
            new AlertDialog.Builder(this)
                    .setTitle("NO DICE DUDE")
                    .setMessage("YOU NEED INTERNET \nTO FIND PLACES")
                    .create().show();
        }
    }


    //----------------------------------------------------------------------------------------------


    // LocationListener methods
    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}

    @Override
    public void onProviderEnabled(String s) {}

    @Override
    public void onProviderDisabled(String s) {}


    //----------------------------------------------------------------------------------------------


    // onBatteryChanged
    @Override
    public void batteryChanged(boolean isPlugged) {
        if (isPlugged == true) {
            Toast.makeText(this, "Phone is Plugged", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Phone Disconnected", Toast.LENGTH_SHORT).show();
        }
    }


    //----------------------------------------------------------------------------------------------


    // the result of user's permission choice
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 11) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                getLocation();

            } else {
                new AlertDialog.Builder(this)
                        .setTitle("DUDE")
                        .setMessage("YOU HAVE TO ALLOW \nUSING LOCATIONS \nIF YOU WANT THIS APP TO WORK")
                        .create().show();
            }

        }
    }


    //----------------------------------------------------------------------------------------------


    // when ServiceGooglePlaces is ready
    public class GooplePlacesReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            // enabling the searchBtn
            searchBtn.setEnabled(true);

            // dismissing progress
            searchProgressDialog.dismiss();


            // getting the ACTION id and checking if its ACTION_GOOGLEPLACES
            String action = intent.getAction();
            if (IntentServiceGooglePlaces.ACTION_GOOGLEPLACES.equals(action)) {

                // getting fragmentMainSearch
                FragmentMainSearch fragmentMainSearch = (FragmentMainSearch) myFragmentAdapter.getItem(1);
                fragmentMainSearch.readFromSearchDB();

                // getting the result code, RESULT_ERROR is the default result code
                int resultCode = intent.getIntExtra(IntentServiceGooglePlaces.GOOGLEPLACES_EXTRARESULT, -1);


                // calling the fragment to run onGooglePlacesResult method
                fragmentMainSearch.onGooglePlacesResult(resultCode);


                // checking result codes and alerting the user
                if (resultCode == IntentServiceGooglePlaces.RESULT_ERROR) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("NO DICE DUDE")
                            .setMessage("YOU GOT INTERNET \nOR LOCATION ISSUES")
                            .create().show();

                } else if (resultCode == IntentServiceGooglePlaces.RESULT_NO) {
                    if (sp.getFloat("lat", -1) == -1 && sp.getFloat("lng", -1) == -1) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("NO DICE DUDE")
                                .setMessage("YOU GOT LOCATION ISSUES")
                                .create().show();
                    } else {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("SORRY DUDE")
                                .setMessage("NO RESULTS")
                                .create().show();
                    }
                }
            }
        }
    }


    //----------------------------------------------------------------------------------------------


    // creating option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        searchBtn = menu.findItem(R.id.searchBtn);

        return super.onCreateOptionsMenu(menu);

    }


    // onItemSelected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            // shared preferences settings
            case R.id.mainMenuSettings:
                Intent intent = new Intent(this, PrefsActivity.class);
                startActivity(intent);
                break;


            // clear main search
            case R.id.mainMenuClearSearch:
                getContentResolver().delete(PlacesProvider.CONTENT_URI_SEARCH, null, null);
                FragmentMainSearch fragmentMainSearch = (FragmentMainSearch) myFragmentAdapter.getItem(1);
                fragmentMainSearch.readFromSearchDB();
                break;


            // clear favorites
            case R.id.mainMenuClearFavorite:
                getContentResolver().delete(PlacesProvider.CONTENT_URI_FAV, null, null);
                fragmentFavorite.readFromFavDB();
                break;


            // user location
            case R.id.myMapLocation:

                // if there's internet connection and user location ready, show userLocation
                if (ConnectionUtil.isOnline(this) && (sp.getFloat("lat", -1) != -1 || sp.getFloat("lng", -1) != -1))
                {
                    if (!isTabletLand) {
                        animateMap(true);
                    }
                    fragmentMap.userLocation();
                } else {

                    Toast.makeText(this, "YO CHECK CONNECTION / LOCATION", Toast.LENGTH_SHORT)
                            .show();
                }
                break;


            // searching places
            case R.id.searchBtn:

                searchPlaces();

                break;
        }


        // hiding the keyBoard
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager keyBoard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            keyBoard.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        return super.onOptionsItemSelected(item);
    }


    //----------------------------------------------------------------------------------------------


    // when added to favorite fragment
    @Override
    public void favoriteAdded(Place place) {

        fragmentFavorite.readFromFavDB();

    }


    //----------------------------------------------------------------------------------------------


    // getLocation method
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest
                    .permission.ACCESS_COARSE_LOCATION}, 11);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, this);
    }


    //----------------------------------------------------------------------------------------------


    // animateMap method
    public void animateMap(final boolean isAnimatedUp) {

        float finalY = 1500;
        float finalAlpha = 0;
        if (isAnimatedUp) {
            finalY = 0;
            finalAlpha = 1;
            mapLayout.setVisibility(View.VISIBLE);
        }

        ObjectAnimator animator1 = ObjectAnimator.ofFloat(mapLayout, "translationY", finalY);
        long duration = 1000;
        animator1.setDuration(duration);

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mapLayout, "alpha", finalAlpha);
        animator2.setDuration(500);

        AnimatorSet set = new AnimatorSet();
        set.play(animator1).with(animator2);


        // using CountDownTimer to make mapFragment Invisible at the end of the animation
        if (isAnimatedUp == false) {
            new CountDownTimer(duration, 1000) {

                @Override
                public void onTick(long l) {
                }

                @Override
                public void onFinish() {
                    mapLayout.setVisibility(View.INVISIBLE);
                }
            }
                    .start();
        }
        set.start();
    }


    //----------------------------------------------------------------------------------------------


    // searchPlaces method
    private void searchPlaces() {
        String searchWord = searchBar.getText().toString();
        Intent searchIntent = new Intent(this, IntentServiceGooglePlaces.class);
        searchIntent.putExtra("searchWord", searchWord);

        startService(searchIntent);

        // showing searchFragment
        pager.setCurrentItem(1);

        // disabling the searchBtn
        searchBtn.setEnabled(false);

        // showing progress
        searchProgressDialog.show();

    }

    //----------------------------------------------------------------------------------------------


}
