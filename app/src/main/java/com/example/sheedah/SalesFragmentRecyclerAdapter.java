package com.example.sheedah;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

//class that contains recycler adapter classes for sales fragment (daily sales , monthly, and annual)
public class SalesFragmentRecyclerAdapter {
    private SalesFragmentRecyclerAdapter() {
    }

    //customer function function for converting date format
    public static String convertDateFormat(String dateString, String targetFormat) throws ParseException {
        SimpleDateFormat originalDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = originalDateFormat.parse(dateString);
        DateFormat newDateFormat = new SimpleDateFormat(targetFormat, Locale.ENGLISH);
        newDateFormat.setTimeZone(TimeZone.getDefault());
        String newDate = newDateFormat.format(date);
        return newDate;
    }

    //adapter class for daily sales fragment recycler
    public static class DailySalesAdapter extends RecyclerView.Adapter<SalesFragmentRecyclerAdapter.ViewHolder> {
        private ArrayList<DailySales> dailySalesList;

        public DailySalesAdapter(ArrayList<DailySales> dailySalesList) {
            this.dailySalesList = dailySalesList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sales_page_recyclerview_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (dailySalesList.size() > 0) {

                try {
                    holder.getSaleDate().setText(convertDateFormat(dailySalesList.get(position).date, "yyyy MMMM, dd"));
                } catch (ParseException e) {
                    holder.getSaleDate().setText(dailySalesList.get(position).date);
                }

                holder.getSaletotalAmount().setText("₦ " + dailySalesList.get(position).totalSales.toString());


            }
        }

        @Override
        public int getItemCount() {
            return dailySalesList.size();
        }


    }


    //adapter class for monthly sale fragment recyclerview
    public static class MonthlySalesAdapter extends RecyclerView.Adapter<SalesFragmentRecyclerAdapter.ViewHolder> {
        private ArrayList<DailySales> dailySalesList;
        Context context;
        private Map<String, Double> monthTotalAmount;
        ArrayList<String> date;
        ArrayList<Double> totalAmount;

        public MonthlySalesAdapter(ArrayList<DailySales> dailySalesList, Context context) {
            this.dailySalesList = dailySalesList;
            this.context = context;
            monthTotalAmount = new HashMap<String, Double>();

            try {
                populateMonthTotalAmount();
            } catch (ParseException e) {
                Toast.makeText(context, "Can't load Monthly Sales Data", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sales_page_recyclerview_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.getSaleDate().setText(date.get(position));
            holder.getSaletotalAmount().setText(totalAmount.get(position).toString());
        }

        @Override
        public int getItemCount() {
            if (date != null)
                return date.size();
            else
                return 0;
        }

        private void populateMonthTotalAmount() throws ParseException {
            if (dailySalesList != null) {
                if (dailySalesList.size() > 0) {

                    String preMonth = convertDateFormat(dailySalesList.get(0).date, "yyyy, MMMM");
                    monthTotalAmount.put(preMonth, dailySalesList.get(0).totalSales);
                    //i is the counter for tracking loop iteration
                    int i = 0;

                    for (DailySales dailySales : dailySalesList) {
                        if (i != 0) {
                            if (preMonth.equals(convertDateFormat(dailySales.date, "yyyy, MMMM"))) {
                                Double preAmount = monthTotalAmount.get(preMonth);
                                monthTotalAmount.put(preMonth, preAmount + dailySales.totalSales);
                            } else {
                                monthTotalAmount.put(convertDateFormat(dailySales.date, "yyyy, MMMM"), dailySales.totalSales);
                                preMonth = convertDateFormat(dailySales.date, "yyyy, MMMM");
                            }
                        }
                        i++;
                    }
                    totalAmount = new ArrayList<Double>(monthTotalAmount.values());
                    date = new ArrayList<String>(monthTotalAmount.keySet());

                }
            }
        }

    }

    //adapter class for annual sale recyclerview
    public static class AnnualSalesAdapter extends RecyclerView.Adapter<SalesFragmentRecyclerAdapter.ViewHolder> {
        private ArrayList<DailySales> dailySalesList;
        Context context;
        private Map<String, Double> yearTotalAmount;
        ArrayList<String> date;
        ArrayList<Double> totalAmount;

        public AnnualSalesAdapter(ArrayList<DailySales> dailySalesList , Context context) {
            this.dailySalesList = dailySalesList;
            yearTotalAmount = new HashMap<String, Double>();
            this.context=context;

            try {
                populateYearTotalAmount();
            } catch (ParseException e) {
                Toast.makeText(context, "Can't load Annual Sales Data", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sales_page_recyclerview_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.getSaleDate().setText(date.get(position));
            holder.getSaletotalAmount().setText(totalAmount.get(position).toString());
        }

        @Override
        public int getItemCount() {
            if (date != null)
                return date.size();
            else
                return 0;
        }

        private void populateYearTotalAmount() throws ParseException {
            if (dailySalesList != null) {
                if (dailySalesList.size() > 0) {

                    String preYear = convertDateFormat(dailySalesList.get(0).date, "yyyy");
                    yearTotalAmount.put(preYear, dailySalesList.get(0).totalSales);
                    //i is the counter for tracking loop iteration
                    int i = 0;

                    for (DailySales dailySales : dailySalesList) {
                        if (i != 0) {
                            if (preYear.equals(convertDateFormat(dailySales.date, "yyyy"))) {
                                Double preAmount = yearTotalAmount.get(preYear);
                                yearTotalAmount.put(preYear, preAmount + dailySales.totalSales);
                            } else {
                                yearTotalAmount.put(convertDateFormat(dailySales.date, "yyyy"), dailySales.totalSales);
                                preYear = convertDateFormat(dailySales.date, "yyyy");
                            }
                        }
                        i++;
                    }
                    totalAmount = new ArrayList<Double>(yearTotalAmount.values());
                    date = new ArrayList<String>(yearTotalAmount.keySet());

                }
            }
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView saleDate, saletotalAmount;

        public ViewHolder(View itemView) {
            super(itemView);
            saleDate = itemView.findViewById(R.id.salesDate);
            saletotalAmount = itemView.findViewById(R.id.salesAmount);
        }

        public TextView getSaleDate() {
            return saleDate;
        }

        public TextView getSaletotalAmount() {
            return saletotalAmount;
        }
    }
}