package com.bmacode17.androideatit.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bmacode17.androideatit.R;
import com.bmacode17.androideatit.common.Common;
import com.bmacode17.androideatit.interfaces.ItemClickListener;
import com.bmacode17.androideatit.models.Food;
import com.bmacode17.androideatit.models.Request;
import com.bmacode17.androideatit.viewHolders.FoodViewHolder;
import com.bmacode17.androideatit.viewHolders.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OrderStatus extends AppCompatActivity {

    private static final String TAG = "Basel";
    FirebaseDatabase database;
    DatabaseReference table_request;
    RecyclerView recyclerView_listOrder;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

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
        setContentView(R.layout.activity_order_status);

        database = FirebaseDatabase.getInstance();
        table_request = database.getReference("request");

        recyclerView_listOrder = (RecyclerView) findViewById(R.id.recyclerView_listOrder);
        recyclerView_listOrder.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView_listOrder.setLayoutManager(layoutManager);

        if(getIntent().getStringExtra("userPhone") == null)
            loadOrders(Common.currentUser.getPhone());
        else
            loadOrders(getIntent().getStringExtra("userPhone"));
    }

    private void loadOrders(String phone) {

        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(Request.class, R.layout.order_cardview,
                OrderViewHolder.class, table_request.orderByChild("phone").equalTo(phone)) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, Request model, int position) {

                viewHolder.textView_orderId.setText(adapter.getRef(position).getKey());
                viewHolder.textView_orderStatus.setText(Common.convertStatusToCode(model.getStatus()));
                viewHolder.textView_orderAddress.setText(model.getAddress());
                viewHolder.textView_orderPhone.setText(model.getPhone());

                final Request clickedItem = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(OrderStatus.this, "" + clickedItem.getName(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        recyclerView_listOrder.setAdapter(adapter);
    }
}
