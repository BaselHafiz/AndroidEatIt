package com.bmacode17.androideatit.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bmacode17.androideatit.R;
import com.bmacode17.androideatit.common.Common;
import com.bmacode17.androideatit.databases.Database;
import com.bmacode17.androideatit.models.User;
import com.facebook.FacebookSdk;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;
    Button button_continue;
    TextView textView_eatIt;
    ProgressBar myProgressBar;
    AlertDialog alertDialog;
    FirebaseDatabase database;
    DatabaseReference table_user;

    // Press Ctrl + O

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add this code before setContentView method
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/cambria.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_main);

        button_continue = (Button) findViewById(R.id.button_continue);
        textView_eatIt = (TextView) findViewById(R.id.textView_eatIt);

        database = FirebaseDatabase.getInstance();
        table_user = database.getReference("user");

        Typeface face = Typeface.createFromAsset(getAssets(),"fonts/myFont.ttf");
        textView_eatIt.setTypeface(face);
        button_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoginSystem();
            }
        });

        // Check session facebook account kit
        if(AccountKit.getCurrentAccessToken() != null){

            final android.app.AlertDialog waitingDialog = new SpotsDialog(this);
            waitingDialog.show();
            waitingDialog.setMessage("Please wait");
            waitingDialog.setCancelable(false);

            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                @Override
                public void onSuccess(Account account) {

                    // Login
                    String userPhone = account.getPhoneNumber().toString();
                    table_user.child(userPhone).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            waitingDialog.dismiss();
                            User localUser = dataSnapshot.getValue(User.class);
                            Intent homeIntent = new Intent(MainActivity.this,Home.class);
                            Common.currentUser = localUser;
                            startActivity(homeIntent);
                            finish();

                            table_user.removeEventListener(this);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                @Override
                public void onError(AccountKitError accountKitError) {

                }
            });
        }
    }

    private void startLoginSystem() {

        Intent intent = new Intent(MainActivity.this , AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE,AccountKitActivity.ResponseType.TOKEN);
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION , configurationBuilder.build());
        startActivityForResult(intent , REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE){

            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if(loginResult.getError() != null){

                Toast.makeText(this, loginResult.getError().getErrorType().getMessage() , Toast.LENGTH_SHORT).show();
                return;
            }else if(loginResult.wasCancelled()){

                Toast.makeText(this, "Cancel" , Toast.LENGTH_SHORT).show();
                return;
            }else{

                if(loginResult.getAccessToken() != null){

                    final android.app.AlertDialog waitingDialog = new SpotsDialog(this);
                    waitingDialog.show();
                    waitingDialog.setMessage("Please wait");
                    waitingDialog.setCancelable(false);

                    // Get current phone
                    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                        @Override
                        public void onSuccess(Account account) {

                            final String userPhone = account.getPhoneNumber().toString();

                            // Check if it exists on Firebase users
                            table_user.orderByKey().equalTo(userPhone).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(!dataSnapshot.child(userPhone).exists()){

                                        // Create a new user and login
                                        User newUser = new User();
                                        newUser.setPhone(userPhone);
                                        newUser.setName("DEFAULT USER");
                                        newUser.setIsStaff("false");

                                        table_user.child(userPhone).setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(MainActivity.this, "User is registered successfully", Toast.LENGTH_SHORT).show();
                                                }
                                                // Login
                                                table_user.child(userPhone).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                                        waitingDialog.dismiss();
                                                        User localUser = dataSnapshot.getValue(User.class);
                                                        Intent homeIntent = new Intent(MainActivity.this,Home.class);
                                                        Common.currentUser = localUser;
                                                        startActivity(homeIntent);
                                                        finish();

                                                        table_user.removeEventListener(this);
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                        });
                                    }
                                    else{
                                        // Login
                                        table_user.child(userPhone).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                waitingDialog.dismiss();
                                                User localUser = dataSnapshot.getValue(User.class);
                                                Intent homeIntent = new Intent(MainActivity.this,Home.class);
                                                Common.currentUser = localUser;
                                                startActivity(homeIntent);
                                                finish();

                                                table_user.removeEventListener(this);
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onError(AccountKitError accountKitError) {
                            Toast.makeText(MainActivity.this, accountKitError.getErrorType().getMessage() , Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
    }
}
