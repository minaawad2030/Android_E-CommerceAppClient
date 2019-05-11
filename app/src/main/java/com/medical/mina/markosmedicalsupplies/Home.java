package com.medical.mina.markosmedicalsupplies;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.medical.mina.markosmedicalsupplies.Common.Common;
import com.medical.mina.markosmedicalsupplies.Database.Database;
import com.medical.mina.markosmedicalsupplies.Interface.ItemClickListener;
import com.medical.mina.markosmedicalsupplies.Model.Category;
import com.medical.mina.markosmedicalsupplies.Model.Token;
import com.medical.mina.markosmedicalsupplies.ViewHolder.MenuViewHolder;
import com.squareup.picasso.Picasso;

import io.paperdb.Paper;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView tvName;
    FirebaseDatabase database;
    DatabaseReference category;
    RecyclerView rv_menu;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Category,MenuViewHolder> adapter;
    SwipeRefreshLayout refreshLayout;
    CounterFab fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Main Menu");
        setSupportActionBar(toolbar);

        database= FirebaseDatabase.getInstance();
        category=database.getReference("Category");
        refreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if(Common.isConnectedToInternet(getBaseContext())) {
                    loadMenu();
                }else
                {
                    Toast.makeText(Home.this,"Please Check Internet Connection",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(Common.isConnectedToInternet(getBaseContext())) {
                    loadMenu();

                }else
                {
                    Toast.makeText(Home.this,"Please Check Internet Connection",Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });

        //Initialize paper to clean it when signing out
        Paper.init(this);


        fab = (CounterFab) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent intent=new Intent(Home.this,Cart.class);
            startActivity(intent);
            }
        });

        fab.setCount(new Database(this).getCountCart());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header=navigationView.getHeaderView(0);
        tvName=(TextView) header.findViewById(R.id.tv_Name);
        tvName.setText(Common.curUser.getName());

        //load items from firebase

        rv_menu=(RecyclerView) findViewById(R.id.recyclerview);
        rv_menu.setHasFixedSize(true);
       // layoutManager =new LinearLayoutManager(this);
        //rv_menu.setLayoutManager(layoutManager);
        rv_menu.setLayoutManager(new GridLayoutManager(this,2));

        updateToken(FirebaseInstanceId.getInstance().getToken());

    }

    private void updateToken(String token) {

        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference tokens=db.getReference("Tokens");
        Token data=new Token(token,false);//client
        tokens.child(Common.curUser.getPhone()).setValue(data);

    }


    @Override
    protected void onResume() {
        super.onResume();
        fab.setCount(new Database(this).getCountCart());
        if(adapter!=null)
            adapter.startListening();
    }

    private void loadMenu(){

        FirebaseRecyclerOptions<Category> options=new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(category,Category.class)
                .build();

            adapter= new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull MenuViewHolder viewHolder, int i, @NonNull Category model) {
                    viewHolder.tv_name.setText(model.getName());
                    Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imageView);
                    final Category clickItem=model;
                    viewHolder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(View view, int position, boolean isLongClick) {
                            Intent intent = new Intent(Home.this, FoodList.class);
                            intent.putExtra("CategoryId", adapter.getRef(position).getKey());
                            startActivity(intent);
                        }
                    });
                }

                @Override
                public MenuViewHolder onCreateViewHolder(ViewGroup parent, int i) {
                    View itemView= LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.menu_item,parent,false);
                    return new MenuViewHolder(itemView);
                }
            };
        adapter.startListening();
        rv_menu.setAdapter(adapter);
        refreshLayout.setRefreshing(false);

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            Intent intent =new Intent(Home.this,FavoritesActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_cart) {
            Intent intent =new Intent(Home.this,Cart.class);
            startActivity(intent);
        } else if (id == R.id.nav_orders) {
            Intent intent=new Intent(Home.this,Orders.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            //delete paper
            Paper.book().destroy();


            Intent intent=new Intent(Home.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
