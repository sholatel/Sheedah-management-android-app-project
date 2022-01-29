package com.example.sheedah;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ActivityAdapter  extends RecyclerView.Adapter<ActivityAdapter.ViewHolder>{
     private  List<ActivityAdapterCustomerList> customerList;
     private Context context;

     public ActivityAdapter (List<ActivityAdapterCustomerList> customerList, Context context) {
         this.customerList=customerList;
         this.context=context;

     }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_content,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ActivityAdapterCustomerList actList=customerList.get(position);
        holder.customerName.setText(actList.customerName);
        holder.status.setText(actList.status);
    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder {
        TextView customerName, status;

        public ViewHolder(View itemView) {
            super(itemView);
            customerName=(TextView) itemView.findViewById(R.id.customer_name);
            status=(TextView) itemView.findViewById(R.id.amount);
        }
    }
}
