package ui.uottawa.com.compassapp;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by joesi on 2016-03-20.
 */
public class helperPreferences {
    private static final String PREFS_NAME = "coffeeBean_prefs";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public helperPreferences(Activity activity) {

        this.sharedPreferences = activity.getSharedPreferences(PREFS_NAME, 0);
        this.editor = sharedPreferences.edit();
    }

    public String GetPreferences(String key) {
        String getValue = sharedPreferences.getString(key, "0");
        return getValue;
    }

    public void SavePreferences(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public void clearAllPreferences() {
        editor.clear();
        editor.commit();
    }
}