package com.vipin.www.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;

import com.google.gson.JsonObject;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import com.vipin.www.popularmovies.data.MovieContract;
import com.vipin.www.popularmovies.data.model.Movie;
import com.vipin.www.popularmovies.tmdbapi.ApiService;
import com.vipin.www.popularmovies.tmdbapi.ServiceGenerator;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MovieDetailActivity extends AppCompatActivity {


    ImageView backDropImage;
    Movie movie;
    TextView tvMovieOverView;
    TextView tvReleaseDate;
    TextView tvPopularity;
    TextView tvUserRating;
    TextView tvReviewText;
    TextView tvReviewAuthor;
    CollapsingToolbarLayout ctl;
    LinearLayout llVideo;
    Target target;
    ApiService apiService;
    boolean reviewTextExpanded;
    boolean isFavorite;
    int primaryDark ;
    int primary ;
    FloatingActionButton fab;
    ShareActionProvider provider;
    String firstTrailer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        primaryDark=ContextCompat.getColor(MovieDetailActivity.this, R.color.colorPrimaryDark);
        primary= ContextCompat.getColor(MovieDetailActivity.this, R.color.colorPrimary);
        apiService = ServiceGenerator.createService(ApiService.class);
        movie = getIntent().getParcelableExtra("movie");
        getReviews();
        isFavorite=isFavorite();


        setContentView(R.layout.activity_movie_detail);
        getTrailers();

        ctl = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        backDropImage = (ImageView) ctl.findViewById(R.id.backdrop_image);

        ctl.setContentScrimColor(primary);
        ctl.setStatusBarScrimColor(primaryDark);
        backDropImage.setImageResource(R.drawable.placeholder);
        backDropImage.setColorFilter(primary, PorterDuff.Mode.DARKEN);

        loadBackdrop(movie.getBackdropPath());


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        tvMovieOverView = (TextView) findViewById(R.id.tv_overview);
        tvReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        tvPopularity = (TextView) findViewById(R.id.tv_popularity);
        tvUserRating = (TextView) findViewById(R.id.tv_rating);
        tvReviewAuthor=(TextView)findViewById(R.id.tv_review_author);
        tvReviewText=(TextView)findViewById(R.id.tv_review);

        fab=(FloatingActionButton) findViewById(R.id.fab_favorite);


        if(isFavorite){
            fab.setImageDrawable(ContextCompat.getDrawable(MovieDetailActivity.this,R.drawable.ic_favorite_white_24dp));
        }

        ctl.setTitle(movie.getOriginalTitle());
        tvMovieOverView.setText(movie.getOverview() == null ? "Not Available" : movie.getOverview());
        tvReleaseDate.setText(movie.getReleaseDate() == null ? "Not Available" : movie.getReleaseDate());
        tvPopularity.setText(String.format("%.2f", movie.getPopularity()));
        tvUserRating.setText(String.format("%.2f", movie.getVoteAverage()));


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scrolling,menu);
        MenuItem item = menu.findItem(R.id.menu_share);
        // Fetch and store ShareActionProvider
        provider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(firstTrailer!=null)
            provider.setShareIntent(getShareIntent());
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch(item.getItemId()){
            case R.id.menu_share:


                break;
            default: return false;


        }
        return super.onOptionsItemSelected(item);
    }

    private Intent getShareIntent(){

        Intent sendIntent=new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT,"https://www.youtube.com/watch?v="+firstTrailer);
        return sendIntent;

    }

    public void loadBackdrop(String backdropPath) {


        if (backdropPath == null) {

          //Do nothing

        } else {

            target = new Target() {
                @Override
                public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {

                    Palette palette = Palette.from(bitmap).generate();

                    ctl.setContentScrimColor(palette.getVibrantColor(primary));
                    ctl.setStatusBarScrimColor(palette.getDarkVibrantColor(primaryDark));
                    backDropImage.setImageBitmap(bitmap);
                    backDropImage.clearColorFilter();

                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                    Toast.makeText(MovieDetailActivity.this, "Failed to load backdrop image", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };

            Picasso.with(MovieDetailActivity.this)
                    .load(ApiService.BASE_IMAGE_URL + ApiService.RES_BACKDROP + backdropPath)
                    .into(target);
        }

    }

    public void getReviews(){


        Call<JsonObject> getReviewsCall = apiService.getReview(movie.getId(),getResources().getString(R.string.api_key));


        getReviewsCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response, Retrofit retrofit) {

                if (response.body() != null) {



                    JsonArray reviews=(JsonArray) response.body().get("results");
                    if(reviews.size()>0) {
                        tvReviewText.setText(((JsonObject) reviews.get(0)).get("content").getAsString());
                        tvReviewAuthor.setText(((JsonObject) reviews.get(0)).get("author").getAsString());
                    }
                    else{
                        tvReviewAuthor.setText(getResources().getString(R.string.message_no_reviews));
                        tvReviewText.setText("");
                    }



                }

            }

            @Override
            public void onFailure(Throwable t) {

            }


        });


    }

    public void getTrailers(){
        llVideo=(LinearLayout)findViewById(R.id.ll_trailers);

        Call<JsonObject> getTrailersCall=apiService.getTrailers(movie.getId(),getResources().getString(R.string.api_key));

        getTrailersCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response, Retrofit retrofit) {
                if (response.body() != null) {

                    JsonArray videos=(JsonArray) response.body().get("results");
                    firstTrailer=((JsonObject)videos.get(0)).get("key").getAsString();
                    invalidateOptionsMenu();

                    for(int i=0;i<videos.size();i++){
                        JsonObject object=(JsonObject) videos.get(i);
                        String name=object.get("name").getAsString();
                        final String id=object.get("key").getAsString();
                        if(name.toLowerCase().contains("trailer")) {
                            TextView tv = new TextView(MovieDetailActivity.this);
                            tv.setClickable(true);

                            tv.setText(name);
                            tv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    playVideo(id);
                                }
                            });


                            tv.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_media_play,0,0,0);
                            tv.setCompoundDrawablePadding(6);


                            tv.setPadding(24,6,0,6);

                            tv.setGravity(Gravity.CENTER_VERTICAL);

                            llVideo.addView(tv);


                        }

                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    public void toggleEllipsize(View v){
        if(reviewTextExpanded) tvReviewText.setMaxLines(10); //Fold
        else tvReviewText.setMaxLines(Integer.MAX_VALUE); // Expand
        reviewTextExpanded=!reviewTextExpanded;

    }

    private void playVideo(String videoId){
        Intent intent = new  Intent(Intent.ACTION_VIEW);
        StringBuilder videoUrl=new StringBuilder("https://www.youtube.com/watch?v=");
        videoUrl.append(videoId);
        intent.setData(Uri.parse(videoUrl.toString()));
        startActivity(intent);
    }


    public void favorite(View view){
        if(!isFavorite) {

            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
            movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
            movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, movie.getOriginalTitle());
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW, movie.getOverview());
            movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
            movieValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, movie.getBackdropPath());
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
            movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, movie.getPopularity());
            movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
            getBaseContext().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, movieValues);

            ((FloatingActionButton) view).setImageDrawable(ContextCompat.getDrawable(MovieDetailActivity.this,
                    R.drawable.ic_favorite_white_24dp));
            Toast.makeText(MovieDetailActivity.this, "Marked Favorite", Toast.LENGTH_SHORT).show();
            isFavorite=true;
        }

        else{
            Toast.makeText(MovieDetailActivity.this, "Already Marked Favorite", Toast.LENGTH_SHORT).show();
        }

    }


    private boolean isFavorite(){

        Cursor c = getBaseContext().getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI, null, MovieContract.MovieEntry.COLUMN_MOVIE_ID + " =? ",
                new String[]{movie.getId() + ""}, null);

        boolean fav = c.moveToFirst();
        c.close();
        return fav;


    }


}
