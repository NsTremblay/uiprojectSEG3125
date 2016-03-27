package ui.uottawa.com.compassapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Joe on 2016-03-21.
 * For CompassApp
 * Singleton class to deal with API Request to google
 */


public class helperAPIRequest {

    private Place[] shops;

    private helperPreferences shPrefs;
    private static helperAPIRequest sInstance = null;

    private helperAPIRequest(Context context) {
        shPrefs = new helperPreferences((Activity) context);
    }

    public static helperAPIRequest getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new helperAPIRequest(context);
        }


        return sInstance;
    }
    public void getCoffeeShopsLocation() {
        //TODO Cases to handle: - Get locations with user search when using search field
        //TODO Cases to handle: - Favorites enabled/disabled
        //TODO Cases to handle: - Chain enabled/disabled (no Tim Hortons, McDonalds, Second Cup, Starbucks, etc) if name.contains(chainName) then remove from search results
        //TODO Cases to handle: - Minimum Rating

        //get the location from shared prefs of the device and enter into the query
        String latitude = shPrefs.GetPreferences(Constants.SHPREF_LOCATION_LATITUDE);
        String longitude = shPrefs.GetPreferences(Constants.SHPREF_LOCATION_LONGITUDE);

        //get the maximum search distance(radius) from shared prefs of the device and enter into the query. Multiply by 1000 to get meters from km stored in shared prefs
        String distance = String.valueOf(Integer.valueOf(shPrefs.GetPreferences(Constants.SHPREF_MAX_SEARCH_DISTANCE)) * 1000);
        Log.d("URL","https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latitude + "," + longitude + "&radius=" + distance + "&type=cafe&key=AIzaSyASnlCMNHORqmbF8-V6GV2WSklHql4ZImo");
        // TODO: 16-03-20 Check location in a more reliable way
        if (latitude != null) {
            new HttpTask("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latitude + "," + longitude + "&radius=" + distance + "&type=cafe&key=AIzaSyASnlCMNHORqmbF8-V6GV2WSklHql4ZImo", "GET") {

                @Override
                protected void onPostExecute(JSONObject json) {
                    super.onPostExecute(json);
                    try {
                        if (json != null) {
                            JSONArray results = json.getJSONArray("results");
                            loadResults(results);

                            Log.d("JSONObject", json.toString());
                            Log.d("JSONArray", results.toString());
                        }

                    } catch (JSONException e) {

                        e.printStackTrace();
                    }
                }
            }.execute();
        } else {
        }


    }
    private void loadResults(JSONArray coffeeshops) {
        try {
            shops = new Place[coffeeshops.length()];
            for (int i = 0; i < coffeeshops.length(); i++) {
                shops[i] = Place.jsonToPontoReferencia(coffeeshops.getJSONObject(i));
            }

        } catch (JSONException je) {
            Log.d("Trying to get json obj", je.toString());
        }
    }

    public Place[] getShops() {
        return shops;
    }


}
