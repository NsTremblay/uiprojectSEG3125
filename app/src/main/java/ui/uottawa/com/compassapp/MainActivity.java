package ui.uottawa.com.compassapp;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
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
import android.telecom.Connection;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements SensorEventListener,
        LocationListener, View.OnClickListener, SeekBar.OnSeekBarChangeListener , ConnectionCallbacks, OnConnectionFailedListener {


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
    private LocationManager locationManager;
    private GoogleApiClient mGoogleApiClient;
    private ImageButton searchImageButton, favoriteImageButton, chainImageButton, ratingImageButton;
    private helperPreferences shPrefs;
    private helperAPIRequest APIRequest;
    private EditText searchBar;
    private Button searchButton;
    private TextView ratingTextView;
    private SeekBar ratingBar;
    private boolean searchVisible = false;
    private boolean ratingBarVisible = false;
    private boolean favoritesEnabled = false;
    private boolean chainsEnabled = false;
    private float rating;
    private Place[] coffeeShops;
    Button coffeeButton;
    RelativeLayout coffeeLayout;
    private int counterForLog;
    private ArrayList<Button> coffeeButtons;
    private TextView centerBean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO Make a loading screen bedore it acquires the device location
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        shPrefs = new helperPreferences(this);
        //APIRequest = helperAPIRequest.getInstance(getApplicationContext());
        shPrefs.SavePreferences(Constants.SHPREF_MAX_SEARCH_DISTANCE, String.valueOf(10));

        coffeeLayout = (RelativeLayout)findViewById(R.id.compass);
        coffeeLayout.setGravity(RelativeLayout.CENTER_IN_PARENT);

        //compassView = (CompassView) findViewById(R.id.compass);
        searchImageButton = (ImageButton) findViewById(R.id.search_image_button);
        favoriteImageButton = (ImageButton) findViewById(R.id.favorite_image_button);
        chainImageButton = (ImageButton) findViewById(R.id.chain_image_button);
        ratingImageButton = (ImageButton) findViewById(R.id.rating_image_button);
        searchBar = (EditText) findViewById(R.id.editText_search);
        searchButton = (Button) findViewById(R.id.search_button);
        ratingBar = (SeekBar) findViewById(R.id.rating_bar);
        ratingTextView = (TextView) findViewById(R.id.rating_text_view);
        centerBean = (TextView) findViewById(R.id.theCenter);

        searchImageButton.setOnClickListener(this);
        favoriteImageButton.setOnClickListener(this);
        chainImageButton.setOnClickListener(this);
        ratingImageButton.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        ratingBar.setOnSeekBarChangeListener(this);
        ratingBar.setProgress((int) (Float.parseFloat(shPrefs.GetPreferences(Constants.SHPREF_MIN_RATING)) * 20));

        // keep screen light on (wake lock light)
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        APIRequest = helperAPIRequest.getInstance(this.getBaseContext());
        coffeeButtons = new ArrayList<Button>();
        coffeeButton = new Button(this);

    }

    @Override
    protected void onStart() {

        mGoogleApiClient.connect();
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
    public void onLocationChanged(Location location) throws NullPointerException{

        currentLocation = location;
        //compassView.setCurrentLocation(location);

        // used to update location info on screen

        geomagneticField = new GeomagneticField(
                (float) currentLocation.getLatitude(),
                (float) currentLocation.getLongitude(),
                (float) currentLocation.getAltitude(),
                System.currentTimeMillis());
        //Everytime location is changed, save it in the preferences for easier access
        shPrefs.SavePreferences(Constants.SHPREF_LOCATION_LATITUDE, String.valueOf(currentLocation.getLatitude()));
        shPrefs.SavePreferences(Constants.SHPREF_LOCATION_LONGITUDE, String.valueOf(currentLocation.getLongitude()));
        //everytime location changes, update the location of surroundings coffee shops
        APIRequest.getCoffeeShopsLocation(false);

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
        //compassView.setBearing((float) bearing);

        if (accelOrMagnetic) {
            //compassView.postInvalidate();
        } else {
            //compassView.updateLocations();
        }

        int cxCompass = coffeeLayout.getWidth()/2;
        int cyCompass = coffeeLayout.getHeight()/2;

        float radiusCompass = 0;

        if (cxCompass > cyCompass) {
            radiusCompass = (float) (cyCompass * 0.9);
        } else {
            radiusCompass = (float) (cxCompass * 0.9);
        }


        if(coffeeShops==null){
            coffeeShops = APIRequest.getShops();
            if(coffeeShops!=null){
            try {
                Log.i("But", "TRying to add buttons "+ coffeeShops.length);
//                coffeeButtons = new Button[coffeeShops.length];
                for (int i = 0; i < coffeeShops.length; i++) {

                    Button tempCoffee = new Button(this);
                    int pow = 0;if(coffeeShops[i].getRating()<3){pow=4;}else{pow=3;}


                    tempCoffee.setLayoutParams(new LinearLayout.LayoutParams((int) Math.pow(coffeeShops[i].getRating(), pow), (int) Math.pow(coffeeShops[i].getRating(), pow
                    )));
                    tempCoffee.setBackgroundResource(R.drawable.round_button);
                    tempCoffee.setGravity(RelativeLayout.CENTER_IN_PARENT);
                    tempCoffee.setId(i);
                    Place tempPlace = coffeeShops[i];
                    tempCoffee.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            // Perform action on click
                            int idOfThePlace = v.getId();
                            Log.i("Button was clicked", "Clicked Button " + idOfThePlace + " place clicked " + coffeeShops[idOfThePlace].getName());
                            centerBean.setText(coffeeShops[idOfThePlace].getName());

                        }
                    });

                    boolean overlap = false;
                    //check if there is overlap
                    coffeeButtons.add(tempCoffee);

                    Location dest = new Location(coffeeShops[i].getName());

                    dest.setLatitude(coffeeShops[i].getLatitude());
                    dest.setLongitude(coffeeShops[i].getLongitude());

                    float bearingLoc;
                    // calculate rotation angle
                    int rotation = (int) (360 - bearing);

                    bearingLoc = currentLocation.bearingTo(dest);

                    double angleRadians = Math.toRadians(bearingLoc) + Math.toRadians(rotation - 90);

                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);

                    int width = size.x;
                    int height = size.y;

                    float x = (float) Math.cos(angleRadians);
                    float y = (float) Math.sin(angleRadians);

                    coffeeButtons.get(i).setTranslationX(cxCompass + (radiusCompass * (float) x) - coffeeButtons.get(i).getWidth() / 2);
                    coffeeButtons.get(i).setTranslationY(cyCompass + (radiusCompass * (float) y) - coffeeButtons.get(i).getHeight() / 2);

                    for (int b = 0; b<i;b++) {
                        Log.i("Over", "There was overlap y"+coffeeButtons.get(i).getY()+":"+coffeeButtons.get(b).getY()+" X"+coffeeButtons.get(i).getX()+":"+coffeeButtons.get(b).getY()+" Then width "+coffeeButtons.get(b).getWidth()+" height"+coffeeButtons.get(i).getHeight());
                        if(coffeeButtons.get(i).getY() > coffeeButtons.get(b).getY()
                                && (coffeeButtons.get(i).getY() < (coffeeButtons.get(b).getY() + coffeeButtons.get(b).getHeight()))
                                && coffeeButtons.get(i).getX() > coffeeButtons.get(b).getX()
                                && (coffeeButtons.get(i).getX() < (coffeeButtons.get(b).getX() + coffeeButtons.get(b).getWidth()))){
                            overlap = true;
                            Log.i("Collision", "There was a collision");
                        }
                    }
                    coffeeLayout.addView(tempCoffee);

                }

                int closestIndex = 0;

                //get the closest coffee shop
                for(int c = 1;c<coffeeShops.length; c++){
                    if( coffeeShops[closestIndex].getDistance(currentLocation.getLatitude(), currentLocation.getLongitude())>coffeeShops[c].getDistance(currentLocation.getLatitude(), currentLocation.getLongitude())){
                        closestIndex = c;
                    }
                }
                centerBean.setText("The closest Coffee Shop is at "+coffeeShops[closestIndex].getDistance(currentLocation.getLatitude(), currentLocation.getLongitude()));

            }catch(Exception e){
                Log.i("Error in shops", "onConnected: ");
            }
            }
        }else {

            if (counterForLog>2){
            try {
                //loop through all of the coffeeshops and update their location
                for (int i = 0; i < coffeeShops.length; i++) {

                    Location dest = new Location(coffeeShops[i].getName());

                    dest.setLatitude(coffeeShops[i].getLatitude());
                    dest.setLongitude(coffeeShops[i].getLongitude());

                    float bearingLoc;
                    // calculate rotation angle
                    int rotation = (int) (360 - bearing);

                    bearingLoc = currentLocation.bearingTo(dest);

                    double angleRadians = Math.toRadians(bearingLoc) + Math.toRadians(rotation - 90);

                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);

                    int width = size.x;
                    int height = size.y;

                    float x = (float) Math.cos(angleRadians);
                    float y = (float) Math.sin(angleRadians);

                    coffeeButtons.get(i).setTranslationX(cxCompass + (radiusCompass * (float) x) - coffeeButtons.get(i).getWidth() / 2);
                    coffeeButtons.get(i).setTranslationY(cyCompass + (radiusCompass * (float) y) - coffeeButtons.get(i).getHeight() / 2);
                }
            } catch (Exception e) {
                Log.i("Button Exception", e.toString());
            }
//                Log.i("Bearing",  String.valueOf(bearing));
                counterForLog = 0;
            }
        }



        counterForLog++;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD
                && accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            // manage fact that compass data are unreliable ...
            // toast ? display on screen ?
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
        if (id == R.id.action_favorites) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.search_image_button) {
            // pop up search bar here
            if (searchVisible) {
                searchBar.setVisibility(View.GONE);
                searchButton.setVisibility(View.GONE);
                searchVisible = false;
            } else if (!searchVisible || ratingBarVisible) {
                ratingBar.setVisibility(View.GONE);
                ratingTextView.setVisibility(View.GONE);
                searchBar.setVisibility(View.VISIBLE);
                searchButton.setVisibility(View.VISIBLE);
                searchVisible = true;
                ratingBarVisible = false;
            }

        } else if (id == R.id.favorite_image_button) {
            if (favoritesEnabled) {
                favoriteImageButton.setImageDrawable(getResources().getDrawable(R.mipmap.favorite_icon_disabled));
                favoritesEnabled = false;
            } else if (!favoritesEnabled) {
                favoriteImageButton.setImageDrawable(getResources().getDrawable(R.mipmap.favorite_icon_enabled));
                favoritesEnabled = true;
            }
            APIRequest.getCoffeeShopsLocation(false);

        } else if (id == R.id.chain_image_button) {
            if (chainsEnabled) {
                chainImageButton.setImageDrawable(getResources().getDrawable(R.mipmap.chain_icon_disabled));
                chainsEnabled = false;
                shPrefs.SavePreferences(Constants.SHPREF_CHAIN_FLAG,"0");
            } else if (!chainsEnabled) {
                chainImageButton.setImageDrawable(getResources().getDrawable(R.mipmap.chain_icon_enabled));
                chainsEnabled = true;
                shPrefs.SavePreferences(Constants.SHPREF_CHAIN_FLAG,"1");
            }
            APIRequest.getCoffeeShopsLocation(false);

        } else if (id == R.id.rating_image_button) {
            if (searchVisible || !ratingBarVisible) {
                searchBar.setVisibility(View.GONE);
                searchButton.setVisibility(View.GONE);
                searchVisible = false;
                ratingBar.setVisibility(View.VISIBLE);
                ratingTextView.setVisibility(View.VISIBLE);
                ratingBarVisible = true;
            } else if (ratingBarVisible) {
                ratingBar.setVisibility(View.GONE);
                ratingTextView.setVisibility(View.GONE);
                ratingBarVisible = false;
            }
        } else if (id == R.id.search_button) {
            shPrefs.SavePreferences(Constants.SHPREF_USER_SEARCH_VALUE, String.valueOf(searchBar.getText()));
            APIRequest.getCoffeeShopsLocation(true);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        rating = (float) (progress / 20.0);
        ratingTextView.setText(Float.toString(rating));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        ratingTextView.setText(Float.toString(rating));
        shPrefs.SavePreferences(Constants.SHPREF_MIN_RATING, String.valueOf(rating));
        //rating changed, redo the request to the API
        APIRequest.getCoffeeShopsLocation(false);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        currentLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        try {
            onLocationChanged(currentLocation);
        }catch (Exception e){
            Log.i("Cannot change", e.toString());
        }

        Log.i("LATITUDE", String.valueOf(currentLocation.getLatitude()));
        Log.i("LONGITUDE", String.valueOf(currentLocation.getLatitude()));

        APIRequest.getCoffeeShopsLocation(false);

//        for (Place coffeeTemp:coffeeShops){
//            Button coffeeButton = new Button(this);
//            coffeeButton.setLayoutParams(new RelativeLayout.LayoutParams(20,20));
//
//            coffeeButton.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//                    // Perform action on click
//                    Log.i("Button was clicked", "Clicked Button");
//                }
//            });
//
//        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult result){

    }

    @Override
    public void onConnectionSuspended(int suspended){

    }


}