package com.example.sheedah;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.media.metrics.Event;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class HomePageFragment extends Fragment  {
    private ArrayList customerTagList, transactionAmountList, transactionStatusList, transactionTimeList,
            transactionDateList;
    private ArrayList<DailySales> dailySalesList;
    private TextView totalSales;
    private RecyclerView recentTransactionRecycler;
    private RecentTransationsAdapter recentTransationsAdapter;
    ProgressBar dailySalesPb;
    FetchDataFragment fetchFrag;


    public HomePageFragment (ArrayList customerTagList,ArrayList transactionAmountList,ArrayList transactionStatusList, ArrayList transactionTimeList,
                             ArrayList transactionDateList) {
        super(R.layout.home_page_fragment_layout);
        this.customerTagList=customerTagList;
        this.transactionAmountList=transactionAmountList;
        this.transactionStatusList=transactionStatusList;
        this.transactionTimeList=transactionTimeList;
        this.transactionDateList=transactionDateList;
        this.dailySalesList= new ArrayList<DailySales>();
        fetchFrag = new FetchDataFragment();

    }

    @Override
    public void onViewCreated( View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dailySalesPb=view.findViewById(R.id.dailySalesPb);
        dailySalesPb.setVisibility(View.VISIBLE);
        recentTransationsAdapter=new RecentTransationsAdapter(this.getContext(),customerTagList,transactionAmountList,transactionStatusList
        ,transactionTimeList,transactionDateList);
        recentTransactionRecycler=view.findViewById(R.id.recentTransactionRecycler);
        totalSales=view.findViewById(R.id.totalSales);
        recentTransactionRecycler.setAdapter(recentTransationsAdapter);
        recentTransactionRecycler.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recentTransactionRecycler.addItemDecoration(new DividerItemDecoration(recentTransactionRecycler.getContext(), DividerItemDecoration.VERTICAL));
    }

    public Double getTotalSales(String date) {
        for (DailySales sales: dailySalesList) {

            if (sales.date.equals(date)) {
                return  sales.totalSales;
            }
        }

        return 0.00;
    }

    public String getDate () {
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
        Calendar c= Calendar.getInstance();
        String date=sdf.format(c.getTime());
        return date;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.getActivity().getSupportFragmentManager().beginTransaction().add(fetchFrag,HomePageActivity.FETCH_DATA_FRAG_TAG).commit();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        fetchFrag.stopDownload();
        this.getActivity().getSupportFragmentManager().beginTransaction().remove(fetchFrag);
        EventBus.getDefault().unregister(this);
        super.onDetach();
    }

    @Subscribe (threadMode = ThreadMode.MAIN)
    public void  getDailySales (DailySales dailySales) {
       dailySalesList.add(dailySales);
       HomePageActivity.dailySalesList.add(dailySales);
       totalSales.setText("₦ "+getTotalSales(getDate()));
       dailySalesPb.setVisibility(View.GONE);
    }
}
