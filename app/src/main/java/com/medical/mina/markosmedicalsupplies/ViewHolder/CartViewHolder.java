package com.medical.mina.markosmedicalsupplies.ViewHolder;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.medical.mina.markosmedicalsupplies.Interface.ItemClickListener;
import com.medical.mina.markosmedicalsupplies.Model.Order;
import com.medical.mina.markosmedicalsupplies.R;

import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Mina on 4/25/2018.
 */

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener , View.OnCreateContextMenuListener {

    public TextView cart_name,cart_price;
    public ElegantNumberButton btn_quantity;
    private ItemClickListener itemClickListener;

    public CartViewHolder(View itemView) {
        super(itemView);
        cart_name=(TextView)itemView.findViewById(R.id.cart_itm_name);
        cart_price=(TextView)itemView.findViewById(R.id.cart_itm_price);
        btn_quantity=(ElegantNumberButton) itemView.findViewById(R.id.btn_qty);
        itemView.setOnCreateContextMenuListener(this);
    }

    public void setCartName (TextView cart_name){
        this.cart_name=cart_name;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select action");
        contextMenu.add(0,0,getAdapterPosition(),"Delete");


    }


}
