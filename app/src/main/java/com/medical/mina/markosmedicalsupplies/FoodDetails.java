package com.medical.mina.markosmedicalsupplies;


import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medical.mina.markosmedicalsupplies.Common.Common;
import com.medical.mina.markosmedicalsupplies.Database.Database;
import com.medical.mina.markosmedicalsupplies.Model.Food;
import com.medical.mina.markosmedicalsupplies.Model.Order;
import com.squareup.picasso.Picasso;

public class FoodDetails extends AppCompatActivity {
    TextView foodName,foodPrice,foodDescription,foodSave;
    ImageView foodImage;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton cart;
    ElegantNumberButton elegantNumberButton;
    String foodId="";
    Food food;
    double price , finalprice,discount;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);

        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("Food");

        foodSave=(TextView)findViewById(R.id.detail_save);
        foodImage=(ImageView)findViewById(R.id.img_food_details);
        foodName=(TextView)findViewById(R.id.tv_food_details_name);
        foodPrice=(TextView)findViewById(R.id.tv_food_details_price);
        foodDescription=(TextView)findViewById(R.id.tv_food_details_description);
        cart=(FloatingActionButton)findViewById(R.id.cart_food_details);
        collapsingToolbarLayout=(CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapesedAppBar);
        elegantNumberButton=(ElegantNumberButton) findViewById(R.id.number_button);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Database(getBaseContext()).addToCart(new Order(
                        foodId ,
                        food.getName(),
                        elegantNumberButton.getNumber(),
                        String.valueOf(finalprice),
                        food.getDiscount()
                        ));
                AlertBoxDialog();
               // Toast.makeText(FoodDetails.this,"Added To Cart",Toast.LENGTH_SHORT).show();
            }
        });
        if(getIntent()!=null)
        {
            foodId=getIntent().getStringExtra("FoodId");
        }
        if(!foodId.isEmpty()&&foodId!=null){
            if(Common.isConnectedToInternet(getBaseContext())) {
                getFoodDetails(foodId);
            }else
            {
                Toast.makeText(this,"Please Check Internet Connection",Toast.LENGTH_SHORT).show();
                return;
            }
        }


    }

    private void AlertBoxDialog() {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(FoodDetails.this);
        alertDialog.setTitle("Item(s) is added to cart");
        alertDialog.setMessage("Go to cart to submit order ?!");
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);
        alertDialog.setPositiveButton("Go to cart", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent=new Intent(FoodDetails.this,Cart.class);
                startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Continue Shopping", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    private void getFoodDetails(String foodId) {

        databaseReference.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                food=dataSnapshot.getValue(Food.class);
                collapsingToolbarLayout.setTitle(food.getName());
                Picasso.with(getBaseContext()).load(food.getImage()).into(foodImage);
                 price=Double.parseDouble(food.getPrice());
                 discount=Double.parseDouble(food.getDiscount());
                 finalprice=price ;
                if(discount==0) {
                    foodSave.setText("");
                    finalprice=price;
                }else{
                    finalprice = price - (price / discount);
                    foodSave.setText("Save "+(price-finalprice)+" L.E!");
                }
                foodPrice.setText(String.valueOf(finalprice));
                foodName.setText(food.getName());
                foodDescription.setText(food.getDescription());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
