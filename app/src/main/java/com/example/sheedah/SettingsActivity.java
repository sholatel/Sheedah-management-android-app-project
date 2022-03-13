package com.example.sheedah;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    Toolbar backBar;
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
        setContentView(R.layout.activity_settings);
        backBar=findViewById(R.id.backBar);

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        //boolean switchVal= sharedPreferences.getBoolean("theme",false);
        getSupportFragmentManager().beginTransaction().add(R.id.settingFragCon,new SettingsFragment()).commit();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);


        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        backBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(SettingsActivity.this,HomePageActivity.class));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //getSharedpreferences().registeronSharedPreferenceChangeListener(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(SettingsActivity.this,HomePageActivity.class));
            return  true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("theme")) {
            boolean switchVal= sharedPreferences.getBoolean(key,false);
            if (switchVal) {
                setTheme(R.style.sheedah_dark);
                recreate();

            }

            else {
                setTheme(R.style.sheedah);
                recreate();
            }
        }

    }
}