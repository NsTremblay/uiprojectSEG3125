package ui.uottawa.com.compassapp;

/**
 * Created by Joe on 2016-03-06.
 * For CompassApp
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.location.Location;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CompassView extends View {

    private float direction = 0;
    private boolean firstDraw;

    private static final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int width = 0;
    private int height = 0;
    private Matrix matrix; // to manage rotation of the compass view
    private Bitmap bitmap;
    private float bearing; // rotation angle to North

    private Place [] coffeeShops;


    private Location currentLocation;
    public CompassView(Context context) {
        super(context);
        initialize();
    }

    public CompassView(Context context, AttributeSet attr) {
        super(context, attr);
        initialize();
    }

    private void initialize() {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.BLACK);
        paint.setTextSize(30);

        firstDraw = true;

        matrix = new Matrix();
        // create bitmap for compass icon
        bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.compass_icon);
    }

    public void setBearing(float b) {
        bearing = b;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int cxCompass = getMeasuredWidth() / 2;
        int cyCompass = getMeasuredHeight() / 2;
        float radiusCompass;

        if (cxCompass > cyCompass) {
            radiusCompass = (float) (cyCompass * 0.9);
        } else {
            radiusCompass = (float) (cxCompass * 0.9);
        }
        canvas.drawCircle(cxCompass, cyCompass, radiusCompass, paint);


        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        if (bitmapWidth > canvasWidth || bitmapHeight > canvasHeight) {
            // resize bitmap to fit in canvas
            bitmap = Bitmap.createScaledBitmap(bitmap,
                    (int) (bitmapWidth * 0.85), (int) (bitmapHeight * 0.85), true);
        }

        // center
        int bitmapX = bitmap.getWidth() / 2;
        int bitmapY = bitmap.getHeight() / 2;
        int parentX = width / 2;
        int parentY = height / 2;
        int centerX = parentX - bitmapX;
        int centerY = parentY - bitmapY;

        // calculate rotation angle
        int rotation = (int) (360 - bearing);

        // reset matrix
        matrix.reset();
        matrix.setRotate(rotation, bitmapX, bitmapY);
        // center bitmap on canvas
        matrix.postTranslate(centerX, centerY);
        // draw bitmap
        canvas.drawBitmap(bitmap, matrix, paint);
        double angleRadians = Math.toRadians(rotation);

        double x = -Math.cos(angleRadians);
        double y = Math.sin(angleRadians);
        Log.d("rotation:", String.valueOf(rotation));
        Log.d("bearing:", String.valueOf(bearing));
        Log.d("X:", String.valueOf(x));
        Log.d("Y:", String.valueOf(y));
        canvas.drawText("TEST TEXT", cxCompass + (radiusCompass * (float) x), cyCompass + (radiusCompass * (float) y), paint);

        Log.d("Coordinatea:", String.valueOf(cxCompass + (radiusCompass * (float) x)));
        Log.d("Coordinateb:", String.valueOf(cyCompass + (radiusCompass * (float) y)));
        float bearingLoc;
        if(currentLocation!=null){
            //loop through all of the returned coffeeshops
            if(coffeeShops!=null) {
                for (int i = 0; i < coffeeShops.length; i++) {
                    Location dest = new Location(coffeeShops[i].getName());
                    dest.setLatitude(coffeeShops[i].getLatitude());
                    dest.setLongitude(coffeeShops[i].getLongitude());

                    bearingLoc = currentLocation.bearingTo(dest);
                    Log.d("bearingLoc1:", String.valueOf(bearingLoc));

                    double angleRadians2 = Math.toRadians(bearingLoc) + Math.toRadians(rotation - 90);

                    double x2 = Math.cos(angleRadians2);
                    double y2 = Math.sin(angleRadians2);
                    canvas.drawText(coffeeShops[i].getName(), cxCompass + (radiusCompass * (float) x2), cyCompass + (radiusCompass * (float) y2), paint);
                }
            }else{
                //call the function to get the locations
                getPlaces();
            }
            Location dest = new Location("dest");

            bearingLoc = currentLocation.bearingTo(dest);

            double angleRadians2 = Math.toRadians(bearingLoc)+Math.toRadians(rotation-90);

            double x2 = Math.cos(angleRadians2);
            double y2 = Math.sin(angleRadians2);
            canvas.drawText("LOC", cxCompass + (radiusCompass * (float) x2), cyCompass + (radiusCompass * (float) y2), paint);

        }else{
            Location dest = new Location("Dest");
            dest.setLatitude(48.48);
            dest.setLongitude(-75.77);

            Location here = new Location("Here");
            dest.setLatitude(45.477);
            dest.setLongitude(-75.77);
            bearingLoc = here.bearingTo(dest);
            Log.d("bearingLoc2:", String.valueOf(bearingLoc));
        }


    }
    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public Location getCurrentLocation(){
        return this.currentLocation;
    }

    public void addCoffee(Place [] coffeeShops){
        this.coffeeShops = coffeeShops;
    }

    private void getPlaces(){
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


    }

    private void loadResults(JSONArray coffeeshops){
        try {
            coffeeShops = new Place[coffeeshops.length()];
            for (int i = 0; 0 < coffeeshops.length(); i++) {
                coffeeShops[i] = Place.jsonToPontoReferencia(coffeeshops.getJSONObject(i));
            }

        }catch (JSONException je)
        {
            Log.d("Trying to get json obj",je.toString());
        }
    }
}
