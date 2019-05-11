package com.medical.mina.markosmedicalsupplies.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.medical.mina.markosmedicalsupplies.Interface.ItemClickListener;
import com.medical.mina.markosmedicalsupplies.R;

/**
 * Created by Mina on 4/28/2018.
 */

public class OrdersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView orderID,orderSatus,orderPhone,orderAddress;
    private ItemClickListener itemClickListener;

    public OrdersViewHolder(View itemView) {
        super(itemView);

        orderID=(TextView)itemView.findViewById(R.id.order_id);
        orderSatus=(TextView)itemView.findViewById(R.id.order_status);
        orderAddress=(TextView)itemView.findViewById(R.id.order_address);
        orderPhone=(TextView)itemView.findViewById(R.id.order_phone);

        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
//        itemClickListener.onClick(view,getAdapterPosition(),    false);


    }
    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener=itemClickListener;
    }
}
