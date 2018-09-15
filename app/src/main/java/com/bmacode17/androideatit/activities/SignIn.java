package com.bmacode17.androideatit.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

import info.hoang8f.widget.FButton;
import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignIn extends AppCompatActivity {

    TextView textView_phone , textView_password , textView_forgetPassword;
    MaterialEditText editText_phoneNumber , editText_secureCode;
    Button button_signIn;
    ProgressBar myProgressBar;
    AlertDialog alertDialog;
    private static final String TAG = "Basel";
    com.rey.material.widget.CheckBox checkBox_rememberMe;
    FirebaseDatabase database;
    DatabaseReference table_user;
    AlertDialog forgetPasswordDialog;

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
        setContentView(R.layout.activity_sign_in);

        textView_password = (TextView) findViewById(R.id.textView_password);
        textView_phone = (TextView) findViewById(R.id.textView_phone);
        textView_forgetPassword = (TextView) findViewById(R.id.textView_forgetPassword);
        button_signIn = (Button) findViewById(R.id.button_signIn);
        checkBox_rememberMe = (com.rey.material.widget.CheckBox)findViewById(R.id.checkbox_rememberMe);


        // Init Paper
        // Paper is a fast NoSQL-like storage for Java/Kotlin objects on Android ,  It's more a Key-value blob store.
        Paper.init(this);

        // Init Firebase
        database = FirebaseDatabase.getInstance();
        table_user = database.getReference("user");

        button_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Common.isConnectedToInternet(getBaseContext())){

                    // Save user Id and password
                    if(checkBox_rememberMe.isChecked()){

                        Paper.book().write(Common.USER_KEY,textView_phone.getText().toString());
                        Paper.book().write(Common.PASSWORD_KEY,textView_password.getText().toString());
                    }

                    openAlertDialog();
                    table_user.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            // check if the user isn't exists in the database
                            if(dataSnapshot.child(textView_phone.getText().toString()).exists()){

                                // Get user information
                                alertDialog.dismiss();
                                User user = dataSnapshot.child(textView_phone.getText().toString()).getValue(User.class);
                                user.setPhone(textView_phone.getText().toString());
                                if(user.getPassword().equals(textView_password.getText().toString())){

                                    Intent homeIntent = new Intent(SignIn.this,Home.class);
                                    Common.currentUser = user;
                                    startActivity(homeIntent);
                                    finish();

                                    table_user.removeEventListener(this);
                                }else{
                                    Toast.makeText(SignIn.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                alertDialog.dismiss();
                                Toast.makeText(SignIn.this, "User isn't exist in the database", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }else{
                    Toast.makeText(SignIn.this, "Check your Internet connection !", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        textView_forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openForgetPasswordDialog();
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

    private void openForgetPasswordDialog() {

        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.forget_password_cardview, null);
        myAlertDialog.setView(dialogView);
        myAlertDialog.setCancelable(true);
        myAlertDialog.setTitle("Forget Password");
        myAlertDialog.setMessage("Enter your secure code");
        myAlertDialog.setIcon(R.drawable.ic_security_black_24dp);
        editText_phoneNumber = (MaterialEditText) dialogView.findViewById(R.id.editText_phoneNumber);
        editText_secureCode = (MaterialEditText) dialogView.findViewById(R.id.editText_secureCode);

        myAlertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        myAlertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        User user = dataSnapshot.child(editText_phoneNumber.getText().toString()).getValue(User.class);
                        if(user.getSecureCode().equals(editText_secureCode.getText().toString()))
                            Toast.makeText(SignIn.this, "Your password : " + user.getPassword(), Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(SignIn.this, "Wrong secure code !", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        forgetPasswordDialog = myAlertDialog.create();
        forgetPasswordDialog.show();
    }
}
