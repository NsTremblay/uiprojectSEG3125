package ui.uottawa.com.compassapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by joesi on 2016-04-07.
 * For uiprojectSEG3125
 */
public class AdapterFavorites extends CursorAdapter {

    private LayoutInflater mInflater;

    private helperDatabase db;

    /**************************************************************************
     * @brief
     * @param
     * @return
     **************************************************************************/
    public AdapterFavorites(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        db = helperDatabase.getInstance(context);
    }

    /**************************************************************************
     * @brief
     * @param
     * @return
     **************************************************************************/
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        if(cursor.getPosition()%2==1) {
            view.setBackgroundColor(context.getResources().getColor(R.color.bg_odd));
        } else {
            view.setBackgroundColor(context.getResources().getColor(R.color.bg_even));
        }
        TextView textview;
        String number;

        textview = (TextView) view.findViewById(R.id.fav_rating);
        number = cursor.getString(cursor.getColumnIndex(helperDatabase.KEY_RATING));
        textview.setText(number);

        textview = (TextView) view.findViewById(R.id.fav_name);
        number = cursor.getString(cursor.getColumnIndex(helperDatabase.KEY_NAME));
        textview.setText(number);

        // edit button
        // delete button
        Button delete_button = (Button) view.findViewById(R.id.delete_fav);

        final int coffee_id = cursor.getInt(cursor.getColumnIndex(helperDatabase.KEY_COFFEE_ID));

        delete_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.deleteFavorite(String.valueOf(coffee_id));
                    FavoritesActivity.refreshAdapter();
                }
            });
    }

    /**************************************************************************
     * @brief
     * @param
     * @return
     **************************************************************************/
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(R.layout.favorites_row, parent, false);
    }


}
