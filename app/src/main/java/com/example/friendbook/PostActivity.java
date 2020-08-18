package com.example.friendbook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity {

     Toolbar toolbar;
     ImageButton getImage;
     EditText getDes;
     Button upload;
     Uri imageUri;
     long counterPost;
     final static int Gallary_pick=1;
     private String currentDate,currentTime,randomValue,fullname,profileimg,currentUser,downloadUrl,description;
     FirebaseAuth mAuth;
     DatabaseReference userRef,PostRef;
     StorageReference postStorageRef;
     ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postStorageRef= FirebaseStorage.getInstance().getReference();
        setContentView(R.layout.activity_post);
       toolbar=(Toolbar)findViewById(R.id.postTool);
       dialog=new ProgressDialog(this);
       getImage=(ImageButton)findViewById(R.id.addImg);
       getDes=(EditText)findViewById(R.id.desc);
       upload=(Button)findViewById(R.id.uploadPost);
       setSupportActionBar(toolbar);
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       getSupportActionBar().setDisplayShowHomeEnabled(true);
         mAuth=FirebaseAuth.getInstance();
         currentUser=mAuth.getCurrentUser().getUid();
         userRef= FirebaseDatabase.getInstance().getReference("Users");
        PostRef= FirebaseDatabase.getInstance().getReference("Posts");
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validation();
            }
        });
       getImage.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
               intent.setType("image/*");
               startActivityForResult(intent,Gallary_pick);
           }
       });

    }

    private void Validation() {
         description=getDes.getText().toString();
        if(description.isEmpty()){
            Toast.makeText(this, "Say Something about the post..", Toast.LENGTH_SHORT).show();
            return;
        }
        if(imageUri==null){
            Toast.makeText(this, "Select Your Image..", Toast.LENGTH_SHORT).show();
        }
        else {

            dialog.setTitle("Adding New Post");
            dialog.setMessage("Please Wait..");
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
            Calendar callForDate=Calendar.getInstance();
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MMMM-yyyy");
            currentDate=simpleDateFormat.format(callForDate.getTime());

            Calendar callForTime=Calendar.getInstance();
            SimpleDateFormat simpleTime=new SimpleDateFormat("HH:mm");
            currentTime=simpleTime.format(callForTime.getTime());
            randomValue=currentDate+currentTime;
            StorageReference filepath=postStorageRef.child("PostImage").child(imageUri.getLastPathSegment()+randomValue+".jpg");

            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> download=taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    download.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            downloadUrl=uri.toString();
                            SavePostInformationToDataBase();

                        }
                    });

                }
            });


        }

    }

    private void SavePostInformationToDataBase() {

        PostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    counterPost=dataSnapshot.getChildrenCount();
                }
                else {
                    counterPost=0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        userRef.child(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    fullname=dataSnapshot.child("FullName").getValue().toString();
                    profileimg=dataSnapshot.child("profileImage").getValue().toString();
                    HashMap putPostInfo=new HashMap();
                    putPostInfo.put("UserId",currentUser);
                    putPostInfo.put("Date",currentDate);
                    putPostInfo.put("Time",currentTime);
                    putPostInfo.put("FullName",fullname);
                    putPostInfo.put("ProfileImg",profileimg);
                    putPostInfo.put("Description",description);
                    putPostInfo.put("PostImg",downloadUrl);
                    putPostInfo.put("counter",counterPost);
                    PostRef.child(currentUser+randomValue).updateChildren(putPostInfo).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {

                            if(task.isSuccessful()){
                                Toast.makeText(PostActivity.this, "PostInformation Is Saved", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                startActivity(new Intent(PostActivity.this,MainActivity.class));
                                finish();

                            }
                            else {
                                String mess=task.getException().getMessage();
                                Toast.makeText(PostActivity.this, "Error : "+mess, Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }


                        }
                    });


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Gallary_pick && resultCode==RESULT_OK && data !=null){
            imageUri=data.getData();
            getImage.setImageURI(imageUri);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

    onBackPressed();

    return true;
    }




}
