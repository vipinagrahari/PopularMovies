package com.vipin.www.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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

}
