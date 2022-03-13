package com.example.sheedah;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentContainerView;
import androidx.preference.PreferenceManager;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    MaterialButton changePasswordbtn;
    ProgressBar profilePb;
    Button saveBtn;
    Toolbar backBar;
    TextInputEditText username, password, email;
    //TextInputLayout usernameLay, passwordLay, emailLay;
    FirebaseUser user;

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
        setContentView(R.layout.activity_profile);
        changePasswordbtn=findViewById(R.id.changePasswordBtn);
        backBar= findViewById(R.id.backBar);
        backBar.setOnClickListener(this);
        profilePb= findViewById(R.id.profilePb);
        saveBtn=findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(this);
        changePasswordbtn.setOnClickListener(this);
        username=findViewById(R.id.profileName);
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                saveBtn.setEnabled(true);
            }
        });
        //password=findViewById(R.id.profilePassoword);
        email=findViewById(R.id.profileEmail);
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                saveBtn.setEnabled(true);
            }
        });
        user=FirebaseAuth.getInstance().getCurrentUser();
        populateProfile();
    }

    void populateProfile() {
        if (user != null) {
            username.setText(user.getDisplayName());
            email.setText(user.getEmail());
        }
    }
    private void updateProfile () {
        if (!(username.getText().toString().isEmpty()) && !(email.getText().toString().isEmpty())) {
            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(username.getText().toString()).build();
            user.updateProfile(profileChangeRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(ProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    profilePb.setVisibility(View.GONE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(ProfileActivity.this, "Profile update failed!",Toast.LENGTH_SHORT).show();
                    profilePb.setVisibility(View.GONE);
                }
            });;
        }

        else if (!(username.getText().toString().isEmpty())) {
            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(username.getText().toString()).build();
            user.updateProfile(profileChangeRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(ProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure( Exception e) {
                    Toast.makeText(ProfileActivity.this, "Profile update failed!",Toast.LENGTH_SHORT).show();
                }
            });
        }

        else  if (!(email.getText().toString().isEmpty()))  {
            user.updateEmail(email.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(ProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this, "Profile update failed!",Toast.LENGTH_SHORT).show();
                }
            });
        }

        else {
            profilePb.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.changePasswordBtn:
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this,R.style.alertDialogueStyle);
                alertBuilder.setTitle("Change password");
                View alertView = View.inflate(alertBuilder.getContext(), R.layout.change_password_layout, null);
                TextInputEditText newPassword = alertView.findViewById(R.id.changePassword);
                TextInputLayout new_password_lay = alertView.findViewById(R.id.change_password_lay);
                alertBuilder.setView(alertView);
                alertBuilder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!(newPassword.getText().toString().isEmpty())) {
                            user.updatePassword(newPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(ProfileActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(Exception e) {
                                    new_password_lay.setError(e.getLocalizedMessage());
                                    new_password_lay.setErrorEnabled(true);
                                }
                            });
                        }
                        else {
                            new_password_lay.setError("Input new password");
                            new_password_lay.setErrorEnabled(true);
                            return;
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alertBuilder.create();
                alertBuilder.show();
                break;

            case R.id.saveBtn:
                profilePb.setVisibility(View.VISIBLE);
                updateProfile();
                break;

            case R.id.backBar:
                onBackPressed();
                break;
        }

    }
}