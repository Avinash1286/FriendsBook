package com.example.friendbook;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    TextView profileDob,profileUserName,profileFullName,profileGender,profileRelation,profileCountry;
    TextView profileStatus;
    CircularImageView profileImage;
    FirebaseAuth mAuth;
    DatabaseReference userDataBase,noFriendRef,noPostRef;
    String currentUser;
    Button noPost,noFriends;
    int friendCount,postCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser().getUid();
        noFriendRef=FirebaseDatabase.getInstance().getReference().child("FriendsList");
        noPostRef=FirebaseDatabase.getInstance().getReference().child("Posts");
        profileImage=(CircularImageView)findViewById(R.id.proImage);
        profileDob=(TextView)findViewById(R.id.proDob);
        profileUserName=(TextView)findViewById(R.id.prousername);
        profileFullName=(TextView)findViewById(R.id.proname);
        profileGender=(TextView)findViewById(R.id.progender);
        profileRelation=(TextView)findViewById(R.id.proRelation);
        profileCountry=(TextView)findViewById(R.id.procountry);
        profileStatus=(TextView) findViewById(R.id.proStatus);
        noFriends=(Button)findViewById(R.id.noOfFriends);
        noPost=(Button)findViewById(R.id.noOfPost);
        noPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this,MyPostActivity.class));
            }
        });
        noPostRef.orderByChild("UserId").startAt(currentUser).endAt(currentUser+"\uf8ff")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()){
                            postCount=(int)dataSnapshot.getChildrenCount();
                            noPost.setText(Integer.toString(postCount)+" Posts");
                        }
                        else {
                            noPost.setText("0 Post");
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        noFriendRef.child(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    friendCount=(int)dataSnapshot.getChildrenCount();
                    noFriends.setText(Integer.toString(friendCount)+" Friends");
                }
                else {
                    noFriends.setText("0 Friends");
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        noFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this,FriendList.class));
            }
        });
        userDataBase= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser);
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
                    profileDob.setText("Date of birth: "+userDob);
                    profileRelation.setText("RelationShip Status: "+userRSS);
                    profileGender.setText("Gender: "+userG);
                    profileStatus.setText(userPS);
                    profileUserName.setText("@"+userN);
                    profileFullName.setText(userFN);
                    profileCountry.setText("Country: "+userC);
                    Picasso.with(getApplicationContext()).load(userProfileImg).placeholder(R.drawable.profile).into(profileImage);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
