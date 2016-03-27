package ui.uottawa.com.compassapp;

/**
 * Created by joesi on 2016-03-20.
 */

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String PREFS_NAME = "coffeeBean_prefs";
    helperPreferences shPrefs;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        this.getSharedPreferences(PREFS_NAME, 0).registerOnSharedPreferenceChangeListener(this);
        shPrefs = new helperPreferences(this);
        if(shPrefs.GetPreferences(Constants.SHPREF_MAX_NUMBER_RESULTS).equals("0")){
            shPrefs.SavePreferences(Constants.SHPREF_MAX_NUMBER_RESULTS, "10");
        }
        if(shPrefs.GetPreferences(Constants.SHPREF_MAX_SEARCH_DISTANCE).equals("0")){
            shPrefs.SavePreferences(Constants.SHPREF_MAX_SEARCH_DISTANCE, "10");
        }
        PreferenceManager.setDefaultValues(this, R.xml.settings,
                false);
        initSummary(getPreferenceScreen());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); ++i) {
            Preference preference = getPreferenceScreen().getPreference(i);
            if (preference instanceof PreferenceGroup) {
                PreferenceGroup preferenceGroup = (PreferenceGroup) preference;
                for (int j = 0; j < preferenceGroup.getPreferenceCount(); ++j) {
                    Preference singlePref = preferenceGroup.getPreference(j);
                    updatePreference(singlePref, singlePref.getKey());
                }
            } else {
                updatePreference(preference, preference.getKey());
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    private void initSummary(Preference p) {
        updatePrefSummary(p);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updatePreference(findPreference(key), key);
    }

    private void updatePreference(Preference preference, String key) {

        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            listPreference.setSummary(listPreference.getEntry());
            return;
        }

        if (key.equals(Constants.SHPREF_MAX_NUMBER_RESULTS)) {
            findPreference(Constants.SHPREF_MAX_NUMBER_RESULTS).setSummary(this.getResources().getString(R.string.max_number_results_summary) + " " + shPrefs.GetPreferences(Constants.SHPREF_MAX_NUMBER_RESULTS) + " " + this.getResources().getString(R.string.results));

        } else if (key.equals(Constants.SHPREF_MAX_SEARCH_DISTANCE)) {
            findPreference(Constants.SHPREF_MAX_SEARCH_DISTANCE).setSummary(this.getResources().getString(R.string.max_search_distance_summary) +" "+ shPrefs.GetPreferences(Constants.SHPREF_MAX_SEARCH_DISTANCE)+ " " +this.getResources().getString(R.string.kilometers));

        }

    }

    private void updatePrefSummary(Preference p) {
        if (p instanceof ListPreference) {
            ListPreference listPref = (ListPreference) p;
            p.setSummary(listPref.getEntry());
        }
        if (p instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) p;
            if (p.getTitle().toString().toLowerCase().contains("password")) {
                p.setSummary("******");
            } else {
                p.setSummary(editTextPref.getText());
            }
        }

    }
}
