package com.example.sheedah;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginPage extends AppCompatActivity  implements View.OnClickListener{
    TextInputEditText email, password;
    TextInputLayout email_lay, password_lay;
    String emailValue, passwordValue;
    MaterialButton login;
    ShapeableImageView logo;
    MaterialTextView forgetPassword , signUp;
    ProgressBar loginPb;
    private FirebaseAuth fireAuth;
    private static String TAG ="LoginPage";
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
        setContentView(R.layout.login_page);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAllowEnterTransitionOverlap(true);

        //initializing views
        logo=findViewById(R.id.logo);
        email=(TextInputEditText)findViewById(R.id.email);
        email.addTextChangedListener(emailTextWatcher);
        password=(TextInputEditText)findViewById(R.id.password);
        password.addTextChangedListener(passwordTextWatcher);
        password.setOnClickListener(this);
        login=(MaterialButton) findViewById(R.id.login_button);
        login.setOnClickListener(this);
        email_lay=(TextInputLayout)findViewById(R.id.email_lay);
        password_lay=(TextInputLayout)findViewById(R.id.password_lay);
        forgetPassword= findViewById(R.id.forgot_password);
        forgetPassword.setOnClickListener(onForgetPasswordClicked);
        signUp=findViewById(R.id.signUp);
        signUp.setOnClickListener(onSignUpClicked);
        loginPb= findViewById(R.id.loginPb);

        //initializing fire base auth instance
        fireAuth=FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //String startStatus= getIntent().getStringExtra(STATUS_KEY);
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
        finishAfterTransition();
    }

    private void signIn () {
        if (validateForm()) {
            loginPb.setVisibility(View.VISIBLE);
            login.setEnabled(false);
            fireAuth.signInWithEmailAndPassword(emailValue, passwordValue)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = fireAuth.getCurrentUser();
                                updateUi();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure( Exception e) {
                    // If sign in fails, display a message to the user.
                    loginPb.setVisibility(View.GONE);
                    login.setEnabled(true);
                    password_lay.setError(e.getLocalizedMessage());
                    email_lay.setErrorEnabled(true);
                    password_lay.setErrorEnabled(true);
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

    View.OnClickListener onForgetPasswordClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            forgetPassword.setEnabled(false);
            Intent intent=new Intent(LoginPage.this, ForgetPasswordEmail.class);
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(LoginPage.this, logo, "logoImage");
                startActivity(intent, activityOptions.toBundle());

            }

            else {
                startActivity(intent);

            }
        }
    } ;

    View.OnClickListener onSignUpClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent=new Intent(LoginPage.this, RegistrationPage.class);
            //intent.putExtra(LoginPage.STATUS_KEY,LoginPage.ACT_FORCE_STATUS);
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(LoginPage.this, logo, "logoImage");
                startActivity(intent, activityOptions.toBundle());
                finishAfterTransition();
            }

            else {
                startActivity(intent);
                finish();
            }

            signUp.setEnabled(false);
        }
    } ;


    @Override
    protected void onResume() {
        super.onResume();
        if (forgetPassword!= null)
            forgetPassword.setEnabled(true);
    }
}