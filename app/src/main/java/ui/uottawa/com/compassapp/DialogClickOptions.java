package ui.uottawa.com.compassapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.database.Cursor;
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

    private List<Integer> homeplayerids_list;
    private List<Integer> pen_codes_listid;

    private Integer player_serverid = 0;
    private Integer player_clientid = 0;
    private int pen_code_id = 0;
    private String pen_codeuniqueid = "0";
    private String code = "";
    private int lenght = 0;
    private String code2 = "";
    private int lenght2 = 0;
    private String code3 = "";
    private int lenght3 = 0;
    private String player_nb = "";
    private Integer pen_period = 0;
    private Integer pen_minutes = 0;
    private Integer pen_seconds = 0;
    private int pen_mode = 0;
    private String from = "";
    private int pen_highlight = 0;
    private int pen_num_codes = 0;
    private helperPreferences sharedPrefs;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        sharedPrefs = new helperPreferences(getActivity());

        //Get the bundle that says where the dialog is created from
        Bundle fromBundle = getArguments();
        if(fromBundle!=null) {
            from = fromBundle.getString("from");
        }




      /*  // TITLE
        TextView title = new TextView(getActivity());
        title.setText(R.string.gs_addpen_home_title);
        title.setTextSize((int) getResources().getDimension(R.dimen.dialog_title_txtsize));
        title.setTextColor(getResources().getColor(R.color.White));
        title.setBackgroundColor(getResources().getColor(R.color.Gray4));

        // DIALOG ACTIONS
        builder.setMessage("")
                .setCustomTitle(title)
                .setPositiveButton(
                        Html.fromHtml("<big><b>" + getString(R.string.gs_addgoal_add) + "</b></big>"),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {


                                // open the shared preferences
                                String selected_gameid = sharedPrefs.GetPreferences(Constants.SHPREF_SELECTED_GAME_ID);
                                helperDatabase db = helperDatabase.getInstance(getActivity());
                                String teamid = db.fetchStringParameter(helperDatabase.TABLE_GAMES,
                                        helperDatabase.KEY_ROWID,
                                        selected_gameid,
                                        helperDatabase.KEY_GAME_HOMETEAM_ID);

                                if(time_mins_textfield.getText().toString().equals("")){
                                    pen_minutes = 0;
                                }else{
                                    pen_minutes = Integer.valueOf(time_mins_textfield.getText().toString());
                                }

                                if(time_secs_textfield.getText().toString().equals("")){
                                    pen_seconds = 0;
                                }
                                else{
                                    pen_seconds = Integer.valueOf(time_secs_textfield.getText().toString());
                                }

                                if ((chrono_mode.equals(Constants.CHRONOMODE_ELAPSEDTIME)) &&
                                        (chrono_mode_calc_elapsed_time.equals(
                                                Constants.CHRONO_ELAPSED_AUTOCALC_ON))) {
                                    int pen_time_seconds = pen_minutes * 60 + pen_seconds;
                                    String[] pen_time = helperGame.computeChronoTime(
                                            pen_time_seconds,
                                            pen_period,
                                            getActivity());

                                    if (pen_time[0].equals("Invalid")) {
                                        Toast.makeText(getActivity(),
                                                R.string.toast_timeinvalid,
                                                Toast.LENGTH_LONG).show();
                                        return;
                                    }

                                    pen_minutes = Integer.valueOf(pen_time[0]);
                                    pen_seconds = Integer.valueOf(pen_time[1]);
                                }

                                if(pen_mode == Constants.PENMODE_HOCKEYQUEBEC_CODES){
                                }else if(pen_mode == Constants.PENMODE_DURATION_ONLY){
                                    lenght = Integer.valueOf(pen_lenght.getText().toString());
                                }else if(pen_mode == Constants.PENMODE_DURATION_TEXT){
                                    code = text_field.getText().toString();
                                    lenght = Integer.valueOf(pen_lenght.getText().toString());
                                }




                                } else {
                                    Toast.makeText(getActivity(),
                                            R.string.toast_penidinvalid,
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                .

                        setNegativeButton(
                                Html.fromHtml("<big><b>" + getString(R.string.gs_addgoal_cancel)

                                        + "</b></big>"),
                                new DialogInterface.OnClickListener()

                                {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User cancelled the dialog
                                        Toast.makeText(getActivity(),
                                                R.string.toast_cancel,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });*/
        // Create the AlertDialog object and return it
        return builder.create();
    }

}

