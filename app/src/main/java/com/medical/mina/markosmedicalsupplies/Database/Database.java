package com.medical.mina.markosmedicalsupplies.Database;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.medical.mina.markosmedicalsupplies.Model.Favorites;
import com.medical.mina.markosmedicalsupplies.Model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mina on 4/21/2018.
 */

public class Database extends SQLiteAssetHelper {
    private static final String DB_NAME = "Ga3n.db";
    private static final int DB_VERSION = 1;

    public Database(Context context) {

        super(context, DB_NAME, null, DB_VERSION);
    }

    public List<Order> getCarts() {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        String[] sqlSelect = {"ID","ProductName", "ProductId", "Quantity", "Price", "Discount"};
        String sqlTable = "OrderDetail";
        queryBuilder.setTables(sqlTable);
        Cursor cursor = queryBuilder.query(db, sqlSelect, null, null, null, null, null);
        final List<Order> result = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                result.add(new Order(
                        cursor.getInt(cursor.getColumnIndex("ID")),
                        cursor.getString(cursor.getColumnIndex("ProductId")),
                        (cursor.getString(cursor.getColumnIndex("ProductName"))),
                        (cursor.getString(cursor.getColumnIndex("Quantity"))),
                        (cursor.getString(cursor.getColumnIndex("Price"))),
                        (cursor.getString(cursor.getColumnIndex("Discount")))
                ));
            } while (cursor.moveToNext());
        }
        return result;
    }


    public void addToCart(Order order){
        SQLiteDatabase db=getWritableDatabase();
        String query=String.format("INSERT INTO OrderDetail(ProductId,ProductName,Quantity,Price,Discount) VALUES ('%s','%s','%s','%s','%s');",
                order.getProductId(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice(),
                order.getDiscount()
        );
        db.execSQL(query);

    }

 /*   public void CheckforEntry(Order order){
        SQLiteDatabase db=getReadableDatabase();
        String query="SELECT* FROM OrderDetail WHERE ProductName="+order.getProductId()+";";
        Cursor cursor=db.query("OrderDetail",new String[]{"ProductName", "ProductId", "Quantity", "Price", "Discount"},null,null,null,null,null);
        List<Order> result=new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                result.add(new Order(cursor.getString(cursor.getColumnIndex("ProductId")),
                        (cursor.getString(cursor.getColumnIndex("ProductName"))),
                        (cursor.getString(cursor.getColumnIndex("Quantity"))),
                        (cursor.getString(cursor.getColumnIndex("Price"))),
                        (cursor.getString(cursor.getColumnIndex("Discount")))
                ));
            } while (cursor.moveToNext());
        }
        int quantity=0;
        Order newOrder=new Order();
        boolean newOrdercount=true;
        for (int i=0;i<cursor.getCount();i++){
            if(order.getProductId()==cursor.getString(cursor.getColumnIndex("ProductId"))){
                newOrder.setProductId(order.getProductId());
                newOrder.setDiscount(order.getDiscount());
                newOrder.setPrice(order.getPrice());
                newOrder.setProductName(order.getProductName());
                if(newOrdercount)
                quantity= Integer.parseInt(order.getQuantity())+quantity+Integer.parseInt(cursor.getString(cursor.getColumnIndex("Quantity")));
                else {
                    quantity = quantity + Integer.parseInt(cursor.getString(cursor.getColumnIndex("Quantity")));
                    newOrdercount=false;
                }
                newOrder.setQuantity(Integer.toString(quantity));

            }
        }
        delete(cursor.getString(cursor.getColumnIndex("ProductName")));
        addToCart(newOrder);
    }

    void delete(String name){
        SQLiteDatabase db=getWritableDatabase();
        db.delete("OrderDetail","ProductName="+name,null);
    }
*/
    public void CleanCart(){
        SQLiteDatabase db=getReadableDatabase();
        String query=String.format("DELETE FROM OrderDetail");
        db.execSQL(query);

    }

    public void addToFavourite(Favorites food){
        SQLiteDatabase database=getReadableDatabase();
        String Query=String.format("INSERT INTO Favorites(" +
                "FoodId,FoodName,FoodPrice,FoodMenuId,FoodImage,FoodDiscount,FoodDescription,UserPhone)" +
                " VALUES('%s','%s','%s','%s','%s','%s','%s','%s');"
                ,food.getFoodId()
                ,food.getFoodName()
                ,food.getFoodPrice()
                ,food.getFoodMenuId()
                ,food.getFoodImage()
                ,food.getFoodDiscount()
                ,food.getFoodDescription()
                ,food.getUserPhone()
        );
        database.execSQL(Query);

    }

    public void removeFromFavourite(String foodID){
        SQLiteDatabase database=getReadableDatabase();
        String Query=String.format("DELETE FROM Favorites WHERE FoodId='%s';",foodID);
        database.execSQL(Query);

    }

    public boolean isFavourite(String foodID,String userPhone){
        SQLiteDatabase database=getReadableDatabase();
        String Query=String.format("SELECT * FROM Favorites WHERE FoodId='%s' AND userPhone='%s';",foodID,userPhone);
        Cursor cursor=database.rawQuery(Query,null);
        if(cursor.getCount()<=0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;

    }

    public List<Favorites> getAllFavorites(String userPhone) {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        String[] sqlSelect = {"UserPhone", "FoodId", "FoodName", "FoodPrice","FoodMenuId","FoodImage","FoodDiscount","FoodDescription"};
        String sqlTable = "Favorites";
        queryBuilder.setTables(sqlTable);
        Cursor cursor = queryBuilder.query(db, sqlSelect, "UserPhone=?",new String[]{userPhone}, null, null, null);
        final List<Favorites> result = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                result.add(new Favorites(
                         cursor.getString(cursor.getColumnIndex("FoodName")),
                         cursor.getString(cursor.getColumnIndex("FoodImage")),
                         cursor.getString(cursor.getColumnIndex("FoodDescription")),
                         cursor.getString(cursor.getColumnIndex("FoodPrice")),
                         cursor.getString(cursor.getColumnIndex("FoodDiscount")),
                         cursor.getString(cursor.getColumnIndex("FoodMenuId")),
                         cursor.getString(cursor.getColumnIndex("FoodId")),
                         cursor.getString(cursor.getColumnIndex("UserPhone"))

                ));
            } while (cursor.moveToNext());
        }
        return result;
    }

    public int getCountCart() {
        int count=0;
        SQLiteDatabase database=getReadableDatabase();
        String Query=String.format("SELECT COUNT(*) FROM OrderDetail");
        Cursor cursor=database.rawQuery(Query,null);
        if(cursor.moveToFirst()){
            do{
                count=cursor.getInt(0);
            }while (cursor.moveToNext());
        }
        return count;
    }

    public void updateCart(Order order) {
        SQLiteDatabase database=getReadableDatabase();
        String Query=String.format("UPDATE OrderDetail SET Quantity=%s WHERE ID=%d",order.getQuantity(),order.getID());
        database.execSQL(Query);
    }
}