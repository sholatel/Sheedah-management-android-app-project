package com.example.sheedah;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialTextInputPicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationPage extends AppCompatActivity implements View.OnClickListener {

    TextInputEditText  reg_username, reg_email, reg_phoneNo , reg_password;
    TextInputLayout reg_username_lay , reg_email_lay, reg_phoneNo_lay , reg_password_lay;
    MaterialButton  register_btn;
    MaterialTextView login;
    FirebaseAuth fireAuth;
    Intent intent;
    String email;
    String password;
    ImageView logoImage;
    private  static String TAG="RegistrationPage";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_page);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAllowEnterTransitionOverlap(true);
        logoImage=(ImageView)findViewById(R.id.logoImage);
        reg_username_lay=(TextInputLayout)findViewById(R.id.reg_username_lay);
        reg_password_lay=(TextInputLayout)findViewById(R.id.reg_password_lay);
        reg_email_lay=(TextInputLayout)findViewById(R.id.reg_email_lay);
        reg_phoneNo_lay=(TextInputLayout)findViewById(R.id.reg_phoneNo_lay);
        reg_username=(TextInputEditText )findViewById(R.id.reg_username);
        reg_email=(TextInputEditText )findViewById(R.id.reg_email);
        reg_email.addTextChangedListener(emailTextWatcher);
        reg_password=(TextInputEditText)findViewById(R.id.reg_password);
        reg_password.addTextChangedListener(passwordTextWatcher);
        reg_phoneNo=(TextInputEditText )findViewById(R.id.reg_phoneNo);
        register_btn=(MaterialButton) findViewById(R.id.reg_button);
        register_btn.setOnClickListener(this);
        login=(MaterialTextView) findViewById(R.id.login);
        login.setOnClickListener(this);

        //getting firebase authentication instance
        fireAuth= FirebaseAuth.getInstance ();





    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = fireAuth.getCurrentUser();
        if(currentUser != null){
            //do something
            reload();
        }
    }

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(false);
    }

    //click listener for register btn
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reg_button:
                formValidator();
                break;
            case R.id.login:
                reload();
                break;

        }

    }


    //function for trigerring the homepage on registration success and do something else on failure
    private void updateUi (boolean registrationState) {
            // do something
        if (registrationState) {
            intent =new Intent(this, HomePageActivity.class);
            startActivity(intent);
        }

        else {

            Toast.makeText(this , "You might enter wrong password or invalid email! Try again!", Toast.LENGTH_SHORT).show();
        }
    }


    private void reload () {
        intent=new Intent(this, LoginPage.class);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(this, logoImage, "logoImage");
            startActivity(intent, activityOptions.toBundle());
            finishAfterTransition();
        }

        else {
            startActivity(intent);
            finish();
        }
    }


    //function that got triggered on clicking the regiter button
    private void register () {
        fireAuth.createUserWithEmailAndPassword(email, password )
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete( Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            updateUi(true);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegistrationPage.this, "Authentication failed!",
                                    Toast.LENGTH_SHORT).show();
                            updateUi(false);
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());

                        }
                    }
                });
    }

    private void formValidator () {

        email= reg_email.getText().toString();
        password=reg_password.getText().toString();

        if (email.isEmpty() && password.isEmpty()) {
            reg_email_lay.setError("Email is required");
            reg_password_lay.setError("Password is required");
            reg_email_lay.setErrorEnabled(true);
            reg_password_lay.setErrorEnabled(true);
            Toast.makeText(this,"Hello", Toast.LENGTH_SHORT).show();
            return;
        }

        else if (email.isEmpty() ) {
            reg_email_lay.setErrorContentDescription("Email is required");
            reg_email_lay.setErrorEnabled(true);
            return;
        }

        else if (password.isEmpty()) {
            reg_password_lay.setErrorContentDescription("Password is required");
            reg_password_lay.setErrorEnabled(true);

            return;
        }

        else if (email.isEmpty() && password.isEmpty()) {
            reg_email_lay.setError("Email is required");
            reg_password_lay.setError("Password is required");
            reg_email_lay.setErrorEnabled(true);
            reg_password_lay.setErrorEnabled(true);
            Toast.makeText(this,"Hello", Toast.LENGTH_SHORT).show();
            return;
        }

        String email_regex = "^(.+)@(.+)$";
        Pattern email_pattern= Pattern.compile(email_regex, Pattern.CASE_INSENSITIVE);
        Matcher email_matcher= email_pattern.matcher(email);

        String password_regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$";
        Pattern password_pattern= Pattern.compile(password_regex);
        Matcher password_matcher= password_pattern.matcher(password);

        if (password_matcher.matches() && email_matcher.matches()) {
            register();
        }

        else if  (password_matcher.matches() == false && email_matcher.matches() == false) {
            reg_email_lay.setError("Wrong email format");
            reg_password_lay.setError("Use at least 8 characters. Include an uppercase letter, lower case letter, and a number");
            reg_email_lay.setErrorEnabled(true);
            reg_password_lay.setErrorEnabled(true);
                reg_email.setText("");
                reg_password.setText("");
            }
        else if (password_matcher.matches() == false) {
               // reg_password_lay.setErrorContentDescription();
                reg_password_lay.setError("Use at least 8 characters. Include an uppercase letter, lower case letter, and a number");
                reg_password_lay.setErrorEnabled(true);
                reg_password.setText("");
            }
        else if (email_matcher.matches() == false) {
                reg_email_lay.setError("Wrong email format");
                reg_email_lay.setErrorEnabled(true);
                reg_email.setText("");
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
                reg_email_lay.setErrorEnabled(false);
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
                reg_password_lay.setErrorEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            //do nothing
        }
    };


    }
