package com.vipin.www.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.vipin.www.popularmovies.data.MovieContract;
import com.vipin.www.popularmovies.data.model.Movie;

/**
 * Created by Vipin on 28-12-2015.
 */
public class Util {

    Context mContext;

    public Util(Context mContext) {
        this.mContext = mContext;
    }

    public boolean checkInternet() {
        ConnectivityManager connMgr = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();

    }

    public boolean saveMovie(Movie movie){
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID,movie.getId());
        movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE,movie.getTitle());
        movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,movie.getOriginalTitle());
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW,movie.getOverview());
        movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH,movie.getPosterPath());
        movieValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH,movie.getBackdropPath());
        movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE,movie.getReleaseDate());
        movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY,movie.getPopularity());
        movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,movie.getVoteAverage());



        mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, movieValues);


        return false;
    }

}
