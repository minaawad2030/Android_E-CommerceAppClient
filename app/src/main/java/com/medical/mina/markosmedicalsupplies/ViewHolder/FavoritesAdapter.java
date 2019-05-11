package com.medical.mina.markosmedicalsupplies.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.medical.mina.markosmedicalsupplies.Common.Common;
import com.medical.mina.markosmedicalsupplies.Database.Database;
import com.medical.mina.markosmedicalsupplies.FavoritesActivity;
import com.medical.mina.markosmedicalsupplies.FoodDetails;
import com.medical.mina.markosmedicalsupplies.FoodList;
import com.medical.mina.markosmedicalsupplies.Interface.ItemClickListener;
import com.medical.mina.markosmedicalsupplies.Model.Favorites;
import com.medical.mina.markosmedicalsupplies.Model.Food;
import com.medical.mina.markosmedicalsupplies.Model.Order;
import com.medical.mina.markosmedicalsupplies.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Mina on 6/16/2018.
 */

public class FavoritesAdapter extends RecyclerView.Adapter<FavoriteViewHolder> {

    private Context context;
    private List<Favorites> favorites;

    public FavoritesAdapter(Context context, List<Favorites> favorites) {
        this.context = context;
        this.favorites = favorites;
    }


    @Override
    public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView= LayoutInflater.from(context)
                .inflate(R.layout.favorite_item,parent,false);

        return new FavoriteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FavoriteViewHolder viewHolder, final int position) {
        viewHolder.f_name.setText(favorites.get(position).getFoodName());
        viewHolder.f_price.setText(favorites.get(position).getFoodPrice() + " LE");
        Picasso.with(context).load(favorites.get(position).getFoodImage()).into(viewHolder.f_img);

        if (!favorites.get(position).getFoodDiscount().equals("0")) {
            Picasso.with(context).load(R.drawable.sale).into(viewHolder.saleIcon);
        } else {
            Picasso.with(context).load(R.drawable.empty).into(viewHolder.saleIcon);
        }
        final int local=position;
        //Quick Cart
        viewHolder.quickCart.setOnClickListener(new View.OnClickListener() {
            boolean isExists=new Database(context).isFavourite(favorites.get(position).getFoodId(),Common.curUser.getPhone());
            @Override
            public void onClick(View view) {
                if (!isExists) {
                    new Database(context).addToCart(new Order(
                            favorites.get(local).getFoodId(),
                            favorites.get(local).getFoodName(),
                            "1",
                            favorites.get(local).getFoodPrice(),
                            favorites.get(local).getFoodDiscount()
                    ));

                }else{
                    //SOme code is going to be here
                }
                Toast.makeText(context, "Item is added to cart!", Toast.LENGTH_SHORT).show();
            }
        });


    final Favorites localfav=favorites.get(position);
    viewHolder.setItemClickListener(new ItemClickListener() {
        @Override
        public void onClick(View view, int position, boolean isLongClick) {
            Intent foodDetail=new Intent(context,FoodDetails.class);
            foodDetail.putExtra("FoodId",favorites.get(position).getFoodId());
            context.startActivity(foodDetail);
        }
    });

    }
    @Override
    public int getItemCount() {
        return favorites.size();
    }
}
