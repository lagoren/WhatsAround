package com.example.galgoren.finalproject.Service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.galgoren.finalproject.db.PlacesDBHelper;
import com.example.galgoren.finalproject.db.PlacesProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class IntentServiceGooglePlaces extends IntentService {


    public static final String ACTION_GOOGLEPLACES = "com.example.galgoren.finalproject.ACTION_GOOGLEPLACES";
    public static final String GOOGLEPLACES_EXTRARESULT = "result";
    public static final int RESULT_SUCSSES = 0;
    public static final int RESULT_NO = 1;
    public static final int RESULT_ERROR = 2;


    // constructor
    public IntentServiceGooglePlaces() {
        super("IntentServiceGooglePlaces");
    }


    //----------------------------------------------------------------------------------------------


    @Override
    protected void onHandleIntent(Intent intent) {


        String searchWord;
        SharedPreferences sp;
        float lat;
        float lng;
        int radius;
        int resultExtra;


        Intent searchPlacesIntent = new Intent(ACTION_GOOGLEPLACES);


        // connecting to SharedPreferences file of the application
        sp = PreferenceManager.getDefaultSharedPreferences(this);


        // getting the info for googlePlaces search
        lat = sp.getFloat("lat", (float) -1);
        lng = sp.getFloat("lng", (float) -1);
        radius = sp.getInt("radius_key", 500);
        searchWord = intent.getStringExtra("searchWord");


        HttpsURLConnection connection = null;
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();


        try {
            String keyWordSection = (searchWord == null) ? "" : "&keyword=" + URLEncoder.encode(searchWord, "UTF8");
            URL url = new URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + lat + ","
                    + lng + "&radius=" + radius + keyWordSection +
                    "&key=AIzaSyBwtTV0saETHLaymmYlifY2uqxppdwSMcg");

            // see the url at log
            Log.e("url for jason: ", url + "");

            connection = (HttpsURLConnection) url.openConnection();
            switch (connection.getResponseCode()) {
                case HttpURLConnection.HTTP_OK:
//				if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line = reader.readLine();
                    while (line != null) {
                        builder.append(line);
                        line = reader.readLine();
                    }

                    
//--------------------------------------------------------------------------------------------------


                    // getting the info from the JSON to the DB
                    JSONObject rootObject = new JSONObject(builder.toString());
                    JSONArray resultsArray = rootObject.getJSONArray("results");
                    ContentValues values = new ContentValues();


                    // checking if there are results
                    if (resultsArray.length() == 0) {

                        resultExtra = RESULT_NO;

                    } else {


                        //
                        for (int i = 0; i < resultsArray.length(); i++) {

                            String placeIcon = resultsArray.getJSONObject(i).getString("icon");
                            String placeId = resultsArray.getJSONObject(i).getString("place_id");
                            String placeName = resultsArray.getJSONObject(i).getString("name");
                            String placeAddress = resultsArray.getJSONObject(i).getString("vicinity");
                            Double placeLat = resultsArray.getJSONObject(i).getJSONObject("geometry").getJSONObject
                                    ("location").getDouble("lat");
                            Double placeLng = resultsArray.getJSONObject(i).getJSONObject("geometry").getJSONObject
                                    ("location").getDouble("lng");
                            values.put(PlacesDBHelper.COL_ID, placeId);
                            values.put(PlacesDBHelper.COL_ICON, placeIcon);
                            values.put(PlacesDBHelper.COL_NAME, placeName);
                            values.put(PlacesDBHelper.COL_ADDRESS, placeAddress);
                            values.put(PlacesDBHelper.COL_LAT, placeLat);
                            values.put(PlacesDBHelper.COL_LNG, placeLng);


                            // clearing the DB if there are no results
                            if (i == 0) {
                                getContentResolver().delete(PlacesProvider.CONTENT_URI_SEARCH, null, null);
                            }

                            // inserting places to searchDB
                            getContentResolver().insert(PlacesProvider.CONTENT_URI_SEARCH, values);
                        }

                        resultExtra = RESULT_SUCSSES;
                    }
                    break;


                // if
                default:
                    resultExtra = RESULT_ERROR;
            }


        } catch (java.io.IOException e) {
            resultExtra = RESULT_ERROR;
            e.printStackTrace();
        } catch (JSONException e) {
            resultExtra = RESULT_ERROR;
            e.printStackTrace();
        }

        // closing internet connection
        finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        // job done, transmitting the broadcast to main activity
        searchPlacesIntent.putExtra(GOOGLEPLACES_EXTRARESULT, resultExtra);
        LocalBroadcastManager.getInstance(IntentServiceGooglePlaces.this).sendBroadcast(searchPlacesIntent);
    }

}