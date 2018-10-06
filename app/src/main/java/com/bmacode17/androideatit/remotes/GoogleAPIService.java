package com.bmacode17.androideatit.remotes;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by Al Badr on 06-Oct-18.
 */

public interface GoogleAPIService {
    @GET
    Call<String> getAddressName(@Url String url);
}


