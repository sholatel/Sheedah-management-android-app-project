package com.example.sheedah;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DebtsAdapter extends RecyclerView.Adapter<DebtsAdapter.ViewHolder> {
    private ArrayList<String> customerNameList, debtAmountList;
    Context context;
    public DebtsAdapter (ArrayList <String> customerNameList, ArrayList<String> debtAmountList ,Context context) {
        this.customerNameList=customerNameList;
        this.debtAmountList=debtAmountList;
        this.context=context;
    }


    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sales_page_recyclerview_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.getCustomerName().setText(customerNameList.get(position));
        holder.getDebtAmount().setText("₦ " +debtAmountList.get(position));
        holder.getDebtAmount().setTextColor(context.getResources().getColor(R.color.red));

    }

    @Override
    public int getItemCount() {
        return customerNameList.size();
    }

    public static  class ViewHolder extends RecyclerView.ViewHolder {
        private TextView customerName, debtAmount;

        public ViewHolder(View itemView) {
            super(itemView);
            customerName = itemView.findViewById(R.id.salesDate);
            debtAmount = itemView.findViewById(R.id.salesAmount);
        }

        public TextView getCustomerName() {
            return customerName;
        }

        public TextView getDebtAmount() {
            return debtAmount;
        }
    }

}
