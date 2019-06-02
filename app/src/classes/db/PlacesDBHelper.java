package com.example.galgoren.finalproject.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by galgoren on 9/19/17.
 */

public class PlacesDBHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME_SEARCH = "searchPlaces";
    public static final String TABLE_NAME_FAV = "favPlaces";
    public static final String COL_ID = "id";
    public static final String COL_ICON = "icon";
    public static final String COL_NAME = "name";
    public static final String COL_ADDRESS = "address";
    public static final String COL_LAT = "lat";
    public static final String COL_LNG = "lng";


    // constructor
    public PlacesDBHelper(Context context) {
        super(context, "places.db", null, 1);
    }



    //----------------------------------------------------------------------------------------------



    // creating the table
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // creating search db
        sqLiteDatabase.execSQL(String.format("CREATE TABLE %s ( %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s REAL, %s REAL )",
                TABLE_NAME_SEARCH, COL_ID, COL_ICON, COL_NAME, COL_ADDRESS, COL_LAT, COL_LNG));

        // creating favorites db
        sqLiteDatabase.execSQL(String.format("CREATE TABLE %s ( %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s REAL, %s REAL )",
                TABLE_NAME_FAV, COL_ID, COL_ICON, COL_NAME, COL_ADDRESS, COL_LAT, COL_LNG));
    }

    // upgrade method
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
