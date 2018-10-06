package com.bmacode17.androideatit.remotes;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by User on 16-Aug-18.
 */

public class RetrofitClient {

    private static Retrofit retrofit1 = null;
    private static Retrofit retrofit2 = null;

    public static Retrofit getClient(String baseUrl){

        if(retrofit1 == null){

            retrofit1 = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit1;
    }

    public static Retrofit getGoogleClient(String baseUrl){

        if(retrofit2 == null){

            retrofit2 = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }
        return retrofit2;
    }
}

