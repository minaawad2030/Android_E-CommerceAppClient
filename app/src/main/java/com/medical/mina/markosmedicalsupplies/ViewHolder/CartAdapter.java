package com.medical.mina.markosmedicalsupplies.ViewHolder;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amulyakhare.textdrawable.TextDrawable;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.medical.mina.markosmedicalsupplies.Cart;
import com.medical.mina.markosmedicalsupplies.Database.Database;
import com.medical.mina.markosmedicalsupplies.Model.Order;
import com.medical.mina.markosmedicalsupplies.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.lang.Double.parseDouble;

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> implements View.OnCreateContextMenuListener
{
    private List<Order> list=new ArrayList<>();
    private Cart cart;

    public CartAdapter(List<Order> list,Cart cart){
        this.list=list;
        this.cart=cart;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(cart);
        View itmView=inflater.inflate(R.layout.cart_layout,parent,false);
        return new CartViewHolder(itmView);
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, final int position) {
        holder.btn_quantity.setNumber(list.get(position).getQuantity());
        holder.btn_quantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                Order order= list.get(position);
                order.setQuantity(String.valueOf(newValue));
                new Database(cart).updateCart(order);


                //TOTAL PRICE UPDATE

                double total=0;
                List<Order> ordersUpdate=new Database(cart).getCarts();
                for(Order item :ordersUpdate){

                    total+=(parseDouble(item.getPrice()))*(parseDouble(item.getQuantity()));
                }
                Locale locale=new Locale("en","US");
                NumberFormat format=NumberFormat.getCurrencyInstance(locale);
                cart.totalPrice.setText(format.format(total));
            }
        });


        //TextDrawable textDrawable=TextDrawable.builder().buildRound(""+list.get(position).getQuantity(), Color.argb(255,241,196,15));
       // holder.cart_img_count.setImageDrawable(textDrawable);
        Locale locale=new Locale("en","US");
        NumberFormat format=NumberFormat.getCurrencyInstance(locale);
        double price = (parseDouble(list.get(position).getPrice()))*(parseDouble(list.get(position).getQuantity()));
        //edit subtotal
        holder.cart_price.setText(format.format(price));
        holder.cart_name.setText(list.get(position).getProductName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

    }
}
