package ui.uottawa.com.compassapp;

import android.app.Activity;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Nicolas on 16-03-20.
 */
public class CoffeeShopView extends Activity {

    private double lat;
    private double lon;
    private String times;
    private String name;

    protected void onCreate(Bundle initialBundleConfigurations){
        super.onCreate(initialBundleConfigurations);

    }

    public CoffeeShopView(JSONObject coffeeShop){
        try{
            //get all the information from the json
            lat = new Double(coffeeShop.getJSONObject("geometry").getJSONObject("location").getJSONObject("lat").toString());
            lon = new Double(coffeeShop.getJSONObject("geometry").getJSONObject("location").getJSONObject("lon").toString());
            name = coffeeShop.getJSONObject("name").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public double getLat(){return lat;}
    public double getLon(){return lon;}
}
