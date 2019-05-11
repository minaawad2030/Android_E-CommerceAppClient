package com.medical.mina.markosmedicalsupplies;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.medical.mina.markosmedicalsupplies.Common.Common;
import com.medical.mina.markosmedicalsupplies.Database.Database;
import com.medical.mina.markosmedicalsupplies.Model.MyResponse;
import com.medical.mina.markosmedicalsupplies.Model.Notification;
import com.medical.mina.markosmedicalsupplies.Model.Order;
import com.medical.mina.markosmedicalsupplies.Model.Request;
import com.medical.mina.markosmedicalsupplies.Model.Sender;
import com.medical.mina.markosmedicalsupplies.Model.Token;
import com.medical.mina.markosmedicalsupplies.Remote.APIService;
import com.medical.mina.markosmedicalsupplies.ViewHolder.CartAdapter;
import com.medical.mina.markosmedicalsupplies.ViewHolder.CartViewHolder;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Double.parseDouble;

public class Cart extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    DatabaseReference databaseReference;
    FirebaseDatabase database;
    public TextView totalPrice;
    FButton fButton;
    List<Order> cart=new ArrayList<>();
    CartAdapter Adapter;


    APIService mSerive;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        mSerive=Common.getFCMService();

        recyclerView=(RecyclerView) findViewById(R.id.rv_cart_cart);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(Cart.this));
        cart=new Database(this).getCarts();
        Adapter=new CartAdapter(cart,this);
        Adapter.notifyDataSetChanged();
        recyclerView.setAdapter(Adapter);
        totalPrice=(TextView)findViewById(R.id.tv_total);

        database=FirebaseDatabase.getInstance();
        databaseReference=database.getReference("Requests");

        fButton=(FButton)findViewById(R.id.Btn_placeorder);
        fButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cart.size()>0)
                    showAlertDialog();//de bta3t el address
                else
                    Toast.makeText(Cart.this,"The Cart is Empty !",Toast.LENGTH_SHORT).show();
            }
        });

        loadList();
    }

    private void showAlertDialog() {
        final AlertDialog.Builder alertDialog=new AlertDialog.Builder(Cart.this);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);
        alertDialog.setTitle("Provide your address");
        alertDialog.setMessage("Please Enter a valid address: ");

        LayoutInflater inflater=this.getLayoutInflater();
        View order_address=inflater.inflate(R.layout.order_address_comment,null);
        final MaterialEditText address=(MaterialEditText)order_address.findViewById(R.id.edt_address);
        final MaterialEditText comment=(MaterialEditText)order_address.findViewById(R.id.edt_comment);

        alertDialog.setView(order_address);
        alertDialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Request request=new Request(
                        Common.curUser.getPhone(),
                        Common.curUser.getName(),
                        address.getText().toString(),
                        totalPrice.getText().toString(),
                        cart,
                        comment.getText().toString(),
                        "0"
                );
                String orderNumber=String.valueOf(System.currentTimeMillis());
                databaseReference.child(orderNumber).setValue(request);
                new Database(getBaseContext()).CleanCart();

                sendNotificationOrder(orderNumber);


                Toast.makeText(Cart.this,"Order is submitted",Toast.LENGTH_LONG).show();
                finish();
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    private void sendNotificationOrder(final String orderNumber) {

        DatabaseReference tokens=FirebaseDatabase.getInstance().getReference("Tokens");
        Query data=tokens.orderByChild("isServerToken").equalTo(true);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapShot: dataSnapshot.getChildren()){
                    Token serverToken=postSnapShot.getValue(Token.class);

                    Notification notification=new Notification("Markos Medical Supplies","You have Submitted an order with number: "+orderNumber);
                    Sender content=new Sender(serverToken.getToken(),notification);
                    mSerive.sendNotification(content)
                    .enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if(response.code()==200) {
                                if (response.body().success == 1) {
                                    Toast.makeText(Cart.this, "Order is submitted", Toast.LENGTH_LONG).show();
                                    finish();
                                } else {
                                    Toast.makeText(Cart.this, "Failed !", Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable throwable) {
                            Log.e("Error", throwable.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadList() {

        cart=new Database(this).getCarts();
        Adapter=new CartAdapter(cart,this);
        Adapter.notifyDataSetChanged();
        recyclerView.setAdapter(Adapter);
        double total=0;
        for(Order order:cart){

            total+=(parseDouble(order.getPrice()))*(parseDouble(order.getQuantity()));
        }
        Locale locale=new Locale("en","US");
        NumberFormat format=NumberFormat.getCurrencyInstance(locale);
        totalPrice.setText(format.format(total));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.DELETE)) 
            deleteCart(item.getOrder());
        return true;
        
    }

    private void deleteCart(int order) {
        cart.remove(order);
        new Database(this).CleanCart();
        for (Order item:cart){
            new Database(this).addToCart(item);
        }
        loadList();
    }
}
