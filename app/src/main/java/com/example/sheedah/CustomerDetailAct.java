package com.example.sheedah;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.preference.PreferenceManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CustomerDetailAct extends AppCompatActivity {
    Customer customer;
    TextView customerText, customerPhoneNoText, customerAddressText, logoText;
    CardView  viewHistoryBtn;
    Toolbar detailBackBar, editBar, deleteBar;
    static final String CUSTOMER_EXTRA_TAG="customer";
    DatabaseReference mDatabase;
    SharedPreferences  sharedPreferences;

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
        setContentView(R.layout.activity_customer_detail);
        customer= (Customer) getIntent().getSerializableExtra("currentCustomer");
        customerText=findViewById(R.id.nameText);
        customerPhoneNoText=findViewById(R.id.phoneNoText);
        customerAddressText=findViewById(R.id.addressText);
        logoText=findViewById(R.id.logoText);
        viewHistoryBtn=findViewById(R.id.viewHistoryBtn);
        viewHistoryBtn.setOnClickListener(onViewHistoryButtonClick);
        detailBackBar=findViewById(R.id.detailBackBar);
        detailBackBar.setOnClickListener(onDetailBackBarClick);
        editBar  = findViewById(R.id.editBar);
        editBar.setOnClickListener(onEditBarClicked);
        deleteBar= findViewById(R.id.deleteBar);
        deleteBar.setOnClickListener(onDeleteCustomerClicked);
        //setCustomerDetails() is function to set the values of the customer details
        setCustomerDetails();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    View.OnClickListener onViewHistoryButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(CustomerDetailAct.this,TransactionHistoryAct.class);
            intent.putExtra(CUSTOMER_EXTRA_TAG,customer);
            startActivity(intent);
        }
    };

    View.OnClickListener onDeleteCustomerClicked= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String titleText="Delete Customer";
            //ForegroundColorSpan alertTitleFore = new ForegroundColorSpan(getResources().getColor(R.color.alertTitleColor));
            //SpannableStringBuilder alertTitleSpan= new SpannableStringBuilder(titleText);
            //alertTitleSpan.setSpan(alertTitleFore,0,titleText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(CustomerDetailAct.this,R.style.alertDialogueStyle);
            alertBuilder.setTitle(titleText).setMessage("\nDo you wish to delete this customer from the database?\nNB: Deleting this customer would erase all the details and transaction history on the database");
            alertBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mDatabase.child("Customers").child(customer.phoneNumber).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(CustomerDetailAct.this,"Customer deleted successfully",Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure( Exception e) {
                            Toast.makeText(CustomerDetailAct.this,"Couldn't delete this customer",Toast.LENGTH_SHORT) .show();
                        }
                    });
                }
            });
            alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //Cancel deletion;
                }
            });
            alertBuilder.create();
            alertBuilder.show();
        }
    };

    public void setCustomerDetails () {
        if (!customer.customerName.isEmpty()) {
            logoText.setText(customer.customerName.substring(0,1));
            customerText.setText(customer.customerName);
        }

        if (!customer.phoneNumber.isEmpty()) {
            customerPhoneNoText.setText(customer.phoneNumber);
        }

        if (!customer.customerAddress.isEmpty()) {
            customerAddressText.setText(customer.customerAddress);
        }
    }

    View.OnClickListener onDetailBackBarClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onBackPressed();
        }
    };

    View.OnClickListener onEditBarClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(CustomerDetailAct.this, EditCustomer.class);
            intent.putExtra(CUSTOMER_EXTRA_TAG, customer);
            startActivity(intent);
        }
    };
}