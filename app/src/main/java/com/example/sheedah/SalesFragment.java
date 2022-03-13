package com.example.sheedah;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class SalesFragment extends Fragment {
    ViewPager2 salesViewPager;
    private ArrayList <String>fragmentTitles;
    public  static ArrayList<DailySales> dailySalesList;
    ProgressBar salesPb;
    //static public String  pageTitle;
    TabLayout pageTitleTab;
    SalesPageSlidesAdapter salesPageSlidesAdapter;
    public static  SalesFragmentRecyclerAdapter.DailySalesAdapter dailySalesAdapter;
    public static RecyclerView dailySalesRecycler;
    FetchDataFragment fetchFrag;

    SalesFragment () {
        super (R.layout.sales_viewpager_fragment);
        fragmentTitles=new ArrayList<>();
        fragmentTitles.add("Daily");
        fragmentTitles.add("Monthly");
        fragmentTitles.add("Annual");
        dailySalesList= new ArrayList<DailySales>();
        fetchFrag= new FetchDataFragment();
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        salesViewPager=view.findViewById(R.id.salesViewPager);
        salesPb=view.findViewById(R.id.salesPb);
        salesPb.setVisibility(View.VISIBLE);
        pageTitleTab=view.findViewById(R.id.pageTitleTab);
        salesPageSlidesAdapter= new SalesPageSlidesAdapter(this);
        salesViewPager.setAdapter(salesPageSlidesAdapter);
        TabLayoutMediator tabLayoutMediator= new TabLayoutMediator(pageTitleTab, salesViewPager, new TabLayoutMediator.TabConfigurationStrategy() {

            @Override
            public void onConfigureTab(TabLayout.Tab tab, int position) {
                tab.setText(fragmentTitles.get(position));

            }
            });
        tabLayoutMediator.attach();
    }




    private class SalesPageSlidesAdapter extends FragmentStateAdapter {
        private  ArrayList<Fragment> fragmentList;


        public SalesPageSlidesAdapter( Fragment fragment) {
            super(fragment);
            fragmentList=new ArrayList<>();
            fragmentList.add(new DailySalesFragment());
            fragmentList.add(new MonthlySalesFragment());
            fragmentList.add(new AnnualSalesFragment());
        }

        @Override
        public Fragment createFragment(int position) {
            return  fragmentList.get(position);
        }

        @Override
        public int getItemCount() {
            return fragmentList.size();
        }


    }

    public static class DailySalesFragment extends  Fragment {
        public DailySalesFragment () {
            super(R.layout.daily_sales_fragment);
        }

        @Override
        public void onViewCreated( View view ,Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            dailySalesRecycler=view.findViewById(R.id.dailySalesRecycler);
            dailySalesAdapter = new SalesFragmentRecyclerAdapter.DailySalesAdapter(dailySalesList);
            dailySalesRecycler.setAdapter(dailySalesAdapter);
            dailySalesRecycler.setLayoutManager(new LinearLayoutManager(this.getContext()));

        }
    }

    public static class MonthlySalesFragment extends  Fragment {
        public MonthlySalesFragment () {
            super(R.layout.monthly_sales_fragment);
        }
        @Override
        public void onViewCreated( View view ,Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            RecyclerView monthlySaleRecycler=view.findViewById(R.id.monthlySalesRecycler);
            SalesFragmentRecyclerAdapter.MonthlySalesAdapter monthlySalesAdapter = new SalesFragmentRecyclerAdapter.MonthlySalesAdapter(dailySalesList, this.getContext());
            monthlySaleRecycler.setAdapter(monthlySalesAdapter);
            monthlySaleRecycler.setLayoutManager(new LinearLayoutManager(this.getContext()));

        }

    }

    public static class AnnualSalesFragment extends  Fragment {
        public AnnualSalesFragment () {
            super(R.layout.annual_sales_fragment);
        }

        @Override
        public void onViewCreated( View view ,Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            RecyclerView annualSaleRecycler=view.findViewById(R.id.annualSalesRecycler);
            SalesFragmentRecyclerAdapter.AnnualSalesAdapter annualSalesAdapter = new SalesFragmentRecyclerAdapter.AnnualSalesAdapter(dailySalesList, this.getContext());
            annualSaleRecycler.setAdapter(annualSalesAdapter);
            annualSaleRecycler.setLayoutManager(new LinearLayoutManager(this.getContext()));

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
        this.getActivity().getSupportFragmentManager().beginTransaction().remove(fetchFrag);
        EventBus.getDefault().unregister(this);
        super.onDetach();
    }

    @Subscribe (threadMode = ThreadMode.MAIN)
    public void updateDailySalesList (DailySales dailySales) {
        dailySalesList.add(dailySales);
        dailySalesAdapter = new SalesFragmentRecyclerAdapter.DailySalesAdapter(dailySalesList);
        dailySalesRecycler.setAdapter(dailySalesAdapter);
        salesPb.setVisibility(View.GONE);
    }
}
