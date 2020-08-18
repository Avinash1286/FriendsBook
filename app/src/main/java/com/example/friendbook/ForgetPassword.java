package com.example.friendbook;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class ForgetPassword extends AppCompatActivity {


    EditText getEmail;
    Button sendLink;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        getEmail=(EditText) findViewById(R.id.sendemail);
        sendLink=(Button)findViewById(R.id.reset);
        mAuth=FirebaseAuth.getInstance();
        sendLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getE=getEmail.getText().toString();
                if (getE.isEmpty()){
                    Toast.makeText(ForgetPassword.this, "Please Enter Your Email First...", Toast.LENGTH_SHORT).show();
                }
                else {

                    mAuth.sendPasswordResetEmail(getE).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText(ForgetPassword.this, "Please Check Your Email Account...", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                String mess=task.getException().getMessage();
                                Toast.makeText(ForgetPassword.this, "Error: "+mess, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
