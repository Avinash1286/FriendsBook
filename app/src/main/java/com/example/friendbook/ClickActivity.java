package com.example.friendbook;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ClickActivity extends AppCompatActivity {

    ImageView cImage;
    Button editPost,delPost;
    TextView postDes;
    DatabaseReference getPostInfo;
    String key,des,img,currentUser,databaseUserId;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click);
        cImage=(ImageView)findViewById(R.id.clickImage);
        editPost=(Button)findViewById(R.id.clickAdd);
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser().getUid();
        delPost=(Button)findViewById(R.id.clickDel);
        postDes=(TextView)findViewById(R.id.contDes);
        key=getIntent().getExtras().get("PostKey").toString();
        editPost.setVisibility(View.INVISIBLE);
        delPost.setVisibility(View.INVISIBLE);
        getPostInfo= FirebaseDatabase.getInstance().getReference().child("Posts").child(key);
        getPostInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                   des=dataSnapshot.child("Description").getValue().toString();
                   img=dataSnapshot.child("PostImg").getValue().toString();
                    postDes.setText(des);
                    databaseUserId=dataSnapshot.child("UserId").getValue().toString();
                    Picasso.with(getApplicationContext()).load(img).into(cImage);
                    if(currentUser.equals(databaseUserId)){
                        editPost.setVisibility(View.VISIBLE);
                        delPost.setVisibility(View.VISIBLE);

                    }

                    editPost.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder=new AlertDialog.Builder(ClickActivity.this);
                            builder.setTitle("Edit Post");
                            final EditText editText=new EditText(ClickActivity.this);
                            editText.setText(des);
                            builder.setView(editText);
                            builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                               getPostInfo.child("Description").setValue(editText.getText().toString());
                                    Toast.makeText(ClickActivity.this, "Post Updated..", Toast.LENGTH_SHORT).show();
                                }
                            });
                            builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                               dialog.dismiss();
                                }
                            });

                            Dialog dialog=builder.create();
                            dialog.show();

                        }
                    });

                    delPost.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getPostInfo.removeValue();
                            startActivity(new Intent(ClickActivity.this,MainActivity.class));
                            Toast.makeText(ClickActivity.this, "Your Post is deleted", Toast.LENGTH_SHORT).show();
                             finish();
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(ClickActivity.this, "Error..", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
