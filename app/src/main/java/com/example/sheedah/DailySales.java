package com.example.sheedah;

public class DailySales {
   public  String date;
    public Double totalSales;

    public DailySales  () {
        //default  constructor
    }

    public DailySales(String date, Double totalSales) {
        this.date = date;
        this.totalSales = totalSales;
    }
}
