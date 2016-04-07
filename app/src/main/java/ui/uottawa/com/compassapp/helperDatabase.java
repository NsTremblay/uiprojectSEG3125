package ui.uottawa.com.compassapp;

/**
 * Created by joesi on 2016-04-06.
 * For uiprojectSEG3125
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by Pierrot on 2014-11-19.
 */

/**
 * Simple notes database access helper class. Defines the basic CRUD operations
 * for the notepad example, and gives the ability to list all notes as well as
 * retrieve or modify a specific note.
 * <p/>
 * This has been improved from the first version of this tutorial through the
 * addition of better error handling and also using returning a Cursor instead
 * of using a collection of inner classes (which is less scalable and not
 * recommended).
 */
public class helperDatabase extends SQLiteOpenHelper {

    private static helperDatabase sInstance = null;


    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Version
    private static int db_upgraded;

    // Database Name
    private static final String DATABASE_NAME = "HockeyStats";

    //Table Names
    public static final String TABLE_FAVORITES = "favorites";

    // Common column names
    public static final String KEY_ROWID = "_id";
    public static final String KEY_NAME = "_id";
    public static final String KEY_COFFEE_ID = "_id";


    /**
     * ***********************************************************************
     *
     * @brief GAMES table create statement
     * @param
     * @return ************************************************************************
     */
    private static final String CREATE_TABLE_FAVORITES = "CREATE TABLE "
            + TABLE_FAVORITES + "(" +
            KEY_ROWID + " INTEGER PRIMARY KEY," +
            KEY_COFFEE_ID + " TEXT," +
            KEY_NAME + " TEXT" +
            ")";

    public static helperDatabase getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new helperDatabase(context.getApplicationContext());
        }
        return sInstance;
    }

    /**************************************************************************
     * @brief
     * @param
     * @return
     **************************************************************************/
    /**
     * Constructor should be private to prevent direct instantiation.
     * make call to static method "getInstance()" instead.
     */
    private helperDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db_upgraded = 0;
    }

    /**
     * ***********************************************************************
     *
     * @param
     * @return ************************************************************************
     * @brief
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_FAVORITES);
    }

    /**
     * ***********************************************************************
     *
     * @param
     * @return ************************************************************************
     * @brief
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db_upgraded = 1;
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);

        // create new tables
        onCreate(db);

    }

    /**
     * ***********************************************************************
     *
     * @param
     * @return ************************************************************************
     * @brief
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db_upgraded = 1;
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);

        // create new tables
        onCreate(db);
    }


    /**
     * ***********************************************************************
     *
     * @param
     * @return ************************************************************************
     * @brief
     */
    public void deleteAllTables() {

        SQLiteDatabase SQLiteDb = this.getWritableDatabase();

        SQLiteDb.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        SQLiteDb.close();
    }

    /**
     * ***********************************************************************
     *
     * @param
     * @return ************************************************************************
     * @brief
     */
    public boolean clearAllTables() {

        SQLiteDatabase SQLiteDb = this.getWritableDatabase();

        int doneDelete = 0;
        doneDelete = SQLiteDb.delete(TABLE_FAVORITES, null, null);
        SQLiteDb.close();
        return doneDelete > 0;
    }

    /**
     * ***********************************************************************
     * @param
     * @return
     * @brief
     * ************************************************************************
     */
    public int getDbUpgraded() {
        return db_upgraded;
    }

    public boolean isFavoriteInDb(String coffee_id) throws SQLException {

        SQLiteDatabase SQLiteDb = this.getReadableDatabase();
        Cursor mCursor = SQLiteDb.query(true,
                TABLE_FAVORITES,
                null,
                KEY_COFFEE_ID + "=?",
                new String[]{coffee_id},
                null,
                null,
                null,
                null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        // db read only, no need to close the sql db (but cursor will need to be closed)
        boolean isin = mCursor.getCount() > 0;
        mCursor.close();
        return isin;
    }

    /**
     *
     * @param
     * @return
     * @brief
     */
    public long addFavorite(String coffee_id,
                        String name) {

        SQLiteDatabase SQLiteDb = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();

        initialValues.put(KEY_COFFEE_ID, coffee_id);
        initialValues.put(KEY_NAME, name);

        long id = SQLiteDb.insert(TABLE_FAVORITES, null, initialValues);
        SQLiteDb.close();
        return id;
    }
}

