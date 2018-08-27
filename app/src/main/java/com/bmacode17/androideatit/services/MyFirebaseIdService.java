package com.bmacode17.androideatit.services;

import com.bmacode17.androideatit.common.Common;
import com.bmacode17.androideatit.models.Token;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by User on 11-Aug-18.
 */

public class MyFirebaseIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String tokenRefreshed = FirebaseInstanceId.getInstance().getToken();
        if(Common.currentUser != null)
            updateTokenToFirebase(tokenRefreshed);
    }

    private void updateTokenToFirebase(String tokenRefreshed) {

        if(Common.currentUser != null){

            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference table_token = db.getReference("token");
            Token token = new Token(tokenRefreshed , false);  // false because this token is sent from the client
            table_token.child(Common.currentUser.getPhone()).setValue(token);
        }
    }
}
