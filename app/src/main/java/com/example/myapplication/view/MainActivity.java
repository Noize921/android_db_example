package com.example.myapplication.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.database.ProductDatabaseHelper;
import com.example.myapplication.database.model.Products;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private ProductDatabaseHelper db;
    private static final String LOG_TAG = "my_debug_log";
    BarChart chart;
    ArrayList<BarEntry> BARENTRY;
    ArrayList<String> BarEntryLabels;
    BarDataSet Bardataset;
    BarData BARDATA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new ProductDatabaseHelper(this);
        db.getReadableDatabase();

        SparseArray<String> sales = db.calcAllSales();
        List<Products> productsList = db.getAllProducts();
        List<String> productsName = new ArrayList<>();

        productsName.add("All");

        for (Products product : productsList) {
            productsName.add(capitalize(product.getName()) + "  (" + product.getUnitOfMeasure() + ")");
        }

        Spinner spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, productsName);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        chart = findViewById(R.id.barChart);
        BARENTRY = new ArrayList<>();
        BarEntryLabels = new ArrayList<String>();

        db.close();
    }

    public void addValuesToBARENTRY(SparseArray<String> sales){
        BARENTRY.clear();

        for (int i = 0; i < sales.size(); i++) {
            BARENTRY.add(new BarEntry(sales.keyAt(i), i));
        }

    }

    public void addValuesToBarEntryLabels(SparseArray<String> sales){
        BarEntryLabels.clear();

        for (int i = 0; i < sales.size(); i++) {
            BarEntryLabels.add(sales.valueAt(i));
        }

    }

    public String capitalize(String name) {

        return name.toLowerCase().substring(0,1).toUpperCase() + name.substring(1);
    }

    public Products containProduct(List<Products> productsList, String productName) {

        for (Products product : productsList) {
            if (product.getName().toLowerCase().equals(productName.toLowerCase())) {
                return product;
            }
        }

        return null;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        List<Products> productsList = db.getAllProducts();
        String item = parent.getItemAtPosition(position).toString();
        String productName = item.split(" ")[0];

        Products product;

        if ((product = containProduct(productsList, productName)) != null) {
            SparseArray<String> productSales = db.calcProductSale(product.getId());
            Log.d(LOG_TAG, productSales.toString());

            addValuesToBARENTRY(productSales);
            addValuesToBarEntryLabels(productSales);

            Bardataset = new BarDataSet(BARENTRY, "Sales");
            BARDATA = new BarData(BarEntryLabels, Bardataset);
            Bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
            chart.setData(BARDATA);
            chart.animateY(1500);
        } else {
            SparseArray<String> sales = db.calcAllSales();

            addValuesToBARENTRY(sales);
            addValuesToBarEntryLabels(sales);

            Bardataset = new BarDataSet(BARENTRY, "Sales");
            BARDATA = new BarData(BarEntryLabels, Bardataset);
            Bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
            chart.setData(BARDATA);
            chart.animateY(1500);
        }

        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
