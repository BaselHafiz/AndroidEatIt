package com.bmacode17.androideatit.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

import com.bmacode17.androideatit.models.User;
import com.bmacode17.androideatit.remotes.APIService;
import com.bmacode17.androideatit.remotes.RetrofitClient;

/**
 * Created by User on 29-Jun-18.
 */

public class Common {

    public static User currentUser;
    public static final String baseUrl = "https://fcm.googleapis.com";
    public static final String DELETE = "Delete";
    public static final String USER_KEY = "User";
    public static final String PASSWORD_KEY = "Password";
    public static final String PHONE_TEXT = "userPhone";

    public static APIService getFCMService(){
        return RetrofitClient.getClient(baseUrl).create(APIService.class);
    }

    public static String convertStatusToCode(String status) {

        if (status.equals("0"))
            return "Placed";
        else if (status.equals("1"))
            return "On my way";
        else
            return "Shipped";
    }

    public static boolean isConnectedToInternet(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {

            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED))
                    return true;
            }
        }
        return false;
    }
}