package com.medical.mina.markosmedicalsupplies.Service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.iid.zzi;
import com.medical.mina.markosmedicalsupplies.Common.Common;
import com.medical.mina.markosmedicalsupplies.Model.Token;

/**
 * Created by Mina on 9/18/2018.
 */

public class MyFirebaseService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String tokenRefreshed=FirebaseInstanceId.getInstance().getToken();
        if(Common.curUser!=null) {
            updateTokenToFirebase(tokenRefreshed);
        }

    }

    private void updateTokenToFirebase(String tokenRefreshed) {
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference tokens=db.getReference("Tokens");
        Token token=new Token(tokenRefreshed,false);//client
        tokens.child(Common.curUser.getPhone()).setValue(token);

    }

}
