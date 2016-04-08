package ui.uottawa.com.compassapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by joesi on 2016-04-06.
 * For uiprojectSEG3125
 */
public class DialogClickOptions extends DialogFragment {

    private String id = "";
    private String lon = "";
    private String lat = "";
    private String name = "";

    CharSequence options[] = new CharSequence[]{"Set as destination", "Add to favorites", "Open in Google Maps"};

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final helperPreferences sharedPrefs = new helperPreferences(getActivity());
        final helperDatabase db = helperDatabase.getInstance(getActivity());

        //Get the bundle that says where the dialog is created from
        Bundle fromBundle = getArguments();
        if (fromBundle != null) {
            id = fromBundle.getString("id");
            lon = fromBundle.getString("lon");
            lat = fromBundle.getString("lat");
            name = fromBundle.getString("name");
        }
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(name);

        // DIALOG ACTIONS
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        sharedPrefs.SavePreferences(Constants.SHPREF_DESTINATION_LONGITUDE, lon);
                        sharedPrefs.SavePreferences(Constants.SHPREF_DESTINATION_LATITUDE, lat);
                        break;
                    case 1:
                        db.addFavorite(id, name);
                        break;
                    case 2:
                        dialog.dismiss();
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?daddr=" + lat + "," + lon));
                        startActivity(intent);
                        break;
                }
            }
        });

        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}

