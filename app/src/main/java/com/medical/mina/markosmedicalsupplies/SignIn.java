package com.medical.mina.markosmedicalsupplies;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medical.mina.markosmedicalsupplies.Common.Common;
import com.medical.mina.markosmedicalsupplies.Model.User;
import com.rengwuxian.materialedittext.MaterialEditText;

import io.paperdb.Paper;

public class SignIn extends AppCompatActivity {
    MaterialEditText phoneNum,pass;
    Button btnSignIn;//Id bta3oo mn 3'ear _
    //Elli b _ da bta3 el main Screen intro
    TextView tvSignin;
    com.rey.material.widget.CheckBox remCheckbox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        phoneNum=(MaterialEditText)findViewById(R.id.ET_Phone);
        pass=(MaterialEditText)findViewById(R.id.ET_Pass);
        btnSignIn=(Button)findViewById(R.id.BtnSignIn);
        tvSignin=(TextView)findViewById(R.id.tv_signin);
        remCheckbox= (com.rey.material.widget.CheckBox) findViewById(R.id.rem_check_box);

        //Initialize paper
        Paper.init(this);

        Typeface bebas_font=Typeface.createFromAsset(getAssets(),"fonts/BEBAS.ttf");
        tvSignin.setTypeface(bebas_font);
        //Firebase
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference tb_user=database.getReference("User");

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Common.isConnectedToInternet(getBaseContext())) {

                    final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
                    mDialog.setMessage("Please Wait...");
                    mDialog.show();

                    if(remCheckbox.isChecked())
                    {
                        Paper.book().write(Common.USER_KEY,phoneNum.getText().toString());
                        Paper.book().write(Common.PASS_KEY,pass.getText().toString());

                    }

                    tb_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //if the user not found in the database
                            if (dataSnapshot.child(phoneNum.getText().toString()).exists()) {
                                mDialog.dismiss();
                                //Get User Data
                                User user = dataSnapshot.child(phoneNum.getText().toString()).getValue(User.class);
                                user.setPhone(phoneNum.getText().toString());
                                if (user.getpassword().equals(pass.getText().toString())) {
                                    Intent homeIntent = new Intent(SignIn.this, Home.class);
                                    Common.curUser = user;
                                    startActivity(homeIntent);
                                    finish();
                                } else {
                                    Toast.makeText(SignIn.this, "Wrong Password or Phone", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                mDialog.dismiss();
                                Toast.makeText(SignIn.this, "User Not found", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else
                    {
                    Toast.makeText(SignIn.this,"Please Check Internet Connection",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

    }
}
