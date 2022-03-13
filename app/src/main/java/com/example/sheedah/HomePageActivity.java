package com.example.sheedah;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.os.Build;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class HomePageActivity extends AppCompatActivity  implements View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener{
    RecyclerView customerRecycler;
    RecyclerView.Adapter customerListAdapter;
    public DrawerLayout drawerLayout;
    NavigationView navView;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar homeBar;
    TextView userName, userEmail;
    FloatingActionButton newInvoiceBtn;
    BottomNavigationView navButton;
    FragmentTransaction home_page_fragmentTrans;
    ConstraintLayout topBarLay;
    DatabaseReference databaseCustomerReference;
    static ArrayList customersList;
    DatabaseReference databaseDailySalesReference;
    static ArrayList dailySalesList;
    RecentTransactionDb recentTransactionDb;
    SQLiteDatabase sqLiteDatabase;
    Cursor transactionCursor;
    FirebaseUser fireUser;
    ShapeableImageView homePageImg;
    SharedPreferences sharedPreferences;

    static  final  String FETCH_DATA_FRAG_TAG = "FETCHDATA";
    HomePageFragment homePageFragment;
    private ArrayList  customerTagList, transactionAmountList, transactionStatusList, transactionTimeList,
            transactionDateList;

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
        setContentView(R.layout.home_page);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //pulling data from server
        databaseCustomerReference= FirebaseDatabase.getInstance().getReference().child("Customers");
        customersList= new ArrayList<Customer>();
        dailySalesList= new ArrayList<DailySales>();
        homePageImg=findViewById(R.id.homeProfileImg);
        homePageImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent=new Intent(HomePageActivity.this, ProfileActivity.class);
                startActivity(profileIntent);
            }
        });
        databaseCustomerReference.addValueEventListener(customerEventListener);
        //initializing view elements
        drawerLayout=findViewById(R.id.home_page_drawer);
        newInvoiceBtn=findViewById(R.id.new_invoice_btn);
        navButton=findViewById(R.id.navButton);
        topBarLay=findViewById(R.id.topBarLay);
        navButton.setOnNavigationItemSelectedListener(this);
        newInvoiceBtn.setOnClickListener(this);
        actionBarDrawerToggle=new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        actionBarDrawerToggle.syncState();
        fireUser= FirebaseAuth.getInstance().getCurrentUser();
        homeBar=findViewById(R.id.homeBar);
        navView=findViewById(R.id.navView);
        View view=View.inflate(this,R.layout.nav_header_layout,navView);
        userName=view.findViewById(R.id.userName);
        userEmail=view.findViewById(R.id.userEmail);
        populateNavView();
        navView.setNavigationItemSelectedListener(navViewItemSelectedListener);
        homeBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
      //  populateRecentTransactionListsData();
        //initializing activity components
        recentTransactionDb=new RecentTransactionDb(this);
        sqLiteDatabase=recentTransactionDb.getReadableDatabase();
        populateRecentTransactionListsData();
        homePageFragment=new HomePageFragment(customerTagList,transactionAmountList,transactionStatusList,
                transactionTimeList,transactionDateList);
        home_page_fragmentTrans=getSupportFragmentManager().beginTransaction();
         if (savedInstanceState==null) {
            home_page_fragmentTrans.setReorderingAllowed(true).add(R.id.homePageFragment, homePageFragment,null).commit();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_invoice_btn:
                startActivity(new Intent(HomePageActivity.this, NewInvoicePage.class));
                finishAfterTransition();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        recentTransactionDb.close();
        super.onDestroy();
    }

    @Override
    public boolean onNavigationItemSelected( MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home_page:
                //fetchFrag.onDetach();
                //fetchFrag= new FetchDataFragment();
                //home_page_fragmentTrans.add(fetchFrag,FETCH_DATA_FRAG_TAG);
                homePageFragment=new HomePageFragment(customerTagList,transactionAmountList,transactionStatusList,transactionTimeList,transactionDateList);
                home_page_fragmentTrans= getSupportFragmentManager().beginTransaction();
                /*if (getSupportFragmentManager().findFragmentByTag(FETCH_DATA_FRAG_TAG)==null)
                    home_page_fragmentTrans.add(new FetchDataFragment(),FETCH_DATA_FRAG_TAG);
                else {
                    home_page_fragmentTrans.remove(new FetchDataFragment());
                    home_page_fragmentTrans.add(new FetchDataFragment(),FETCH_DATA_FRAG_TAG);
                }
                 */
                home_page_fragmentTrans.replace(R.id.homePageFragment,homePageFragment,null).commit();
                //getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).replace(R.id.homePageFragment,homePageFragment,null).commit();
                topBarLay.setVisibility(View.VISIBLE);
                return true;
            case R.id.new_customer:
                home_page_fragmentTrans= getSupportFragmentManager().beginTransaction();
                NewCustomerFragment newCustomerFragment=new NewCustomerFragment();
                home_page_fragmentTrans.setReorderingAllowed(true).replace(R.id.homePageFragment,newCustomerFragment,null).commit();
                topBarLay.setVisibility(View.GONE);
                return true;
            case R.id.customers_details:
                //fetchFrag.onStop();
                //fetchFrag= new FetchDataFragment();
                //home_page_fragmentTrans.add(fetchFrag,FETCH_DATA_FRAG_TAG);
                home_page_fragmentTrans= getSupportFragmentManager().beginTransaction();
               /* if (getSupportFragmentManager().findFragmentByTag(FETCH_DATA_FRAG_TAG)==null)
                    home_page_fragmentTrans.add(new FetchDataFragment(),FETCH_DATA_FRAG_TAG);
                else {
                    home_page_fragmentTrans.remove(new FetchDataFragment());
                    home_page_fragmentTrans.add(new FetchDataFragment(),FETCH_DATA_FRAG_TAG);
                }

                */
                CustomerDetailsFragment customerDetailsFragment= new CustomerDetailsFragment();
                home_page_fragmentTrans.setReorderingAllowed(true).replace(R.id.homePageFragment,customerDetailsFragment,null).commit();
                topBarLay.setVisibility(View.GONE);
                return true;
            case R.id.new_sale:
                //fetchFrag.onStop();
                //fetchFrag= new FetchDataFragment();
                //home_page_fragmentTrans.add(fetchFrag,FETCH_DATA_FRAG_TAG);
                home_page_fragmentTrans= getSupportFragmentManager().beginTransaction();

                SalesFragment salesFragment= new SalesFragment();
                home_page_fragmentTrans.setReorderingAllowed(true).replace(R.id.homePageFragment,salesFragment,null).commit();
                topBarLay.setVisibility(View.GONE);
                return true;
            case R.id.debt:
                home_page_fragmentTrans= getSupportFragmentManager().beginTransaction();
                //fetchFrag.onStop();
                //fetchFrag= new FetchDataFragment();
                //home_page_fragmentTrans.add(fetchFrag,FETCH_DATA_FRAG_TAG);
                DebtFragment debtFragment= new DebtFragment();
                home_page_fragmentTrans.setReorderingAllowed(true).replace(R.id.homePageFragment,debtFragment,null).commit();
                topBarLay.setVisibility(View.GONE);
                return true;
            default:
                return false;
        }
    }

    //query recent transaction databas
    private void   queryRecentTransaction () {

// Define a projection that specifies which columns from the database
// will be actually used after this query.
        String[] projection = {
                SheedahLocalDatabaseContract.RecentTransactions.COLUMN_NAME_1,
                SheedahLocalDatabaseContract.RecentTransactions.COLUMN_NAME_2,
                SheedahLocalDatabaseContract.RecentTransactions.COLUMN_NAME_3,
                SheedahLocalDatabaseContract.RecentTransactions.COLUMN_NAME_4,
                SheedahLocalDatabaseContract.RecentTransactions.COLUMN_NAME_5
        };
// sorting the results in the resulting Cursor
        String sortOrder =
                SheedahLocalDatabaseContract.RecentTransactions.COLUMN_NAME_5 + " DESC";
        //performing the query
        transactionCursor = sqLiteDatabase.query(
                SheedahLocalDatabaseContract.RecentTransactions.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );
    }

    private  void populateRecentTransactionListsData  ()  {
        queryRecentTransaction();
        customerTagList= new ArrayList();
        transactionAmountList=new ArrayList();
        transactionStatusList=new ArrayList();
        transactionTimeList=new ArrayList();
        transactionDateList=new ArrayList();
        while(transactionCursor.moveToNext()) {
            String  customerTag = transactionCursor.getString(
                    transactionCursor.getColumnIndexOrThrow(SheedahLocalDatabaseContract.RecentTransactions.COLUMN_NAME_1));
            String  transactionAmount = transactionCursor.getString(
                    transactionCursor.getColumnIndexOrThrow(SheedahLocalDatabaseContract.RecentTransactions.COLUMN_NAME_2));

            String  transactionStatus = transactionCursor.getString(
                    transactionCursor.getColumnIndexOrThrow(SheedahLocalDatabaseContract.RecentTransactions.COLUMN_NAME_3));

            String  transactionTime = transactionCursor.getString(
                    transactionCursor.getColumnIndexOrThrow(SheedahLocalDatabaseContract.RecentTransactions.COLUMN_NAME_4));

            String  transactionDate = transactionCursor.getString(
                    transactionCursor.getColumnIndexOrThrow(SheedahLocalDatabaseContract.RecentTransactions.COLUMN_NAME_5));
            customerTagList.add(customerTag);
            transactionAmountList.add(transactionAmount);
            transactionStatusList.add(transactionStatus);
            transactionTimeList.add(transactionTime);
            transactionDateList.add(transactionDate);
        }
        transactionCursor.close();
    }

    ValueEventListener customerEventListener =new ValueEventListener() {

        @Override
        public void onDataChange( DataSnapshot snapshot) {
           Customer customer= new Customer();
           customersList= new ArrayList();
            for (DataSnapshot childCustomer:snapshot.getChildren()) {
                customer=childCustomer.getValue(Customer.class);
                customersList.add(customer);
            }
        }


        @Override
        public void onCancelled( DatabaseError error) {
            //do something
        }
    };

    NavigationView.OnNavigationItemSelectedListener navViewItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected( MenuItem item) {
            switch (item.getItemId()) {
                case  R.id.logout:
                    FirebaseAuth.getInstance().signOut();
                    Intent intent =new Intent(HomePageActivity.this,LoginPage.class);
                    startActivity(intent);
                    finishAfterTransition();
                    return true;

                case R.id.profile:
                    Intent profileIntent=new Intent(HomePageActivity.this, ProfileActivity.class);
                        startActivity(profileIntent);
                        return  true;

                case R.id.settings:
                    Intent settingsIntent=new Intent(HomePageActivity.this, SettingsActivity.class);
                    startActivity(settingsIntent);
                    finishAfterTransition();

                    return true;

                default:
                    return true;
            }
        }
    };

    private void populateNavView () {
        if (fireUser !=null) {
        fireUser= FirebaseAuth.getInstance().getCurrentUser();
        String name=fireUser.getDisplayName();
        String email=fireUser.getEmail();
        userName.setText(name);
        userEmail.setText(email);
        }
    }



}