package com.example.myapplication.service;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import java.util.List;

public class ProductService {
    private static final String LOG_TAG = "my_debug_log";

    public static String create(String tableName, List<String> fields, List<String> dataTypes, int fieldsNumber) {
        String query = "CREATE TABLE " + tableName + " (";

        for (int i = 0; i < fieldsNumber; i++) {
            if (i == fieldsNumber - 1) {
                query = query + fields.get(i) + " " + dataTypes.get(i) + ")";
                break;
            }

            query = query + fields.get(i) + " " + dataTypes.get(i) + ", ";
        }

        return query;
    }

    public static void saveAllFromFile(String tableName, List<String> fields, List<List<String>> data, SQLiteDatabase db) {
        ContentValues values = new ContentValues();

        for (int i = 0; i < data.size(); i++) {
            List<String> currentList = data.get(i);

            values.put(fields.get(0), currentList.get(0));
            values.put(fields.get(1), currentList.get(1));
            values.put(fields.get(2), currentList.get(2));

            db.insert(tableName, null, values);
        }
    }
}
