package com.example.galgoren.finalproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by galgoren on 9/26/17.
 */

public class SeekBarPreference extends Preference implements SeekBar.OnSeekBarChangeListener {


    // final vars
    private static final int DEFAULT_VALUE = 500;
    private int maxValue = 5000;
    private int minValue = 0;
    private int interval = 1;
    private int currentValue;
    private SeekBar seekBar;
    private TextView textRadiusSummery;
    private SharedPreferences sharedPreferences;


    // constructor (alt + enter)
    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPreference(context, attrs);
    }


    public SeekBarPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPreference(context, attrs);
    }


    //----------------------------------------------------------------------------------------------


    // initialize the seek bar
    private void initPreference(Context context, AttributeSet attrs) {
        setValuesFromXml(attrs);
        seekBar = new SeekBar(context, attrs);
        seekBar.setMax(maxValue);
        seekBar.setOnSeekBarChangeListener(this);

        setWidgetLayoutResource(R.layout.seekbar_preference);
    }


    //----------------------------------------------------------------------------------------------


    // read the custom attributes from the xml
    private void setValuesFromXml(AttributeSet attrs) {
        maxValue = attrs.getAttributeIntValue("http://schemas.android.com/apk/res/android", "max", DEFAULT_VALUE);
        minValue = attrs.getAttributeIntValue("http://schemas.android.com/apk/res/android", "min", 0);
        interval = attrs.getAttributeIntValue("http://schemas.android.com/apk/res/android", "interval", 1);
    }


    //----------------------------------------------------------------------------------------------


    // loading the title and the summery
    @Override
    protected View onCreateView(ViewGroup parent) {
        View view = super.onCreateView(parent);

        // arranging the Title and summery above the seek bar
        LinearLayout layout = (LinearLayout) view;
        layout.setOrientation(LinearLayout.VERTICAL);

        textRadiusSummery = view.findViewById(R.id.textRadiusSummery);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        return view;
    }


    //----------------------------------------------------------------------------------------------


    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        ViewGroup newContainer = view.findViewById(R.id.seekBarPrefBarContainer);
        ViewParent oldContainer = seekBar.getParent();

        try {
            if (oldContainer != newContainer) {
                if (oldContainer != null) {
                    ((ViewGroup) oldContainer).removeView(seekBar);
                }

                newContainer.removeAllViews();
                newContainer.addView(seekBar, ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        } catch (Exception e) {
            Log.e("SeekBar Preference", "Error binding view: " + e.toString());
        }

        // if dependency is false at the beginning, disable the seek bar
        if (view != null && !view.isEnabled()) {
            seekBar.setEnabled(false);
        }

        updateView(view);
    }


    //----------------------------------------------------------------------------------------------


    // Update a SeekBarPreference view with our current state
    private void updateView(View view) {

        try {
            seekBar.setProgress(currentValue);

        } catch (Exception e) {
            Log.e("SeekBar Preference", "Error updating seek bar preference", e);
        }
    }


    //----------------------------------------------------------------------------------------------


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        int newValue = progress + minValue;

        if (newValue > maxValue) newValue = maxValue;

        else if (newValue < minValue) newValue = minValue;

        else if (interval != 1 && newValue % interval != 0)
            newValue = Math.round(((float) newValue) / interval) * interval;

        if (!callChangeListener(newValue)) {
            seekBar.setProgress(currentValue - minValue);
            return;
        }

        // change accepted, storing it in shared preferences
        currentValue = newValue;
        String spDistanceUnit = sharedPreferences.getString("search_unites_key", "Km");

        // converting to miles if necessary
        if (spDistanceUnit.equalsIgnoreCase("miles")) {
            float newerValue = newValue * 1.609347f;
            newValue = (int) newerValue;
        }

        persistInt(newValue);

        // show the current value as the summary
        float currentValueForUser = currentValue / 1000f;
        textRadiusSummery.setText(currentValueForUser + "");

    }


    //----------------------------------------------------------------------------------------------


    // override of seekBar methods
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    protected Object onGetDefaultValue(TypedArray ta, int index) {

        int defaultValue = ta.getInt(index, DEFAULT_VALUE);

        return defaultValue;
    }


    //----------------------------------------------------------------------------------------------


    // what to do when initializing the value of the preference
    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {

        if (restoreValue) {
            currentValue = getPersistedInt(currentValue);
        } else {
            int temp = 0;
            try {
                temp = (Integer) defaultValue;
            } catch (Exception e) {
                Log.e("SeekBar Preference", " Invalid default value: " + defaultValue.toString());
            }

            persistInt(temp);
            currentValue = temp;
        }
    }


    //----------------------------------------------------------------------------------------------


    // making sure that the seekBar is disabled if the preference is disabled
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        seekBar.setEnabled(enabled);
    }

    // changing the enabled state according to the view that it depends on
    @Override
    public void onDependencyChanged(Preference dependency, boolean disableDependent) {
        super.onDependencyChanged(dependency, disableDependent);

        //disabling movement of seek bar when dependency is false
        if (seekBar != null) {
            seekBar.setEnabled(!disableDependent);
        }
    }
}
