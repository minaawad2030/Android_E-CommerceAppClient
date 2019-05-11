package com.medical.mina.markosmedicalsupplies.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

import com.medical.mina.markosmedicalsupplies.Model.User;
import com.medical.mina.markosmedicalsupplies.Remote.APIService;
import com.medical.mina.markosmedicalsupplies.Remote.RetrofitClient;

/**
 * Created by Mina on 3/13/2018.
 */

public class Common  {
    public static User curUser;

    public static final String DELETE="Delete";
    public static final String USER_KEY="User";
    public static final String PASS_KEY="Password";
    private static final String BASE_URL="https://fcm.googleapis.com/";

    public static APIService getFCMService(){
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }



    public static boolean isConnectedToInternet (Context context){
        ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager!=null){
            NetworkInfo[] info=connectivityManager.getAllNetworkInfo();
            if(info !=null){
                for(int i=0;i<info.length;i++){
                    if(info[i].getState()== NetworkInfo.State.CONNECTED){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static String convertCode(String code){
        if(code.equals("0")){
            return "submitted";
        }else if(code.equals("1")){
            return "Out for delivery";
        }else{
            return "Delivered";
        }
    }

    public static String priceAfterDiscount(String price,String discount){
        double p=Double.parseDouble(price);
        double d=Double.parseDouble(discount);
        double finalPrice;
        if(d==0){
            finalPrice=p;
        }
        else{
            finalPrice=p-(p/d);
        }
        return String.valueOf(finalPrice);
    }

}
