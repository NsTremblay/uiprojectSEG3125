package ui.uottawa.com.compassapp;

/**
 * Created by Joe on 2016-03-06.
 * For CompassApp
 */

import android.app.Activity;
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
    private helperAPIRequest APIRequest;
    private Activity activity;
    private Location currentLocation;
    public CompassView(Context context) {
        super(context);
        activity = (Activity) context;
        initialize();
    }

    public CompassView(Context context, AttributeSet attr) {
        super(context, attr);
        activity = (Activity) context;
        initialize();
    }

    private void initialize() {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.WHITE);
        paint.setTextSize(30);
        APIRequest = helperAPIRequest.getInstance(activity);
        coffeeShops = APIRequest.getShops();
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
        coffeeShops = APIRequest.getShops();

        int cxCompass = getMeasuredWidth() / 2;
        int cyCompass = getMeasuredHeight() / 2;
        float radiusCompass;

        if (cxCompass > cyCompass) {
            radiusCompass = (float) (cyCompass * 0.9);
        } else {
            radiusCompass = (float) (cxCompass * 0.9);
        }
        canvas.drawCircle(cxCompass, cyCompass, radiusCompass, paint);

        // calculate rotation angle
        int rotation = (int) (360 - bearing);

        /*Log.d("rotation:", String.valueOf(rotation));
        Log.d("bearing:", String.valueOf(bearing));
        Log.d("X:", String.valueOf(x));
        Log.d("Y:", String.valueOf(y));

        Log.d("Coordinatea:", String.valueOf(cxCompass + (radiusCompass * (float) x)));
        Log.d("Coordinateb:", String.valueOf(cyCompass + (radiusCompass * (float) y)));*/
        float bearingLoc;
        if(currentLocation!=null){
            //loop through all of the returned coffeeshops
            if(coffeeShops!=null) {
                for (int i = 0; i < coffeeShops.length; i++) {
                    Location dest = new Location(coffeeShops[i].getName());
                    dest.setLatitude(coffeeShops[i].getLatitude());
                    dest.setLongitude(coffeeShops[i].getLongitude());

                    bearingLoc = currentLocation.bearingTo(dest);
                    //Log.d("bearingLoc1:", String.valueOf(bearingLoc));

                    double angleRadians = Math.toRadians(bearingLoc) + Math.toRadians(rotation - 90);

                    double x = Math.cos(angleRadians);
                    double y = Math.sin(angleRadians);

                    //TODO Draw a clickable object instead of only text
                    canvas.drawText(coffeeShops[i].getName(), cxCompass + (radiusCompass * (float) x), cyCompass + (radiusCompass * (float) y), paint);
                }
            }else{
                //call the function to get the locations
                //getPlaces();
            }
           /* Location dest = new Location("dest");

            bearingLoc = currentLocation.bearingTo(dest);

            double angleRadians2 = Math.toRadians(bearingLoc)+Math.toRadians(rotation-90);

            double x2 = Math.cos(angleRadians2);
            double y2 = Math.sin(angleRadians2);
            canvas.drawText("LOC", cxCompass + (radiusCompass * (float) x2), cyCompass + (radiusCompass * (float) y2), paint);*/

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
}
