package com.example.sheedah;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class EditCustomer extends AppCompatActivity {
    TextInputEditText editCustomerName, editCustomerAddress, editPhoneNo;
    TextInputLayout phone_no_lay;
    SwitchCompat debtSwitch;
    ProgressBar editCustomerPb;
    Customer customer;
    String name, address, phoneNo, debtStatement;
    Button saveBar;
    boolean isDebt;
    SharedPreferences sharedPreferences;
    Toolbar backBar;

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
        setContentView(R.layout.edit_customer);
        editCustomerName = findViewById(R.id.edit_customer_name);
        editCustomerAddress =findViewById(R.id.edit_customer_address);
        editPhoneNo = findViewById(R.id.edit_phone_no);
        editPhoneNo.addTextChangedListener(phoneNoTextWatcher);
        phone_no_lay=findViewById(R.id.edit_phone_no_lay);
        debtSwitch =findViewById(R.id.isDebtSwitch);
        backBar=findViewById(R.id.editBackBar);
        backBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        saveBar= findViewById(R.id.editCustomerBtn);
        saveBar.setOnClickListener(onDetailsEdited);
        debtSwitch.setOnCheckedChangeListener(onDebtSwitchChecked);
        editCustomerPb=findViewById(R.id.editCustomerPb);
        customer= (Customer) getIntent().getSerializableExtra(CustomerDetailAct.CUSTOMER_EXTRA_TAG);
        debtStatement= customer.debtStatement;
        isDebt=customer.debtStatus;
        //settings customer current details
        editCustomerName.setText(customer.customerName);
        editCustomerAddress.setText(customer.customerAddress);
        editPhoneNo.setText(customer.phoneNumber);
        if (customer.debtStatus) {
            debtSwitch.setChecked(true);
        }

        else {
            debtSwitch.setEnabled(false);
        }



    }

    View.OnClickListener onDetailsEdited = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            updateCustomerDetails();
        }
    };

    TextWatcher phoneNoTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            //do nothing
            if (i2>0) {
                saveBar.setEnabled(true);
            }

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (i2>0) {
                saveBar.setEnabled(true);
                phone_no_lay.setErrorEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

            phoneNo=editable.toString();
            if (phoneNo.isEmpty()) {
                phone_no_lay.setError("Customer phone Number is required");
                phone_no_lay.setErrorEnabled(true);
                saveBar.setEnabled(false);
            }
        }
    };

    CompoundButton.OnCheckedChangeListener onDebtSwitchChecked = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (!b) {
                debtStatement = "0.00";
                isDebt=false;
            }
        }
    };

    private void updateCustomerDetails() {
        //do something
        editCustomerPb.setVisibility(View.VISIBLE);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> updateData = new HashMap<>();

        if (editCustomerName.getText().toString().isEmpty()) {
            name = customer.customerName;
        } else {
            name = editCustomerName.getText().toString();
        }

        if (editCustomerAddress.getText().toString().isEmpty()) {
            address = customer.customerAddress;
        } else {
            address = editCustomerAddress.getText().toString();
        }
        updateData.put("customerAddress", address);
        updateData.put("customerName", name);
        updateData.put("debtStatus", isDebt);
        updateData.put("debtStatement", debtStatement);
        updateData.put("phoneNumber", phoneNo);


        mDatabase.child("Customers").child(customer.phoneNumber).updateChildren(updateData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                onNewCustomerAdded("success");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        onNewCustomerAdded("failure");
                    }
                });
    }

    public void onNewCustomerAdded(String status) {
        if (status == "success") {
            Toast.makeText(this, "Customer Edited successfully", Toast.LENGTH_SHORT).show();
            editCustomerPb.setVisibility(View.GONE);
            finish();
        } else if (status == "failure") {
            Toast.makeText(this, "Customer update failed!", Toast.LENGTH_SHORT).show();
        }
    }

}