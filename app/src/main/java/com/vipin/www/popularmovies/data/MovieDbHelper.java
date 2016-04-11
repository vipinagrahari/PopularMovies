package com.vipin.www.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.vipin.www.popularmovies.data.MovieContract.MovieEntry;
import com.vipin.www.popularmovies.data.model.Movie;

/**
 * Created by user on 4/7/2016.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "movies.db";



    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("DATABASE ON CREATE");

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE "+ MovieEntry.TABLE_NAME +"("+
                MovieEntry._ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                MovieEntry.COLUMN_MOVIE_ID +" INTEGER UNIQUE NOT NULL, "+
                MovieEntry.COLUMN_TITLE +" STRING NOT NULL, "+
                MovieEntry.COLUMN_ORIGINAL_TITLE+" STRING, "+
                MovieEntry.COLUMN_BACKDROP_PATH+" STRING, "+
                MovieEntry.COLUMN_MOVIE_OVERVIEW+" STRING, "+
                MovieEntry.COLUMN_POSTER_PATH+" STRING, "+
                MovieEntry.COLUMN_POPULARITY+" REAL, "+
                MovieEntry.COLUMN_VOTE_AVERAGE+" REAL, "+
                MovieEntry.COLUMN_RELEASE_DATE+" STRING);";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ MovieEntry.TABLE_NAME);
        onCreate(db);

    }
}
