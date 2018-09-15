package com.bmacode17.androideatit.activities;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bmacode17.androideatit.R;
import com.bmacode17.androideatit.common.Common;
import com.bmacode17.androideatit.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignUp extends AppCompatActivity {

    MaterialEditText textView_name , textView_phone , textView_password, textView_secureCode;
    Button button_signUp;
    ProgressBar myProgressBar;
    AlertDialog alertDialog;
    private static final String TAG = "Basel";

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
        setContentView(R.layout.activity_sign_up);

        textView_name = (MaterialEditText) findViewById(R.id.textView_name);
        textView_password = (MaterialEditText) findViewById(R.id.textView_password);
        textView_phone = (MaterialEditText) findViewById(R.id.textView_phone);
        textView_secureCode = (MaterialEditText) findViewById(R.id.textView_secureCode);
        button_signUp = (Button) findViewById(R.id.button_signUp);

//      Init Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("user");

        button_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Common.isConnectedToInternet(getBaseContext())){

                    openAlertDialog();
                    table_user.addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            // check if the phone number is already exists
                            if(dataSnapshot.child(textView_phone.getText().toString()).exists()){
                                // Get user information
                                alertDialog.dismiss();
                                Toast.makeText(SignUp.this, "Phone number is already registered", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                alertDialog.dismiss();
                                User user = new User(textView_name.getText().toString(),textView_password.getText().toString(),
                                        textView_secureCode.getText().toString());
                                table_user.child(textView_phone.getText().toString()).setValue(user);
                                Toast.makeText(SignUp.this, "Successfull sign up", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }else{
                    Toast.makeText(SignUp.this, "Check your Internet connection !", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    public void openAlertDialog() {

        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.myprogressdialog, null);
        myAlertDialog.setView(dialogView);
        myAlertDialog.setCancelable(true);
        myProgressBar = (ProgressBar) dialogView.findViewById(R.id.progressBar);
        alertDialog = myAlertDialog.create();
        alertDialog.show();

        try {
            Thread.sleep(3000);
        } catch (Exception e){}
    }
}

