package com.medical.mina.markosmedicalsupplies;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.medical.mina.markosmedicalsupplies.Common.Common;
import com.medical.mina.markosmedicalsupplies.Model.Request;
import com.medical.mina.markosmedicalsupplies.ViewHolder.FoodViewHolder;
import com.medical.mina.markosmedicalsupplies.ViewHolder.OrdersViewHolder;

public class Orders extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    FirebaseRecyclerAdapter<Request,OrdersViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        database=FirebaseDatabase.getInstance();
        databaseReference=database.getReference("Requests");

        recyclerView=(RecyclerView)findViewById(R.id.rv_orders);
        recyclerView.setHasFixedSize(true);
        layoutManager =new LinearLayoutManager(Orders.this);
        recyclerView.setLayoutManager(layoutManager);


        if(getIntent().getStringExtra("userPhone") instanceof String) {
            loadOrders(getIntent().getStringExtra("userPhone"));
        }else{
            loadOrders(Common.curUser.getPhone());

        }
    }

    private void loadOrders(String phone) {
        Query getOrdersByUser=databaseReference.orderByChild("phone").equalTo(phone);
        FirebaseRecyclerOptions<Request> options=new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(getOrdersByUser,Request.class)
                .build();

        adapter= new FirebaseRecyclerAdapter<Request, OrdersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrdersViewHolder viewHolder, int position, @NonNull Request model) {

                viewHolder.orderID.setText("Order ID: "+adapter.getRef(position).getKey());
                viewHolder.orderSatus.setText("Status: "+CodeToStatus(model.getStatus()));
                viewHolder.orderPhone.setText("Phone Number: "+model.getPhone());
                viewHolder.orderAddress.setText("Address: "+model.getAddress());
            }

            @Override
            public OrdersViewHolder onCreateViewHolder(ViewGroup parent, int i) {
                View itemView= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.order_layout,parent,false);
                return new OrdersViewHolder(itemView);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onStart() {

        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {

        super.onStop();
        adapter.stopListening();
    }

    private String CodeToStatus(String status) {
       if(status.equals("0")){
           return "Submitted";
       }else if(status.equals("1")){
           return "Out For Delivery";
       }else {
           return "Delivered";
       }
    }
}
