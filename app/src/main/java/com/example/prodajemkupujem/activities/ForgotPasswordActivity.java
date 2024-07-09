package com.example.prodajemkupujem.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.example.prodajemkupujem.Utils;
import com.example.prodajemkupujem.databinding.ActivityForgotPasswordBinding;

public class ForgotPasswordActivity extends AppCompatActivity {

    
    private ActivityForgotPasswordBinding binding;

    
    private static final String TAG = "FORGOT_PASS_TAG";

    
    private FirebaseAuth firebaseAuth;

    
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        
        firebaseAuth = FirebaseAuth.getInstance();

        
        binding.toolbarBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

    private String email = "";

    private void validateData(){
        Log.d(TAG, "validateData: ");
        
        email = binding.emailEt.getText().toString().trim();

        Log.d(TAG, "validateData: email: "+email);
        
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            
            binding.emailEt.setError("Invalid Email Pattern!");
            binding.emailEt.requestFocus();
        } else {
            
            sendPasswordRecoveryInstructions();
        }

    }

    private void sendPasswordRecoveryInstructions(){
        Log.d(TAG, "sendPasswordRecoveryInstructions: ");
        
        progressDialog.setMessage("Sending password recovery instructions to "+email);
        progressDialog.show();

        
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        
                        progressDialog.dismiss();
                        Utils.toast(ForgotPasswordActivity.this, "Instructions to reset password is sent to "+email);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        
                        Log.e(TAG, "onFailure: ", e);
                        progressDialog.dismiss();
                        Utils.toast(ForgotPasswordActivity.this, "Failed to send due to "+e.getMessage());
                    }
                });
    }
}