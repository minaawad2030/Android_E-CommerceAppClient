package com.medical.mina.markosmedicalsupplies;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class SignUp extends AppCompatActivity {

    MaterialEditText EtPhoneNum,EtPass,etName;
    Button btnSignUp;
    TextView tvSignup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        EtPhoneNum=(MaterialEditText)findViewById(R.id.ET_Phone_SignUp);
        EtPass=(MaterialEditText)findViewById(R.id.ET_Pass_SignUp);
        etName=(MaterialEditText)findViewById(R.id.ET_Name_SignUp);
        btnSignUp=(Button)findViewById(R.id.BtnSignUp_SignUp);
        tvSignup=(TextView)findViewById(R.id.tv_signup);
        final FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference tb_User=database.getReference("User");

        Typeface bebasFont=Typeface.createFromAsset(getAssets(),"fonts/BEBAS.ttf");
        tvSignup.setTypeface(bebasFont);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Common.isConnectedToInternet(getBaseContext())) {
                    final ProgressDialog mDialog = new ProgressDialog(SignUp.this);
                    mDialog.setMessage("Please Wait...");
                    mDialog.show();
                    tb_User.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Check if the phone number is already exists
                            if (dataSnapshot.child(EtPhoneNum.getText().toString()).exists()) {
                                mDialog.dismiss();
                                Toast.makeText(SignUp.this, "Already Registered", Toast.LENGTH_LONG).show();
                            } else {
                                mDialog.dismiss();
                                User user = new User(etName.getText().toString(), EtPass.getText().toString());
                                tb_User.child(EtPhoneNum.getText().toString()).setValue(user);
                                Toast.makeText(SignUp.this, "Done!!", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }else
                {
                    Toast.makeText(SignUp.this,"Please Check Internet Connection",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
}
