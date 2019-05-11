package com.medical.mina.markosmedicalsupplies;

import android.content.Intent;
import android.graphics.Paint;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.medical.mina.markosmedicalsupplies.Common.Common;
import com.medical.mina.markosmedicalsupplies.Database.Database;
import com.medical.mina.markosmedicalsupplies.Interface.ItemClickListener;
import com.medical.mina.markosmedicalsupplies.Model.Category;
import com.medical.mina.markosmedicalsupplies.Model.Favorites;
import com.medical.mina.markosmedicalsupplies.Model.Food;
import com.medical.mina.markosmedicalsupplies.Model.Order;
import com.medical.mina.markosmedicalsupplies.ViewHolder.FoodViewHolder;
import com.medical.mina.markosmedicalsupplies.ViewHolder.MenuViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference list;
    String CategoryId="";
    FirebaseRecyclerAdapter<Food,FoodViewHolder> adapter;
    SwipeRefreshLayout refreshLayout;

    //Search
    FirebaseRecyclerAdapter<Food,FoodViewHolder> searchAdapter;
    List<String> suggustList=new ArrayList<>();
    MaterialSearchBar searchBar;


    //Fav
    Database LocalDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);
        database=FirebaseDatabase.getInstance();
        list=database.getReference("Food");
        //relativeLayout=(RelativeLayout)findViewById(R.id.rl_menu_item);
        recyclerView=(RecyclerView)findViewById(R.id.food_recylcler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Refresh Layout
        refreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if(getIntent()!=null){
                    CategoryId=getIntent().getStringExtra("CategoryId");
                }
                if(!CategoryId.isEmpty()&&CategoryId!=null){
                    if(Common.isConnectedToInternet(getBaseContext())) {
                        loadList(CategoryId);
                    }else
                    {
                        Toast.makeText(FoodList.this,"Please Check Internet Connection",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });



        refreshLayout.post(new Runnable() {
            @Override
            public void run() {

                if(getIntent()!=null){
                    CategoryId=getIntent().getStringExtra("CategoryId");
                }
                if(!CategoryId.isEmpty()&&CategoryId!=null){
                    if(Common.isConnectedToInternet(getBaseContext())) {
                        loadList(CategoryId);
                    }else
                    {
                        Toast.makeText(FoodList.this,"Please Check Internet Connection",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                //Search functions
                searchBar=(MaterialSearchBar) findViewById(R.id.searchBar);
                searchBar.setHint("Enter item name");
                loadSuggestions();
                searchBar.setCardViewElevation(10);
                searchBar.addTextChangeListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        List<String> suggest=new ArrayList<String>();
                        for(String search:suggustList){
                            if(search.toLowerCase().contains(searchBar.getText().toLowerCase()))
                                suggest.add(search);
                        }
                        searchBar.setLastSuggestions(suggest);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                    @Override
                    public void onSearchStateChanged(boolean b) {
                        if(!b){
                            recyclerView.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onSearchConfirmed(CharSequence charSequence) {
                        startItemSearch(charSequence);
                    }

                    @Override
                    public void onButtonClicked(int i) {

                    }
                });
            }

        });


        LocalDB=new Database(this);




    }

    private void startItemSearch(CharSequence charSequence) {
        Query searchByName=list.orderByChild("name").equalTo(charSequence.toString());
        FirebaseRecyclerOptions<Food> option=new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(searchByName,Food.class).build();

        searchAdapter=new FirebaseRecyclerAdapter<Food, FoodViewHolder>(option) {
            @Override
            protected void onBindViewHolder(@NonNull final FoodViewHolder viewHolder, int position, @NonNull final Food model) {
                viewHolder.f_name.setText(model.getName());
                String priceAfterDis=Common.priceAfterDiscount(model.getPrice(),model.getDiscount());
                Toast.makeText(FoodList.this, ""+priceAfterDis, Toast.LENGTH_SHORT).show();
                viewHolder.f_price.setText(priceAfterDis + " LE");

                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.f_img);

                if (!model.getDiscount().equals("0")) {

                    viewHolder.f_dis.setText("Save "+(Double.parseDouble(model.getPrice())-Double.parseDouble(priceAfterDis))+"!");
                    Picasso.with(getBaseContext()).load(R.drawable.sale).into(viewHolder.saleIcon);
                } else {
                    Picasso.with(getBaseContext()).load(R.drawable.empty).into(viewHolder.saleIcon);
                }
                final int local=position;
                //Quick Cart

                viewHolder.quickCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        double price=Double.parseDouble(model.getPrice());
                        double discount=Double.parseDouble(model.getDiscount());
                        double afterDiscount;
                        if(discount==0){
                            afterDiscount=price;
                        }else{
                            afterDiscount=price-(price/discount);
                        }
                        new Database(getBaseContext()).addToCart(new Order(
                                adapter.getRef(local).getKey(),
                                model.getName(),
                                "1",
                                String.valueOf(afterDiscount),
                                model.getDiscount()
                        ));
                        Toast.makeText(FoodList.this, "Item is added to cart!", Toast.LENGTH_SHORT).show();
                    }
                });

                //add favourites
                if (LocalDB.isFavourite(adapter.getRef(position).getKey(), Common.curUser.getPhone()))
                    viewHolder.favIcon.setImageResource(R.drawable.ic_favorite_black_24dp);

                viewHolder.favIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Favorites favorites = new Favorites();
                        favorites.setFoodId(adapter.getRef(local).getKey());
                        favorites.setFoodName(model.getName());
                        favorites.setFoodDescription(model.getDescription());
                        favorites.setFoodDiscount(model.getDiscount());
                        favorites.setFoodImage(model.getImage());
                        favorites.setFoodMenuId(model.getMenuId());
                        favorites.setFoodPrice(model.getPrice());
                        favorites.setUserPhone(Common.curUser.getPhone());


                        if (!LocalDB.isFavourite(adapter.getRef(local).getKey(), Common.curUser.getPhone())) {
                            LocalDB.addToFavourite(favorites);
                            viewHolder.favIcon.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(FoodList.this, "" + model.getName() + " is added to Favorites", Toast.LENGTH_SHORT).show();
                        } else {

                            LocalDB.removeFromFavourite(adapter.getRef(local).getKey());
                            viewHolder.favIcon.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(FoodList.this, "" + model.getName() + " is removed from Favorites", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Intent
                        Intent intent = new Intent(FoodList.this, FoodDetails.class);
                        intent.putExtra("FoodId",searchAdapter.getRef(position).getKey());
                        startActivity(intent);
                    }
                });
            }

            @Override
            public FoodViewHolder onCreateViewHolder(ViewGroup parent, int i) {
                View itemView= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_itm,parent,false);
                return new FoodViewHolder(itemView);
            }
        };
        searchAdapter.startListening();
        recyclerView.setAdapter(searchAdapter);
    }

    private void loadSuggestions() {

        list.orderByChild("menuId").equalTo(CategoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                        {
                            Food itm=postSnapshot.getValue(Food.class);
                            suggustList.add(itm.getName());
                        }
                        searchBar.setLastSuggestions(suggustList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void loadList(String categoryId) {
        Query searchByName=list.orderByChild("menuId").equalTo(categoryId);
        FirebaseRecyclerOptions<Food> option=new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(searchByName,Food.class).build();

        adapter= new FirebaseRecyclerAdapter<Food,FoodViewHolder>(option) {
            @Override
            protected void onBindViewHolder(@NonNull final FoodViewHolder viewHolder, int position, @NonNull final Food model) {
                viewHolder.f_name.setText(model.getName());
                final String priceAfterDis=Common.priceAfterDiscount(model.getPrice(),model.getDiscount());
                viewHolder.f_price.setText(priceAfterDis + " LE");
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.f_img);

                if (!model.getDiscount().equals("0")) {
                    viewHolder.f_was.setText("Was");
                    viewHolder.f_dis.setText(model.getPrice()+" L.E.");
                    viewHolder.f_dis.setPaintFlags(viewHolder.f_dis.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
                    Picasso.with(getBaseContext()).load(R.drawable.sale).into(viewHolder.saleIcon);
                } else {
                    viewHolder.f_was.setText("");
                    viewHolder.f_dis.setText("");
                    Picasso.with(getBaseContext()).load(R.drawable.empty).into(viewHolder.saleIcon);
                }
                final int local=position;
                //Quick Cart
                viewHolder.quickCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        new Database(getBaseContext()).addToCart(new Order(
                                adapter.getRef(local).getKey(),
                                model.getName(),
                                "1",
                                priceAfterDis,
                                model.getDiscount()
                        ));
                        Toast.makeText(FoodList.this, "Item is added to cart!", Toast.LENGTH_SHORT).show();
                    }
                });

                //add favourites
                if (LocalDB.isFavourite(adapter.getRef(position).getKey(), Common.curUser.getPhone()))
                    viewHolder.favIcon.setImageResource(R.drawable.ic_favorite_black_24dp);

                viewHolder.favIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Favorites favorites = new Favorites();
                        favorites.setFoodId(adapter.getRef(local).getKey());
                        favorites.setFoodName(model.getName());
                        favorites.setFoodDescription(model.getDescription());
                        favorites.setFoodDiscount(model.getDiscount());
                        favorites.setFoodImage(model.getImage());
                        favorites.setFoodMenuId(model.getMenuId());
                        favorites.setFoodPrice(model.getPrice());
                        favorites.setUserPhone(Common.curUser.getPhone());


                        if (!LocalDB.isFavourite(adapter.getRef(local).getKey(), Common.curUser.getPhone())) {
                            LocalDB.addToFavourite(favorites);
                            viewHolder.favIcon.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(FoodList.this, "" + model.getName() + " is added to Favorites", Toast.LENGTH_SHORT).show();
                        } else {

                            LocalDB.removeFromFavourite(adapter.getRef(local).getKey());
                            viewHolder.favIcon.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(FoodList.this, "" + model.getName() + " is removed from Favorites", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Intent
                        Intent intent = new Intent(FoodList.this, FoodDetails.class);
                        intent.putExtra("FoodId", adapter.getRef(position).getKey());
                        startActivity(intent);
                    }
                });
            }


            @Override
            public FoodViewHolder onCreateViewHolder(ViewGroup parent, int i) {
                View itemView= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_itm,parent,false);
                return new FoodViewHolder(itemView);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        refreshLayout.setRefreshing(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null)
            adapter.startListening();
    }
}
