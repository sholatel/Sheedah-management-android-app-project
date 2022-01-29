package com.example.sheedah;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginPage extends AppCompatActivity  implements View.OnClickListener{
    TextInputEditText email, password;
    TextInputLayout email_lay, password_lay;
    String emailValue, passwordValue;
    MaterialButton login;
    private FirebaseAuth fireAuth;
    private static String TAG ="LoginPage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAllowEnterTransitionOverlap(true);

        //initializing views
        email=(TextInputEditText)findViewById(R.id.email);
        email.addTextChangedListener(emailTextWatcher);
        password=(TextInputEditText)findViewById(R.id.password);
        password.addTextChangedListener(passwordTextWatcher);
        password.setOnClickListener(this);
        login=(MaterialButton) findViewById(R.id.login_button);
        login.setOnClickListener(this);
        email_lay=(TextInputLayout)findViewById(R.id.email_lay);
        password_lay=(TextInputLayout)findViewById(R.id.password_lay);

        //initializing fire base auth instance
        fireAuth=FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser fireUser =fireAuth.getCurrentUser();
        if (fireUser!=null) {
            updateUi();
        }
    }

    @Override
    public void onBackPressed() {
       this.moveTaskToBack(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                signIn();
            case R.id.forgot_password:
                //do something
        }
    }

    //function for starting the home page activity on login successful
    private  void updateUi () {
        startActivity(new Intent(this,HomePageActivity.class));
    }

    private void signIn () {
        if (validateForm()) {
            fireAuth.signInWithEmailAndPassword(emailValue, passwordValue)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = fireAuth.getCurrentUser();
                                updateUi();
                            } else {
                                // If sign in fails, display a message to the user.
                                password_lay.setError("Invalid login details");
                                email_lay.setErrorEnabled(true);
                                password_lay.setErrorEnabled(true);
                            }
                        }
                    });
        }
    }

    private  boolean validateForm () {
        emailValue=email.getText().toString();
        passwordValue= password.getText().toString();

        if (emailValue.isEmpty() && passwordValue.isEmpty())  {
            email_lay.setError("Email required");
            password_lay.setError("Email required");
            email_lay.setErrorEnabled(true);
            password_lay.setErrorEnabled(true);
            return false;
        }

        else if (emailValue.isEmpty()) {
            email_lay.setError("Email required");
            email_lay.setErrorEnabled(true);
            return  false;
        }

        else if (passwordValue.isEmpty()) {
            password_lay.setError("Email required");
            password_lay.setErrorEnabled(true);
            return  false;
        }
        else {
            return  true;
        }
    }

    TextWatcher emailTextWatcher =new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //do nothing
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count>0) {
                    email_lay.setErrorEnabled(false);
                }
        }

        @Override
        public void afterTextChanged(Editable s) {
            //do nothing
        }
    };

    TextWatcher passwordTextWatcher =new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //do nothing
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (count>0) {
                password_lay.setErrorEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            //do nothing
        }
    };



}