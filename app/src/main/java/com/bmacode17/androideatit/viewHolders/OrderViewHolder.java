package com.bmacode17.androideatit.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bmacode17.androideatit.R;
import com.bmacode17.androideatit.interfaces.ItemClickListener;

/**
 * Created by User on 08-Jul-18.
 */

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView textView_orderId, textView_orderStatus, textView_orderPhone, textView_orderAddress;
    private ItemClickListener itemClickListener;

    public OrderViewHolder(View itemView) {
        super(itemView);

        textView_orderId = (TextView) itemView.findViewById(R.id.textView_orderId);
        textView_orderStatus = (TextView) itemView.findViewById(R.id.textView_orderStatus);
        textView_orderPhone = (TextView) itemView.findViewById(R.id.textView_orderPhone);
        textView_orderAddress = (TextView) itemView.findViewById(R.id.textView_orderAddress);

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
