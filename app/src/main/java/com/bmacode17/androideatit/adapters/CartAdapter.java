package com.bmacode17.androideatit.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bmacode17.androideatit.R;
import com.bmacode17.androideatit.activities.Cart;
import com.bmacode17.androideatit.databases.Database;
import com.bmacode17.androideatit.models.Order;
import com.bmacode17.androideatit.viewHolders.CartViewHolder;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by User on 06-Jul-18.
 */

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {

    private List<Order> listData = new ArrayList<>();
    private Cart cart;

    public CartAdapter(List<Order> listData, Cart cart) {
        this.listData = listData;
        this.cart = cart;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(cart);
        View itemView = inflater.inflate(R.layout.cart_list_cardview, parent, false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CartViewHolder holder, final int position) {

        Picasso.with(cart.getBaseContext())
                .load(listData.get(position).getImage())
                .resize(70,70)
                .centerCrop().into(holder.imageView_cartItemImage);

        holder.numberButton_quantity.setNumber(listData.get(position).getQuantity());
        holder.numberButton_quantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {

                Order order = listData.get(position);
                order.setQuantity(String.valueOf(newValue));
                new Database(cart).updateCart(order);

                List<Order> carts = new Database(cart).getCarts();

                // Updarte textView_totalPrice
                int totalPrice = 0;
                for (Order i : carts)
                    totalPrice += Integer.parseInt(i.getPrice()) * Integer.parseInt(i.getQuantity());
                Locale locale = new Locale("en", "US");
                NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                cart.textView_totalPrice.setText(fmt.format(totalPrice));

                int price = Integer.parseInt(listData.get(position).getPrice()) * Integer.parseInt(listData.get(position).getQuantity());
                holder.textView_cartItemPrice.setText(fmt.format(price));
                holder.textView_cartItemName.setText(listData.get(position).getProductName());
            }
        });

        Locale locale = new Locale("en", "US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        int price = Integer.parseInt(listData.get(position).getPrice()) * Integer.parseInt(listData.get(position).getQuantity());
        holder.textView_cartItemPrice.setText(fmt.format(price));
        holder.textView_cartItemName.setText(listData.get(position).getProductName());
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}
