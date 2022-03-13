package com.example.sheedah;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ViewHolder> {
    static ArrayList<Customer> customersList;
    Customer currentCustomer;


    public CustomerAdapter (ArrayList<Customer> customersList) {
        this.customersList=customersList;
    }

    //function for filtering the recycler view item when search query is input
    public void filterRecyclerItem (ArrayList<Customer> filteredCustomerList) {
        customersList=filteredCustomerList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.customers_details_recyclerview_layout,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.getCustomerLogo().setText(customersList.get(position).customerName.substring(0,1));
        holder.getCustomerName().setText(customersList.get(position).customerName);
        holder.getCustomerPhoneNo().setText(customersList.get(position).phoneNumber);

    }

    @Override
    public int getItemCount() {
        return customersList.size();
    }

    public static  class ViewHolder extends  RecyclerView.ViewHolder {
        TextView customerLogo, customerName, customerPhoneNo;
        public ViewHolder( View itemView) {
            super(itemView);
            customerLogo= itemView.findViewById(R.id.customer_logo);
            customerName=itemView.findViewById(R.id.customer_name);
            customerPhoneNo=itemView.findViewById(R.id.customer_phone_no);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos=getLayoutPosition();
                   // Toast.makeText(itemView.getContext(),customersList.get(pos).customerName,Toast.LENGTH_SHORT).show();;
                    Customer currentCustomer = customersList.get(pos);
                    Intent  intent = new Intent(view.getContext(),CustomerDetailAct.class);
                    intent.putExtra("currentCustomer",currentCustomer);
                    view.getContext().startActivity(intent);
                }
            });
            
        }

        public TextView getCustomerLogo () {
            return  customerLogo;
        }
        public TextView getCustomerName () {
            return  customerName;
        }
        public TextView getCustomerPhoneNo () {
            return  customerPhoneNo;
        }
    }
    
    View.OnClickListener onRecyclerItemClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(view.getContext(),currentCustomer.customerName, Toast.LENGTH_SHORT).show();

        }   
    };

}