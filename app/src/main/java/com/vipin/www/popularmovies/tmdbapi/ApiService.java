package com.vipin.www.popularmovies.tmdbapi;


import com.vipin.www.popularmovies.model.Discover;
import com.vipin.www.popularmovies.model.Movie;

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
    String POPULARITY_DESC = "popularity.desc";
    String RATING_DESC = "vote_average.desc";


    @GET("movie/{id}")
    Call<Movie> getMovie(@Path("id") int id, @Query("api_key") String apiKey);

    @GET("discover/movie")
    Call<Discover> discoverMovies(@Query("api_key") String apiKey, @Query("page") int page, @Query("sort_by") String sortBy);

}
