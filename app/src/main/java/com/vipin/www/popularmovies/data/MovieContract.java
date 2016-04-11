package com.vipin.www.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by user on 4/7/2016.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY="com.vipin.www.popularmovies";

    public static final Uri BASE_URI= Uri.parse("content://"+CONTENT_AUTHORITY);

    public static final String PATH_MOVIE="movie";



    public static final class MovieEntry implements BaseColumns{

        public static final Uri CONTENT_URI=BASE_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE= ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE= ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_MOVIE;

        public static final String TABLE_NAME="movie";

        public static final String COLUMN_MOVIE_ID="movie_id";
        public static final String COLUMN_TITLE="movie_title";
        public static final String COLUMN_MOVIE_OVERVIEW="movie_overview";
        public static final String COLUMN_ORIGINAL_TITLE="movie_original_title";
        public static final String COLUMN_POSTER_PATH="movie_poster_path";
        public static final String COLUMN_BACKDROP_PATH="movie_backdrop_path";
        public static final String COLUMN_VOTE_AVERAGE="movie_vote_average";
        public static final String COLUMN_POPULARITY="movie_popularity";
        public static final String COLUMN_RELEASE_DATE="movie_release_date";




        public static Uri buildMovieUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

    }
}
