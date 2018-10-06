package com.bmacode17.androideatit.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bmacode17.androideatit.R;
import com.bmacode17.androideatit.adapters.CartAdapter;
import com.bmacode17.androideatit.common.Common;
import com.bmacode17.androideatit.common.Config;
import com.bmacode17.androideatit.databases.Database;
import com.bmacode17.androideatit.models.Food;
import com.bmacode17.androideatit.models.MyNotification;
import com.bmacode17.androideatit.models.MyResponse;
import com.bmacode17.androideatit.models.Order;
import com.bmacode17.androideatit.models.Request;
import com.bmacode17.androideatit.models.Sender;
import com.bmacode17.androideatit.models.Token;
import com.bmacode17.androideatit.remotes.APIService;
import com.bmacode17.androideatit.remotes.GoogleAPIService;
import com.bmacode17.androideatit.viewHolders.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Cart extends AppCompatActivity {

    private static final String TAG = "Basel";
    public TextView textView_totalPrice;
    FirebaseDatabase database;
    DatabaseReference table_request;
    RecyclerView recyclerView_listCart;
    RecyclerView.LayoutManager layoutManager;
    RadioButton radioButton_shipToThisAddress , radioButton_homeAddress;
    EditText editText_notes;
//    EditText editText_address;
    FButton button_placeOrder;
    List<Order> carts = new ArrayList<>();
    CartAdapter cartAdapter;
    AlertDialog addressDialog;
    APIService mService;
    GoogleAPIService mGoogleMapAPIService;

    String address, notes;
    Place shippingAddress;

    private final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private final int PLAY_SERVICES_RESOLUTION_REQUEST = 2;
    private FusedLocationProviderClient mFusedLocationClient;
    Location currentLocation;

    // Press Ctrl + O
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            if (checkPlayServices()) {
                getDeviceLocation();
            }
        } else
            checkLocationPermission();

        // Add this code before setContentView method
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/cambria.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        setContentView(R.layout.activity_cart);

        // Init services
        mService = Common.getFCMService();
        mGoogleMapAPIService = Common.getGoogleMapAPIService();

        button_placeOrder = (FButton) findViewById(R.id.button_placeOrder);
        textView_totalPrice = (TextView) findViewById(R.id.textView_totalPrice);

        database = FirebaseDatabase.getInstance();
        table_request = database.getReference("request");

        recyclerView_listCart = (RecyclerView) findViewById(R.id.recyclerView_listCart);
        recyclerView_listCart.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView_listCart.setLayoutManager(layoutManager);

        loadCartList();

        button_placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (carts.size() > 0)
                    openAddressDialog();
                else
                    Toast.makeText(Cart.this, "Your cart is empty ! ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openAddressDialog() {

        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.addressdialog, null);
        myAlertDialog.setView(dialogView);
        myAlertDialog.setCancelable(true);
        myAlertDialog.setTitle("One more step !");
//        editText_address = (EditText) dialogView.findViewById(R.id.editText_address);
        final PlaceAutocompleteFragment placeAutocompleteFragment_address = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.fragment_placeAutoComplete);
        // Hide search icon before fragment
        placeAutocompleteFragment_address.getView().findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE);
        // Set hint for the fragment
        ((EditText) placeAutocompleteFragment_address.getView().findViewById(R.id.place_autocomplete_search_input)).setHint("CLICK HERE !");

        ((EditText) placeAutocompleteFragment_address.getView().findViewById(R.id.place_autocomplete_search_input)).setTextSize(15);

        placeAutocompleteFragment_address.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                shippingAddress = place;
            }

            @Override
            public void onError(Status status) {
                Log.d(TAG, "onError: " + status.getStatusMessage());
            }
        });

        editText_notes = (EditText) dialogView.findViewById(R.id.editText_notes);
        radioButton_shipToThisAddress = (RadioButton) dialogView.findViewById(R.id.radioButton_shipToThisAddress);
        radioButton_homeAddress = (RadioButton) dialogView.findViewById(R.id.radioButton_homeAddress);

        radioButton_shipToThisAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){

                        Log.d(TAG, "onCheckedChanged: " + String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%f,%f&sensor=false",
                                currentLocation.getLatitude(),currentLocation.getLongitude()));

                        mGoogleMapAPIService.getAddressName(String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%f,%f&sensor=false",
                                currentLocation.getLatitude(),currentLocation.getLongitude()))
                                .enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {

                                        Log.d(TAG, "onResponse: " + response.body().toString());

                                        try {

                                            JSONObject jsonObject = new JSONObject(response.body().toString());

                                            if(jsonObject.getJSONArray("results").length() > 0){

                                                JSONArray resultsArray = jsonObject.getJSONArray("results");
                                                JSONObject firstObject = resultsArray.getJSONObject(0);
                                                address = firstObject.getString("formatted_address");
                                                ((EditText) placeAutocompleteFragment_address.getView().findViewById(R.id.place_autocomplete_search_input)).setText(address);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        Toast.makeText(Cart.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "onFailure: " + t.getMessage());
                                    }
                                });
                }
            }
        });

        radioButton_homeAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

        myAlertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        myAlertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(Cart.this, "Order is canceled", Toast.LENGTH_LONG).show();
                // Remove fragment
                getFragmentManager().beginTransaction()
                        .remove(getFragmentManager().findFragmentById(R.id.fragment_placeAutoComplete)).commit();
            }
        });

        myAlertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(!radioButton_shipToThisAddress.isChecked() && !radioButton_homeAddress.isChecked()){

                    if(shippingAddress != null){

                        address = shippingAddress.getAddress().toString();
                    }else{

                        Toast.makeText(Cart.this, "Please enter address or select option address", Toast.LENGTH_SHORT).show();
                        // Remove fragment
                        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.fragment_placeAutoComplete)).commit();
                        return;
                    }
                }

                if(TextUtils.isEmpty(address)){

                    Toast.makeText(Cart.this, "Please enter address or select option address", Toast.LENGTH_SHORT).show();
                    // Remove fragment
                    getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.fragment_placeAutoComplete)).commit();
                    return;
                }

                notes = editText_notes.getText().toString();
                Request request = new Request(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        address,
                        textView_totalPrice.getText().toString(),
                        carts,
                        notes,
                        String.format("%s,%s",shippingAddress.getLatLng().latitude,shippingAddress.getLatLng().longitude));

                // Remove fragment
                getFragmentManager().beginTransaction()
                        .remove(getFragmentManager().findFragmentById(R.id.fragment_placeAutoComplete)).commit();

                // Submit to firebase
                // currentTimeMillis is considered as a key
                String orderNumber = String.valueOf(System.currentTimeMillis());
                table_request.child(orderNumber).setValue(request);
                dialog.dismiss();

                // Delete carts
                new Database(getBaseContext()).cleanCarts();
                sendOrderNotification(orderNumber);
            }
        });

        addressDialog = myAlertDialog.create();
        addressDialog.show();
    }

    private void sendOrderNotification(final String orderNumber) {

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference table_token = db.getReference("token");
        Query data = table_token.orderByChild("serverToken").equalTo(true);  // Get all nodes which its isServerToken is true
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot item : dataSnapshot.getChildren()) {

                    Token serverToken = item.getValue(Token.class);

                    // Create Raw payload to send
                    MyNotification notification = new MyNotification("Basel", "New order: " + orderNumber);
                    Sender content = new Sender(serverToken.getToken(), notification);
                    mService.sendNotification(content)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                                    if (response.code() == 200) {

                                        if (response.body().success == 1) {
                                            Toast.makeText(Cart.this, "Thank you , order is placed ", Toast.LENGTH_LONG).show();
                                            finish();
                                        } else
                                            Toast.makeText(Cart.this, "Failed ", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadCartList() {

        carts = new Database(this).getCarts();
        cartAdapter = new CartAdapter(carts, this);
        cartAdapter.notifyDataSetChanged();
        recyclerView_listCart.setAdapter(cartAdapter);

        int totalPrice = 0;
        for (Order i : carts)
            totalPrice += Integer.parseInt(i.getPrice()) * Integer.parseInt(i.getQuantity());
        Locale locale = new Locale("en", "US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        textView_totalPrice.setText(fmt.format(totalPrice));
    }

    public boolean onContextItemSelected(MenuItem item) {

        if (item.getTitle().equals(Common.DELETE))
            deleteCart(item.getOrder());

        return super.onContextItemSelected(item);
    }

    private void deleteCart(int position) {

        // we'll remove item from list<order> by position
        carts.remove(position);
        // then , we delete all old data from SQLite
        new Database(this).cleanCarts();
        // finally , we'll update the SQLite using the new updated carts
        for (Order item : carts)
            new Database(this).addToCarts(item);
        loadCartList();
    }

    private boolean checkPlayServices() {

        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {

            if (GoogleApiAvailability.getInstance().isUserResolvableError(resultCode)) {

                GoogleApiAvailability.getInstance().getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();

            } else {

                Toast.makeText(this, "Device isn't supported", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }

    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        try {

            Task location = mFusedLocationClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful() && task.getResult() != null) {

                        currentLocation = (Location) task.getResult();
                        Log.d(TAG, "onComplete: Location: " + currentLocation.getLatitude() + " " + currentLocation.getLongitude());
                        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        Toast.makeText(Cart.this, currentLocation.getLatitude() + " , " + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(Cart.this, "Unable to get current location !", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (SecurityException ex) {
            Log.d(TAG, "GetDeviceLocation : SecurityException: " + ex.getMessage());
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED)
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(Cart.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayServices()) {
                        getDeviceLocation();
                    }
                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}
