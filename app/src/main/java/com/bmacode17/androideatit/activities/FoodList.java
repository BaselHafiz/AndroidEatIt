package com.bmacode17.androideatit.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bmacode17.androideatit.R;
import com.bmacode17.androideatit.common.Common;
import com.bmacode17.androideatit.databases.Database;
import com.bmacode17.androideatit.interfaces.ItemClickListener;
import com.bmacode17.androideatit.models.Category;
import com.bmacode17.androideatit.models.Food;
import com.bmacode17.androideatit.viewHolders.FoodViewHolder;
import com.bmacode17.androideatit.viewHolders.MenuViewHolder;
import com.facebook.CallbackManager;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FoodList extends AppCompatActivity {

    private static final String TAG = "Basel";
    FirebaseDatabase database;
    DatabaseReference table_food;
    RecyclerView recyclerView_foodList;
    RecyclerView.LayoutManager layoutManager;
    String categoryId = "";
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;
    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchAdapter;
    Database localDb;
    SwipeRefreshLayout swipeRefreshLayout_foodList;
    List<String> suggestedList;
    MaterialSearchBar searchBar_foodList;

    CallbackManager callbackManager;
    ShareDialog shareDialog;

    // Create target from Picasso
    Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

            // Create photo from Bitmap
            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();
            if(ShareDialog.canShow(SharePhotoContent.class)){

                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();
                shareDialog.show(content);
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

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
        setContentView(R.layout.activity_food_list);

        localDb = new Database(this);

        // Init facebook
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        // Init firebase
        database = FirebaseDatabase.getInstance();
        table_food = database.getReference("food");
        suggestedList = new ArrayList<>();

        // Load menu
        // Use firebase UI to bind data from Firebase to Recycler view
        recyclerView_foodList = (RecyclerView) findViewById(R.id.recyclerView_foodList);
        recyclerView_foodList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView_foodList.setLayoutManager(layoutManager);

        swipeRefreshLayout_foodList = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout_foodList);
        swipeRefreshLayout_foodList.setColorSchemeResources(R.color.colorPrimary
                , android.R.color.holo_green_dark
                , android.R.color.holo_orange_dark
                , android.R.color.holo_blue_dark);

        swipeRefreshLayout_foodList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if(getIntent() != null)
                    categoryId = getIntent().getStringExtra("categoryId");

                if(! categoryId.isEmpty() && categoryId !=null){

                    if(Common.isConnectedToInternet(getBaseContext()))
                        loadFoodList(categoryId);
                    else{
                        Toast.makeText(FoodList.this, "Check your Internet connection !", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });

        // Default , load for the first time

        swipeRefreshLayout_foodList.post(new Runnable() {
            @Override
            public void run() {

                if(getIntent() != null)
                    categoryId = getIntent().getStringExtra("categoryId");

                if(! categoryId.isEmpty() && categoryId !=null){

                    if(Common.isConnectedToInternet(getBaseContext()))
                        loadFoodList(categoryId);
                    else{
                        Toast.makeText(FoodList.this, "Check your Internet connection !", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                searchBar_foodList = (MaterialSearchBar) findViewById(R.id.searchBar_foodList);
                searchBar_foodList.setHint("Enter your food ... ");
                loadSuggestedFoodList();    // Load the suggests from Firebase
                searchBar_foodList.setLastSuggestions(suggestedList);
                searchBar_foodList.setCardViewElevation(10);
                searchBar_foodList.addTextChangeListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                        // when the user type his text , the suggest list is changed

                        List<String> suggest = new ArrayList<>();

                        for(String search:suggestedList){

                            if(search.toLowerCase().contains(searchBar_foodList.getText().toLowerCase())){
                                suggest.add(search);
                            }
                            searchBar_foodList.setLastSuggestions(suggest);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                searchBar_foodList.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                    @Override
                    public void onSearchStateChanged(boolean enabled) {

                        // When the search bar is closed , we'll restore the original adapter
                        if (!enabled) {
                            recyclerView_foodList.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onSearchConfirmed(CharSequence text) {

                        // When the search is finished , we'll display the result of search adapter
                        startSearch(text);
                    }

                    @Override
                    public void onButtonClicked(int buttonCode) {

                    }
                });
            }
        });
    }

    private void startSearch(CharSequence text) {

        // Create query by name
        Query query = table_food.orderByChild("name").equalTo(text.toString());

        FirebaseRecyclerOptions<Food> options = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(query,Food.class)
                .build();

        searchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder viewHolder, int position, @NonNull Food model) {

                viewHolder.textView_foodName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imageView_foodImage);
                final Food clickedItem = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        // Toast.makeText(FoodList.this, "" + clickedItem.getName(), Toast.LENGTH_SHORT).show();
                        Intent foodDetailsIntent = new Intent(FoodList.this,FoodDetails.class);
                        // CategoryId is a key , so we just get the key of the clicked item
                        foodDetailsIntent.putExtra("foodId" , searchAdapter.getRef(position).getKey());
                        startActivity(foodDetailsIntent);
                    }
                });
            }

            @Override
            public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_list_cardview,parent,false);
                return new FoodViewHolder(itemView);
            }
        };

        searchAdapter.startListening();
        recyclerView_foodList.setAdapter(searchAdapter);
    }

    private void loadSuggestedFoodList() {

        table_food.orderByChild("menuId").equalTo(categoryId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){

                    Food item = postSnapshot.getValue(Food.class);
                    suggestedList.add(item.getName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadFoodList(String categoryId) {

        // Create query by categoryId
        Query query = table_food.orderByChild("menuId").equalTo(categoryId);  // like select * from foods where MenuId = categoryId

        FirebaseRecyclerOptions<Food> options = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(query,Food.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FoodViewHolder viewHolder, final int position, @NonNull final Food model) {

                viewHolder.textView_foodName.setText(model.getName());
                viewHolder.textView_food_price.setText(String.format("$ %s", model.getPrice().toString()));
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imageView_foodImage);

                if (localDb.isFavourites(adapter.getRef(position).getKey()))
                    viewHolder.imageView_favourite.setImageResource(R.drawable.ic_favorite_black_24dp);

                viewHolder.imageView_favourite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!localDb.isFavourites(adapter.getRef(position).getKey())) {

                            localDb.addToFavourites(adapter.getRef(position).getKey());
                            viewHolder.imageView_favourite.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(FoodList.this, model.getName() + " is added to Favourites", Toast.LENGTH_SHORT).show();
                        } else {

                            localDb.removeFromFavourites(adapter.getRef(position).getKey());
                            viewHolder.imageView_favourite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(FoodList.this, model.getName() + " is removed from Favourites", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                viewHolder.imageView_share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Picasso.with(getApplicationContext())
                                .load(model.getImage())
                                .into(target);
                    }
                });

                final Food clickedItem = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        // Toast.makeText(FoodList.this, "" + clickedItem.getName(), Toast.LENGTH_SHORT).show();
                        Intent foodDetailsIntent = new Intent(FoodList.this, FoodDetails.class);
                        // CategoryId is a key , so we just get the key of the clicked item
                        foodDetailsIntent.putExtra("foodId", adapter.getRef(position).getKey());
                        startActivity(foodDetailsIntent);
                    }
                });

            }

            @Override
            public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_list_cardview,parent,false);
                return new FoodViewHolder(itemView);
            }
        };

        adapter.startListening();
        recyclerView_foodList.setAdapter(adapter);
        // pc_0 - Using an unspecified index.
        // Consider adding '".indexOn": "MenuId"' at food to your security and Firebase Database rules for better performance
        // do it in the firebase console website in Rules
        swipeRefreshLayout_foodList.setRefreshing(false);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(searchAdapter != null)
            searchAdapter.stopListening();
        if(adapter != null)
            adapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Common.isConnectedToInternet(getBaseContext()))
            loadFoodList(categoryId);
        else {
            Toast.makeText(FoodList.this, "Check your Internet connection !", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
