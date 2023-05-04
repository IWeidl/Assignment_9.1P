package com.iweidl.assignment71p.sqlitehelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LostFoundDBHelper extends SQLiteOpenHelper {
    // Constructor for the database helper
    public LostFoundDBHelper(@Nullable Context context) {
        super(context, Util.DATABASE_NAME, null, Util.DATABASE_VERSION);
    }

    // Build the command to create the table from Util's properties
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE_COMMAND = "CREATE TABLE "
                + Util.TABLE_NAME + "("
                + Util.ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Util.STATUS + " TEXT,"
                + Util.NAME + " TEXT,"
                + Util.PHONE + " TEXT,"
                + Util.DESCRIPTION + " TEXT,"
                + Util.DATE + " DATE,"
                + Util.LOCATION + " TEXT"
                + ")";

        sqLiteDatabase.execSQL(CREATE_TABLE_COMMAND);
    }

    // This is called whenever Util.DATABASE_VERSION is incremented
    // Basically clean slates the DB between upgrades
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Util.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    // Inserst item into the database, taking special care to handle the date field correctly
    public boolean insertItem(String status, String name, String phone, String description, Date date, String location) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(Util.STATUS, status);
        contentValues.put(Util.NAME, name);
        contentValues.put(Util.PHONE, phone);
        contentValues.put(Util.DESCRIPTION, description);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        contentValues.put(Util.DATE, dateFormat.format(date));

        contentValues.put(Util.LOCATION, location);

        long result = db.insert(Util.TABLE_NAME, null, contentValues);
        db.close();
        return result != -1;
    }

    // Delete an item by its primary key
    public void deleteItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Util.TABLE_NAME, Util.ITEM_ID + "=?", new String[]{String.valueOf(id)});
    }

    // Get an item from the database using its ID
    public LostFoundItem getItem(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Util.TABLE_NAME,
                new String[]{Util.ITEM_ID, Util.STATUS, Util.NAME, Util.PHONE, Util.DESCRIPTION, Util.DATE, Util.LOCATION},
                Util.ITEM_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            // Extra handling for the date field, can never be too safe when it comes to date validation
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = null;
            try {
                date = dateFormat.parse(cursor.getString(5));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            LostFoundItem item = new LostFoundItem(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    date,
                    cursor.getString(6)
            );
            cursor.close();
            return item;
        }
        return null;
    }

    // Same as getItem(), but instead gets all items in db, then iterates over them and returns them in a list
    public List<LostFoundItem> getAllItems() {
        List<LostFoundItem> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + Util.TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date = null;
                try {
                    date = dateFormat.parse(cursor.getString(5));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                LostFoundItem item = new LostFoundItem(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        date,
                        cursor.getString(6)
                );

                items.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return items;
    }
}
