package com.example.sheedah;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class NewInvoicePage extends AppCompatActivity implements  View.OnClickListener{
    Button addbtn;
    Spinner catSpinner;
    TextInputEditText amount_field, quantity_field;
    Double amount_field_value;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.new_invoice_page);
        addbtn=findViewById(R.id.addBtn);
        addbtn.setOnClickListener(this);
        catSpinner=findViewById(R.id.product_category);
        amount_field=findViewById(R.id.amount_field);
        amount_field.addTextChangedListener(amountTextWatcher);
        quantity_field=findViewById(R.id.quantity_field);
        quantity_field.addTextChangedListener(quantityTextWatcher);
        ArrayAdapter <CharSequence> catSpinnerAdapter= ArrayAdapter.createFromResource(this, R.array.product_categories, android.R.layout.simple_spinner_dropdown_item);
        catSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        catSpinner.setAdapter(catSpinnerAdapter);
    }

    @Override
    public void onClick(View v) {

    }


    TextWatcher amountTextWatcher =new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //do nothing
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (count>0) {
                amount_field_value=Double.parseDouble(amount_field.getText().toString());
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            //do nothing
        }
    };

    TextWatcher quantityTextWatcher =new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //do nothing

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (count>0) {
                int quantity_field_value=Integer.parseInt(quantity_field.getText().toString());
                if (amount_field_value>0) {
                    String current_amount_field_value= Double.toString(amount_field_value * quantity_field_value);
                    amount_field.setText(current_amount_field_value);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            //do nothing
        }
    };


}