package com.medical.mina.markosmedicalsupplies.Remote;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
;

import com.medical.mina.markosmedicalsupplies.Model.MyResponse;
import com.medical.mina.markosmedicalsupplies.Model.Sender;

/**
 * Created by Mina on 9/18/2018.
 */

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAck1syRI:APA91bGDvqqfVzHnFa4bQdRSrlFd_Qd0I2Ab1XRPzEBUmtqj8dVlxomjt-7XcXoy9Mc0_gxqoNXz1XBOaajHK9yakuYXgyX7H3IbS-KQ8M2Ia92nZaomCC_-j_hBSL0cri34bU3ROye_"

            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
