package com.bmacode17.androideatit.remotes;


import com.bmacode17.androideatit.models.MyResponse;
import com.bmacode17.androideatit.models.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by User on 14-Aug-18.
 */

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json" ,
                    "Authorization:key=AAAA6SWtQGM:APA91bFdf_KotLNm7w7qARvllmQocxvjHf9lXFPZndiiWKh8IY5g6GmHVKZ7dVjFUPXBUsIgv7SgTpjAnZ3IBgb199VET40zjidhchukdkm5cr_ph3VlwRNnkb8-uuyJUj4RgHuy_dCcSYrG6nQ3oFhjTy3-j_LmxQ"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
