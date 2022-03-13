package com.example.sheedah;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

public class TransactionHistoryAdapter  extends  RecyclerView.Adapter<TransactionHistoryAdapter.ViewHolder> {
    ArrayList <Map> transactionHistoryList;
    ArrayList <String> transactionDateList;
    ArrayList <String> transactionTimeList;
    ArrayList <String> transactionInvoiceList;
    ArrayList <String> transactionAmountList;
    ArrayList <String> transactionProductList;
    ArrayList <String> transactionQuantityList;
    Context context;


    public TransactionHistoryAdapter (Context context, ArrayList transactionHistoryList) {
        this.transactionHistoryList=transactionHistoryList;
        this.context=context;
        setTransactionList();
        setTransactionItemList();

    }


    //function for setting the transaction date, time and invoice list
    private void setTransactionList () {
        transactionDateList= new ArrayList<>();
        transactionTimeList = new ArrayList<>();
        transactionInvoiceList= new ArrayList<>();
        for (int i=0; i<transactionHistoryList.size(); i++) {
            transactionInvoiceList.add(transactionHistoryList.get(i).get("invoiceNo").toString());
            transactionDateList.add(transactionHistoryList.get(i).get("date").toString());
            transactionTimeList.add(transactionHistoryList.get(i).get("time").toString());
        }
    }

    //function for setting the transaction amount, product, and quantity list
    private void setTransactionItemList () {
        transactionAmountList= new ArrayList<>();
        transactionProductList = new ArrayList<>();
        transactionQuantityList= new ArrayList<>();
        for (int i=0; i<transactionHistoryList.size(); i++) {
            ArrayList<Map> items = (ArrayList<Map>) transactionHistoryList.get(i).get("items");
            String amounts= "";
            String products= "";
            String quantity= "";
            for (int j=0; j<items.size(); j++) {
                if (j==0) {
                    amounts="₦ "+items.get(j).get("amount")+"\n\n";
                    products=items.get(j).get("product")+"\n\n";
                    quantity=items.get(j).get("quantity")+"\n\n";
                }

                else  {
                    amounts=amounts + "₦ "+items.get(j).get("amount")+"\n\n";
                    products=products + items.get(j).get("product")+"\n\n";
                    quantity=quantity + items.get(j).get("quantity")+"\n\n";
                }
            }
            transactionAmountList.add(amounts);
            transactionQuantityList.add(quantity);
            transactionProductList.add(products);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_history_recycler_content_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position==0) {
            holder.getDateColumn().setVisibility(View.VISIBLE);
            holder.getTimeColumn().setVisibility(View.VISIBLE);
            holder.getAmountColumn().setVisibility(View.VISIBLE);
            holder.getDescriptionColumn().setVisibility(View.VISIBLE);
            holder.getInvoiceNoColumn().setVisibility(View.VISIBLE);
            holder.getQuantityColumn().setVisibility(View.VISIBLE);
        }


            holder.getDateCell().setText(transactionDateList.get(position));
            holder.getTimeCell().setText(transactionTimeList.get(position));
            holder.getAmountCell().setText(transactionAmountList.get(position));
            holder.getDescriptionCell().setText(transactionProductList.get(position));
            holder.getInvoiceNoCell().setText(transactionInvoiceList.get(position));
            holder.getQuantityCell().setText(transactionQuantityList.get(position));
    }

    @Override
    public int getItemCount() {
        return transactionHistoryList.size();
    }

    public static  class ViewHolder extends  RecyclerView.ViewHolder {
        TextView dateColumn, timeColumn, amountColumn,   descriptionColumn, invoiceNoColumn, quantityColumn;
        TextView dateCell, timeCell, amountCell,descriptionCell, invoiceNoCell, quantityCell;

        public ViewHolder( View itemView) {
            super(itemView);
            dateColumn=itemView.findViewById(R.id.dateColumn);
            timeColumn=itemView.findViewById(R.id.timeColumn);
            amountColumn=itemView.findViewById(R.id.amountColumn);
            quantityColumn=itemView.findViewById(R.id.quantityColumn);
            descriptionColumn=itemView.findViewById(R.id.descriptionColumn);
            invoiceNoColumn=itemView.findViewById(R.id.invoiceNoColumn);
            dateCell=itemView.findViewById(R.id.dateCell);
            timeCell=itemView.findViewById(R.id.timeCell);
            amountCell=itemView.findViewById(R.id.amountCell);
            quantityCell=itemView.findViewById(R.id.quantityCell);
            descriptionCell=itemView.findViewById(R.id.descriptionCell);
            invoiceNoCell=itemView.findViewById(R.id.invoiceNoCell);

        }

        public TextView getDateColumn() {
            return dateColumn;
        }

        public TextView getTimeColumn() {
            return timeColumn;
        }

        public TextView getAmountColumn() {
            return amountColumn;
        }


        public TextView getDescriptionColumn() {
            return descriptionColumn;
        }

        public TextView getInvoiceNoColumn() {
            return invoiceNoColumn;
        }

        public TextView getDateCell() {
            return dateCell;
        }

        public TextView getTimeCell() {
            return timeCell;
        }

        public TextView getAmountCell() {
            return amountCell;
        }


        public TextView getDescriptionCell() {
            return descriptionCell;
        }

        public TextView getInvoiceNoCell() {
            return invoiceNoCell;
        }

        public TextView getQuantityCell() {
            return quantityCell;
        }

        public TextView getQuantityColumn() {
            return quantityColumn;
        }
    }
}
