package ui.uottawa.com.compassapp;

/**
 * Created by joesi on 2016-03-20.
 * For uiprojectSEG3125
 */

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

public class NumberPickerPreference extends DialogPreference {

    private int value;
    private NumberPicker numberPicker;
    private int DEFAULT_VALUE;
    helperPreferences shPrefs;
    private int dialogId;
    private final int MAX_SEARCH_DISTANCE_DIALOG = 0;
    private final int MAX_NUMBER_RESULTS_DIALOG = 1;

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        Activity activity = (Activity) context;
        shPrefs = new helperPreferences(activity);
        DEFAULT_VALUE = 10;
        setDialogLayoutResource(R.layout.number_pref);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);

        //gives the string resource id
        String strResId = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "title");

        //compare to know which dialog we have
        if (strResId.equals("@"+String.valueOf(R.string.max_search_distance_title))) {
            dialogId = MAX_SEARCH_DISTANCE_DIALOG;
        } else if (strResId.equals("@"+String.valueOf(R.string.max_number_results_title))) {
            dialogId = MAX_NUMBER_RESULTS_DIALOG;

        }

        setDialogIcon(null);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {

        if (positiveResult) {
            if (dialogId == MAX_NUMBER_RESULTS_DIALOG) {
                persistInt(numberPicker.getValue());
                shPrefs.SavePreferences(Constants.SHPREF_MAX_NUMBER_RESULTS, String.valueOf(numberPicker.getValue()));

            } else if (dialogId == MAX_SEARCH_DISTANCE_DIALOG) {
                persistInt(numberPicker.getValue());
                shPrefs.SavePreferences(Constants.SHPREF_MAX_SEARCH_DISTANCE, String.valueOf(numberPicker.getValue()));
            }
        }
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            // Restore existing state
            value = this.getPersistedInt(DEFAULT_VALUE);
        } else {
            // Set default state from the XML attribute
            value = 0;
            persistInt(value);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInteger(index, DEFAULT_VALUE);
    }

    @Override
    protected View onCreateDialogView() {

        int max = 100;
        int min = 1;

        LayoutInflater inflater =
                (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.number_pref, null);

        numberPicker = (NumberPicker) view.findViewById(R.id.number_picker);

        // Initialize state
        numberPicker.setMaxValue(max);
        numberPicker.setMinValue(min);
        numberPicker.setWrapSelectorWheel(false);
        //set default value
        if (this.getPersistedInt(DEFAULT_VALUE) == 0) {
            numberPicker.setValue(10);

        } else {
            numberPicker.setValue(this.getPersistedInt(DEFAULT_VALUE));
        }

        return view;
    }


    //  This code copied from android's settings guide.

    private static class SavedState extends BaseSavedState {
        // Member that holds the setting's value
        // Change this data type to match the type saved by your Preference
        int value;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel source) {
            super(source);
            // Get the current preference's value
            value = source.readInt();  // Change this to read the appropriate data type
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            // Write the preference's value
            dest.writeInt(value);  // Change this to write the appropriate data type
        }

        // Standard creator object using an instance of this class
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {

                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }


    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        // Check whether this Preference is persistent (continually saved)
        if (isPersistent()) {
            // No need to save instance state since it's persistent, use superclass state
            return superState;
        }

        // Create instance of custom BaseSavedState
        final SavedState myState = new SavedState(superState);
        // Set the state's value with the class member that holds current setting value
        myState.value = value;
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        // Check whether we saved the state in onSaveInstanceState
        if (state == null || !state.getClass().equals(SavedState.class)) {
            // Didn't save the state, so call superclass
            super.onRestoreInstanceState(state);
            return;
        }

        // Cast state to custom BaseSavedState and pass to superclass
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());

        // Set this Preference's widget to reflect the restored state
        numberPicker.setValue(myState.value);
    }
}