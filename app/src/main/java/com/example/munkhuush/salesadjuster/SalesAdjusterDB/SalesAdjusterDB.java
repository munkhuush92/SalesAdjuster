package com.example.munkhuush.salesadjuster.SalesAdjusterDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.munkhuush.salesadjuster.Item.ItemContent;
import com.example.munkhuush.salesadjuster.R;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by Munkhuush on 9/13/17.
 */

public class SalesAdjusterDB {
    public static final int DB_VERSION = 3;
    public static final String DB_NAME = "Favorite.db";
    private static final String COURSE_TABLE = "Favorite";
    private FavoriteDBHelper mFavoriteDBHelper;
    private SQLiteDatabase mSQLiteDatabase;

    public SalesAdjusterDB(Context context) {
        mFavoriteDBHelper = new FavoriteDBHelper(
                context, DB_NAME, null, DB_VERSION);
        mSQLiteDatabase = mFavoriteDBHelper.getWritableDatabase();
    }



    /**
     * Inserts the course into the local sqlite table. Returns true if successful, false otherwise.
     * @param favoriteAmount
     * @param favoritePercent
     * @param favoriteName
     * @return true or false
     */
    public boolean insertFavorite(int id,  String favoriteAmount, String favoritePercent, String favoriteName, String favoriteType) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("favoriteID", id);
        contentValues.put("favoriteName", favoriteName);
        contentValues.put("favoriteAmount", favoriteAmount);

        contentValues.put("favoriteType", favoriteType);
        contentValues.put("favoritePercent", favoritePercent);

        Log.i("SYSTEM", contentValues.toString());
        long rowId = mSQLiteDatabase.insert("Favorite", null, contentValues);
        return rowId != -1;
    }
    public boolean updateFavoriteByID(int id, String favoriteAmount, String favoritePercent, String favoriteName, String favoriteType){
        ContentValues contentValues = new ContentValues();
        contentValues.put("favoriteID", id);
        if(favoriteAmount!=null){   contentValues.put("favoriteAmount", favoriteAmount);        }
        if(favoritePercent!=null){   contentValues.put("favoritePercent", favoritePercent);      }
        contentValues.put("favoriteName", favoriteName);
        contentValues.put("favoriteType", favoriteType);
        long rowId =mSQLiteDatabase.update(COURSE_TABLE, contentValues, "favoriteID="+id,null);
        Log.i("EXECUTED", ""+rowId);
        return rowId!=-1;
    }
    public boolean updateAll(){
        boolean noError = false;
        List<ItemContent.SaleItem> items = getCourses();
        for(int i = 0; i<items.size(); i++){

            noError =updateFavoriteByID(items.get(i).favoriteID ,items.get(i).favoriteAmount, items.get(i).favoritePercent,items.get(i).favoriteName, items.get(i).favoriteType);

        }
        return noError;
    }

    public void closeDB() {
        mSQLiteDatabase.close();
    }

    /**
     * Delete all the data from the COURSE_TABLE
     */
    public void deleteFavorites() {
        mSQLiteDatabase.delete(COURSE_TABLE, null, null);
    }



    /**
     * Returns the list of courses from the local Course table.
     * @return list
     */
    public List<ItemContent.SaleItem> getCourses() {

        String[] columns = {
                "favoriteID", "favoritePercent", "favoriteName", "favoriteAmount", "favoriteType"
        };

        Cursor c = mSQLiteDatabase.query(
                COURSE_TABLE,  // The table to query
                columns,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        c.moveToFirst();
        List<ItemContent.SaleItem> list = new ArrayList<ItemContent.SaleItem>();
        for (int i=0; i<c.getCount(); i++) {
            String id = c.getString(0);
            String favoriteAmount = c.getString(1);
            String favoritePercent = c.getString(2);
            String favoriteName = c.getString(3);
            String favoriteType = c.getString(4);
            Log.i("IDER", " "+id + favoriteAmount +" "+ favoritePercent+" "+favoriteName +" "+favoriteType);
            if(id!=null){
                ItemContent.SaleItem favorite = new ItemContent.SaleItem(Integer.parseInt(id), favoriteAmount, favoritePercent, favoriteName, favoriteType);
                list.add(favorite);
                c.moveToNext();
            }

        }

        return list;
    }





    class FavoriteDBHelper extends SQLiteOpenHelper{

        private final String CREATE_COURSE_SQL;

        private final String DROP_COURSE_SQL;

        public FavoriteDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
            CREATE_COURSE_SQL = context.getString(R.string.CREATE_FAVORITE_SQL);
            Log.i("CREATED", CREATE_COURSE_SQL);
            DROP_COURSE_SQL = context.getString(R.string.DROP_FAVORITE_SQL);

        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            Log.i("CREATED!!", CREATE_COURSE_SQL);
            sqLiteDatabase.execSQL(CREATE_COURSE_SQL);
//            insert(0, "107", "-20.75", "Summer Sale" , "percent");
//            insert(1, "101", "-10.75", "Happy Hour" , "percent");
//            insert(2, "102", "1.75", "Extra Charge", "amount" );
//            insert(3, "103", "-14.75", "Military Discount", "percent" );
//            insert(4, "104", "12.25", "Custom Tax" , "percent");
//            insert(5, "105", "-10.00", "10% discount" , "percent");
//            insert(6, "106", "-0.75", "Poop discount" , "percent");
//            insert(7, "107", "15.75", "Winter raise amount" , "amount");
//            insert(8, "107", "15.75", "Winter raise amount", "amount" );
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL(DROP_COURSE_SQL);
            onCreate(sqLiteDatabase);
        }
//        public boolean insert(int id, String favoriteName, String favoriteAmount, String favoritePercent, String favoriteType){
//            ContentValues contentValues = new ContentValues();
//            contentValues.put("favoriteID", id);
//            contentValues.put("favoriteName", favoriteName);
//            contentValues.put("favoriteAmount", favoriteAmount);
//
//            contentValues.put("favoriteType", favoriteType);
//            contentValues.put("favoritePercent", favoritePercent);
//
//            long rowId = mSQLiteDatabase.insert("Favorite", null, contentValues);
//            return rowId!=-1;
//        }

    }
}
