package com.example.sheedah;


import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.greenrobot.eventbus.EventBus;
import java.util.ArrayList;


//class for fetching data from server on background thread
public class FetchDataFragment  extends Fragment {
    private  boolean isStarted=false;
    Customer customer;
    DailySales dailySales;
    DatabaseReference databaseCustomerReference;
    DatabaseReference databaseDailySalesReference;

    public void restartDownload() {
        new BackgroundDownloader().start();
    }
    public void stopDownload () {
        databaseDailySalesReference.removeEventListener(customerEventListener);
        databaseDailySalesReference.removeEventListener(dailySalesEventListener);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (!isStarted) {
            isStarted=true;
            databaseCustomerReference= FirebaseDatabase.getInstance().getReference().child("Customers");
            databaseDailySalesReference=FirebaseDatabase.getInstance().getReference().child("Daily-sales");
            restartDownload();
        }
    }

    class BackgroundDownloader  extends  Thread {
        @Override
        public void run() {
            //do something
            databaseCustomerReference.addValueEventListener(customerEventListener);
            databaseDailySalesReference.addValueEventListener(dailySalesEventListener);


        }
    }

    ValueEventListener customerEventListener =new ValueEventListener() {

        @Override
        public void onDataChange( DataSnapshot snapshot) {
            customer= new Customer();
            for (DataSnapshot childCustomer:snapshot.getChildren()) {
                Customer customer=childCustomer.getValue(Customer.class);
                EventBus.getDefault().post(customer);
                //SystemClock.sleep(100);
            }
        }


        @Override
        public void onCancelled( DatabaseError error) {
            //do something
        }
    };

    ValueEventListener dailySalesEventListener =new ValueEventListener() {

        @Override
        public void onDataChange( DataSnapshot snapshot) {
            dailySales= new DailySales();
            for (DataSnapshot childDailySales:snapshot.getChildren()) {
                DailySales dailySales=childDailySales.getValue(DailySales.class);
                EventBus.getDefault().post(dailySales);
                //SystemClock.sleep(200);
            }

        }


        @Override
        public void onCancelled( DatabaseError error) {
            //do something
        }
    };

    @Override
    public void onStop() {
        databaseCustomerReference.removeEventListener(customerEventListener);
        databaseDailySalesReference.removeEventListener(dailySalesEventListener);
        super.onStop();
    }
}
