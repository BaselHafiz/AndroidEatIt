package com.bmacode17.androideatit.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.ParseException;

import com.bmacode17.androideatit.models.User;
import com.bmacode17.androideatit.remotes.APIService;
import com.bmacode17.androideatit.remotes.GoogleAPIService;
import com.bmacode17.androideatit.remotes.RetrofitClient;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by User on 29-Jun-18.
 */

public class Common {

    public static User currentUser;
    public static final String baseUrl = "https://fcm.googleapis.com";
    public static final String GOOGLE_API_URL = "https://maps.googleapis.com";
    public static final String INTENT_FOOD_ID = "foodId";
    public static final String DELETE = "Delete";
    public static final String USER_KEY = "User";
    public static final String PASSWORD_KEY = "Password";
    public static final String PHONE_TEXT = "userPhone";

    public static APIService getFCMService(){

        return RetrofitClient.getClient(baseUrl).create(APIService.class);
    }

    public static GoogleAPIService getGoogleMapAPIService(){

        return RetrofitClient.getGoogleClient(GOOGLE_API_URL).create(GoogleAPIService.class);
    }

    public static String convertStatusToCode(String status) {

        if (status.equals("0"))
            return "Placed";
        else if (status.equals("1"))
            return "On the way";
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

    // This function converts currency to number based on Locale
    public static BigDecimal formatCurrency(String amount , Locale locale) throws java.text.ParseException {

        NumberFormat format = NumberFormat.getCurrencyInstance(locale);
        if(format instanceof DecimalFormat)
            ((DecimalFormat) format).setParseBigDecimal(true);

        return (BigDecimal) format.parse(amount.replace("[^\\d.,]",""));
    }
}