package com.medical.mina.markosmedicalsupplies.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.medical.mina.markosmedicalsupplies.Interface.ItemClickListener;
import com.medical.mina.markosmedicalsupplies.R;

/**
 * Created by Mina on 6/16/2018.
 */

public class FavoriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView f_name,f_price;
    public ImageView f_img;
    public ImageView saleIcon;
    public ImageView favIcon,quickCart;
    public void setItemClickListener(ItemClickListener itemClickListener) {

        this.itemClickListener = itemClickListener;
    }

    private ItemClickListener itemClickListener;

    public FavoriteViewHolder(View itemView) {
        super(itemView);
        f_name=(TextView)itemView.findViewById(R.id.tv_food);
        f_price=(TextView)itemView.findViewById(R.id.tv_food_price);
        f_img=(ImageView)itemView.findViewById(R.id.img_food);
        saleIcon =(ImageView)itemView.findViewById(R.id.sale_icon);
        //favIcon=(ImageView)itemView.findViewById(R.id.fav);
        quickCart=(ImageView)itemView.findViewById(R.id.quick_cart);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }


}


