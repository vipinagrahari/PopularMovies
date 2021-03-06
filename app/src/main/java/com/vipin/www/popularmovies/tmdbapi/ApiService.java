package com.vipin.www.popularmovies.tmdbapi;


import com.google.gson.JsonObject;
import com.vipin.www.popularmovies.data.model.Discover;
import com.vipin.www.popularmovies.data.model.Movie;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by vipin on 20-12-2015.
 */
public interface ApiService {

    String API_BASE_URL = "https://api.themoviedb.org/3/";
    String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    String RES_POSTER = "w342";
    String RES_BACKDROP = "w780";
    String POPULARITY = "popular";
    String RATING = "top_rated";


    @GET("movie/{id}")
    Call<Movie> getMovie(@Path("id") int id, @Query("api_key") String apiKey);

    @GET("movie/{sort_by}")
    Call<Discover> discoverMovies(@Path("sort_by") String sortBy, @Query("api_key") String apiKey, @Query("page") int page);

    @GET("movie/{id}/reviews")
    Call<JsonObject> getReview(@Path("id") int id, @Query("api_key") String apiKey);

    @GET("movie/{id}/videos")
    Call<JsonObject> getTrailers(@Path("id") int id, @Query("api_key") String apiKey);

}
