package com.acubeapps.lockscreen.shortcuts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajitesh.shukla on 7/14/16.
 */
public class AppListStore extends SQLiteOpenHelper {

    public static final String APP_LIST_DB = "applistdb";
    public static final int DATABASE_VERSION = 1;
    public static final String APP_LIST_TABLE = "applisttable";
    public static final String APP_ID = "appid";
    public static final String PACKAGE_NAME = "packagename";

    public AppListStore(Context context) {
        super(context, APP_LIST_DB, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + APP_LIST_TABLE + "("
                + APP_ID + " INTEGER PRIMARY KEY,"
                + PACKAGE_NAME + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void removePackage(String packageName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(APP_LIST_TABLE, PACKAGE_NAME + "=?", new String[] { packageName });
        db.close();
    }

    public void addPackage(AppInfo appInfo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PACKAGE_NAME, appInfo.getPackageName());
        db.insert(APP_LIST_TABLE, null, values);
        db.close();
    }

    public int getPackageCount() {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + APP_LIST_TABLE;
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor.getCount();
    }

    public List<String> getPackageNameList() {
        List<String> packageNameList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + APP_LIST_TABLE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                packageNameList.add(cursor.getString(cursor.getColumnIndex(PACKAGE_NAME)));
            } while (cursor.moveToNext());
        }
        return packageNameList;
    }
}
