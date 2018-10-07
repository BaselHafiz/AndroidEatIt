package com.bmacode17.androideatit.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bmacode17.androideatit.R;
import com.bmacode17.androideatit.common.Common;
import com.bmacode17.androideatit.interfaces.ItemClickListener;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;

/**
 * Created by User on 06-Jul-18.
 */

public class CartViewHolder extends RecyclerView.ViewHolder implements
        View.OnClickListener ,
        View.OnCreateContextMenuListener {

    public TextView textView_cartItemName , textView_cartItemPrice;
    public ElegantNumberButton numberButton_quantity;
    private ItemClickListener itemClickListener;
    public ImageView imageView_cartItemImage;

    public void setTextView_cartItemName(TextView textView_cartItemName) {
        this.textView_cartItemName = textView_cartItemName;
    }

    public CartViewHolder(View itemView) {
        super(itemView);

        textView_cartItemName = (TextView) itemView.findViewById(R.id.textView_cartItemName);
        textView_cartItemPrice = (TextView) itemView.findViewById(R.id.textView_cartItemPrice);
        numberButton_quantity = (ElegantNumberButton) itemView.findViewById(R.id.numberButton_quantity);
        imageView_cartItemImage = (ImageView) itemView.findViewById(R.id.imageView_cartItemImage);

        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        menu.setHeaderTitle("Select the action");
        menu.add(0, 0, getAdapterPosition(), Common.DELETE);
    }
}
