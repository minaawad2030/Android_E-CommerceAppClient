package com.medical.mina.markosmedicalsupplies;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medical.mina.markosmedicalsupplies.Common.Common;
import com.medical.mina.markosmedicalsupplies.Model.User;

import info.hoang8f.widget.FButton;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    Button btnSignUp,btnSignIn;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSignUp = (Button) findViewById(R.id.Btn_SignUp);
        btnSignIn = (Button) findViewById(R.id.Btn_SignIn);
       // NetworkConnectivity networkConnectivity = new NetworkConnectivity();
        //boolean ConnectionState = networkConnectivity.checkConnectivity(MainActivity.this);

        //Init Paper
        Paper.init(this);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent signIn = new Intent(MainActivity.this, SignIn.class);
            startActivity(signIn);
                }
            });

            btnSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent signUp = new Intent(MainActivity.this, SignUp.class);
                    startActivity(signUp);
                }
            });

        String user=Paper.book().read(Common.USER_KEY);
        String pass=Paper.book().read(Common.PASS_KEY);
        if(user!=null && pass!=null) {
            if (!user.isEmpty() && !pass.isEmpty()) {
                login(user, pass);
            }
        }
    }

    private void login(final String phone, final String pass) {
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference tb_user=database.getReference("User");
        if(Common.isConnectedToInternet(getBaseContext())) {

            final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
            mDialog.setMessage("Please Wait...");
            mDialog.show();

            tb_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //if the user not found in the database
                    if (dataSnapshot.child(phone).exists()) {
                        mDialog.dismiss();
                        //Get User Data
                        User user = dataSnapshot.child(phone).getValue(User.class);
                        user.setPhone(phone);
                        if (user.getpassword().equals(pass)) {
                            Intent homeIntent = new Intent(MainActivity.this, Home.class);
                            Common.curUser = user;
                            startActivity(homeIntent);
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Wrong Password or Phone", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        mDialog.dismiss();
                        Toast.makeText(MainActivity.this, "User Not found", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else
        {
            Toast.makeText(MainActivity.this,"Please Check Internet Connection",Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
