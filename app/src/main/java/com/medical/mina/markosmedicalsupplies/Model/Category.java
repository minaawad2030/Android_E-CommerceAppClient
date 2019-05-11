package com.medical.mina.markosmedicalsupplies.Model;



/**
 * Created by Mina on 3/13/2018.
 */

public class Category {

    private String Name;
    private String Image;

    public Category(){

    }

    public Category(String Name, String Image) {
        this.Name = Name;
        this.Image = Image;
    }

    public String getName() {
        return this.Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getImage() {
        return this.Image;
    }

    public void setImage(String Image) {
        this.Image = Image;
    }
}
