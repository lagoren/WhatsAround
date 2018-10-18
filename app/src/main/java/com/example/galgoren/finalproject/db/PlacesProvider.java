package com.example.galgoren.finalproject.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by galgoren on 9/19/17.
 */

public class PlacesProvider extends ContentProvider {


    private PlacesDBHelper helper;
    private static final String AUTHORIZATION = "com.example.galgoren.finalproject.db";
    public static final Uri CONTENT_URI_SEARCH = Uri.parse("content://" + AUTHORIZATION + "/" + PlacesDBHelper.TABLE_NAME_SEARCH);
    public static final Uri CONTENT_URI_FAV = Uri.parse("content://" + AUTHORIZATION + "/" + PlacesDBHelper.TABLE_NAME_FAV);


    //----------------------------------------------------------------------------------------------


    // empty constructor
    public PlacesProvider(){}


    //----------------------------------------------------------------------------------------------


    // onCreate
    @Override
    public boolean onCreate() {
        helper = new PlacesDBHelper(getContext());
        return true;
    }


    //----------------------------------------------------------------------------------------------


    // read method
    @Override
    public Cursor query(Uri uri, String[] columns, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = helper.getReadableDatabase();
        // searching with getLastPathSegment that returns the end of the Uri and getting one of our TABLE_NAME
        Cursor c = db.query(uri.getLastPathSegment(), columns, selection, selectionArgs, null, null, sortOrder);
        return c;
    }


    // insert method
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = helper.getWritableDatabase();
        long rowNum = db.insert(uri.getLastPathSegment(), null, values);
        db.close();
        return Uri.withAppendedPath(uri, rowNum + "");
    }


    // delete method
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int count = db.delete(uri.getLastPathSegment(), selection, selectionArgs);
        db.close();
        return count;
    }


    // update method
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int count = db.update(uri.getLastPathSegment(), values, selection, selectionArgs);
        db.close();
        return count;
    }


    //----------------------------------------------------------------------------------------------

    // return the type of the uri table or row
    @Override
    public String getType(Uri uri) {

        throw new UnsupportedOperationException("");
    }
}
