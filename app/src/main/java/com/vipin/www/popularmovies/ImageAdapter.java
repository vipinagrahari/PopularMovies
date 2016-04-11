package com.vipin.www.popularmovies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.vipin.www.popularmovies.data.model.Movie;
import com.vipin.www.popularmovies.tmdbapi.ApiService;

import java.util.List;

/**
 * Created by Vipin on 22-12-2015.
 */


public class ImageAdapter extends BaseAdapter {

    List<Movie> movies;
    Context mContext;


    public ImageAdapter(Context context, List<Movie> movies) {

        mContext = context;
        this.movies = movies;

    }

    @Override
    public int getCount() {

        Log.e("Size", movies.size() + "");
        return movies.size();


    }

    @Override
    public Movie getItem(int position) {
        return movies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void addMovies(List<Movie> moreMovies) {
        movies.addAll(moreMovies);
        this.notifyDataSetChanged();
    }

    public void removeAll() {
        movies.removeAll(movies);
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.element_grid_movie, parent, false);
            holder = new ViewHolder();
            holder.posterImage = (ImageView) convertView.findViewById(R.id.poster_image);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Picasso.with(mContext)
                .load(ApiService.BASE_IMAGE_URL + ApiService.RES_POSTER + getItem(position).getPosterPath())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(holder.posterImage);
        return convertView;
    }


    private static class ViewHolder {
        ImageView posterImage;
    }


}
