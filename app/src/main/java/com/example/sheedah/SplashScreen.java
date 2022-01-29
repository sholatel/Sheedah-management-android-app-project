package com.example.sheedah;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {
    Intent  login_intent, home_page_intent;
    ImageView logoImage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);


        login_intent=new Intent(this, RegistrationPage.class);
        logoImage=(ImageView)findViewById(R.id.logo);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startNewActivity();
            }
        }, 500);


    }

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(false);
    }



    public void startNewActivity () {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(this, logoImage, "logoImage");
            startActivity(login_intent, activityOptions.toBundle());
            finishAfterTransition();
        }

        else {


            startActivity(login_intent);
            finish();
        }
    }
}