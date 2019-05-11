package com.medical.mina.markosmedicalsupplies.Model;

/**
 * Created by Mina on 9/18/2018.
 */

public class Token {
    private String token;
    private boolean isServerToken;

    public Token(){

    }
    public Token(String token, boolean isServerToken) {
        this.token = token;
        this.isServerToken = isServerToken;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isServerToken() {
        return isServerToken;
    }

    public void setServerToken(boolean serverToken) {
        isServerToken = serverToken;
    }
}
