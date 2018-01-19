package com.hcodestudio.movieapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.hcodestudio.movieapp.model.Movie;

import java.util.ArrayList;
import java.util.List;

import static android.os.Build.ID;

/**
 * Created by hassan on 11/25/2017.
 */

public class FavouriteDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "favourite.db";
    private static final int DATABASE_VERSION = 2;
    private static final String LOG_TAG = "FAVOURITE";

    SQLiteOpenHelper openHelper;
    SQLiteDatabase db;


    public FavouriteDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void open(){
        Log.i(LOG_TAG, "Database opened");
        db = openHelper.getWritableDatabase();
    }

    public void close(){
        Log.i(LOG_TAG, "Database closed");
        openHelper.close();
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_FAVOURITE_TABLE = "CREATE TABLE " +  FavouriteContract.FavouriteEntry.TABLE_NAME + " (" +
                FavouriteContract.FavouriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavouriteContract.FavouriteEntry.COLUMN_MOVIEID + " INTEGER NOT NULL, " +
                FavouriteContract.FavouriteEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                FavouriteContract.FavouriteEntry.COLUMN_USERRATING + " REAL NOT NULL, " +
                FavouriteContract.FavouriteEntry.COLUMN_POSTERPATH + " TEXT NOT NULL, " +
                FavouriteContract.FavouriteEntry.COLUMN_PLOTSYNOPSIS + " TEXT NOT NULL " + "); ";
        sqLiteDatabase.execSQL(SQL_CREATE_FAVOURITE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavouriteContract.FavouriteEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void addFavorite(Movie movie){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(FavouriteContract.FavouriteEntry.COLUMN_MOVIEID, movie.getId());
        contentValues.put(FavouriteContract.FavouriteEntry.COLUMN_TITLE, movie.getOriginalTitle());
        contentValues.put(FavouriteContract.FavouriteEntry.COLUMN_USERRATING, movie.getVoteAverage());
        contentValues.put(FavouriteContract.FavouriteEntry.COLUMN_POSTERPATH, movie.getPosterPath());
        contentValues.put(FavouriteContract.FavouriteEntry.COLUMN_PLOTSYNOPSIS, movie.getOverview());

        db.insert(FavouriteContract.FavouriteEntry.TABLE_NAME, null, contentValues);
        db.close();
    }

    public void deleteFavorite(int movieId){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(FavouriteContract.FavouriteEntry.TABLE_NAME,
                FavouriteContract.FavouriteEntry.COLUMN_MOVIEID + " = " + movieId, null);
    }

    public List<Movie> getAllFavorite(){
        String[] columns = {
                FavouriteContract.FavouriteEntry._ID,
                FavouriteContract.FavouriteEntry.COLUMN_MOVIEID,
                FavouriteContract.FavouriteEntry.COLUMN_TITLE,
                FavouriteContract.FavouriteEntry.COLUMN_USERRATING,
                FavouriteContract.FavouriteEntry.COLUMN_POSTERPATH,
                FavouriteContract.FavouriteEntry.COLUMN_PLOTSYNOPSIS
        };

        String sortOrder = FavouriteContract.FavouriteEntry._ID + " ASC";
        List<Movie> favoriteList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(FavouriteContract.FavouriteEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                sortOrder);

        if (cursor.moveToFirst()){
            do {
                Movie movie = new Movie();
                movie.setId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(FavouriteContract.FavouriteEntry.COLUMN_MOVIEID))));
                movie.setOriginalTitle(cursor.getString(cursor.getColumnIndex(FavouriteContract.FavouriteEntry.COLUMN_TITLE)));
                movie.setVoteAverage(Double.valueOf(cursor.getString(cursor.getColumnIndex(FavouriteContract.FavouriteEntry.COLUMN_USERRATING))));
                movie.setPosterPath(cursor.getString(cursor.getColumnIndex(FavouriteContract.FavouriteEntry.COLUMN_POSTERPATH)));
                movie.setOverview(cursor.getString(cursor.getColumnIndex(FavouriteContract.FavouriteEntry.COLUMN_PLOTSYNOPSIS)));

                favoriteList.add(movie);

            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return favoriteList;
    }
}
