package com.example.sheedah;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TransactionHistoryAct extends AppCompatActivity {

    Customer customer;
    DatabaseReference historyReference;
    Toolbar historyBackBar;
    ProgressBar historyPb;
    RecyclerView recyclerView;
    ArrayList<Map> transactionsList;
    Map<String,Object> transactionsMap;
    TransactionHistoryAdapter transactionHistoryAdapter;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        boolean switchVal= sharedPreferences.getBoolean("theme",false);
        if (switchVal) {
            setTheme(R.style.sheedah_dark);
        }

        else {
            setTheme(R.style.sheedah);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_history_activity_layout);
        customer= (Customer) getIntent().getSerializableExtra(CustomerDetailAct.CUSTOMER_EXTRA_TAG);
        historyPb=findViewById(R.id.historyPb);
        historyBackBar= findViewById(R.id.historyBackBar);
        historyBackBar.setOnClickListener(onHistoryBackBarClick);
        recyclerView=findViewById(R.id.historyRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL));
        historyReference= FirebaseDatabase.getInstance().getReference().child("Customers").child(customer.phoneNumber).child("transactionHistory");
        historyReference.addValueEventListener(transactionHistoryEventListener);
    }

    @Override
    protected void onStop() {
        historyReference.removeEventListener(transactionHistoryEventListener);
        super.onStop();
    }

    ValueEventListener transactionHistoryEventListener =new ValueEventListener() {

        @Override
        public void onDataChange( DataSnapshot snapshot) {
            transactionsList= new ArrayList<>();
            transactionsMap= new HashMap<>();
            for (DataSnapshot childHistory:snapshot.getChildren()) {
                transactionsMap= (Map<String,Object>)childHistory.getValue();
                transactionsList.add(transactionsMap);
            }
            historyPb.setVisibility(View.GONE);
            transactionHistoryAdapter = new TransactionHistoryAdapter(TransactionHistoryAct.this,transactionsList);
            recyclerView.setAdapter(transactionHistoryAdapter);
        }

        @Override
        public void onCancelled( DatabaseError error) {
            //do something
        }
    };

    View.OnClickListener onHistoryBackBarClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onBackPressed();
        }
    };
}