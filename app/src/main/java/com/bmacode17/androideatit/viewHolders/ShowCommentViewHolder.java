package com.bmacode17.androideatit.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bmacode17.androideatit.R;
import com.bmacode17.androideatit.interfaces.ItemClickListener;

/**
 * Created by Basel on 08-Nov-18.
 */

public class ShowCommentViewHolder extends RecyclerView.ViewHolder{

    public TextView textView_userPhone, textView_comment;
    public RatingBar ratingBar_showComments;

    public ShowCommentViewHolder(View itemView) {
        super(itemView);

        textView_userPhone = (TextView) itemView.findViewById(R.id.textView_userPhone);
        textView_comment = (TextView) itemView.findViewById(R.id.textView_comment);
        ratingBar_showComments = (RatingBar) itemView.findViewById(R.id.ratingBar_showComments);
    }
}
