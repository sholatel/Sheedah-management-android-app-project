package com.example.sheedah;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;


public class DebtFragment extends Fragment {
    public static  ArrayList<Customer> customersList;
    RecyclerView debtRecycler;
    DebtsAdapter debtsAdapter;
    ProgressBar debtsPb;
    FetchDataFragment fetchFrag;


    public DebtFragment () {
        super (R.layout.debt_fragment_layout);
        customersList= new ArrayList<>();
        fetchFrag= new FetchDataFragment();

    }

    @Override
    public void onViewCreated( View view,  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        debtRecycler=view.findViewById(R.id.debtRecycler);
        debtRecycler.setLayoutManager( new LinearLayoutManager(this.getContext()));
        debtsPb=view.findViewById(R.id.debtsPb);
    }

    public ArrayList [] getDebtorsDetails() {
        ArrayList<String> customerNamesAndPhoneNo=new ArrayList<>();
        ArrayList<String> customerDebtAmount=new ArrayList<>();
        for (Customer customer: customersList) {
            if (customer.debtStatus) {
                customerNamesAndPhoneNo.add(customer.customerName + "_" + customer.phoneNumber);
                customerDebtAmount.add(customer.debtStatement);
            }
        }
        return   new ArrayList[] {customerNamesAndPhoneNo,customerDebtAmount};
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateCustomerList2 (Customer customer) {
        customersList.add(customer);
        ArrayList [] arrayList = getDebtorsDetails();
        debtsAdapter= new DebtsAdapter(arrayList[0],arrayList[1], this.getContext());
        debtRecycler.setAdapter(debtsAdapter);
        debtsPb.setVisibility(View.GONE);

    }
}
