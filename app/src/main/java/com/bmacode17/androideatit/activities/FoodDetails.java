package com.bmacode17.androideatit.activities;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bmacode17.androideatit.R;
import com.bmacode17.androideatit.common.Common;
import com.bmacode17.androideatit.databases.Database;
import com.bmacode17.androideatit.models.Food;
import com.bmacode17.androideatit.models.Order;
import com.bmacode17.androideatit.models.Rating;
import com.bmacode17.androideatit.viewHolders.FoodViewHolder;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kotlin.collections.ArraysKt;

public class FoodDetails extends AppCompatActivity implements RatingDialogListener{

    private static final String TAG = "Basel";
    FirebaseDatabase database;
    DatabaseReference table_food , table_rating;
    String foodId = "";
    TextView textView_food_name , textView_foodPrice , textView_foodDescription;
    ImageView imageView_food_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    ElegantNumberButton numberCounter;
    FloatingActionButton fabCart , fabRating;
    Food currentFood;
    RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);

        textView_food_name = (TextView) findViewById(R.id.textView_food_name);
        textView_foodPrice = (TextView) findViewById(R.id.textView_foodPrice);
        textView_foodDescription = (TextView) findViewById(R.id.textView_foodDescription);
        imageView_food_image = (ImageView) findViewById(R.id.imageView_food_image);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
        fabCart = (FloatingActionButton) findViewById(R.id.fabCart);
        fabRating = (FloatingActionButton) findViewById(R.id.fabRating);
        numberCounter= (ElegantNumberButton) findViewById(R.id.numberCounter);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        database = FirebaseDatabase.getInstance();
        table_food = database.getReference("food");
        table_rating = database.getReference("rating");
        
        fabRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showRatingDialog();
            }
        });

        fabCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Database(getBaseContext()).addToCarts(new Order(
                        foodId,
                        currentFood.getName(),
                        numberCounter.getNumber(),
                        currentFood.getPrice(),
                        currentFood.getDiscount()));

                Toast.makeText(FoodDetails.this, "Added To Cart", Toast.LENGTH_SHORT).show();
            }
        });

        // Get food id from intent
        if(getIntent() != null)
            foodId = getIntent().getStringExtra("foodId");

        if(! foodId.isEmpty() && foodId !=null){
            if(Common.isConnectedToInternet(getBaseContext())){

                loadFoodDetails(foodId);
                getRatingFood(foodId);
            }
            else{
                Toast.makeText(FoodDetails.this, "Check your Internet connection !", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    private void getRatingFood(String foodId) {

        Query foodRatingQuery = table_rating.orderByChild("foodId").equalTo(foodId);

        foodRatingQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int count = 0 , sum = 0 ;
                for(DataSnapshot item:dataSnapshot.getChildren()){

                    Rating ratingItem = item.getValue(Rating.class);
                    sum += Integer.parseInt(ratingItem.getRateValue());
                    count++;
                }
                if(count != 0){
                    float ratingAverage = sum/count;
                    ratingBar.setRating(ratingAverage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showRatingDialog() {

        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad","Not Good","Quite Ok","Very Good","Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate this food")
                .setDescription("Please select some stars and give your feedback")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Write your comment here")
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(android.R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimary)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(FoodDetails.this)
                .show();
    }

    private void loadFoodDetails(String foodId) {

        table_food.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                currentFood = dataSnapshot.getValue(Food.class);
                Picasso.with(getBaseContext()).load(currentFood.getImage()).into(imageView_food_image);
                collapsingToolbarLayout.setTitle(currentFood.getName());
                textView_food_name.setText(currentFood.getName());
                textView_foodPrice.setText(currentFood.getPrice());
                textView_foodDescription.setText(currentFood.getDescription());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int value, String comment) {

        // Get rating and upload to firebase
        final Rating rating = new Rating(Common.currentUser.getPhone(),
                foodId,
                String.valueOf(value),
                comment);
        /*
        There is a problem in this function. When a user is giving rating for the first time it works absolutely fine.
        But suppose the user changes his/her mind about the rating and try to update the ratings then the problem arises.
        For giving new ratings, the app needs to be forced closed before submitting new ratings.
        Or else if the rating is immediately updated after the first one, the app stops working and hangs until
        and unless it is forced closed. After force close, the ratings work fine if the rating is updated.
        And in the Firebase console when rating is updated without closing the app,
        the rating keeps on shuffling between the previous rating and the currently updated rating which makes
        the app to stop working﻿
        */

        table_rating.child(Common.currentUser.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(Common.currentUser.getPhone()).exists()){

                    // Remove old value
                    table_rating.child(Common.currentUser.getPhone()).removeValue();
                    // Add new value
                    table_rating.child(Common.currentUser.getPhone()).setValue(rating);
                }
                else
                    table_rating.child(Common.currentUser.getPhone()).setValue(rating);

                Toast.makeText(FoodDetails.this, "Thank you for your feedback", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
