package com.bmacode17.androideatit.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bmacode17.androideatit.R;
import com.bmacode17.androideatit.interfaces.ItemClickListener;

/**
 * Created by User on 30-Jun-18.
 */

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView textView_foodName , textView_food_price;
    public ImageView imageView_foodImage , imageView_favourite , imageView_share , imageView_addToCart;
    private ItemClickListener itemClickListener;

    public FoodViewHolder(View itemView) {
        super(itemView);

        textView_foodName = (TextView) itemView.findViewById(R.id.textView_foodName);
        textView_food_price = (TextView) itemView.findViewById(R.id.textView_food_price);
        imageView_foodImage = (ImageView) itemView.findViewById(R.id.imageView_foodImage);
        imageView_favourite = (ImageView) itemView.findViewById(R.id.imageView_favourite);
        imageView_share = (ImageView) itemView.findViewById(R.id.imageView_share);
        imageView_addToCart = (ImageView) itemView.findViewById(R.id.imageView_addToCart);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);
    }
}
