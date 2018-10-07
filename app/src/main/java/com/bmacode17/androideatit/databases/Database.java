package com.bmacode17.androideatit.databases;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.bmacode17.androideatit.models.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 04-Jul-18.
 */

public class Database extends SQLiteAssetHelper {

    private static final String DB_NAME = "EatItDB.db";
    private static final int DB_VERSION = 1;
    private static final String TAG = "Basel";

    public Database(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public List<Order> getCarts(){

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"id","productId","productName","quantity","price","discount", "image"};
        String sqlTable = "OrderDetails";

        qb.setTables(sqlTable);
        Cursor cursor = qb.query(db,sqlSelect,null,null,null,null,null);

        final List<Order> results = new ArrayList<>();
        if(cursor.moveToFirst()){

            do{

                results.add(new Order(cursor.getInt(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("productId")),
                        cursor.getString(cursor.getColumnIndex("productName")),
                        cursor.getString(cursor.getColumnIndex("quantity")),
                        cursor.getString(cursor.getColumnIndex("price")),
                        cursor.getString(cursor.getColumnIndex("discount")),
                        cursor.getString(cursor.getColumnIndex("image"))));

            }while(cursor.moveToNext());
        }
        return results;
    }

    public void addToCarts(Order order){

        SQLiteDatabase db = getWritableDatabase();
        String query = String.format("INSERT INTO OrderDetails(productId,productName,quantity,price,discount,image) VALUES('%s','%s','%s','%s','%s','%s');",
                order.getProductId(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice(),
                order.getDiscount(),
                order.getImage());

        db.execSQL(query);
    }

    public void cleanCarts(){

        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetails");
        db.execSQL(query);
    }

    public void addToFavourites(String foodId){

        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO Favourites(foodId) VALUES('%s');",foodId);
        db.execSQL(query);
    }

    public void removeFromFavourites(String foodId){

        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM Favourites WHERE foodId = '%s';",foodId);
        db.execSQL(query);
    }

    public boolean isFavourites(String foodId){

        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT * FROM Favourites WHERE foodId = '%s';",foodId);
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.getCount() <=0){

            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public int getCountCarts() {
        int count = 0;
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT COUNT(*) FROM OrderDetails");
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                count = cursor.getInt(0);
            }while(cursor.moveToNext());
        }
        return count;
    }

    public void updateCart(Order order) {

        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE OrderDetails SET quantity = '%s' WHERE id = '%d';",order.getQuantity(),order.getId());
        db.execSQL(query);
    }
}
