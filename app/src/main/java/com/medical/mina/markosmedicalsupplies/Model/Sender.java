package com.medical.mina.markosmedicalsupplies.Model;



/**
 * Created by Mina on 9/18/2018.
 */

public class Sender {
    public String to;
    public Notification notification;

    public Sender(String to, Notification notification) {
        this.to = to;
        this.notification = notification;
    }
}
