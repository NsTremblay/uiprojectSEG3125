package ui.uottawa.com.compassapp;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements SensorEventListener,
        LocationListener {

    public static final String NA = "N/A";
    public static final String FIXED = "FIXED";
    // location min time
    private static final int LOCATION_MIN_TIME = 30 * 1000;
    // location min distance
    private static final int LOCATION_MIN_DISTANCE = 10;
    // Gravity for accelerometer data
    private float[] gravity = new float[3];
    // magnetic data
    private float[] geomagnetic = new float[3];
    // Rotation data
    private float[] rotation = new float[9];
    // orientation (azimuth, pitch, roll)
    private float[] orientation = new float[3];
    // smoothed values
    private float[] smoothed = new float[3];
    // sensor manager
    private SensorManager sensorManager;
    // sensor gravity
    private Sensor sensorGravity;
    private Sensor sensorMagnetic;
    private Location currentLocation;
    private GeomagneticField geomagneticField;
    private double bearing = 0;
    private CompassView compassView;
    private final String TAG = getClass().getSimpleName();
    private GoogleMap mMap;
    private String[] places;
    private LocationManager locationManager;
    private Location loc;
    private GoogleApiClient mGoogleApiClient;
    private Place [] shops;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        compassView = (CompassView) findViewById(R.id.compass);
        // keep screen light on (wake lock light)
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        APIRequest();
    }

    @Override
    protected void onStart() {
        super.onStart();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorGravity = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        // listen to these sensors
        sensorManager.registerListener(this, sensorGravity,
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorMagnetic,
                SensorManager.SENSOR_DELAY_NORMAL);

        // I forgot to get location manager from system service ... Ooops <img src="http://www.ssaurel.com/blog/wp-includes/images/smilies/icon_biggrin.gif" alt=":D" class="wp-smiley">
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // request location data
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                LOCATION_MIN_TIME, LOCATION_MIN_DISTANCE, this);

        // get last known position
        Location gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (gpsLocation != null) {
            currentLocation = gpsLocation;
        } else {
            // try with network provider
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location networkLocation = locationManager
                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (networkLocation != null) {
                currentLocation = networkLocation;
            } else {
                // Fix a position
                currentLocation = new Location(FIXED);
                currentLocation.setAltitude(1);
                currentLocation.setLatitude(43.296482);
                currentLocation.setLongitude(5.36978);
            }

            // set current location
            onLocationChanged(currentLocation);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // remove listeners
        sensorManager.unregisterListener(this, sensorGravity);
        sensorManager.unregisterListener(this, sensorMagnetic);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        compassView.setCurrentLocation(location);
        // used to update location info on screen
        updateLocation(location);
        geomagneticField = new GeomagneticField(
                (float) currentLocation.getLatitude(),
                (float) currentLocation.getLongitude(),
                (float) currentLocation.getAltitude(),
                System.currentTimeMillis());
    }

    private void updateLocation(Location location) {
        if (FIXED.equals(location.getProvider())) {

        }

        // better => make this creation outside method
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        NumberFormat formatter = new DecimalFormat("#0.00", dfs);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        boolean accelOrMagnetic = false;

        // get accelerometer data
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // we need to use a low pass filter to make data smoothed
            smoothed = LowPassFilter.filter(event.values, gravity);
            gravity[0] = smoothed[0];
            gravity[1] = smoothed[1];
            gravity[2] = smoothed[2];
            accelOrMagnetic = true;

        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            smoothed = LowPassFilter.filter(event.values, geomagnetic);
            geomagnetic[0] = smoothed[0];
            geomagnetic[1] = smoothed[1];
            geomagnetic[2] = smoothed[2];
            accelOrMagnetic = true;

        }

        // get rotation matrix to get gravity and magnetic data
        SensorManager.getRotationMatrix(rotation, null, gravity, geomagnetic);
        // get bearing to target
        SensorManager.getOrientation(rotation, orientation);
        // east degrees of true North
        bearing = orientation[0];
        // convert from radians to degrees
        bearing = Math.toDegrees(bearing);

        // fix difference between true North and magnetical North
        if (geomagneticField != null) {
            bearing += geomagneticField.getDeclination();
        }

        // bearing must be in 0-360
        if (bearing < 0) {
            bearing += 360;
        }

        // update compass view
        compassView.setBearing((float) bearing);

        if (accelOrMagnetic) {
            compassView.postInvalidate();
        }

        updateTextDirection(bearing); // display text direction on screen
    }

    private void updateTextDirection(double bearing) {
        int range = (int) (bearing / (360f / 16f));
        String dirTxt = "";

        if (range == 15 || range == 0)
            dirTxt = "N";
        if (range == 1 || range == 2)
            dirTxt = "NE";
        if (range == 3 || range == 4)
            dirTxt = "E";
        if (range == 5 || range == 6)
            dirTxt = "SE";
        if (range == 7 || range == 8)
            dirTxt = "S";
        if (range == 9 || range == 10)
            dirTxt = "SW";
        if (range == 11 || range == 12)
            dirTxt = "W";
        if (range == 13 || range == 14)
            dirTxt = "NW";


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD
                && accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            // manage fact that compass data are unreliable ...
            // toast ? display on screen ?
        }
    }


    private void loadResults(JSONArray coffeeshops){
        try {
            shops = new Place[coffeeshops.length()];
            for (int i = 0; 0 < coffeeshops.length(); i++) {
                shops[i] = Place.jsonToPontoReferencia(coffeeshops.getJSONObject(i));
            }

        }catch (JSONException je)
        {
            Log.d("Trying to get json obj",je.toString());
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /**
     *
     * @param filterInclude
     * @param filterExclude
     *
     * This function will get the location of all of the coffee shops around the user
     *
     */

    private void showCoffeeShops(String filterInclude, String filterExclude){

        //get the location of the device and enter into the query
        String latitude = Double.toString(compassView.getCurrentLocation().getLatitude());
        String longitude = Double.toString(compassView.getCurrentLocation().getLongitude());

        //lets try to add some filters


        String distance = Integer.toString(10000);
        
        // TODO: 16-03-20 Check location in a more reliable way
        if(latitude!=null){
            new HttpTask("https://maps.googleapis.com/maps/api/place/textsearch/json?location="+latitude+","+longitude+"&radius="+distance+"&type=cafe&key=AIzaSyASnlCMNHORqmbF8-V6GV2WSklHql4ZImo", "GET") {

                @Override
                protected void onPostExecute(JSONObject json) {
                    super.onPostExecute(json);
                    try {
                        if (json != null) {
                            JSONArray results = json.getJSONArray("results");

                            //remove the coffeeshops that are not in the list

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

        
    }
    private void APIRequest() {


        new HttpTask("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=45.5017,-75.7755&radius=10000&type=cafe&key=AIzaSyASnlCMNHORqmbF8-V6GV2WSklHql4ZImo", "GET") {

            @Override
            protected void onPostExecute(JSONObject json) {
                super.onPostExecute(json);
                try {
                    if (json != null) {
                        JSONArray results = json.getJSONArray("results");
                        Log.d("JSONObject", json.toString());
                        Log.d("JSONArray", results.toString());
                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        }.execute();
    }
}