package com.example.sheedah;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;


//the adapter for the recyclerview for displaying the cache of transaction details in the recent transaction database
public class RecentTransationsAdapter extends RecyclerView.Adapter<RecentTransationsAdapter.ViewHolder> {
    Context context;
    private ArrayList  customerTagList, transactionAmountList, transactionStatusList, transactionTimeList,
        transactionDateList;
    String transDateHolder;

    public RecentTransationsAdapter(Context context, ArrayList customerTagList, ArrayList transactionAmountList, ArrayList transactionStatusList
    , ArrayList transactionTimeList, ArrayList transactionDateList) {
            this.context=context;
            this.customerTagList=customerTagList;
            this.transactionAmountList=transactionAmountList;
            this.transactionStatusList=transactionStatusList;
            this.transactionTimeList=transactionTimeList;
            this.transactionDateList=transactionDateList;
    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_transaction_recyclerview_layout,parent,false);
        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder( ViewHolder holder, int position) {

        //if the first item, transaction date textview should be visible


        if (position==0) {
            holder.transDate.setVisibility(View.VISIBLE);
        }



        //if not the first item, transaction date textview should be visible based on the previous value of transDateHolder
        if (position>0) {
            if (transDateHolder.equals(transactionDateList.get(position)))
            holder.transDate.setVisibility(View.GONE);
            else {
                holder.transDate.setVisibility(View.VISIBLE);
            }
        }




        if (transactionStatusList.get(position).equals("On credit")) {
            holder.getTransStatus().setTextColor(context.getResources().getColor(R.color.red));
        }

        else {
            holder.getTransStatus().setTextColor(context.getResources().getColor(R.color.green));
        }
        holder.getCustomerTag().setText(customerTagList.get(position).toString());
        holder.getTransAmount().setText("₦ " +transactionAmountList.get(position).toString());
        holder.getTransStatus().setText(transactionStatusList.get(position).toString());
        holder.getTransTime().setText(transactionTimeList.get(position).toString());
        holder.getTransDate().setText(transactionDateList.get(position).toString());

        //reseting the date to the new date of the date list
        transDateHolder=transactionDateList.get(position).toString();
    }

    @Override
    public int getItemCount() {
        return transactionAmountList.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {
        private TextView  customerTag, transAmount, transStatus, transTime ,transDate;
        public ViewHolder(View itemView) {
            super(itemView);
            customerTag= itemView.findViewById(R.id.customer_tag);
            transAmount=itemView.findViewById(R.id.transAmount);
            transStatus=itemView.findViewById(R.id.tranStatus);
            transTime=itemView.findViewById(R.id.tranTime);
            transDate=itemView.findViewById(R.id.transactionDate);
        }

        public TextView getCustomerTag() {
            return customerTag;
        }

        public TextView getTransAmount() {
            return transAmount;
        }

        public TextView getTransStatus() {
            return transStatus;
        }

        public TextView getTransTime() {
            return transTime;
        }

        public TextView getTransDate() {
            return transDate;
        }
    }
}
