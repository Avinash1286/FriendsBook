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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.util.regex.Pattern;

public class LogIn extends AppCompatActivity {

     EditText logEmailText,logPassText;
    Button butLogIn;
    ImageView facebook,twitter,google;
    ProgressDialog dialog;
    TextView createAccount,forget;
    FirebaseAuth mAuth;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
             mAuth=FirebaseAuth.getInstance();
             dialog=new ProgressDialog(this);
        logEmailText=(EditText)findViewById(R.id.logemailText);
        logPassText=(EditText)findViewById(R.id.logpassText);
        butLogIn=(Button)findViewById(R.id.login);
        facebook=(ImageView)findViewById(R.id.loginfacebook);
        twitter=(ImageView)findViewById(R.id.logintwiter);
        google=(ImageView)findViewById(R.id.logingoogle);
        createAccount=(TextView)findViewById(R.id.creatnewaccount);
        forget=(TextView)findViewById(R.id.forget);
        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogIn.this,ForgetPassword.class));
            }
        });
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogIn.this,Register.class));
            }
        });
        butLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=logEmailText.getText().toString();
                String pass=logPassText.getText().toString();
                if(email.isEmpty()){
                    logEmailText.setError("Please Enter Your Email");
                    logEmailText.requestFocus();

                }
                if(pass.isEmpty()){
                    logPassText.setError("Please Enter Your Email");
                    logPassText.requestFocus();
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    logEmailText.setError("Your Email is not valid");
                    logEmailText.requestFocus();
                }

                else {
                    dialog.setTitle("Logging");
                    dialog.setMessage("Please wait while we are logging your account");
                    dialog.show();
                    dialog.setCanceledOnTouchOutside(true);
                    mAuth.signInWithEmailAndPassword(email,pass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                               
                                    if(task.isSuccessful()){
                                        startActivity(new Intent(LogIn.this,MainActivity.class));
                                        Toast.makeText(LogIn.this, "You LoggedIn Successfully", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    finish();
                                    }
                                    if(!task.isSuccessful()){
                                        String mess=task.getException().getMessage();
                                        Toast.makeText(LogIn.this, "LogIn failed: "+mess, Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    }
                                    
                                }
                            });
                    
                }
                
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentcuser=mAuth.getCurrentUser();
        if(currentcuser !=null){
            startActivity(new Intent(LogIn.this,MainActivity.class));
            finish();
        }
    }
}
