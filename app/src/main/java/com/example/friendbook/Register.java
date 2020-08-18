package com.example.friendbook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    EditText reEmail,rePass,reConPass;
    Button createAccount;
   ProgressDialog dialog;
    FirebaseAuth creatAcc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
       dialog=new ProgressDialog(this);
        reEmail=(EditText)findViewById(R.id.emailText);
        rePass=(EditText)findViewById(R.id.passText);
        reConPass=(EditText)findViewById(R.id.confText);
        createAccount=(Button)findViewById(R.id.butcreatAccount);
        creatAcc=FirebaseAuth.getInstance();
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=reEmail.getText().toString();
                String password=rePass.getText().toString();
                String confirmPassword=reConPass.getText().toString();
                if(email.isEmpty()){
                    reEmail.setError("Please Enter Your Email");
                    reEmail.requestFocus();
                    return;
                }
                 if(password.isEmpty()){
                    rePass.setError("Please Enter Your Password");
                    rePass.requestFocus();
                    return;
                }
                 if(password.length()<6){
                    rePass.setError("Password is less then 6 characters");
                    rePass.requestFocus();
                }
                 if(!password.equals(confirmPassword)){

                    reConPass.setError("Your Password and Confirm Password is Matching");

                }
                 if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    reEmail.setError("Your Email Is Not Valid");
                    reEmail.requestFocus();

                }
                else {
                    dialog.setTitle("Creating Account");
                    dialog.setMessage("Please wait while we are creating your account..");
                    dialog.show();
                    dialog.setCanceledOnTouchOutside(true);
                    creatAcc.createUserWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if(task.isSuccessful()){

                                        SendToSetUpAct();

                                        Toast.makeText(Register.this, "Your account is created successfully..", Toast.LENGTH_SHORT).show();
                                         dialog.dismiss();
                                    }
                                    if(!task.isSuccessful()){

                                        Toast.makeText(Register.this, "Registration Failed", Toast.LENGTH_SHORT).show();

                                        dialog.dismiss();

                                    }




                                }
                            });
                }

            }
        });

    }
    public void SendToSetUpAct(){
        startActivity(new Intent(Register.this,UserInfo.class));
        finish();
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentcuser=creatAcc.getCurrentUser();
        if(currentcuser !=null){
            startActivity(new Intent(Register.this,MainActivity.class));
            finish();
        }
    }
}
