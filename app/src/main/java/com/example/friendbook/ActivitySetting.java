package com.example.friendbook;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
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

public class ActivitySetting extends AppCompatActivity {

    EditText setUserName,setFullName,setCountryName,setGender,setRelation,setProfileStatus,setDOB;
    Button updateAccountSetting;
    CircularImageView setProfileImage;
    Toolbar settingToolBar;
    FirebaseAuth mAuth;
     String currentUser;
   final   int Gallary_Pick=1;
     DatabaseReference userDataBase;
     StorageReference userProRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser().getUid();
        userDataBase= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser);
        userProRef= FirebaseStorage.getInstance().getReference().child("profileimage");
        settingToolBar=(Toolbar)findViewById(R.id.settingTool);
        setSupportActionBar(settingToolBar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUserName=(EditText)findViewById(R.id.set_pofile_username);
        setFullName=(EditText)findViewById(R.id.set_pofile_Fullname);
        setCountryName=(EditText)findViewById(R.id.set_pofile_status_country);
        setGender=(EditText)findViewById(R.id.gender);
        setRelation=(EditText)findViewById(R.id.relation);
        setProfileStatus=(EditText)findViewById(R.id.set_pofile_status);
        setDOB=(EditText)findViewById(R.id.dateofbirth);
        updateAccountSetting=(Button)findViewById(R.id.update_profile);
        setProfileImage=(CircularImageView)findViewById(R.id.set_profile);
        userDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String userN=dataSnapshot.child("UserName").getValue().toString();
                    String userFN=dataSnapshot.child("FullName").getValue().toString();
                    String userC=dataSnapshot.child("Country").getValue().toString();
                    String userDob=dataSnapshot.child("Dob").getValue().toString();
                    String userRSS=dataSnapshot.child("RelationShipStatus").getValue().toString();
                    String userG=dataSnapshot.child("Gender").getValue().toString();
                    String userPS=dataSnapshot.child("ProfileStatus").getValue().toString();
                    String userProfileImg=dataSnapshot.child("profileImage").getValue().toString();
                        setDOB.setText(userDob);
                        setRelation.setText(userRSS);
                        setGender.setText(userG);
                        setProfileStatus.setText(userPS);
                    setUserName.setText(userN);
                    setFullName.setText(userFN);
                    setCountryName.setText(userC);
                    Picasso.with(getApplicationContext()).load(userProfileImg).placeholder(R.drawable.profile).into(setProfileImage);
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        updateAccountSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            String ProStatus=setProfileStatus.getText().toString();
            String ProUserName=setUserName.getText().toString();
            String ProFullName=setFullName.getText().toString();
            String ProCountryName=setCountryName.getText().toString();
            String ProGender=setGender.getText().toString();
            String ProRelation=setRelation.getText().toString();
            String ProDob=setDOB.getText().toString();
            if(ProFullName.isEmpty()){
                Toast.makeText(ActivitySetting.this, "UserName is required", Toast.LENGTH_SHORT).show();
                setUserName.setError("Please Enter Your Username");
                setUserName.requestFocus();
            return;
            }

            if(ProUserName.isEmpty()){
                Toast.makeText(ActivitySetting.this, "UserName is required", Toast.LENGTH_SHORT).show();
                setUserName.setError("Please Enter Your user Name");
                setUserName.requestFocus();
            return;
            }
            if (ProCountryName.isEmpty()){
                Toast.makeText(ActivitySetting.this, "Country is required", Toast.LENGTH_SHORT).show();
                setCountryName.setError("Please enter your country name");
                setCountryName.requestFocus();
                return;
            }

           else {

                HashMap uInfo=new HashMap();
                uInfo.put("UserName",ProUserName);
                uInfo.put("FullName",ProFullName);
                uInfo.put("Country",ProCountryName);
                uInfo.put("Dob",ProDob);
                uInfo.put("RelationShipStatus",ProRelation);
                uInfo.put("Gender",ProGender);
                uInfo.put("ProfileStatus",ProStatus);
                userDataBase.updateChildren(uInfo).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                   if (task.isSuccessful()){
                       Toast.makeText(ActivitySetting.this, "User Account Setting is updated successfully", Toast.LENGTH_SHORT).show();
                   }
                   else {
                       String mess=task.getException().getMessage();
                       Toast.makeText(ActivitySetting.this, "Error : "+mess, Toast.LENGTH_SHORT).show();
                   }

                    }
                });


            }
           startActivity(new Intent(ActivitySetting.this,MainActivity.class));
           finish();



            }
        });

        setProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,Gallary_Pick);
            }
        });
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        onBackPressed();
        finish();
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==Gallary_Pick && resultCode==RESULT_OK && data !=null){
            final Uri ImageUri=data.getData();
            StorageReference filepath=userProRef.child(currentUser+".jpg");
            filepath.putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
               Task<Uri> download=taskSnapshot.getMetadata().getReference().getDownloadUrl();
               download.addOnSuccessListener(new OnSuccessListener<Uri>() {
                   @Override
                   public void onSuccess(Uri uri) {

                       String ImageUrl=uri.toString();
                       userDataBase.child("profileImage").setValue(ImageUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {

                               if(task.isSuccessful()){
                                   Toast.makeText(ActivitySetting.this, "Your Profile Picture Is Updated", Toast.LENGTH_SHORT).show();
                               }
                               else {
                                   String mess=task.getException().getMessage();
                                   Toast.makeText(ActivitySetting.this, "Error: "+mess, Toast.LENGTH_SHORT).show();
                               }
                           }
                       });
                   }
               });
                }
            });

        }
        super.onActivityResult(requestCode, resultCode, data);

    }
}
