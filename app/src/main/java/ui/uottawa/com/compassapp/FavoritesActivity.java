package ui.uottawa.com.compassapp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.CursorAdapter;
import android.widget.ListView;


/**
 * Created by joesi on 2016-04-07.
 * For uiprojectSEG3125
 */
public class FavoritesActivity extends AppCompatActivity {
    static CursorAdapter dataAdapter;
    static ListView listview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        helperDatabase db = helperDatabase.getInstance(getApplicationContext());


        Cursor favs_cursor = db.getAllFavorites();

        // Create an instance of our custom adapter.
        dataAdapter = new AdapterFavorites(getApplicationContext(), favs_cursor, 0);

        // Get the view where to display
        listview = (ListView) findViewById(R.id.listView);

        // assign adapter to ListView
        listview.setAdapter(dataAdapter);
    }

    public static void refreshAdapter(){
        dataAdapter.notifyDataSetChanged();
        listview.invalidate();
        listview.invalidateViews();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // go to previous screen when app icon in action bar is clicked
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
