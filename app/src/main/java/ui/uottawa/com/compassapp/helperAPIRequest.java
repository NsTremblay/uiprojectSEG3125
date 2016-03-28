package ui.uottawa.com.compassapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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
    private Context ctx;
    private double rating;

    private helperAPIRequest(Context context) {
        ctx = context;
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

    public void getCoffeeShopsLocation(final boolean user_search) {
        //TODO Done. see this method Cases to handle: - Get locations with user search when using search field(when user uses search function, other flags wont be taken in account
        //TODO Cases to handle: - Favorites enabled/disabled
        //TODO Done. see loadresults method Cases to handle: - Chain enabled/disabled (no Tim Hortons, McDonalds, Second Cup, Starbucks, etc) if name.contains(chainName) then remove from search results.
        //TODO Done. see loadresults method Cases to handle: - Minimum Rating
        //get the location from shared prefs of the device and enter into the query
        String latitude = shPrefs.GetPreferences(Constants.SHPREF_LOCATION_LATITUDE);
        String longitude = shPrefs.GetPreferences(Constants.SHPREF_LOCATION_LONGITUDE);
        rating = Double.parseDouble(shPrefs.GetPreferences(Constants.SHPREF_MIN_RATING));

        //get the maximum search distance(radius) from shared prefs of the device and enter into the query. Multiply by 1000 to get meters from km stored in shared prefs
        String distance = String.valueOf(Integer.valueOf(shPrefs.GetPreferences(Constants.SHPREF_MAX_SEARCH_DISTANCE)) * 1000);
        String api_url = "";

        //handling the user search
        if (user_search) {
            String user_search_value = shPrefs.GetPreferences(Constants.SHPREF_USER_SEARCH_VALUE);
            if (!user_search_value.equals("") || !user_search_value.equals("0")) {
                api_url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latitude + "," + longitude + "&radius=" + distance + "&type=cafe&name=" + user_search_value + "&key=AIzaSyASnlCMNHORqmbF8-V6GV2WSklHql4ZImo";
            } else {
                api_url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latitude + "," + longitude + "&radius=" + distance + "&type=cafe&key=AIzaSyASnlCMNHORqmbF8-V6GV2WSklHql4ZImo";
                Toast.makeText(ctx, R.string.invalid_search, Toast.LENGTH_LONG).show();
            }
        } else {
            api_url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latitude + "," + longitude + "&radius=" + distance + "&type=cafe&key=AIzaSyASnlCMNHORqmbF8-V6GV2WSklHql4ZImo";
        }
        Log.d("URL", "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latitude + "," + longitude + "&radius=" + distance + "&type=cafe&key=AIzaSyASnlCMNHORqmbF8-V6GV2WSklHql4ZImo");
        // TODO: 16-03-20 Check location in a more reliable way
        new HttpTask(api_url, "GET") {

            @Override
            protected void onPostExecute(JSONObject json) {
                super.onPostExecute(json);
                try {
                    if (json != null) {
                        JSONArray results = json.getJSONArray("results");
                        loadResults(results, user_search);
                        Log.d("JSONObject", json.toString());
                        Log.d("JSONArray", results.toString());
                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        }.execute();

    }

    private void loadResults(JSONArray coffeeshops, boolean user_search) {
        String chain_flag = shPrefs.GetPreferences(Constants.SHPREF_CHAIN_FLAG);
        try {
            shops = new Place[coffeeshops.length()];
            for (int i = 0; i < coffeeshops.length(); i++) {
                Place currentPlace = Place.jsonToPontoReferencia(coffeeshops.getJSONObject(i));
                Log.d("PLACEINFO", currentPlace.toString());

                if (!user_search) {
                    //Dealing with the rating
                    if (currentPlace.getRating() >= this.rating) {
                        shops[i] = currentPlace;
                        Log.d("PLACEINFORATING", currentPlace.toString());
                    }
                    if (chain_flag.equals("1")) {//chain flag enabled, remove all the chain cafes/restaurant from the results.
                        //Dealing with chain cafes
                        if (!currentPlace.getName().contains("Tim Hortons") || !currentPlace.getName().contains("Starbucks") || !currentPlace.getName().contains("McDonald's") || !currentPlace.getName().contains("Second Cup")) {
                            shops[i] = currentPlace;
                            Log.d("PLACEINFOCHAIN", currentPlace.toString());
                        }
                    }
                } else { // user search used, return all results,ignored other parameters*/
                    shops[i] = currentPlace;
                    Log.d("PLACEINFOSEARCH", currentPlace.toString());
                }
            }


        } catch (JSONException je) {
            Log.d("Trying to get json obj", je.toString());
        }
    }

    public Place[] getShops() {
        return shops;
    }


}
