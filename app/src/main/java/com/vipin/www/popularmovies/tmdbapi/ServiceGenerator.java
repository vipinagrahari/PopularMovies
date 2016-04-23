package com.vipin.www.popularmovies.tmdbapi;


import com.squareup.okhttp.OkHttpClient;

import java.net.CookieManager;
import java.net.CookiePolicy;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by Vipin on 20-12-2015.
 */
public class ServiceGenerator {


    private static OkHttpClient httpClient = new OkHttpClient();


    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(ApiService.API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());


    public static <S> S createService(Class<S> serviceClass) {


        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        httpClient.setCookieHandler(cookieManager);
        Retrofit retrofit = builder.client(httpClient).build();
        return retrofit.create(serviceClass);
    }
}
