package com.example.sheedah;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class CustomerDetailsFragment extends Fragment {
    RecyclerView customerRecycler;
    CustomerAdapter customerAdapter;
    ArrayList<Customer> customersList;
    ScrollView customerScroll;
    ProgressBar customerPb;
    FetchDataFragment fetchFrag;
    SearchView searchView;

    public  CustomerDetailsFragment () {
        super (R.layout.customers_fragment_layout);
       // this.customersList =customersList;
         this.customersList=new ArrayList<Customer>();
         fetchFrag= new FetchDataFragment();
    }

    @Override
    public void onViewCreated( View view,  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        customerScroll=(ScrollView) view.findViewById(R.id.customerScroll);
        searchView= view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(onSearcyQueryInput);
        customerPb=view.findViewById(R.id.customerPb);
        customerRecycler=view.findViewById(R.id.customerRecycler);
        customerRecycler.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }

    SearchView.OnQueryTextListener onSearcyQueryInput = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {

            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            filter(newText);
            return false;
        }
    };

    public void  filter (String newText) {
            ArrayList<Customer> filteredList = new ArrayList<>();

            //looping through the customerList loop to compare if there is a match of the customer name input in the search view
            for (Customer customer: customersList) {
                if (customer.customerName.contains(newText)) {
                    filteredList.add(customer);
                }
            }

            //if the filteredList is empty, a toast should be made to the customer
            if (filteredList.isEmpty()) {
                //Toast.makeText(getContext(), "Customer not found!", Toast.LENGTH_SHORT).show();
            }

            //else, the recycler view would be filtered
            else {
                customerAdapter.filterRecyclerItem(filteredList);
            }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        EventBus.getDefault().register(this);
        this.getActivity().getSupportFragmentManager().beginTransaction().add(fetchFrag,HomePageActivity.FETCH_DATA_FRAG_TAG).commit();

    }

    @Override
    public void onDetach() {
        fetchFrag.stopDownload();
        EventBus.getDefault().unregister(this);
        this.getActivity().getSupportFragmentManager().beginTransaction().remove(fetchFrag);
        super.onDetach();
    }

    @Subscribe (threadMode = ThreadMode.MAIN)
    public void getCustomer (Customer customer) {
        customersList.add(customer);
        customerAdapter=new CustomerAdapter(customersList);
        customerAdapter.notifyDataSetChanged();
        customerRecycler.setAdapter(customerAdapter);
        customerPb.setVisibility(View.GONE);
    }
}
