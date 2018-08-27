package com.bmacode17.androideatit.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bmacode17.androideatit.R;
import com.bmacode17.androideatit.interfaces.ItemClickListener;

/**
 * Created by User on 29-Jun-18.
 */

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView textView_menuName;
    public ImageView imageView_menuImage;
    private ItemClickListener itemClickListener;

    public MenuViewHolder(View itemView) {
        super(itemView);

        textView_menuName = (TextView) itemView.findViewById(R.id.textView_menuName);
        imageView_menuImage = (ImageView) itemView.findViewById(R.id.imageView_menuImage);
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
