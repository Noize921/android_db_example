package com.example.myapplication.database;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.LinearGradient;
import android.util.SparseArray;
import android.util.Log;

import com.example.myapplication.database.model.Products;
import com.example.myapplication.database.model.Sales;
import com.example.myapplication.service.ProductService;
import com.example.myapplication.service.SaleService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ProductDatabaseHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = "my_debug_log";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "sales.db";
    private static final String SALES_PATH = "sales.txt";
    private static final String PRODUCTS_PATH = "products.txt";
    private static final String SALES_TABLE_NAME = "sales";
    private static final String PRODUCTS_TABLE_NAME = "products";
    private Context context;
    private List<String> fields;
    private List<String> dataTypes;
    private List<List<String>> data;
    private int fieldsNumber;

    public ProductDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        setTable(PRODUCTS_PATH);
        db.execSQL(ProductService.create(PRODUCTS_TABLE_NAME, fields, dataTypes, fieldsNumber));
        ProductService.saveAllFromFile(PRODUCTS_TABLE_NAME, fields, data, db);

        setTable(SALES_PATH);
        db.execSQL(SaleService.create(SALES_TABLE_NAME, fields, dataTypes, fieldsNumber));
        SaleService.saveAllFromFile(SALES_TABLE_NAME, fields, data, db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SALES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PRODUCTS_TABLE_NAME);

        onCreate(db);
    }

    public List<Products> getAllProducts() {
        setTable(PRODUCTS_PATH);
        List<Products> result = new ArrayList<>();
        String query = "SELECT * FROM " + PRODUCTS_TABLE_NAME;

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Products product = new Products();
                product.setId(cursor.getInt(cursor.getColumnIndex(fields.get(0))));
                product.setName(cursor.getString(cursor.getColumnIndex(fields.get(1))));
                product.setUnitOfMeasure(cursor.getString(cursor.getColumnIndex(fields.get(2))));
                product.setPrice(cursor.getDouble(cursor.getColumnIndex(fields.get(3))));
                product.setCategoryId(cursor.getInt(cursor.getColumnIndex(fields.get(4))));

                result.add(product);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return result;
    }

    public List<Sales> getAllSales() {
        setTable(SALES_PATH);
        List<Sales> result = new ArrayList<>();
        String query = "SELECT * FROM " + SALES_TABLE_NAME;

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Sales sales = new Sales();
                sales.setProductId(cursor.getInt(cursor.getColumnIndex(fields.get(0))));
                sales.setQuantitySold(cursor.getDouble(cursor.getColumnIndex(fields.get(1))));
                sales.setDate(cursor.getString(cursor.getColumnIndex(fields.get(2))));

                result.add(sales);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return result;
    }

    public SparseArray<String> calcAllSales() {
        setTable(SALES_PATH);

        SparseArray<String> result = new SparseArray<>();
        List<String> dates = new ArrayList<>();
        String query = "SELECT DISTINCT " + fields.get(2) + " FROM " + SALES_TABLE_NAME;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                dates.add(cursor.getString(cursor.getColumnIndex(fields.get(2))));
            } while (cursor.moveToNext());
        }

        cursor.close();

        for(String date : dates) {
            String countQuery = "SELECT SUM(" + fields.get(1) + ") AS TOTAL_SALES FROM " + SALES_TABLE_NAME + " WHERE " + fields.get(2) + " = ?";
            Cursor countQueryResult = database.rawQuery(countQuery, new String[] {date});

            if (countQueryResult.moveToFirst()) {
                do {
                    result.put(countQueryResult.getInt(countQueryResult.getColumnIndex("TOTAL_SALES")), date);
                } while (cursor.moveToNext());
            }

            countQueryResult.close();
        }

        database.close();

        return result;
    }

    public SparseArray<String> calcProductSale(Integer productId) {
        setTable(SALES_PATH);

        SparseArray<String> result = new SparseArray<>();
        List<String> dates = new ArrayList<>();
        String query = "SELECT DISTINCT " + fields.get(2) + " FROM " + SALES_TABLE_NAME;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                dates.add(cursor.getString(cursor.getColumnIndex(fields.get(2))));
            } while (cursor.moveToNext());
        }

        cursor.close();

        for(String date : dates) {
            String countQuery = "SELECT SUM(" + fields.get(1) + ") AS SALES_NR FROM " + SALES_TABLE_NAME + " WHERE " + fields.get(2) + " = ? AND " + fields.get(0) + " = ?";
            Cursor countQueryResult = database.rawQuery(countQuery, new String[] {date, Integer.toString(productId)});

            if (countQueryResult.moveToFirst()) {
                do {
                    result.put(countQueryResult.getInt(countQueryResult.getColumnIndex("SALES_NR")), date);
                } while (cursor.moveToNext());
            }

            countQueryResult.close();
        }

        database.close();

        return result;
    }

    /*
     * Private methods
     * */

    private void setTable(String filePath) {
        SparseArray<List<String>> sparseArray = readFromFile(filePath);
        fields = getFields(sparseArray);
        dataTypes = getDataTypes(sparseArray);
        data = getData(sparseArray);
        fieldsNumber = getFieldsNumber(sparseArray);
    }

    private SparseArray<List<String>> readFromFile(String filePath) {
        SparseArray<List<String>> dataMap = new SparseArray<>();
        BufferedReader bufferedReader;
        String line;
        int lineIndex = 0;
        AssetManager assetManager = context.getAssets();

        try {
            InputStream inputStream = assetManager.open(filePath);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            while ((line = bufferedReader.readLine()) != null) {
                lineIndex++;

                List<String> lineList = Arrays.asList(line.split("[\\s]+"));
                dataMap.append(lineIndex, lineList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataMap;
    }

    private List<String> getFields(SparseArray<List<String>> dataMap){

        return dataMap.get(1);
    }

    private List<String> getDataTypes(SparseArray<List<String>> dataMap){

        return dataMap.get(2);
    }

    private List<List<String>> getData(SparseArray<List<String>> dataMap) {
        List<List<String>> result = new ArrayList<>();

        for (int i = 3; i <= dataMap.size(); i++) {
            result.add(dataMap.get(i));
        }

        return result;
    }

    private int getFieldsNumber(SparseArray<List<String>> dataMap) {

        return dataMap.get(1).size();
    }
}
