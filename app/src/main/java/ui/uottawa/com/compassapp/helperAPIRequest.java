package ui.uottawa.com.compassapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Joe on 2016-03-21.
 * For CompassApp
 * Singleton class to deal proceed API Request to google
 */


public class helperAPIRequest {

    private static helperAPIRequest instance = new helperAPIRequest();

     private helperAPIRequest() {
        //no instance
    }
    public static helperAPIRequest getInstance() {
        return instance;
    }

   /* private void getPlaces(){
        //get the location of the device and enter into the query
        String latitude = Double.toString(currentLocation.getLatitude());
        String longitude = Double.toString(currentLocation.getLongitude());

        String distance = Integer.toString(1000);

        // TODO: 16-03-20 Check location in a more reliable way
        if(latitude!=null){
            new HttpTask("https://maps.googleapis.com/maps/api/place/textsearch/json?location="+latitude+","+longitude+"&radius="+distance+"&type=cafe&key=AIzaSyASnlCMNHORqmbF8-V6GV2WSklHql4ZImo", "GET") {

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
        }else{}


    }*/


}
