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
                    "Authorization:key=AAAA6SWtQGM:APA91bECtoKqnJnw9jKyy2Q68mbXgDQfU-2GlLJf64jLfwYmygsTIIDvlC0arwsvWi4gb47_WXQumenDIrsieJcTqJs4c34PASOje085aihmCykmEGCrGZOfLpTn_fnBD8sE9GRvJVsH"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
