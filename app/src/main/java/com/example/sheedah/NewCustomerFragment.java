package com.example.sheedah;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class NewCustomerFragment extends Fragment  implements  View.OnClickListener {
    TextInputEditText customerName, customerAddress, customerPhoneNo;
    TextInputLayout phone_no_lay;
    Button addBtn;
    String name, address, phoneNo;
    ProgressBar newCustomerPb;
    private DatabaseReference mDatabase;
    public NewCustomerFragment () {
        super(R.layout.new_customer_fragment_layout);
    }
    @Override
    public void onViewCreated(View view,  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        customerName=view.findViewById(R.id.customer_name);
        customerName.addTextChangedListener(customerNameTextWatcher);
        customerAddress=view.findViewById(R.id.customer_address);
        customerAddress.addTextChangedListener(customerAddressTextWatcher);
        customerPhoneNo=view.findViewById(R.id.phone_no);
        customerPhoneNo.addTextChangedListener(phoneNoTextWatcher);
        addBtn=view.findViewById(R.id.addCustomerBtn);
        addBtn.setOnClickListener(this);
        newCustomerPb=view.findViewById(R.id.newCustomerPb);
        phone_no_lay= view.findViewById(R.id.phone_no_lay);
    }



    TextWatcher customerNameTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            //do nothing
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            //do nothing
        }

        @Override
        public void afterTextChanged(Editable editable) {

            name=editable.toString();
            if (name.isEmpty()) {
               name="Unknown";
            }
        }
    };

    TextWatcher customerAddressTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            //do nothing
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            //do nothing
        }
        //
        @Override
        public void afterTextChanged(Editable editable) {

            address=editable.toString();
            if (address.isEmpty()) {
                address="Unknown";
            }
        }
    };

    TextWatcher phoneNoTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            //do nothing
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (i2>0) {
                addBtn.setEnabled(true);
                phone_no_lay.setErrorEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

            phoneNo=editable.toString();
            if (phoneNo.isEmpty()) {
                phone_no_lay.setError("Customer phone Number is required");
                phone_no_lay.setErrorEnabled(true);
            }
        }
    };


    @Override
    public void onClick(View view) {
        newCustomerPb.setVisibility(View.VISIBLE);
        mDatabase=FirebaseDatabase.getInstance().getReference();
        if (customerName.getText().toString().isEmpty()) {
            name="Unknown";
        }

        else {
            name=customerName.getText().toString();
        }

        if (customerAddress.getText().toString().isEmpty()) {
            address="Unknown";
        }
        else {
            address=customerAddress.getText().toString();
        }

        Customer customer=new Customer(name,address,phoneNo,"0.00",false);
        mDatabase.child("Customers").child(phoneNo).setValue(customer).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                onNewCustomerAdded("success");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                       onNewCustomerAdded("failure");
                    }
                });


    }

    public void onNewCustomerAdded(String status) {
        if (status=="success")  {
            Toast.makeText(this.getContext(), "New Customer Added!",Toast.LENGTH_SHORT).show();
            newCustomerPb.setVisibility(View.GONE);
        }

        else if (status=="failure") {
            Toast.makeText(this.getContext(), "Addition new customer failed!",Toast.LENGTH_SHORT).show();
        }
    }


}
