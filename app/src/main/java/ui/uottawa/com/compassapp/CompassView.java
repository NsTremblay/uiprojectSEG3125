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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CompassView extends View {


    private static final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int width = 0;
    private int height = 0;
    private float bearing; // rotation angle to North

    private Place[] coffeeShops;
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

        float bearingLoc;
        if (currentLocation != null) {
            //loop through all of the returned coffeeshops
            if (coffeeShops != null) {
                for (Place coffeeShop : coffeeShops) {
                    Location dest = new Location(coffeeShop.getName());
                    dest.setLatitude(coffeeShop.getLatitude());
                    dest.setLongitude(coffeeShop.getLongitude());

                    bearingLoc = currentLocation.bearingTo(dest);
                    //Log.d("bearingLoc1:", String.valueOf(bearingLoc));

                    double angleRadians = Math.toRadians(bearingLoc) + Math.toRadians(rotation - 90);

                    double x = Math.cos(angleRadians);
                    double y = Math.sin(angleRadians);

                    //TODO Draw a clickable object instead of only text
                    canvas.drawText(coffeeShop.getName(), cxCompass + (radiusCompass * (float) x), cyCompass + (radiusCompass * (float) y), paint);
                }
            }
        } else {
            Toast.makeText(activity, "Acquiring GPS Position", Toast.LENGTH_SHORT).show();
        }
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }
}
