package com.example.friendbook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;


import java.util.HashMap;

public class UserInfo extends AppCompatActivity {
    EditText usrname,fullname,countryname;
    Button saveInfo;
    String currentUser;
    FirebaseAuth mAuth;
    DatabaseReference userRef;
   ProgressDialog dialog;
   CircularImageView profileImg;
   final static int Gallary_pick=1;
   StorageReference userProfileImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        usrname=(EditText)findViewById(R.id.username);
        mAuth=FirebaseAuth.getInstance();
        profileImg=(CircularImageView)findViewById(R.id.profileimg);
        userProfileImage= FirebaseStorage.getInstance().getReference().child("profileimage");
        dialog=new ProgressDialog(this);
        currentUser=mAuth.getCurrentUser().getUid();
        userRef= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser);
        fullname=(EditText)findViewById(R.id.fullname);
        countryname=(EditText)findViewById(R.id.country);
        saveInfo=(Button)findViewById(R.id.saveuserdata);
        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallary=new Intent();
                gallary.setAction(Intent.ACTION_GET_CONTENT);
                gallary.setType("image/*");
                startActivityForResult(gallary,Gallary_pick);

            }
        });

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
           if(dataSnapshot.exists()){

               if(dataSnapshot.hasChild("profileImage")){
                   String img=dataSnapshot.child("profileImage").getValue().toString();
                   Picasso.with(UserInfo.this).load(img).placeholder(R.drawable.profile).into(profileImg);
               }
               else {
                   Toast.makeText(UserInfo.this, "Image is not fetched Please try again..", Toast.LENGTH_SHORT).show();
               }


           }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        saveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uName=usrname.getText().toString();
                String fName=fullname.getText().toString();
                String cName=countryname.getText().toString();
                if(uName.isEmpty()){
                    usrname.setError("UserName can't be empty");
                    usrname.requestFocus();
                return;
                }
                if (fName.isEmpty()){
                    fullname.setError("FullName can't be empty");
                    fullname.requestFocus();
                 return;
                }
                if (cName.isEmpty()){
                    countryname.setError("Country can't be empty");
                    countryname.requestFocus();
                    return;
                }
                else {
                    dialog.setTitle("Saving");
                    dialog.setMessage("Please wait while we are saving your information");
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                    HashMap uInfo=new HashMap();
                    uInfo.put("UserName",uName);
                    uInfo.put("FullName",fName);
                    uInfo.put("Country",cName);
                    uInfo.put("Dob","None");
                    uInfo.put("RelationShipStatus","None");
                    uInfo.put("Gender","None");
                    uInfo.put("ProfileStatus","Hey There I am the avi and the avi is great..");
                    userRef.updateChildren(uInfo).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {

                            if(task.isSuccessful()){
                                Toast.makeText(UserInfo.this, "Your Information is Successfully saved", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            }
                            else {
                                String error=task.getException().getMessage();
                                Toast.makeText(UserInfo.this, "Error :"+error, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            }


                        }
                    });

                }
                startActivity(new Intent(UserInfo.this,MainActivity.class));
                finish();

            }
        });



    }

   @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Gallary_pick && resultCode==RESULT_OK && data !=null){
            Uri imageUri=data.getData();

            StorageReference filepath=userProfileImage.child(currentUser+".jpg");
          filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
              @Override
              public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                  Toast.makeText(UserInfo.this, "Image is added to firebase..", Toast.LENGTH_SHORT).show();
                  Task<Uri> result=taskSnapshot.getMetadata().getReference().getDownloadUrl();
                  result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                      @Override
                      public void onSuccess(Uri uri) {
                         String downloadUrl=uri.toString();
                          userRef.child("profileImage").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                              @Override
                              public void onComplete(@NonNull Task<Void> task) {

                                  if(task.isSuccessful()){
                                      Toast.makeText(UserInfo.this, "Image Link is successfully saved", Toast.LENGTH_SHORT).show();
                                      dialog.dismiss();

                                  }
                                  else {
                                      Toast.makeText(UserInfo.this, "Image link is not saved", Toast.LENGTH_SHORT).show();
                                      dialog.dismiss();
                                  }

                              }
                          });

                      }
                  });




              }
          });





        }

        }


        }

