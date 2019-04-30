package com.roque.app.recomiendo.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.roque.app.recomiendo.R;

public class RegisterUserActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText mEmail, mPassword, mConfirmPass;
    private Button mBtnRegister;
    private ProgressBar mProgressBar;

    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mFirebaseAuth = FirebaseAuth.getInstance();

        mEmail = (EditText) findViewById(R.id.edt_register_email);
        mPassword = (EditText) findViewById(R.id.edt_register_password);
        mConfirmPass = (EditText) findViewById(R.id.edt_register_confimrpass);
        mBtnRegister = (Button) findViewById(R.id.btn_register_register);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_register_progressbar);


        mPassword.setTransformationMethod(new PasswordTransformationMethod());
        mConfirmPass.setTransformationMethod(new PasswordTransformationMethod());

        mBtnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v == mBtnRegister) {
            registerUser();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if(currentUser != null){
            sendToMain();
        }
    }

    private void registerUser(){
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString();
        String confirmpass = mConfirmPass.getText().toString();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmpass)){
            if (password.equals(confirmpass)){
                mProgressBar.setVisibility(View.VISIBLE);

                mFirebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Intent setupIntet = new Intent(RegisterUserActivity.this,SetupActivity.class);
                            setupIntet.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(setupIntet);
                            finish();
                        }else {
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(RegisterUserActivity.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();

                        }

                        mProgressBar.setVisibility(View.GONE);

                    }
                });
            }else {
                Toast.makeText(RegisterUserActivity.this, "Confirm Password and Password Field doesn't match.", Toast.LENGTH_LONG).show();

            }
        }
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(RegisterUserActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }


}
