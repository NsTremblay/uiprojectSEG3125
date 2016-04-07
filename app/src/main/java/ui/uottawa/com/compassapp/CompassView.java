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
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
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

public class CompassView extends View {


    private static final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int width = 0;
    private int height = 0;
    private float bearing; // rotation angle to North

    private Place[] coffeeShops;
    private helperAPIRequest APIRequest;
    private Activity activity;
    private Location currentLocation;
    private ArrayList<Button> coffeeButtons;
    private RelativeLayout parent;

    public CompassView(Context context) {
        super(context);
        activity = (Activity) context;
        initialize();

        //put all of the views we need around the circle

    }

    public CompassView(Context context, AttributeSet attr) {
        super(context, attr);
        activity = (Activity) context;
        initialize();
    }

    private void initialize() {

        parent = (RelativeLayout) findViewById(R.id.compassLayout);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        paint.setColor(Color.WHITE);
        paint.setTextSize(30);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DARKEN));
        APIRequest = helperAPIRequest.getInstance(activity);
        coffeeShops = APIRequest.getShops();
        coffeeButtons = new ArrayList<Button>();

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
        ViewGroup parentView = (ViewGroup)CompassView.this.getParent();

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
                //make the circles before Drawing them and store them
                for (int i = 0; i < coffeeShops.length; i++) {
                    if (coffeeShops[i] != null) {
                      /*  Button tempCoffee = new Button(activity);
                        int pow;
                        if (coffeeShops[i].getRating() < 3) {
                            pow = 4;
                        } else {
                            pow = 3;
                        }
                        tempCoffee.setLayoutParams(new LinearLayout.LayoutParams((int) Math.pow(coffeeShops[i].getRating(), pow), (int) Math.pow(coffeeShops[i].getRating(), pow
                        )));
                        tempCoffee.setBackgroundResource(R.drawable.round_button);
                        tempCoffee.setGravity(RelativeLayout.CENTER_IN_PARENT);
                        tempCoffee.setId(i);
                        tempCoffee.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                // Perform action on click
                                Log.i("Button was clicked", "Clicked Button ");
                            }
                        });
                        Log.i("Hey", "onSensorChanged: ");*/

                        //for (Place coffeeShop : coffeeShops) {
                        Location dest = new Location(coffeeShops[i].getName());
                        dest.setLatitude(coffeeShops[i].getLatitude());
                        dest.setLongitude(coffeeShops[i].getLongitude());

                        bearingLoc = currentLocation.bearingTo(dest);
                        //Log.d("bearingLoc1:", String.valueOf(bearingLoc));
                        double angleRadians = Math.toRadians(bearingLoc) + Math.toRadians(rotation - 90);

                        double x = Math.cos(angleRadians);
                        double y = Math.sin(angleRadians);
                        Button tempCoffee = new Button(activity);
                        int pow;
                        if (coffeeShops[i].getRating() < 3) {
                            pow = 4;
                        } else {
                            pow = 3;
                        }
                        tempCoffee.setLayoutParams(new LinearLayout.LayoutParams((int) Math.pow(coffeeShops[i].getRating(), pow), (int) Math.pow(coffeeShops[i].getRating(), pow
                        )));
                        tempCoffee.setBackgroundResource(R.mipmap.ic_launcher);
                        tempCoffee.setGravity(RelativeLayout.CENTER_IN_PARENT);
                        tempCoffee.setId(i);
                        tempCoffee.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                // Perform action on click
                                Log.i("Button was clicked", "Clicked Button ");
                            }
                        });
                        coffeeButtons.add(tempCoffee);

                        if (i <= coffeeButtons.size()) {
                            Button currentbutton = coffeeButtons.get(i);
                            currentbutton.setTranslationX(cxCompass + (radiusCompass * (float) x) - currentbutton.getWidth() / 2);
                            currentbutton.setTranslationY(cyCompass + (radiusCompass * (float) y) - currentbutton.getHeight() / 2);
                            canvas.drawText(coffeeShops[i].getName(), cxCompass + (radiusCompass * (float) x), cyCompass + (radiusCompass * (float) y), paint);
                            if(currentbutton.getTranslationX()!=0&&currentbutton.getTranslationY()!=0) {
                                parentView.addView(tempCoffee);
                            }
                        }

                    }
                }
            } else {
                Toast.makeText(activity, "Acquiring GPS Position", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

}