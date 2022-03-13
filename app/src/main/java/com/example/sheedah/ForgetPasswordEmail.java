package com.example.sheedah;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordEmail extends AppCompatActivity {
    Toolbar forgetPasswordBack;
    TextInputEditText forgetPasswordEmail;
    TextInputLayout forgetPasswordEmailLay;
    ImageView logo;
    MaterialButton resetButton;
    ProgressBar forgetPb;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        boolean switchVal= sharedPreferences.getBoolean("theme",false);
        if (switchVal) {
            setTheme(R.style.sheedah_dark);
        }

        else {
            setTheme(R.style.sheedah);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password_layout);
        forgetPasswordBack=findViewById(R.id.forgetPasswordBack);
        forgetPasswordBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        forgetPasswordEmail=findViewById(R.id.forget_password_email);
        forgetPasswordEmail.addTextChangedListener(emailTextWatcher);
        forgetPasswordEmailLay=findViewById(R.id.forget_password_email_lay);
        logo=findViewById(R.id.forgetPasswordLogo);
        resetButton= findViewById(R.id.reset_password_button);
        resetButton.setOnClickListener(onResetButtonClicked);
        forgetPb= findViewById(R.id.forgetPb);

    }

    View.OnClickListener onResetButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String email= forgetPasswordEmail.getText().toString();
            resetButton.setEnabled(false);
            forgetPb.setVisibility(View.VISIBLE);
            if (email.isEmpty()) {
                forgetPasswordEmailLay.setError("Email is required");
                forgetPasswordEmailLay.setErrorEnabled(true);
                resetButton.setEnabled(true);
                forgetPb.setVisibility(View.GONE);
                return;
            }
            FirebaseAuth auth= FirebaseAuth.getInstance();
            auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgetPasswordEmail.this, "Reset Link sent to your Email",Toast.LENGTH_SHORT).show();
                            forgetPb.setVisibility(View.GONE);
                            onBackPressed();

                        }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                 forgetPasswordEmailLay.setError( e.getLocalizedMessage());
                 forgetPasswordEmailLay.setErrorEnabled(true);
                 resetButton.setEnabled(true);
                 forgetPb.setVisibility(View.GONE);
                }
            });
        }
    };

    TextWatcher emailTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {
            if (count>0) {
                forgetPasswordEmailLay.setErrorEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
}