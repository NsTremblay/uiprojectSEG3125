package ui.uottawa.com.compassapp;

/**
 * Created by Joe on 2016-03-06.
 * For CompassApp
 */

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CompassView extends View implements View.OnClickListener {


    private static final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int width = 0;
    private int height = 0;
    private float bearing; // rotation angle to North
    private float distance = 0; // rotation angle to North

    private Place[] coffeeShops;
    private helperAPIRequest APIRequest;
    private Activity activity;
    private Location currentLocation;
    private ArrayList<Button> coffeeButtons;
    private helperPreferences shPrefs;
    private boolean first =true;
    ViewGroup parentView;
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
        shPrefs = new helperPreferences(activity);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        paint.setColor(Color.WHITE);
        paint.setTextSize(30);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DARKEN));
        APIRequest = helperAPIRequest.getInstance(activity);
        coffeeShops = APIRequest.getShops();
        coffeeButtons = new ArrayList<>();

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
        if(first) {
            parentView = (ViewGroup) CompassView.this.getParent();
            first = false;
        }
        coffeeShops = APIRequest.getShops();

        int cxCompass = getMeasuredWidth() / 2;
        int cyCompass = getMeasuredHeight() / 2;
        float radiusCompass;

        if (cxCompass > cyCompass) {
            radiusCompass = (float) (cyCompass * 0.9);
        } else {
            radiusCompass = (float) (cxCompass * 0.9);
        }

        // calculate rotation angle
        int rotation = (int) (360 - bearing);

        float bearingLoc;
        if (currentLocation != null) {
            //loop through all of the returned coffeeshops
            if (coffeeShops != null) {
                //make the circles before Drawing them and store them
                Button tempCoffee = null;
                for (int i = 0; i < coffeeShops.length; i++) {
                    if (coffeeShops[i] != null) {

                        Location dest = new Location(coffeeShops[i].getName());
                        dest.setLatitude(coffeeShops[i].getLatitude());
                        dest.setLongitude(coffeeShops[i].getLongitude());

                        bearingLoc = currentLocation.bearingTo(dest);
                        double angleRadians = Math.toRadians(bearingLoc) + Math.toRadians(rotation - 90);

                        double x = Math.cos(angleRadians);
                        double y = Math.sin(angleRadians);
                        tempCoffee = new Button(activity);
                        int pow;
                        if (coffeeShops[i].getRating() < 3) {
                            pow = 4;
                        } else {
                            pow = 3;
                        }
                        tempCoffee.setLayoutParams(new LinearLayout.LayoutParams((int) Math.pow(coffeeShops[i].getRating(), pow), (int) Math.pow(coffeeShops[i].getRating(), pow
                        )));
                        if (shPrefs.GetPreferences(Constants.SHPREF_DEST_COFFEE_ID).equals(coffeeShops[i].getId())) {
                            tempCoffee.setBackgroundResource(R.mipmap.coffee_bean_icon);
                        } else {
                            if (coffeeShops[i].getIcon().equals("https://maps.gstatic.com/mapfiles/place_api/icons/cafe-71.png") || coffeeShops[i].getIcon().equals("https://maps.gstatic.com/mapfiles/place_api/icons/shopping-71.png")) {
                                tempCoffee.setBackgroundResource(R.mipmap.cafe_icon);
                            }
                            if (coffeeShops[i].getIcon().equals("https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png")) {
                                tempCoffee.setBackgroundResource(R.mipmap.restaurant_icon);
                            }
                        }

                        tempCoffee.setGravity(RelativeLayout.CENTER_IN_PARENT);
                        tempCoffee.setId(i);
                        tempCoffee.setOnClickListener(this);
                        coffeeButtons.add(tempCoffee);
                        if(i<coffeeButtons.size()) {
                            coffeeButtons.get(i).setTranslationX(cxCompass + (radiusCompass * (float) x) - coffeeButtons.get(i).getWidth() / 2);
                            coffeeButtons.get(i).setTranslationY(cyCompass + (radiusCompass * (float) y) - coffeeButtons.get(i).getHeight() / 2);
                            canvas.drawText(coffeeShops[i].getName(), cxCompass + (radiusCompass * (float) x) - 60, cyCompass + (radiusCompass * (float) y) + 60, paint);
                            parentView.addView(tempCoffee);
                        }
                        tempCoffee = null;
                    }
                }
            }
        }

    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    @Override
    public void onClick(View v) {
        DialogFragment optionsDialog = new DialogClickOptions();
        optionsDialog.show(activity.getFragmentManager(), "options");
        Bundle info = new Bundle();
        info.putString("id", coffeeShops[v.getId()].getId());
        info.putString("name", coffeeShops[v.getId()].getName());
        info.putString("lon", String.valueOf(coffeeShops[v.getId()].getLongitude()));
        info.putString("lat", String.valueOf(coffeeShops[v.getId()].getLatitude()));
        info.putString("rating", String.valueOf(coffeeShops[v.getId()].getRating()));
        optionsDialog.setArguments(info);
    }


}