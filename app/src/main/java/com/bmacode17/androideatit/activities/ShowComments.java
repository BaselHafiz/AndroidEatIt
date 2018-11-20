package com.bmacode17.androideatit.activities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bmacode17.androideatit.R;
import com.bmacode17.androideatit.common.Common;
import com.bmacode17.androideatit.databases.Database;
import com.bmacode17.androideatit.models.Food;
import com.bmacode17.androideatit.models.Rating;
import com.bmacode17.androideatit.viewHolders.FoodViewHolder;
import com.bmacode17.androideatit.viewHolders.ShowCommentViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class ShowComments extends AppCompatActivity {

    private static final String TAG = "Basel";
    FirebaseDatabase database;
    DatabaseReference table_rating;
    String foodId = "";
    RecyclerView recyclerView_showComments;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder> adapter;
    SwipeRefreshLayout swipeRefreshLayout_showComments;

    // Press Ctrl + O
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (adapter != null)
            adapter.stopListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add this code before setContentView method
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/cambria.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_show_comments);

        // Init firebase
        database = FirebaseDatabase.getInstance();
        table_rating = database.getReference("rating");

        recyclerView_showComments = (RecyclerView) findViewById(R.id.recyclerView_showComments);
        recyclerView_showComments.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView_showComments.setLayoutManager(layoutManager);

        swipeRefreshLayout_showComments = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout_showComments);
        swipeRefreshLayout_showComments.setColorSchemeResources(R.color.colorPrimary
                , android.R.color.holo_green_dark
                , android.R.color.holo_orange_dark
                , android.R.color.holo_blue_dark);

        swipeRefreshLayout_showComments.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (getIntent() != null)
                    foodId = getIntent().getStringExtra(Common.INTENT_FOOD_ID);

                if (!foodId.isEmpty() && foodId != null) {

                    if (Common.isConnectedToInternet(getBaseContext()))
                        loadComments(foodId);
                    else {
                        Toast.makeText(ShowComments.this, "Check your Internet connection !", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });

        // Thread , load comments on the first launch
        swipeRefreshLayout_showComments.post(new Runnable() {
            @Override
            public void run() {

                swipeRefreshLayout_showComments.setRefreshing(true);
                if(getIntent() != null)
                    foodId = getIntent().getStringExtra(Common.INTENT_FOOD_ID);

                if(! foodId.isEmpty() && foodId !=null){

                    if(Common.isConnectedToInternet(getBaseContext()))
                        loadComments(foodId);
                    else{
                        Toast.makeText(ShowComments.this, "Check your Internet connection !", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });
    }

    private void loadComments(String foodId) {

        // Create query by foodId
        Query query = table_rating.orderByChild(Common.INTENT_FOOD_ID).equalTo(foodId);

        FirebaseRecyclerOptions<Rating> options = new FirebaseRecyclerOptions.Builder<Rating>()
                .setQuery(query,Rating.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ShowCommentViewHolder holder, int position, @NonNull Rating model) {

                holder.ratingBar_showComments.setRating(Float.parseFloat(model.getRateValue()));
                holder.textView_comment.setText(model.getComment());
                holder.textView_userPhone.setText(model.getUserPhone());
            }

            @Override
            public ShowCommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.comment_cardview,parent,false);
                return new ShowCommentViewHolder(itemView);
            }
        };

        adapter.startListening();
        recyclerView_showComments.setAdapter(adapter);
        swipeRefreshLayout_showComments.setRefreshing(false);
    }
}
