package com.bhavaniprasad.newsfeed;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/***
 * Networkconnection class to check for network and returns network status
 */
public class NetworkConnection {

    public static NetworkInfo getNetworkInfo(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    public boolean isConnected(Context context){
        NetworkInfo info = NetworkConnection.getNetworkInfo(context);
        return (info != null && info.isConnected());
    }



}