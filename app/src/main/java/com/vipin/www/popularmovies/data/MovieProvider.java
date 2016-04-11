package com.vipin.www.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.UnsupportedSchemeException;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by user on 4/7/2016.
 */
public class MovieProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher=buildUriMatcher();
    private MovieDbHelper dbHelper;

    static final int MOVIE=1;
    static final int MOVIE_WITH_ID=2;


    static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
        final String authority=MovieContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        uriMatcher.addURI(authority, MovieContract.PATH_MOVIE+"/#", MOVIE_WITH_ID);

        return uriMatcher;
    }


    @Override
    public boolean onCreate() {
        dbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;

        switch(sUriMatcher.match(uri)){
            case MOVIE: {
                retCursor = dbHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case MOVIE_WITH_ID:{
                retCursor=dbHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID+ " =? ",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            }
            default: throw new UnsupportedOperationException("Unknown uri: " + uri);


        }
        retCursor.setNotificationUri(getContext().getContentResolver(),uri);


        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {


        switch(sUriMatcher.match(uri)){
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_WITH_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri : "+uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db= dbHelper.getWritableDatabase();
        Uri returnUri;

        switch(sUriMatcher.match(uri)){
            case MOVIE:
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri : "+uri);

        }
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;

    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {

                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase db=dbHelper.getWritableDatabase();
        int rowsDeleted;
        if(selection==null) selection="1";

        switch(sUriMatcher.match(uri)){
            case MOVIE:
                rowsDeleted=db.delete(MovieContract.MovieEntry.TABLE_NAME,selection,selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri : "+uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
